package org.nexusbpm.service.r;

import org.nexusbpm.common.NexusTestCase;
import org.nexusbpm.service.NexusServiceException;
import java.net.URI;
import junit.framework.Assert;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RServiceTest extends NexusTestCase {

  private static String dbCode =
          "library(\"rJava\");\nlibrary(RJDBC);\n"
          + "location<-sprintf(\"%s.csv\", reqId);\n"
          + "fileName=sprintf(\"//%s/rserve/%s\", serverAddress, location);\n"
          + "drv<-JDBC(\"org.postgresql.Driver\",\"D:/workspace/yawl/build/3rdParty/lib/postgresql-8.0-311.jdbc3.jar\");\n"
          + "conn<-dbConnect(drv,\"jdbc:postgresql:yawl\",\"postgres\",\"admin\");\n"
          + "d<-dbGetQuery(conn, \"select * from yspecification\");\n"
          + "write.table(d, file=location,sep=\",\",row.names=FALSE);\n";
  private static String imageCode =
          "t=seq(0,2*pi,length=10000);\n"
          + "png(filename=imageLocation, width=800, height=600, bg=\"grey\");\n"
          + "plot(radius*cos(t * 5),radius*sin(t * 3), type=\"l\", col=\"blue\");\n"
          + "dev.off();\n"
          + "myfile = file(imageLocation);\n"
          + "radius <- radius + 1;\n";
  private String unique = "output-" + System.currentTimeMillis();

  private RWorkItem getPlotData() throws Exception {
    RWorkItem data = new RWorkItem();
    data.setServerAddress("localhost");
    data.getParameters().put("radius", new Integer(1000));
    data.getParameters().put("imageLocation", "test.png");
    data.setCode(imageCode);
    return data;
  }

  @Test
  public void testRPlottingWithOutputGraph() throws Exception {
    RServiceImpl service = new RServiceImpl();
    RWorkItem data = getPlotData();
    service.execute(data);
    assertThat("plot R command must not return error " + data.getErr(), data.getErr(), nullValue());
    assertThat("radius should reflect change from R code", (Double) data.getResults().get("radius"), equalTo(1001.0D));
    URI uri = (URI) data.getResults().get("myfile");
//    ImageIcon icon = new ImageIcon(uri.toURL());
//    JOptionPane.showConfirmDialog(null, "", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);
    FileObject file = VFS.getManager().resolveFile(uri.toString());
    Assert.assertEquals(9585, file.getContent().getSize());
  }

  @Test
  public void testRSyntaxExceptionHandling() throws Exception {
    RServiceImpl service = new RServiceImpl();
    RWorkItem data = new RWorkItem();
    data.setCode("xxx");
    data.setServerAddress(getProperty("test.r.server"));
    try {
      service.execute(data);
      Assert.fail("Exception should have been thrown");
    } catch (NexusServiceException e) {
    }
    Assert.assertTrue(data.getErr().contains("Error in try({ : object 'xxx' not found"));
  }
}
