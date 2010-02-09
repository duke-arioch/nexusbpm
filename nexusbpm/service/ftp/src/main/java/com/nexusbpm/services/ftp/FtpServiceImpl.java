package com.nexusbpm.services.ftp;

import com.nexusbpm.common.data.ParameterMap;
import com.nexusbpm.services.NexusService;
import com.nexusbpm.services.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServiceImpl implements NexusService {

    public final static Logger logger = LoggerFactory.getLogger(FtpServiceImpl.class);

    public ParameterMap execute(ParameterMap inData) throws NexusServiceException {
        FtpParameterMap data = new FtpParameterMap(inData);
        try {
            FileSystemManager manager = VFS.getManager();
            FileSystemOptions opts = new FileSystemOptions();
            FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
            FileObject source = manager.resolveFile(data.getInput().toString());
            FileObject dest = manager.resolveFile(data.getOutput().toString());
            dest.copyFrom(source, Selectors.SELECT_SELF);
        } catch (Exception e) {
            logger.error("FTP Service error!", e);
            throw new NexusServiceException("Error in FTP service!", e, data, false);
        }
        return data;
    }
}
