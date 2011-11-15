package org.nexusbpm.service.excel;

import java.net.URI;
import org.nexusbpm.service.NexusServiceRequest;

public class ExcelServiceRequest extends NexusServiceRequest {

  public static final String DEF_SHEET = "Sheet1";
  public static final String DEF_START_CELL = "A1";
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

  public void setColumnLimit(final int columnLimit) {
    this.columnLimit = columnLimit;
  }

  public URI getDataFile() {
    return dataFile;
  }

  public void setDataFile(final URI dataFile) {
    this.dataFile = dataFile;
  }

  public String getExcelAnchor() {
    return excelAnchor == null ? DEF_START_CELL : excelAnchor;
  }

  public void setExcelAnchor(final String excelAnchor) {
    this.excelAnchor = excelAnchor;
  }

  public URI getOutputFile() {
    return outputFile;
  }

  public void setOutputFile(final URI outputFile) {
    this.outputFile = outputFile;
  }

  public int getRowLimit() {
    return rowLimit;
  }

  public void setRowLimit(final int rowLimit) {
    this.rowLimit = rowLimit;
  }

  public String getSheetName() {
    return sheetName == null ? DEF_SHEET : sheetName;
  }

  public void setSheetName(final String sheetName) {
    this.sheetName = sheetName;
  }

  public boolean isSkipHeader() {
    return skipHeader;
  }

  public void setSkipHeader(final boolean skipHeader) {
    this.skipHeader = skipHeader;
  }

  public URI getTemplateFile() {
    return templateFile;
  }

  public void setTemplateFile(final URI templateFile) {
    this.templateFile = templateFile;
  }
}
