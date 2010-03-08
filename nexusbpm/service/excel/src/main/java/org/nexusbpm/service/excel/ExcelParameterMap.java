package org.nexusbpm.service.excel;

import java.util.Map;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterType;
import org.apache.commons.vfs.FileObject;

public class ExcelParameterMap extends ParameterLinkedHashMap {

    private static final long serialVersionUID = 1L;

    public static final String EXCEL_SKIP_HEADER_FIELDNAME = "skipHeader";
    public static final String EXCEL_COL_LIMIT_FIELDNAME = "columnLimit";
    public static final String EXCEL_ANCHOR_FIELDNAME = "excelAnchor";
    public static final String EXCEL_ROW_LIMIT_FIELDNAME = "rowLimit";
    public static final String EXCEL_SHEET_NAME_FIELDNAME = "sheetName";
    public static final String EXCEL_TEMPLATE_FILE_FIELDNAME = "templateFile";
    public static final String EXCEL_DATA_FILE_FIELDNAME = "dataFile";
    public static final String EXCEL_OUTPUT_FILE_FIELDNAME = "outputFile";
    Parameter skipHeader = new Parameter(EXCEL_SKIP_HEADER_FIELDNAME, ParameterType.BOOLEAN, Boolean.FALSE, true, Parameter.DIRECTION_INPUT);
    Parameter colLimit = new Parameter(EXCEL_COL_LIMIT_FIELDNAME, ParameterType.INT, 0, true, Parameter.DIRECTION_INPUT);
    Parameter anchor = new Parameter(EXCEL_ANCHOR_FIELDNAME, ParameterType.STRING, "A1", true, Parameter.DIRECTION_INPUT);
    Parameter rowLimit = new Parameter(EXCEL_ROW_LIMIT_FIELDNAME, ParameterType.INT, 0, true, Parameter.DIRECTION_INPUT);
    Parameter sheetName = new Parameter(EXCEL_SHEET_NAME_FIELDNAME, ParameterType.STRING, "Sheet1", true, Parameter.DIRECTION_INPUT);
    Parameter templateFile = new Parameter(EXCEL_TEMPLATE_FILE_FIELDNAME, ParameterType.BINARY_FILE, null, true, Parameter.DIRECTION_INPUT);
    Parameter dataFile = new Parameter(EXCEL_DATA_FILE_FIELDNAME, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_INPUT);
    Parameter outputFile = new Parameter(EXCEL_OUTPUT_FILE_FIELDNAME, ParameterType.BINARY_FILE, null, true, Parameter.DIRECTION_OUTPUT);

    private Parameter[] parms = {skipHeader, colLimit, anchor, rowLimit, sheetName, templateFile, dataFile, outputFile};
    
    public ExcelParameterMap() {
        super();
        for (Parameter p: parms) add(p);
    }

    public ExcelParameterMap(Map<? extends String, ? extends Parameter> m) {
        super(m);
        for (Parameter p: parms) add(p);
    } 

    public Boolean isSkipHeader() {
        return (Boolean) getValue(EXCEL_SKIP_HEADER_FIELDNAME);
    }

    public void setSkipHeader(Boolean skipHeader) {
    	setValue(EXCEL_SKIP_HEADER_FIELDNAME, skipHeader);
    }

    public Integer getColLimit() {
        return (Integer) getValue(EXCEL_COL_LIMIT_FIELDNAME);
    }

    public void setColLimit(Integer colLimit) {
        setValue(EXCEL_COL_LIMIT_FIELDNAME, colLimit);
    }

    public String getAnchor() {
        return (String) getValue(EXCEL_ANCHOR_FIELDNAME);
    }

    public void setAnchor(String anchor) {
        setValue(EXCEL_ANCHOR_FIELDNAME, anchor);
    }

    public Integer getRowLimit() {
        return (Integer) getValue(EXCEL_ROW_LIMIT_FIELDNAME);
    }

    public void setRowLimit(Integer rowLimit) {
        setValue(EXCEL_ROW_LIMIT_FIELDNAME, rowLimit);
    }

    public String getSheetName() {
        return (String) getValue(EXCEL_SHEET_NAME_FIELDNAME);
    }

    public void setSheetName(String sheetName) {
        setValue(EXCEL_SHEET_NAME_FIELDNAME, sheetName);
    }

    public FileObject getTemplateFile() {
        return (FileObject) getValue(EXCEL_TEMPLATE_FILE_FIELDNAME);
    }

    public void setTemplateFile(FileObject templateFile) {
        setValue(EXCEL_TEMPLATE_FILE_FIELDNAME, templateFile);
    }

    public FileObject getDataFile() {
        return (FileObject) getValue(EXCEL_DATA_FILE_FIELDNAME);
    }

    public void setDataFile(FileObject dataFile) {
        setValue(EXCEL_DATA_FILE_FIELDNAME, dataFile);
    }

    public FileObject getOutputFile() {
        return (FileObject) getValue(EXCEL_OUTPUT_FILE_FIELDNAME);
    }

    public void setOutputFile(FileObject outputFile) {
        setValue(EXCEL_OUTPUT_FILE_FIELDNAME, outputFile);
    }
}
