package org.nexusbpm.service.r;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RSession;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.REngine.Rserve.RConnection;

import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.nexusbpm.common.data.NexusWorkItem;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RFileOutputStream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class RServiceImpl implements NexusService {

  public static final Logger LOGGER = LoggerFactory.getLogger(RServiceImpl.class);

  @Override
  public void execute(NexusWorkItem data) throws NexusServiceException {
    StringBuilder result = new StringBuilder("R service call results:\n");
    RWorkItem rData = (RWorkItem) data;
    RSession session;
    RConnection connection = null;
    try {
      result.append("Session Attachment: \n");
      byte[] sessionBytes = rData.getSession();
      if (sessionBytes != null && sessionBytes.length > 0) {
        session = RUtils.bytesToSession(sessionBytes);
        result.append("  attaching to " + session + "\n");
        connection = session.attach();
      } else {
        result.append("  creating new session\n");
        connection = new RConnection(rData.getServerAddress());
      }
      // assign any necessary data from incoming attributes
      result.append("Input Parameters: \n");

      for (String attributeName : rData.getParameters().keySet()) {
        Object parameter = rData.getParameters().get(attributeName);
        if (!rData.isRequiredParameter(attributeName)) {
          if (parameter instanceof URI) {
            FileObject file = VFS.getManager().resolveFile(((URI) parameter).toString());
            RFileOutputStream ros = connection.createFile(file.getName().getBaseName());
            IOUtils.copy(file.getContent().getInputStream(), ros);
            connection.assign(attributeName, file.getName().getBaseName());
          } else {
            connection.assign(attributeName, RUtils.convertToREXP(parameter));
          }
          result.append("  " + parameter.getClass().getSimpleName() + " " + attributeName + "=" + parameter + "\n");
        }
      }
      REXP x = connection.eval(RUtils.wrapCode(rData.getCode().replace('\r', '\n')));
      result.append("Execution results:\n" + x.asString() + "\n");
      if (x.isNull() || x.asString().startsWith("Error")) {
        // only error has an attribute (the class)
        data.setErr(x.asString());
        // what should we do after an error?
        throw new NexusServiceException("R error: " + x.asString());
      }
      result.append("Output Parameters:\n");
      String[] rVariables = connection.eval("ls();").asStrings();
      for (String varname : rVariables) {
        String[] s = connection.eval("class(" + varname + ")").asStrings();
        if (s.length == 2 && "file".equals(s[0]) && "connection".equals(s[1])) {
          String rFileName = connection.eval("showConnections(TRUE)[" + varname + "]").asString();
          result.append("  R File " + varname + "=" + rFileName + "\n");
          RFileInputStream rInputStream = connection.openFile(rFileName);
          File f = File.createTempFile("nexus-" + data.getWorkItemId(), ".dat");
          IOUtils.copy(rInputStream, new FileOutputStream(f));
          data.getResults().put(varname, f.getCanonicalFile().toURI());
        } else {
          Object varvalue = RUtils.convertREXP(connection.eval(varname));
          data.getResults().put(varname, varvalue);
          String printValue = varvalue == null ? "null" : varvalue.getClass().isArray() ? Arrays.asList(varvalue).toString() : varvalue.toString();
          result.append("  " + (varvalue == null ? "" : varvalue.getClass().getSimpleName()) + " " + varname + "=" + printValue + "\n");
        }
      }
    } catch (RserveException rse) {
      rData.setErr(rse.getMessage());
      LOGGER.error("Rserve Exception", rse);
    } catch (REXPMismatchException rme) {
      rData.setErr(rme.getMessage());
      LOGGER.error("REXP Mismatch Exception", rme);
    } catch (IOException rme) {
      rData.setErr(rme.getMessage());
      LOGGER.error("IO Exception copying file ", rme);
    } finally {
      result.append("Session Detachment:\n");
      if (connection != null) {
        RSession outSession = null;
        if (rData.isKeepSession() != null
                && rData.isKeepSession().booleanValue()) {
          try {
            outSession = connection.detach();
          } catch (RserveException e) {
            LOGGER.debug("Error detaching R session", e);
          }
        }
        boolean close = outSession == null;
        if (!close) {
          rData.setSession(RUtils.sessionToBytes(outSession));
          result.append("  suspended session for later use\n");
        }
        connection.close();
        rData.setSession(null);
        result.append("  session closed.\n");
      }
    }
    data.setOut(result.toString());
  }

  @Override
  public NexusWorkItem createCompatibleWorkItem(NexusWorkItem item) {
    return new RWorkItem(item);
  }
}
