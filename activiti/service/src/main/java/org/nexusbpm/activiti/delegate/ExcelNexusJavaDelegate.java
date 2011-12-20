package org.nexusbpm.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.Expression;
import org.nexusbpm.service.excel.ExcelServiceImpl;
import org.nexusbpm.service.excel.ExcelServiceRequest;
import org.nexusbpm.service.excel.ExcelServiceResponse;

public class ExcelNexusJavaDelegate extends NexusJavaDelegate implements
        JavaDelegate {

  private Expression skipHeader;
  private Expression rowLimit;
  private Expression columnLimit;
  private Expression excelAnchor;
  private Expression sheetName;
  private Expression dataFile;
  private Expression templateFile;
  private Expression outputFile;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    ExcelServiceImpl impl = (ExcelServiceImpl) Context.getProcessEngineConfiguration().getBeans().get("excelService");

    ExcelServiceRequest request = new ExcelServiceRequest();
    request.setSkipHeader(evaluateToBoolean(skipHeader, execution));
    request.setRowLimit(evaluateToInt(rowLimit, execution));
    request.setColumnLimit(evaluateToInt(columnLimit, execution));
    request.setExcelAnchor(evaluateToString(excelAnchor, execution));
    request.setSheetName(evaluateToString(sheetName, execution));
    request.setDataFile(evaluateToUri(dataFile, execution));
    request.setOutputFile(evaluateToUri(outputFile, execution));
    request.setTemplateFile(evaluateToUri(templateFile, execution));
    ExcelServiceResponse response = (ExcelServiceResponse) impl.execute(request);
  }
}
