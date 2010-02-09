package com.nexusbpm.services.r;

import java.io.IOException;

import com.nexusbpm.common.NexusTestCase;
import com.nexusbpm.common.data.Parameter;
import com.nexusbpm.common.data.ParameterType;
import com.nexusbpm.services.NexusServiceException;
import junit.framework.Assert;

public class RServiceTest extends NexusTestCase {
    private String unique = "output-" + System.currentTimeMillis();
    
    private void setSharedData(RParameterMap data) {
        data.setServerAddress(getProperty("test.r.server"));
        data.setRequestId(unique);
        data.setProcessName("rprocess");
        data.setProcessVersion("1");
    }
//    private OutputDataflowStreamProvider getOutputProvider(String filename) throws IOException{
//        return DataflowStreamProviderFactory
//        .getInstance()
//        .getOutputProvider("nexus/RService", filename, "rtest", "12", "111");
//    }
    
    private RParameterMap getPlotData() throws Exception {
        RParameterMap data = new RParameterMap();
        setSharedData(data);
        data.put(new Parameter("radius", null, null, ParameterType.INT, new Integer(1000), false, Parameter.DIRECTION_INPUT_AND_OUTPUT));
//        data.put(new Parameter("imageLocation", null, null, ParameterType.BINARY_FILE, getOutputProvider("test.png"), false, Parameter.DIRECTION_OUTPUT));
        data.setCode( 
            "t=seq(0,2*pi,length=10000);\n" +
            "png(filename=imageLocation, width=800, height=600, bg=\"grey\");\n" +
            "plot(radius*cos(t * 5),radius*sin(t * 3), type=\"l\", col=\"blue\");\n" +
            "dev.off();\n" +
            "radius <- radius + 1;\n" 
        );
        return data;
    }
    
    public void testRPlottingWithOutputGraph() throws Exception{
        RServiceImpl r = new RServiceImpl();
        RParameterMap data = (RParameterMap) r.execute(getPlotData());
        System.out.println(data.getCode());
        System.out.println(data.getOutput());
        System.out.println(data.getError());
        
        if (!"".equals(data.getError())) {
            System.out.println("error: " + data.getError());
            return;
        }
        else {
//            System.out.println(data.get("imageLocation").getValue());
//            Icon icon = (Icon) new ImageIcon(new URL(data.get("imageLocation").getValue().toString()));
//            JOptionPane.showConfirmDialog(null, "", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);
        }
        Assert.assertEquals(1001, data.get("radius").getValue());
    }
    
    public void testRSyntaxExceptionHandling() throws Exception {
        RServiceImpl r = new RServiceImpl();
        RParameterMap data = new RParameterMap();
        data.setCode("xxx");
        data.setServerAddress(getProperty("test.r.server"));
        try {
            data = (RParameterMap) r.execute(data);
            System.out.println("Code:\n" + data.getCode());
            System.out.println("Output:\n" + data.getOutput());
            System.out.println("Error:\n" + data.getError());
            Assert.fail("Exception should have been thrown");
        } catch(NexusServiceException e) {
            e.printStackTrace(System.out);
            data = new RParameterMap(e.getOutputData());
        }
        System.out.println("Code:\n" + data.getCode());
        System.out.println("Output:\n" + data.getOutput());
        System.out.println("Error:\n" + data.getError());
        Assert.assertTrue(data.getError().contains("Error in try({ : object \"xxx\" not found"));
    }
    
//	private RParameterMap getDBData() throws Exception {
//		RParameterMap data = new RParameterMap();
//		setSharedData(data);
//		data.put(new Parameter("fileName", null, null, ParameterType.FILE.getType(), URI.create("test.csv"), false, Parameter.DIRECTION_INPUT));
//		String code = 
//			"library(\"rJava\");\nlibrary(RJDBC);\n" +
//			"location<-sprintf(\"%s.csv\", reqId);\n" +
//			"fileName=sprintf(\"//%s/rserve/%s\", serverAddress, location);\n" + 
//			"drv<-JDBC(\"org.postgresql.Driver\",\"D:/workspace/yawl/build/3rdParty/lib/postgresql-8.0-311.jdbc3.jar\");\n" + 
//			"conn<-dbConnect(drv,\"jdbc:postgresql:yawl\",\"postgres\",\"admin\");\n" + 
//			"d<-dbGetQuery(conn, \"select * from yspecification\");\n" +
//			"write.table(d, file=location,sep=\",\",row.names=FALSE);\n"  
//			;
//			data.setCode(code); 
//
//		return data;
//	}
    private RParameterMap get2WayData() throws Exception {
        RParameterMap data = new RParameterMap();
        setSharedData(data);
//        URI testUri = DataflowStreamProviderFactory.getInstance().getOutputProvider("testService", "testproc", "12", "111", "test.csv").getURI();
//        data.put(new Parameter("file", null, null, ParameterType.ASCII_FILE, testUri, false, Parameter.DIRECTION_INPUT));
//        data.put(new Parameter("imageLocation", null, null, ParameterType.BINARY_FILE, URI.create("my2.png"), false, Parameter.DIRECTION_OUTPUT));
        data.setCode( 
            "mydata<-read.csv(file);\n" + 
            "png(filename=imageLocation, width=800, height=600, bg=\"grey\");\n" +
            "plot(mydata);\n" +
            "dev.off();\n"
        );
        return data;
    }
    
    public void xtestDBR() throws Exception { //first i installed the rjdbc package on R...
        RServiceImpl r = new RServiceImpl();
        RParameterMap data = (RParameterMap) r.execute(get2WayData());
        Assert.assertNotNull(data.get("file").getValue());
        System.out.println(data.getOutput());
        System.out.println(data.getError());
        
    }
}
