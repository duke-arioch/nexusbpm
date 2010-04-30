package org.nexusbpm.service.ftp;

import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.nexusbpm.common.data.NexusWorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServiceImpl implements NexusService {

    public final static Logger LOGGER = LoggerFactory.getLogger(FtpServiceImpl.class);

    public void execute(NexusWorkItem inData) throws NexusServiceException {
        FtpWorkItem data =(FtpWorkItem) inData;
        try {
            FileSystemManager manager = VFS.getManager();
            FileSystemOptions opts = new FileSystemOptions();
            FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
            FileObject source = manager.resolveFile(data.getInput().toString());
            FileObject dest = manager.resolveFile(data.getOutput().toString());
            dest.copyFrom(source, Selectors.SELECT_SELF);
        } catch (Exception e) {
            LOGGER.error("FTP Service error!", e);
            throw new NexusServiceException("Error in FTP service!", e);
        }
    }

  public NexusWorkItem createCompatibleWorkItem(NexusWorkItem item) {
    return new FtpWorkItem(item);
  }

}
