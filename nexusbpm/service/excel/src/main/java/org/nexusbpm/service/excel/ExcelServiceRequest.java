package org.nexusbpm.service.excel;

import java.net.URI;
import org.nexusbpm.service.NexusServiceRequest;

public class ExcelServiceRequest extends NexusServiceRequest {

  public static final String DEFAULT_SHEET_NAME = "Sheet1";
  public static final String DEFAULT_START_CELL = "A1";
  private static final long serialVersionUID = 1L;
  private boolean skipHeader;
  private int rowLimit;
  private int columnLimit;
  private String excelAnchor;
  private String sheetName;
  private URI templateFile;
  private URI dataFile;
  private URI outputFile;

  public int getColumnLimit() {
    return columnLimit;
  }

  public void setColumnLimit(int columnLimit) {
    this.columnLimit = columnLimit;
  }

  public URI getDataFile() {
    return dataFile;
  }

  public void setDataFile(URI dataFile) {
    this.dataFile = dataFile;
  }

  public String getExcelAnchor() {
    return excelAnchor == null ? DEFAULT_START_CELL : excelAnchor;
  }

  public void setExcelAnchor(String excelAnchor) {
    this.excelAnchor = excelAnchor;
  }

  public URI getOutputFile() {
    return outputFile;
  }

  public void setOutputFile(URI outputFile) {
    this.outputFile = outputFile;
  }

  public int getRowLimit() {
    return rowLimit;
  }

  public void setRowLimit(int rowLimit) {
    this.rowLimit = rowLimit;
  }

  public String getSheetName() {
    return sheetName == null ? DEFAULT_SHEET_NAME : sheetName;
  }

  public void setSheetName(String sheetName) {
    this.sheetName = sheetName;
  }

  public boolean isSkipHeader() {
    return skipHeader;
  }

  public void setSkipHeader(boolean skipHeader) {
    this.skipHeader = skipHeader;
  }

  public URI getTemplateFile() {
    return templateFile;
  }

  public void setTemplateFile(URI templateFile) {
    this.templateFile = templateFile;
  }
}
