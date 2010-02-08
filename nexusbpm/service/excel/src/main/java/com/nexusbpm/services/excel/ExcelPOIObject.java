package com.nexusbpm.services.excel;

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
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.Ostermiller.util.CSVParser;
import com.nexusbpm.common.data.ObjectConverter;
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
//    protected Map<Class, HSSFCellStyle> styles;
//    protected Map<Integer, Integer> columnTypes;
    
    protected ExcelStyleFactory styleFactory;
    
    protected Map<String, Object> cache;
//    protected List<String> cacheQueue;
    
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
//        styles = null;
        usingTemplate = false;
        
        if(template != null /* && template.fileExists() */) {
            if(logger.isDebugEnabled()) {
                logger.debug("Attempting to read POI template file " + template.getName().getURI());
            }
            InputStream templateStream = template.getContent().getInputStream();
            if(templateStream == null) {
                throw new IOException("Couldn't load template file " + template.getName().getURI());
            }
            
            POIFSFileSystem templateFile = new POIFSFileSystem(templateStream);
            if(templateFile == null) {
                throw new IOException("Couldn't create POI file from template " + template.getName().getURI());
            }
            
            try {
                book = new HSSFWorkbook(templateFile);
            } catch(NullPointerException e) {
                IOException ex = new IOException(
                        "Couldn't read from POI template file " + template.getName().getURI());
                ex.initCause(e);
                throw ex;
            }
        }
        
        usingTemplate = book != null;
        
        if(book == null) {
            if(logger.isDebugEnabled()) {
                logger.debug("Creating blank POI template (no template file was found)");
            }
            book = new HSSFWorkbook();
        }
        
        int sheetIndex = book.getSheetIndex(sheetName);
        if(sheetIndex >= 0 && sheetIndex < book.getNumberOfSheets()) {
            if(logger.isDebugEnabled()) {
                logger.debug("Found sheet named '" + sheetName + "' at index " + sheetIndex);
            }
            sheet = book.getSheetAt(sheetIndex);
        } else {
            if(logger.isDebugEnabled()) {
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
        if(logger.isDebugEnabled()) {
            logger.debug("POI anchor=" + anchorString + ";rowLimit=" + rowLimit + ";colLimit=" + colLimit);
        }
        
        Anchor anchor = new Anchor(anchorString);
        int rowOffset = anchor.getRowIndex() - 1;
        short columnOffset = (short) (anchor.getColIndex() - 1);
        
        if(skipHeader) {
            parser.getLine();
        }
        int row = 0;
        for(; (row < rowLimit) || (rowLimit == -1); row++) {
            String[] line = parser.getLine();
            if(line == null) break;
            
            List<String> rowValues = Arrays.asList(line);
            currentRow = sheet.getRow(rowOffset + row);
            if(currentRow == null) {
                currentRow = sheet.createRow(rowOffset + row);
            }
            
            insertRow(row == 0 && !skipHeader, rowValues, columnOffset, colLimit);
            
            if(logger.isDebugEnabled() && (row % 1000) == 0 && row > 0) {
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
        
        if(columnLimit > 0 && columnLimit < size) {
            size = columnLimit;
        }
        
        // check if the bound is exceeded if this row is added starting from the anchor
        if (checkOutOfBound(columnOffset, size)) {
            throw new IndexOutOfBoundsException("Excel spreadsheet column index " +
                    (columnOffset + size) + " exceeds the limit of 256");
        }
        
        Iterator<String> iter = rowValues.iterator();
        short columnIndex = columnOffset;
        int columnNumber = 0;
        
        while (iter.hasNext()) {
            String value = iter.next();
            
            HSSFCell cell = currentRow.getCell((short) (columnIndex));
            
            if (cell == null) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Creating cell for column " + columnNumber +
                            " in row " + currentRow.getRowNum());
                }
                cell = currentRow.createCell((short) (columnIndex));
//                if(!header) {
//                    setDefaultCellType(cell);
//                }
            }
            
            if(logger.isTraceEnabled()) {
                logger.trace("Value for column " + columnNumber + " row " +
                        currentRow.getRowNum() + " is " + value);
            }
            
            if(value == null) {
                clearCellValue(cell);
//            } else if(header) {
//                setCellValue(cell, HSSFCell.CELL_TYPE_STRING, value);
//            } else if(usingTemplate) {
//                setCellValue(cell, cell.getCellType(), value);
            } else {
                setCellValue(cell, value);
            }
            
//            if (obj == null) {
//                // Calling setCellValue(String) automatically sets the cell type to
//                // HSSFCell.CELL_TYPE_STRING, and avoids possible problems with calling
//                // setCellType() manually.
//                cell.setCellValue(new HSSFRichTextString("null"));
//            } else if((cell.getCellType() == HSSFCell.CELL_TYPE_BLANK ||
//                    cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA ||
//                    cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) &&
//                    HSSFDateUtil.isCellDateFormatted(cell)) {
//                // Calling setCellValue(double) automatically sets the cell type to
//                // HSSFCell.CELL_TYPE_NUMERIC, and avoids possible problems with calling
//                // setCellType() manually.
//                cell.setCellValue((java.util.Date) ObjectConverter.convert(obj, java.util.Date.class));
//            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
//                try {
//                    Double d = new Double(obj.toString());
//                    cell.setCellValue(d);
//                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//                } catch(Exception e) {
//                    String s = obj.toString();
//                    cell.setCellValue(new HSSFRichTextString(s));
//                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//                }
//            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//                // Calling setCellValue(double) automatically sets the cell type to
//                // HSSFCell.CELL_TYPE_NUMERIC, and avoids possible problems with calling
//                // setCellType() manually.
//                cell.setCellValue(Double.parseDouble(obj.toString()));
//            } else {
//                // Calling setCellValue(String) automatically sets the cell type to
//                // HSSFCell.CELL_TYPE_STRING, and avoids possible problems with calling
//                // setCellType() manually.
//                cell.setCellValue(new HSSFRichTextString(obj.toString()));
//            }
            
//            LOG.debug( "insertRow colOff=" + columnOffset + ";colLim=" + columnLimit
//                + ";i=" + i + ";index=" + index + ";objClass=" + (obj == null ? "null":obj.getClass().getName())
//                + ";obj=" + obj);
            
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
        
//        if(cacheMap == null) {
//            cacheMap = new HashMap<String, Object>();
//            cacheQueue = new LinkedList<String>();
//        }
        if(cache == null) {
            cache = new LRUHashMap<String, Object>(64, MAX_CACHE_SIZE);
        }
        
        if(cache.containsKey(sourceValue)) {
            value = cache.get(sourceValue);
        } else {
            value = ObjectConverter.convert(sourceValue, true);
            
//            if(cacheQueue.size() > MAX_CACHE_SIZE) {
//                cacheMap.remove(cacheQueue.remove(0));
//            }
            
            cache.put(sourceValue, value);
        }
        
        if(value instanceof Date) {
            boolean setStyle = !usingTemplate || !HSSFDateUtil.isCellDateFormatted(cell);
            cell.setCellValue((Date) value);
            if(setStyle) {
                cell.setCellStyle(getStyleFactory().getCellStyle(value.getClass(), cell.getCellStyle()));
//                HSSFCellStyle style = getCellStyle(value.getClass());
//                HSSFCellStyle cellStyle = cell.getCellStyle();
//                cellStyle.setDataFormat(style.getDataFormat());
////                cell.setCellStyle(getCellStyle(value.getClass()));
            }
        } else if(value instanceof Number) {
            String format = getStyleFactory().getCellFormat(cell);
            boolean setStyle = false;
//                !usingTemplate || format.equalsIgnoreCase("General") ||
//                format.equals("@") || format.equalsIgnoreCase("text");
            cell.setCellValue(((Number) value).doubleValue());
            if(setStyle) {
                Class c = value.getClass();
                if(BigInteger.class.equals(c) || Long.class.equals(c) || Integer.class.equals(c) ||
                        Short.class.equals(c) || Byte.class.equals(c)) {
                    cell.setCellStyle(getStyleFactory().getCellStyle(Integer.class, cell.getCellStyle()));
                } else {
                    cell.setCellStyle(getStyleFactory().getCellStyle(Float.class, cell.getCellStyle()));
                }
//                HSSFCellStyle style = getCellStyle(Number.class);
//                HSSFCellStyle cellStyle = cell.getCellStyle();
//                cellStyle.setDataFormat(style.getDataFormat());
////                cell.setCellStyle(getCellStyle(Number.class));
            }
        } else if(value instanceof Boolean) {
            cell.setCellValue(((Boolean) value).booleanValue());
        } else {
            cell.setCellValue((value.toString()));
        }
    }
    
    protected ExcelStyleFactory getStyleFactory() {
        if(styleFactory == null) {
            styleFactory = new ExcelStyleFactory(book);
        }
        return styleFactory;
    }
    
//    protected void setDefaultCellType(HSSFCell cell) {
//        if(columnTypes == null) {
//            columnTypes = new HashMap<Integer, Integer>();
//        }
//        Integer cellNum = Integer.valueOf(cell.getCellNum());
//        if(!columnTypes.containsKey(cellNum)) {
//            HSSFDataFormat dataFormat = book.createDataFormat();
//            short formatIndex = cell.getCellStyle().getDataFormat();
//            String format = dataFormat.getFormat(formatIndex);
//            if(format != null) {
////                System.out.println("Cell at " + currentRow.getRowNum() + "," + cell.getCellNum() + " is " + format);
//                if(format.equals("@") || format.equals("text")) {
//                    columnTypes.put(cellNum, Integer.valueOf(HSSFCell.CELL_TYPE_STRING));
//                } else if(HSSFDateUtil.isADateFormat(formatIndex, format)) {
//                    columnTypes.put(cellNum, Integer.valueOf(HSSFCell.CELL_TYPE_NUMERIC));
//                } else {
//                    columnTypes.put(cellNum, null);
//                }
////                else if(!format.equals("General")) {
////                    
////                }
//            }
//        }
//        Integer type = columnTypes.get(cellNum);
//        if(type != null && type.intValue() != HSSFCell.CELL_TYPE_BLANK) {
//            cell.setCellType(type.intValue());
//        }
//    }
    
//    /**
//     * Converts the given value based on the given cell type and sets
//     * it as the cell value.
//     * @throws ObjectConversionException 
//     */
//    protected void setCellValue(HSSFCell cell, int cellType, String value) {
//        try {
//            switch(cellType) {
//                case HSSFCell.CELL_TYPE_BLANK:
//                    // if the cell has no type or formatting, convert the value appropriately
//                    setCellValue(cell, value);
//                    break;
//                case HSSFCell.CELL_TYPE_BOOLEAN:
//                    cell.setCellValue(ObjectConverter.convertToBoolean(value).booleanValue());
//                    break;
//                case HSSFCell.CELL_TYPE_FORMULA:
//                case HSSFCell.CELL_TYPE_NUMERIC:
//                    try {
//                        cell.setCellValue(ObjectConverter.convertToDouble(value).doubleValue());
//                    } catch(ObjectConversionException e) {
//                        try {
//                            cell.setCellValue(ObjectConverter.convertToDate(value));
//                        } catch(ObjectConversionException ex) {
//                            throw e;
//                        }
//                    }
//                    break;
//                case HSSFCell.CELL_TYPE_ERROR:
//                case HSSFCell.CELL_TYPE_STRING:
//                    cell.setCellValue(new HSSFRichTextString(value));
//                    break;
//            }
//        } catch(ObjectConversionException e) {
//            cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
//        }
//    }
    
    /**
     * Clears the value for the given cell, but attempts to preserve formatting.
     */
    protected void clearCellValue(HSSFCell cell) {
        // if we're not using a template, then no value has been set yet
        if(usingTemplate) {
//            HSSFCellStyle style = cell.getCellStyle();
            cell.setCellValue((String) null);
//            cell.setCellStyle(style);
        }
////        String formula = cell.getCellFormula();
////        HSSFCellStyle style = cell.getCellStyle();
////        int type = cell.getCellType();
//        
//        // TODO attempt to preserve some of the formatting.
//        
//        cell.setCellValue((HSSFRichTextString) null);
//        
////        cell.setCellType(type);
////        cell.setCellStyle(style);
////        cell.setCellFormula(formula);
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
    
//    /**
//     * @param table
//     * @param anchorString
//     * @param rowLimit
//     * @param colLimit
//     * @throws Exception
//     */
//    public void insertTableAtAnchor() throws Exception {
//        InputDataflowStreamProvider table = map.getDataFile();
//        String anchorString = map.getAnchor();
//        int rowLimit = -1;
//        if (map.getRowLimit() != null && map.getRowLimit().intValue() > 0) {
//            rowLimit = map.getRowLimit().intValue();
//        }
//        int colLimit = -1;
//        if (map.getColLimit() != null && map.getColLimit().intValue() > 0) {
//            colLimit = map.getColLimit().intValue();
//        }
//        
//        CSVParser parser = new CSVParser(table.getInputStream(true));
//        LOG.debug( "POI anchor=" + anchorString + ";rowLimit=" + rowLimit + ";colLimit=" + colLimit );
//        int rowOffset = 0;
//        short columnOffset = 0;
//        Anchor anchor = new Anchor(anchorString);
//        
//        rowOffset = anchor.getRowIndex() - 1;
//        columnOffset = (short) (anchor.getColIndex() - 1);
//        if (map.isSkipHeader() != null && map.isSkipHeader().booleanValue()) parser.getLine();
//        for (int i = 0; (i < rowLimit) || (rowLimit == -1); i++) {
//            String[] x = parser.getLine();
//            if (x == null) break;
//            List row = Arrays.asList(x);
//            currentRow = sheet.getRow(rowOffset + i);
//            if (currentRow == null) {
//                currentRow = sheet.createRow((short) (rowOffset + i));
//            }
//            
//            insertRow(columnOffset, colLimit, row);
//            if ((i % 1000) == 0 && i > 0) {
//                save();
//                LOG.debug( "Performing incremental save of spreadsheet at row " + i + " of " + rowLimit);
//            }
//            else if( (i % 100) == 0 && i > 0 ) {
//                LOG.debug( "Inserted " + i + " rows of " + rowLimit
//                        + ";lastRow=" + sheet.getLastRowNum());
//            }
//        }
//    }
}
