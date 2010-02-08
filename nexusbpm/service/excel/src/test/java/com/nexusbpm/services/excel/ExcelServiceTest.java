package com.nexusbpm.services.excel;

import java.net.URI;

import com.nexusbpm.common.NexusTestCase;
import com.nexusbpm.services.NexusServiceException;
import org.apache.commons.vfs.FileMonitor;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.operations.FileOperationProvider;
import org.apache.commons.vfs.provider.jar.JarFileProvider;
import org.apache.commons.vfs.provider.res.ResourceFileProvider;
import org.apache.commons.vfs.provider.url.UrlFileProvider;
import org.junit.Test;

public class ExcelServiceTest extends NexusTestCase {
    @Test
    public void testSpreadsheet() throws NexusServiceException, FileSystemException {
        ExcelServiceImpl service = new ExcelServiceImpl();
        ExcelParameterMap data = new ExcelParameterMap();
        FileSystemManager manager = VFS.getManager();
//        java.net.URL resource = getClass().getClassLoader().getResource("test.csv");
//        System.out.println(">>>>" + resource );
        FileObject file = manager.resolveFile("res:test.csv");
        System.out.println(">>>>" + file.getName().getFriendlyURI());
        System.out.println(">>>>" + file.getContent().getSize());



        FileObject template = manager.resolveFile("res:test.xls");
        FileObject csv = manager.resolveFile("res:test.csv");
        FileObject outfile = manager.resolveFile("tmp:test.out.xls");
        data.setSheetName("Billing Statement");
        data.setAnchor("A16");
        data.setColLimit(Integer.valueOf(15));
        data.setRowLimit(Integer.valueOf(15));
        data.setProcessName("test");
        data.setRequestId("1-2-3");
        data.setProcessVersion("1");
        data.setTemplateFile(template);
        data.setSkipHeader(Boolean.FALSE);
        data.setDataFile(csv);
        data.setOutputFile(outfile);
        service.execute(data);
    }
}
