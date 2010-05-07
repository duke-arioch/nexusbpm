package org.nexusbpm.service.r;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
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

  public static final Logger logger = LoggerFactory.getLogger(RServiceImpl.class);

  @Override
  public void execute(NexusWorkItem data) throws NexusServiceException {
    StringBuilder result = new StringBuilder("R service call results:\n");
    RWorkItem rData = (RWorkItem) data;
    RSession session = null;
    RConnection c = null;
    Exception ex = null;
    try {
      result.append("Session Attachment: \n");
      byte[] sessionBytes = rData.getSession();
      if (sessionBytes != null && sessionBytes.length > 0) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(sessionBytes);
        ObjectInputStream stream = new ObjectInputStream(byteStream);
        session = (RSession) stream.readObject();
      }
      if (session != null) {
        result.append("  attaching to " + session + "\n");
        c = session.attach();
      } else {
        result.append("  creating new session\n");
      }
      // connect to Rserve if we didn't attach to a session yet
      if (c == null) {
        c = new RConnection(rData.getServerAddress());
      }
      // assign any necessary data from incoming attributes
      result.append("Input Parameters: \n");

      for (String attributeName : rData.getParameters().keySet()) {
        Object parameter = rData.getParameters().get(attributeName);
        if (!rData.isRequiredParameter(attributeName)) {
          if (parameter instanceof URI) {
            FileObject file = VFS.getManager().resolveFile(((URI) parameter).toString());
            RFileOutputStream ros = c.createFile(file.getName().getBaseName());
            IOUtils.copy(file.getContent().getInputStream(), ros);
            c.assign(attributeName, file.getName().getBaseName());
          } else {
            c.assign(attributeName, RUtils.convertToREXP(parameter));
          }
          result.append("  " + parameter.getClass().getSimpleName() + " " + attributeName + "=" + parameter + "\n");
        }
      }
      REXP x = c.eval(RUtils.wrapCode(rData.getCode().replace('\r', '\n')));
      result.append("Execution results:\n" + x.asString() + "\n");
      if (x.isNull() || x.asString().startsWith("Error")) {
        // only error has an attribute (the class)
        data.setErr(x.asString());
        // what should we do after an error?
        throw new NexusServiceException("R error: " + x.asString());
      }
      result.append("Output Parameters:\n");
      // process dynamic attributes: (storing attributes)

      REXP vars = c.eval("ls();");
      List fileCols = Arrays.asList(new String[]{
                "description", "class", "mode", "text", "opened", "can read", "can write"});

      String[] rVariables = vars.asStrings();
      for (String varname : rVariables) {
        String[] s = c.eval("class(" + varname + ")").asStrings();
        if (s.length == 2 && "file".equals(s[0]) && "connection".equals(s[1])) {
          RFileInputStream is = c.openFile(varname);
          File f = File.createTempFile("nexus-" + data.getWorkItemId(), ".dat");
          IOUtils.copy(is, new FileOutputStream(f));
          data.getResults().put(varname, new URI("file://" + f.getAbsolutePath()));
        } else {
          Object varvalue = RUtils.convertREXP(c.eval(varname));
          data.getResults().put(varname, varvalue);
          String printValue = varvalue == null ? "null" : varvalue.getClass().isArray() ? Arrays.asList(varvalue).toString() : varvalue.toString();
          result.append("  " + (varvalue == null ? "" : varvalue.getClass().getSimpleName()) + " " + varname + "=" + printValue + "\n");
        }
      }
    } catch (REXPMismatchException rme) {
      rData.setErr(rme.getMessage());
      ex = rme;
    } catch (RserveException re) {
      rData.setErr(re.getRequestErrorDescription());
      ex = re;
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      rData.setErr(sw.toString());
      // e.printStackTrace();
      ex = e;
    } finally {
      result.append("Session Detachment:\n");
      if (c != null) {
        RSession outSession = null;
        if (rData.isKeepSession() != null
                && rData.isKeepSession().booleanValue()) {
          try {
            outSession = c.detach();
          } catch (RserveException e) {
            logger.debug("Error detaching R session", e);
          }
        }
        boolean close = outSession == null;
        if (!close) {
          try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);
            stream.writeObject(outSession);
            stream.flush();
            rData.setSession(byteStream.toByteArray());
            result.append("  Session: " + session + "\n");
            result.append("  suspended session for later use\n");
          } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            if (rData.getErr() != null && rData.getErr().length() > 0) {
              pw.println(rData.getErr());
              pw.println();
            }
            pw.println("Error detaching session!");
            e.printStackTrace(pw);
            rData.setErr(sw.toString());
            result.append("  Error detaching session!\n");
            close = true;
          }
        }
        c.close();
        rData.setSession(null);
        result.append("  session closed.\n");
      }
    }
    data.setOut(result.toString());
    if (ex != null) {
      logger.error("R service error", ex);
      throw new NexusServiceException("R service error", ex);
    }
  }

  @Override
  public NexusWorkItem createCompatibleWorkItem(NexusWorkItem item) {
    return new RWorkItem(item);
  }
}
