package org.nexusbpm.service.r;

import org.nexusbpm.service.NexusServiceException;
import java.net.URI;
import junit.framework.Assert;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RServiceTest {

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

  private RServiceRequest getPlotData() {
    final RServiceRequest data = new RServiceRequest();
    data.setServerAddress("localhost");
    data.getInputVariables().put("radius", 1000);
    data.getInputVariables().put("imageLocation", "test.png");
    data.setCode(imageCode);
    return data;
  }

  @Test
  @Ignore
  public void testRPlottingWithOutputGraph() throws Exception {
    final RServiceImpl service = new RServiceImpl();
    final RServiceRequest data = getPlotData();
    final RServiceResponse response = service.execute(data);
    assertThat("plot R command must not return error " + response.getErr(), response.getErr(), nullValue());
    assertThat("radius should reflect change from R code", (Double) response.getOutputVariables().get("radius"), equalTo(1001.0D));
    final URI uri = (URI) response.getOutputVariables().get("myfile");
//    ImageIcon icon = new ImageIcon(uri.toURL());
//    JOptionPane.showConfirmDialog(null, "", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);
    final FileObject file = VFS.getManager().resolveFile(uri.toString());
    assertThat("File size must be 9585", file.getContent().getSize(), equalTo(9585L));
  }

  @Test(expected=NexusServiceException.class)
  @Ignore
  public void testRSyntaxExceptionHandling() throws NexusServiceException{
    final RServiceImpl service = new RServiceImpl();
    final RServiceRequest data = new RServiceRequest();
    data.setCode("xxx");
    data.setServerAddress("localhost");
    service.execute(data);
    Assert.fail("Exception should have been thrown");
  }
}
