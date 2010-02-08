package com.nexusbpm.services.ftp;

import java.io.OutputStream;
import java.net.URI;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileUtil;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServiceTest {

    FakeFtpServer ftpServer = new FakeFtpServer();
    public static final Logger logger = LoggerFactory.getLogger(FtpServiceTest.class);

//    @Before
    public void before() throws Exception {
        UserAccount account = new UserAccount("nexusbpm", "nexusbpm", "/home");
        account.setPasswordRequiredForLogin(false);
        ftpServer.addUserAccount(account);

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/home"));
        ftpServer.setFileSystem(fileSystem);
        ftpServer.start();
        logger.error("Started FTP Service");
    }

//    @After
    public void after() throws Exception {
        ftpServer.stop();
        logger.error("Stopped FTP Service");
    }

    @Test
    public void testRoundtrip() throws Exception {
        FileSystemManager manager = VFS.getManager();
        FileSystemOptions opts = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
        FileObject source = manager.resolveFile("res:testfile.xml");
        FileObject dest = manager.resolveFile("ftp://nexusbpm:nexusbpm@localhost//tmp/testout.xml");
        dest.copyFrom(source,Selectors.SELECT_SELF);


//        FtpServiceImpl service = new FtpServiceImpl();
//        FtpParameterMap data = new FtpParameterMap();
//        data.setInput(new URI("res:testfile.xml"));
//        data.setOutput(new URI("ftp://nexusbpm:nexusbpm@localhost/remotefile.xml"));
//
//        FtpParameterMap outData = (FtpParameterMap) service.execute(data);
//
//        data.setInput(new URI("ftp://nexusbpm@localhost/remotefile.xml"));
//        data.setOutput(new URI("tmp://testfile.out.xml"));
//
//        System.out.println(outData);
    }
}
