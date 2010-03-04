package org.nexusbpm.service.ftp;

import org.nexusbpm.service.ftp.FtpServiceImpl;
import org.nexusbpm.service.ftp.FtpParameterMap;
import java.net.URI;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServiceTest {

    FakeFtpServer ftpServer = new FakeFtpServer();
    public static final Logger logger = LoggerFactory.getLogger(FtpServiceTest.class);

    @Before
    public void before() throws Exception {
        UserAccount account = new UserAccount("nexusbpm", "nexusbpm", "/tmp");
        account.setPasswordRequiredForLogin(false);
        ftpServer.addUserAccount(account);

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/tmp"));
        ftpServer.setFileSystem(fileSystem);
        ftpServer.start();
    }

    @After
    public void after() throws Exception {
        ftpServer.stop();
    }

    @Test
    public void testRoundtrip() throws Exception {
        FtpServiceImpl service = new FtpServiceImpl();
        FtpParameterMap data = new FtpParameterMap();
        data.setInput(new URI("res:testfile.xml"));
        data.setOutput(new URI("ftp://nexusbpm:nexusbpm@localhost/remotefile.xml"));
        FtpParameterMap outData = (FtpParameterMap) service.execute(data);

        data.setInput(new URI("ftp://nexusbpm:nexusbpm@localhost/remotefile.xml"));
        data.setOutput(new URI("tmp://testfile.out.xml"));
        outData = (FtpParameterMap) service.execute(data);

        System.out.println(outData);
    }
}
