package org.nexusbpm.service.excel;

import java.net.URI;
import java.net.URISyntaxException;

import org.nexusbpm.common.NexusTestCase;
import org.nexusbpm.service.NexusServiceException;
import org.junit.Test;

public class ExcelServiceTest extends NexusTestCase {

  @Test
  public void testSpreadsheet() throws NexusServiceException, URISyntaxException {
    ExcelServiceImpl service = new ExcelServiceImpl();
    ExcelServiceRequest data = new ExcelServiceRequest();
    URI file = new URI("res:test.csv");
    URI template = new URI("res:test.xls");
    URI csv = new URI("res:test.csv");
    URI outfile = new URI("tmp:test.out.xls");
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
