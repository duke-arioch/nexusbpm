package org.nexusbpm.service.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelStyleFactory {
//    protected Map<Class, HSSFCellStyle> styles;
//    protected Map<Short, Set<HSSFCellStyle>> styles;
    
    protected HSSFWorkbook book;
    
    public ExcelStyleFactory(HSSFWorkbook book) {
        this.book = book;
    }
    
    public HSSFCellStyle getCellStyle(Class c, HSSFCellStyle template) {
        return getCellStyle(getFormatIndex(c), template);
    }
    
    public HSSFCellStyle getCellStyle(String format, HSSFCellStyle template) {
        return getCellStyle(getFormatIndex(format), template);
    }
    
    public HSSFCellStyle getCellStyle(short format, HSSFCellStyle template) {
        if(template.getDataFormat() == format) {
            return template;
        }
        
        for(short index = 0; index < book.getNumCellStyles(); index++) {
            HSSFCellStyle style = book.getCellStyleAt(index);
            if(matches(style, template, format)) {
                return style;
            }
        }
        
        HSSFCellStyle style = clone(template);
        
        style.setDataFormat(format);
        
        return style;
    }
    
    protected HSSFCellStyle clone(HSSFCellStyle style) {
        HSSFCellStyle retval = book.createCellStyle();
        
        retval.setAlignment(style.getAlignment());
        retval.setBorderBottom(style.getBorderBottom());
        retval.setBorderLeft(style.getBorderLeft());
        retval.setBorderRight(style.getBorderRight());
        retval.setBorderTop(style.getBorderTop());
        retval.setBottomBorderColor(style.getBottomBorderColor());
        retval.setDataFormat(style.getDataFormat());
        retval.setFillBackgroundColor(style.getFillBackgroundColor());
        retval.setFillForegroundColor(style.getFillForegroundColor());
        retval.setFillPattern(style.getFillPattern());

//        retval.setFont(style.getFontIndex());
        //look back into this later...the api has changed.
        retval.setHidden(style.getHidden());
        retval.setIndention(style.getIndention());
        retval.setLeftBorderColor(style.getLeftBorderColor());
        retval.setLocked(style.getLocked());
        retval.setRightBorderColor(style.getRightBorderColor());
        retval.setRotation(style.getRotation());
        retval.setTopBorderColor(style.getTopBorderColor());
        retval.setVerticalAlignment(style.getVerticalAlignment());
        retval.setWrapText(style.getWrapText());
        
        return retval;
    }
    
    protected boolean matches(HSSFCellStyle candidate, HSSFCellStyle template, short format) {
        return candidate.getAlignment() == template.getAlignment() &&
            candidate.getBorderBottom() == template.getBorderBottom() &&
            candidate.getBorderLeft() == template.getBorderLeft() &&
            candidate.getBorderRight() == template.getBorderRight() &&
            candidate.getBorderTop() == template.getBorderTop() &&
            candidate.getBottomBorderColor() == template.getBottomBorderColor() &&
            candidate.getDataFormat() == format &&
            candidate.getFillBackgroundColor() == template.getFillBackgroundColor() &&
            candidate.getFillForegroundColor() == template.getFillForegroundColor() &&
            candidate.getFillPattern() == template.getFillPattern() &&
            candidate.getFontIndex() == template.getFontIndex() &&
            candidate.getHidden() == template.getHidden() &&
            candidate.getIndention() == template.getIndention() &&
            candidate.getLeftBorderColor() == template.getLeftBorderColor() &&
            candidate.getLocked() == template.getLocked() &&
            candidate.getRightBorderColor() == template.getRightBorderColor() &&
            candidate.getRotation() == template.getRotation() &&
            candidate.getTopBorderColor() == template.getTopBorderColor() &&
            candidate.getVerticalAlignment() == template.getVerticalAlignment() &&
            candidate.getWrapText() == template.getWrapText();
    }
    
//    protected HSSFCellStyle getCellStyle(Class c) {
//        if(styles == null) {
//            styles = new HashMap<Class, HSSFCellStyle>();
//        }
//        if(styles.get(c) == null) {
//            HSSFDataFormat format = book.createDataFormat();
//            
//            HSSFCellStyle style = book.createCellStyle();
//            
//            if(Number.class.equals(c)) {
//                style.setDataFormat(format.getFormat("0.00"));
//                styles.put(Number.class, style);
//            } else if(java.sql.Date.class.equals(c)) {
//                style.setDataFormat(format.getFormat("yyyy-mm-dd"));
//                styles.put(java.sql.Date.class, style);
//            } else if(java.sql.Time.class.equals(c)) {
//                style.setDataFormat(format.getFormat("hh:mm:ss"));
//                styles.put(java.sql.Time.class, style);
//            } else if(java.sql.Timestamp.class.equals(c) ||
//                    java.util.Date.class.equals(c)) {
//                style.setDataFormat(format.getFormat("yyyy-mm-dd hh:mm:ss"));
//                styles.put(java.util.Date.class, style);
//                styles.put(java.sql.Timestamp.class, style);
//            }
//        }
//        return styles.get(c);
//    }
    
    public short getFormatIndex(Class c) {
        return getFormatIndex(getFormatString(c));
    }
    
    public short getFormatIndex(String formatString) {
        HSSFDataFormat format = book.createDataFormat();
        return format.getFormat(formatString);
    }
    
    public String getFormatString(Class c) {
        if(Integer.class.equals(c)) {
            return "#,##0";
        } else if(Float.class.equals(c)) {
            return "#,##0.00";
        } else if(java.sql.Date.class.equals(c)) {
            return "yyyy-mm-dd";
        } else if(java.sql.Time.class.equals(c)) {
            return "hh:mm:ss";
        } else if(java.sql.Timestamp.class.equals(c) || java.util.Date.class.equals(c)) {
            return "yyyy-mm-dd hh:mm:ss";
        }
        return "General";
    }
    
    public String getCellFormat(HSSFCell cell) {
        HSSFDataFormat dataFormat = book.createDataFormat();
        return dataFormat.getFormat(cell.getCellStyle().getDataFormat());
    }
}
