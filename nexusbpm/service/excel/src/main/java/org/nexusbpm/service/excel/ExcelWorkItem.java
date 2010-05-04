package org.nexusbpm.service.excel;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

public class ExcelWorkItem extends NexusWorkItemImpl {

  public static final String DEFAULT_SHEET_NAME = "Sheet1";
  public static final String DEFAULT_START_CELL = "A1";
  private static final long serialVersionUID = 1L;
  public static final String EXCEL_SKIP_HEADER_FIELDNAME = "skipHeader";
  public static final String EXCEL_COL_LIMIT_FIELDNAME = "columnLimit";
  public static final String EXCEL_ANCHOR_FIELDNAME = "excelAnchor";
  public static final String EXCEL_ROW_LIMIT_FIELDNAME = "rowLimit";
  public static final String EXCEL_SHEET_NAME_FIELDNAME = "sheetName";
  public static final String EXCEL_TEMPLATE_FILE_FIELDNAME = "templateFile";
  public static final String EXCEL_DATA_FILE_FIELDNAME = "dataFile";
  public static final String EXCEL_OUTPUT_FILE_FIELDNAME = "outputFile";
  public static final List<String> EXCEL_INPUT_FIELDS = Arrays.asList(
          EXCEL_SKIP_HEADER_FIELDNAME,
          EXCEL_COL_LIMIT_FIELDNAME,
          EXCEL_ANCHOR_FIELDNAME,
          EXCEL_ROW_LIMIT_FIELDNAME,
          EXCEL_SHEET_NAME_FIELDNAME,
          EXCEL_TEMPLATE_FILE_FIELDNAME,
          EXCEL_DATA_FILE_FIELDNAME,
          WORKITEM_OUT_KEY,
          WORKITEM_ERR_KEY,
          WORKITEM_RETURN_CODE_KEY);

  public static final List<String> EXCEL_OUTPUT_FIELDS = Arrays.asList(
          EXCEL_OUTPUT_FILE_FIELDNAME,
          WORKITEM_OUT_KEY,
          WORKITEM_ERR_KEY,
          WORKITEM_RETURN_CODE_KEY);

  public ExcelWorkItem() {
    super();
  }

  public ExcelWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredInputParameterNames() {
    return EXCEL_INPUT_FIELDS;
  }

  @Override
  public List<String> getRequiredOutputParameterNames() {
    return EXCEL_OUTPUT_FIELDS;
  }

  public Boolean isSkipHeader() {
    return (Boolean) getParameters().get(EXCEL_SKIP_HEADER_FIELDNAME);
  }

  public void setSkipHeader(Boolean skipHeader) {
    getParameters().put(EXCEL_SKIP_HEADER_FIELDNAME, skipHeader);
  }

  public Integer getColLimit() {
    return (Integer) getParameters().get(EXCEL_COL_LIMIT_FIELDNAME);
  }

  public void setColLimit(Integer colLimit) {
    getParameters().put(EXCEL_COL_LIMIT_FIELDNAME, colLimit);
  }

  public String getAnchor() {
    String retval = (String) getParameters().get(EXCEL_ANCHOR_FIELDNAME);
    return retval == null ? DEFAULT_START_CELL : retval;
  }

  public void setAnchor(String anchor) {
    getParameters().put(EXCEL_ANCHOR_FIELDNAME, anchor);
  }

  public Integer getRowLimit() {
    return (Integer) getParameters().get(EXCEL_ROW_LIMIT_FIELDNAME);
  }

  public void setRowLimit(Integer rowLimit) {
    getParameters().put(EXCEL_ROW_LIMIT_FIELDNAME, rowLimit);
  }

  public String getSheetName() {
    String retval = (String) getParameters().get(EXCEL_SHEET_NAME_FIELDNAME);
    return retval == null ? DEFAULT_SHEET_NAME : retval;
  }

  public void setSheetName(String sheetName) {
    getParameters().put(EXCEL_SHEET_NAME_FIELDNAME, sheetName);
  }

  public FileObject getTemplateFile() {
    return (FileObject) getParameters().get(EXCEL_TEMPLATE_FILE_FIELDNAME);
  }

  public void setTemplateFile(FileObject templateFile) {
    getParameters().put(EXCEL_TEMPLATE_FILE_FIELDNAME, templateFile);
  }

  public FileObject getDataFile() {
    return (FileObject) getParameters().get(EXCEL_DATA_FILE_FIELDNAME);
  }

  public void setDataFile(FileObject dataFile) {
    getParameters().put(EXCEL_DATA_FILE_FIELDNAME, dataFile);
  }

  public FileObject getOutputFile() {
    return (FileObject) getResults().get(EXCEL_OUTPUT_FILE_FIELDNAME);
  }

  public void setOutputFile(FileObject outputFile) {
    getResults().put(EXCEL_OUTPUT_FILE_FIELDNAME, outputFile);
  }
}
