package org.nexusbpm.service.ftp;

import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.nexusbpm.service.NexusServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServiceImpl implements NexusService {

  public final static Logger LOGGER = LoggerFactory.getLogger(FtpServiceImpl.class);

  @Override
  public FtpServiceResponse execute(final NexusServiceRequest inData) throws NexusServiceException {
    final FtpServiceRequest data = (FtpServiceRequest) inData;
    try {
      final FileSystemManager manager = VFS.getManager();
      final FileSystemOptions opts = new FileSystemOptions();
      FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
      final FileObject source = manager.resolveFile(data.getInput().toString());
      final FileObject dest = manager.resolveFile(data.getOutput().toString());
      dest.copyFrom(source, Selectors.SELECT_SELF);
    } catch (Exception e) {
      LOGGER.error("FTP Service error!", e);
      throw new NexusServiceException("Error in FTP service!", e);
    } 
    return new FtpServiceResponse();
  }

}
