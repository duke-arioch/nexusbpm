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

  private static final transient Logger LOGGER = LoggerFactory.getLogger(ExcelPOIObject.class);
  protected transient FileObject template;
  protected transient FileObject output;
  protected transient String sheetName;
  protected transient HSSFWorkbook book;
  protected transient HSSFSheet sheet;
  protected transient HSSFRow currentRow;
  protected transient boolean usingTemplate;
  protected transient ExcelStyleFactory styleFactory;
  protected transient Map<String, Object> cache;
  protected static final int MAX_CACHE_SIZE = 128;

  public ExcelPOIObject(final FileObject template, final String sheetName) {
    this.template = template;
    this.sheetName = sheetName;
  }

  public void setOutputStreamProvider(final FileObject output) {
    this.output = output;
  }

  public void initialize() throws Exception {
    book = null;
    sheet = null;
    usingTemplate = false;

    if (template != null /* && template.fileExists() */) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Attempting to read POI template file " + template.getName().getURI());
      }
      final InputStream templateStream = template.getContent().getInputStream();
      if (templateStream == null) {
        throw new IOException("Couldn't load template file " + template.getName().getURI());
      }

      final POIFSFileSystem templateFile = new POIFSFileSystem(templateStream);
      if (templateFile == null) {
        throw new IOException("Couldn't create POI file from template " + template.getName().getURI());
      }

      try {
        book = new HSSFWorkbook(templateFile);
      } catch (NullPointerException e) {
        throw new IOException(
                "Couldn't read from POI template file " + template.getName().getURI(), e);
      }
    }

    usingTemplate = book != null;

    if (book == null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Creating blank POI template (no template file was found)");
      }
      book = new HSSFWorkbook();
    }

    final int sheetIndex = book.getSheetIndex(sheetName);
    if (sheetIndex >= 0 && sheetIndex < book.getNumberOfSheets()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Found sheet named '" + sheetName + "' at index " + sheetIndex);
      }
      sheet = book.getSheetAt(sheetIndex);
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Creating new sheet named '" + sheetName + "'");
      }
      sheet = book.createSheet(sheetName);
    }
  }

  /**
   * Reads data in CSV format and inserts it into the spreadsheet at the specified anchor.
   */
  public int insertTableAtAnchor(
          final FileObject inputData, final String anchorString,
          final boolean skipHeader, final int rowLimit, final int colLimit) throws Exception {
    final CSVParser parser = new CSVParser(inputData.getContent().getInputStream());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("POI anchor=" + anchorString + ";rowLimit=" + rowLimit + ";colLimit=" + colLimit);
    }

    final Anchor anchor = new Anchor(anchorString);
    final int rowOffset = anchor.getRowIndex() - 1;
    final int columnOffset = anchor.getColIndex() - 1;

    if (skipHeader) {
      parser.getLine();
    }
    int row = 0;
    for (; (row < rowLimit) || (rowLimit == -1); row++) {
      final String[] line = parser.getLine();
      if (line == null) {
        break;
      }

      final List<String> rowValues = Arrays.asList(line);
      currentRow = sheet.getRow(rowOffset + row);
      if (currentRow == null) {
        currentRow = sheet.createRow(rowOffset + row);
      }

      insertRow(row == 0 && !skipHeader, rowValues, (short) columnOffset, colLimit);

      if (LOGGER.isDebugEnabled() && (row % 1000) == 0 && row > 0) {
        LOGGER.debug("Inserted " + row + " rows of " + rowLimit + ";lastRow=" + sheet.getLastRowNum());
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
  protected void insertRow(final boolean header,
          final List<String> rowValues, final short columnOffset, final int columnLimit) throws Exception {
    int size = rowValues.size();

    if (columnLimit > 0 && columnLimit < size) {
      size = columnLimit;
    }

    // check if the bound is exceeded if this row is added starting from the anchor
    if (checkOutOfBound(columnOffset, size)) {
      throw new IndexOutOfBoundsException("Excel spreadsheet column index "
              + (columnOffset + size) + " exceeds the limit of 256");
    }

    final Iterator<String> iter = rowValues.iterator();
    int columnIndex = columnOffset;
    int columnNumber = 0;

    while (iter.hasNext()) {
      final String value = iter.next();

      HSSFCell cell = currentRow.getCell((short) (columnIndex));

      if (cell == null) {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Creating cell for column " + columnNumber
                  + " in row " + currentRow.getRowNum());
        }
        cell = currentRow.createCell((short) (columnIndex));
//                if(!header) {
//                    setDefaultCellType(cell);
//                }
      }

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Value for column " + columnNumber + " row "
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
  protected void setCellValue(final HSSFCell cell, final String sourceValue) {
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
      final boolean setStyle = !usingTemplate || !HSSFDateUtil.isCellDateFormatted(cell);
      cell.setCellValue((Date) value);
      if (setStyle) {
        cell.setCellStyle(getStyleFactory().getCellStyle(value.getClass(), cell.getCellStyle()));
      }
    } else if (value instanceof Number) {
//      String format = getStyleFactory().getCellFormat(cell);
      final boolean setStyle = false;
      cell.setCellValue(((Number) value).doubleValue());
      if (setStyle) {
        final Class c = value.getClass();
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
      cell.setCellValue(value.toString());
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
  protected void clearCellValue(final HSSFCell cell) {
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
  protected boolean checkOutOfBound(final int start, final int len) {
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
    final OutputStream stream = output.getContent().getOutputStream();
    book.write(stream);
    stream.flush();
    stream.close();
  }
}
