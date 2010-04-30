package org.nexusbpm.service.excel;

import org.nexusbpm.service.excel.ExcelServiceImpl;
import org.nexusbpm.service.excel.ExcelWorkItem;
import java.net.URI;

import org.nexusbpm.common.NexusTestCase;
import org.nexusbpm.service.NexusServiceException;
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
        ExcelWorkItem data = new ExcelWorkItem();
        FileSystemManager manager = VFS.getManager();
        FileObject file = manager.resolveFile("res:test.csv");
        FileObject template = manager.resolveFile("res:test.xls");
        FileObject csv = manager.resolveFile("res:test.csv");
        FileObject outfile = manager.resolveFile("tmp:test.out.xls");
        data.setSheetName("Billing Statement");
        data.setAnchor("A16");
        data.setColLimit(Integer.valueOf(15));
        data.setRowLimit(Integer.valueOf(15));
        data.setName("test");
        data.setWorkItemId("1-2-3");
        data.setTemplateFile(template);
        data.setSkipHeader(Boolean.FALSE);
        data.setDataFile(csv);
        data.setOutputFile(outfile);
        service.execute(data);
    }
}
