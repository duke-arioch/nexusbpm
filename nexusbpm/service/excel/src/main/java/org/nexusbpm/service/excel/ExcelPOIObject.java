package org.nexusbpm.service.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.Ostermiller.util.CSVParser;
import org.nexusbpm.common.data.ObjectConverter;
import org.apache.commons.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author catch23
 * @author Matthew Sandoz (Updates 2007)
 * @authoer Nathan Rose
 */
public class ExcelPOIObject {

  private static final transient Logger logger = LoggerFactory.getLogger(ExcelPOIObject.class);
  protected FileObject template;
  protected FileObject output;
  protected String sheetName;
  protected HSSFWorkbook book;
  protected HSSFSheet sheet;
  protected HSSFRow currentRow;
  protected boolean usingTemplate;
  protected ExcelStyleFactory styleFactory;
  protected Map<String, Object> cache;
  protected final int MAX_CACHE_SIZE = 128;

  public ExcelPOIObject(FileObject template, String sheetName) {
    this.template = template;
    this.sheetName = sheetName;
  }

  public void setOutputStreamProvider(FileObject output) {
    this.output = output;
  }

  public void initialize() throws Exception {
    book = null;
    sheet = null;
    usingTemplate = false;

    if (template != null /* && template.fileExists() */) {
      if (logger.isDebugEnabled()) {
        logger.debug("Attempting to read POI template file " + template.getName().getURI());
      }
      InputStream templateStream = template.getContent().getInputStream();
      if (templateStream == null) {
        throw new IOException("Couldn't load template file " + template.getName().getURI());
      }

      POIFSFileSystem templateFile = new POIFSFileSystem(templateStream);
      if (templateFile == null) {
        throw new IOException("Couldn't create POI file from template " + template.getName().getURI());
      }

      try {
        book = new HSSFWorkbook(templateFile);
      } catch (NullPointerException e) {
        IOException ex = new IOException(
                "Couldn't read from POI template file " + template.getName().getURI());
        ex.initCause(e);
        throw ex;
      }
    }

    usingTemplate = book != null;

    if (book == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Creating blank POI template (no template file was found)");
      }
      book = new HSSFWorkbook();
    }

    int sheetIndex = book.getSheetIndex(sheetName);
    if (sheetIndex >= 0 && sheetIndex < book.getNumberOfSheets()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Found sheet named '" + sheetName + "' at index " + sheetIndex);
      }
      sheet = book.getSheetAt(sheetIndex);
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("Creating new sheet named '" + sheetName + "'");
      }
      sheet = book.createSheet(sheetName);
    }
  }

  /**
   * Reads data in CSV format and inserts it into the spreadsheet at the specified anchor.
   */
  public int insertTableAtAnchor(
          FileObject inputData, String anchorString,
          boolean skipHeader, int rowLimit, int colLimit) throws Exception {
    CSVParser parser = new CSVParser(inputData.getContent().getInputStream());
    if (logger.isDebugEnabled()) {
      logger.debug("POI anchor=" + anchorString + ";rowLimit=" + rowLimit + ";colLimit=" + colLimit);
    }

    Anchor anchor = new Anchor(anchorString);
    int rowOffset = anchor.getRowIndex() - 1;
    short columnOffset = (short) (anchor.getColIndex() - 1);

    if (skipHeader) {
      parser.getLine();
    }
    int row = 0;
    for (; (row < rowLimit) || (rowLimit == -1); row++) {
      String[] line = parser.getLine();
      if (line == null) {
        break;
      }

      List<String> rowValues = Arrays.asList(line);
      currentRow = sheet.getRow(rowOffset + row);
      if (currentRow == null) {
        currentRow = sheet.createRow(rowOffset + row);
      }

      insertRow(row == 0 && !skipHeader, rowValues, columnOffset, colLimit);

      if (logger.isDebugEnabled() && (row % 1000) == 0 && row > 0) {
        logger.debug("Inserted " + row + " rows of " + rowLimit + ";lastRow=" + sheet.getLastRowNum());
      }
    }
    return row;
  }

  /**
   * @param rowValues
   * @param columnOffset
   * @param columnLimit
   * @throws Exception
   */
  protected void insertRow(boolean header,
          List<String> rowValues, short columnOffset, int columnLimit) throws Exception {
    int size = rowValues.size();

    if (columnLimit > 0 && columnLimit < size) {
      size = columnLimit;
    }

    // check if the bound is exceeded if this row is added starting from the anchor
    if (checkOutOfBound(columnOffset, size)) {
      throw new IndexOutOfBoundsException("Excel spreadsheet column index "
              + (columnOffset + size) + " exceeds the limit of 256");
    }

    Iterator<String> iter = rowValues.iterator();
    short columnIndex = columnOffset;
    int columnNumber = 0;

    while (iter.hasNext()) {
      String value = iter.next();

      HSSFCell cell = currentRow.getCell((short) (columnIndex));

      if (cell == null) {
        if (logger.isTraceEnabled()) {
          logger.trace("Creating cell for column " + columnNumber
                  + " in row " + currentRow.getRowNum());
        }
        cell = currentRow.createCell((short) (columnIndex));
//                if(!header) {
//                    setDefaultCellType(cell);
//                }
      }

      if (logger.isTraceEnabled()) {
        logger.trace("Value for column " + columnNumber + " row "
                + currentRow.getRowNum() + " is " + value);
      }

      if (value == null) {
        clearCellValue(cell);
      } else {
        setCellValue(cell, value);
      }
      columnIndex++;
      columnNumber++;
      if ((columnNumber >= columnLimit) && (columnLimit != -1)) {
        break;
      }
    }
  }

  /**
   * Sets the cell value based on the type of value given.
   */
  protected void setCellValue(HSSFCell cell, String sourceValue) {
    Object value;

    if (cache == null) {
      cache = new LRUHashMap<String, Object>(64, MAX_CACHE_SIZE);
    }

    if (cache.containsKey(sourceValue)) {
      value = cache.get(sourceValue);
    } else {
      value = ObjectConverter.convert(sourceValue, true);

      cache.put(sourceValue, value);
    }

    if (value instanceof Date) {
      boolean setStyle = !usingTemplate || !HSSFDateUtil.isCellDateFormatted(cell);
      cell.setCellValue((Date) value);
      if (setStyle) {
        cell.setCellStyle(getStyleFactory().getCellStyle(value.getClass(), cell.getCellStyle()));
      }
    } else if (value instanceof Number) {
      String format = getStyleFactory().getCellFormat(cell);
      boolean setStyle = false;
      cell.setCellValue(((Number) value).doubleValue());
      if (setStyle) {
        Class c = value.getClass();
        if (BigInteger.class.equals(c) || Long.class.equals(c) || Integer.class.equals(c)
                || Short.class.equals(c) || Byte.class.equals(c)) {
          cell.setCellStyle(getStyleFactory().getCellStyle(Integer.class, cell.getCellStyle()));
        } else {
          cell.setCellStyle(getStyleFactory().getCellStyle(Float.class, cell.getCellStyle()));
        }
      }
    } else if (value instanceof Boolean) {
      cell.setCellValue(((Boolean) value).booleanValue());
    } else {
      cell.setCellValue((value.toString()));
    }
  }

  protected ExcelStyleFactory getStyleFactory() {
    if (styleFactory == null) {
      styleFactory = new ExcelStyleFactory(book);
    }
    return styleFactory;
  }

  /**
   * Clears the value for the given cell, but attempts to preserve formatting.
   */
  protected void clearCellValue(HSSFCell cell) {
    // if we're not using a template, then no value has been set yet
    if (usingTemplate) {
      cell.setCellValue((String) null);
    }
  }

  /**
   * @param start
   * @param len
   * @return boolean
   */
  protected boolean checkOutOfBound(int start, int len) {
    boolean ret = false;
    if (start + len - 1 > 256) {
      ret = true;
    }
    return ret;
  }

  /**
   * @throws Exception
   */
  public void save() throws Exception {
    OutputStream stream = output.getContent().getOutputStream();
    book.write(stream);
    stream.flush();
    stream.close();
  }
}
