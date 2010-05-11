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
import org.nexusbpm.service.NexusServiceRequest;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RFileOutputStream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class RServiceImpl implements NexusService {

  public static final Logger LOGGER = LoggerFactory.getLogger(RServiceImpl.class);

  @Override
  public RServiceResponse execute(NexusServiceRequest inData) throws NexusServiceException {
    RServiceRequest data = (RServiceRequest) inData;
    RServiceResponse retval = new RServiceResponse();
    StringBuilder result = new StringBuilder("R service call results:\n");
    RSession session;
    RConnection connection = null;
    try {
      result.append("Session Attachment: \n");
      byte[] sessionBytes = data.getSession();
      if (sessionBytes != null && sessionBytes.length > 0) {
        session = RUtils.bytesToSession(sessionBytes);
        result.append("  attaching to " + session + "\n");
        connection = session.attach();
      } else {
        result.append("  creating new session\n");
        connection = new RConnection(data.getServerAddress());
      }
      // assign any necessary data from incoming attributes
      result.append("Input Parameters: \n");

      for (String attributeName : data.getInputVariables().keySet()) {
        Object parameter = data.getInputVariables().get(attributeName);
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
      REXP x = connection.eval(RUtils.wrapCode(data.getCode().replace('\r', '\n')));
      result.append("Execution results:\n" + x.asString() + "\n");
      if (x.isNull() || x.asString().startsWith("Error")) {
        // only error has an attribute (the class)
        retval.setErr(x.asString());
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
          File f = File.createTempFile("nexus-" + data.getRequestId(), ".dat");
          IOUtils.copy(rInputStream, new FileOutputStream(f));
          retval.getOutputVariables().put(varname, f.getCanonicalFile().toURI());
        } else {
          Object varvalue = RUtils.convertREXP(connection.eval(varname));
          retval.getOutputVariables().put(varname, varvalue);
          String printValue = varvalue == null ? "null" : varvalue.getClass().isArray() ? Arrays.asList(varvalue).toString() : varvalue.toString();
          result.append("  " + (varvalue == null ? "" : varvalue.getClass().getSimpleName()) + " " + varname + "=" + printValue + "\n");
        }
      }
    } catch (RserveException rse) {
      retval.setErr(rse.getMessage());
      LOGGER.error("Rserve Exception", rse);
    } catch (REXPMismatchException rme) {
      retval.setErr(rme.getMessage());
      LOGGER.error("REXP Mismatch Exception", rme);
    } catch (IOException rme) {
      retval.setErr(rme.getMessage());
      LOGGER.error("IO Exception copying file ", rme);
    } finally {
      result.append("Session Detachment:\n");
      if (connection != null) {
        RSession outSession = null;
        if (retval.isKeepSession()) {
          try {
            outSession = connection.detach();
          } catch (RserveException e) {
            LOGGER.debug("Error detaching R session", e);
          }
        }
        boolean close = outSession == null;
        if (!close) {
          retval.setSession(RUtils.sessionToBytes(outSession));
          result.append("  suspended session for later use\n");
        }
        connection.close();
        retval.setSession(null);
        result.append("  session closed.\n");
      }
    }
    retval.setOut(result.toString());
    return retval;
  }

}
