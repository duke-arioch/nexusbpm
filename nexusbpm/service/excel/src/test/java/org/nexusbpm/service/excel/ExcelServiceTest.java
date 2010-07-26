package org.nexusbpm.service.excel;

import java.net.URI;
import java.net.URISyntaxException;

import org.nexusbpm.service.NexusServiceException;
import org.junit.Test;

public class ExcelServiceTest {

  @Test
  public void testSpreadsheet() throws NexusServiceException, URISyntaxException {
    final ExcelServiceImpl service = new ExcelServiceImpl();
    final ExcelServiceRequest data = new ExcelServiceRequest();
    final URI template = new URI("res:test.xls");
    final URI csv = new URI("res:test.csv");
    final URI outfile = new URI("tmp:test.out.xls");
    data.setSheetName("Billing Statement");
    data.setExcelAnchor("A16");
    data.setColumnLimit(15);
    data.setRowLimit(15);
    data.setRequestId("test.1-2-3");
    data.setTemplateFile(template);
    data.setSkipHeader(Boolean.FALSE);
    data.setDataFile(csv);
    data.setOutputFile(outfile);
    service.execute(data);
  }
}
