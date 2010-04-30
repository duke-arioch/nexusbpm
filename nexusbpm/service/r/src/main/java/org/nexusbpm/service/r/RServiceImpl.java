package org.nexusbpm.service.r;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RSession;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.REngine.Rserve.RConnection;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ObjectConverter;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileUtil;
import org.apache.commons.vfs.VFS;
import org.nexusbpm.common.data.NexusWorkItem;
import org.rosuda.REngine.REXPMismatchException;
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
      rData.setOut("");
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

//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
//this requires more thought than i can now muster                
/*

        if(!parameter.isRequired()) {
        if(parameter.isFile()) {
        if(parameter.isInput()) {
        FileObject file = VFS.getManager().resolveFile(((URI) parameter.getValue()).toString());
        OutputStream ostream = c.createFile(file.getName().getBaseName());
        FileUtil.writeContent(file, ostream);
        result.append("  " + parameter.getType().getName() + " " + attributeName + "="
        + parameter.getValue() + " mapped to " + file.getName().getBaseName() + "\n");
        c.assign(attributeName, file.getName().getBaseName());
        } else {
        FileObject file = VFS.getManager().resolveFile(((URI) parameter.getValue()).toString());
        c.assign(attributeName, file.getName().getBaseName());
        }
        } else if(parameter.isInput()) {
        result.append("  " + parameter.getType().getName() + " " + attributeName + "=" + parameter.getValue() + "\n");
        Object val = parameter.getValue();
        if(val instanceof Integer) {
        int i[] = { ((Integer) val).intValue() };
        c.assign(attributeName, i);
        } else if(val instanceof Number) {
        double d[] = { ((Number) val).doubleValue() };
        c.assign(attributeName, d);
        } else if(val != null) {
        c.assign(attributeName, val.toString());
        } else {
        c.assign(attributeName, (String) "");
        }
        }
        }

         */

      }
      REXP x = c.eval(RUtils.wrapCode(rData.getCode().replace('\r', '\n')));
      result.append("Execution results:\n" + x.asString() + "\n");
      if (x.isNull() || x.asString().startsWith("Error")) {
        // only error has an attribute (the class)
        rData.setErr(x.asString());
        // what should we do after an error?
        throw new NexusServiceException("R error: " + x.asString());
      }
      result.append("Output Parameters:\n");
      // process dynamic attributes: (storing attributes)

//this requires more thought than i can now muster
//this requires more thought than i can now muster
//this requires more thought than i can now muster
//this requires more thought than i can now muster
//this requires more thought than i can now muster
            /*
      for(String attributeName : rData.keySet()) {
      Parameter parameter = rData.get(attributeName);
      if(!parameter.isOutput() ||
      attributeName.equals("code") || attributeName.equals("output") ||
      attributeName.equals("error") || attributeName.equals("session"))
      continue;
      String ac = c.eval("if (exists(\"" + attributeName + "\")) class(" + attributeName + ")[1] else '..'")
      .asString();
      if(!(ac == null || ac.equals(".."))) {
      REXP var = c.eval(attributeName);
      Object o = RUtils.convertREXP(var, parameter.getType().getJavaClass());
      if(parameter.isFile()) {
      // outputting to a file
      InputStream istream = c.openFile(var.asString());
      FileObject file = VFS.getManager().resolveFile(((URI) parameter.getValue()).toString());
      OutputStream ostream = file.getContent().getOutputStream();
      copyStream(istream, ostream);
      rData.get(attributeName).setValue(((URI) parameter.getValue()).toString());
      result.append("  " + attributeName + "=" + rData.get(attributeName).getValue() + "\n");
      } else {
      rData.get(attributeName).setValue(
      ObjectConverter.convert(o, parameter.getType().getJavaClass()));
      result.append("  " + attributeName + "=" + parameter.getValue() + "\n");
      }
      } else {
      result.append("Missing Output Variable " + attributeName + "\n");
      }
      }
       */
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
        if (outSession != null) {
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
        if (close) {
          c.close();
          rData.setSession(null);
          result.append("  session closed.\n");
        }
      }
    }
    rData.setOut(result.toString());
    if (ex != null) {
      logger.error("R service error", ex);
      throw new NexusServiceException("R service error", ex);
    }
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
//this will require more careful thought than i can now muster.
  }// run()

  protected void copyStream(InputStream istream, OutputStream ostream)
          throws IOException {
    byte[] b = new byte[65536];
    int numread = 0;
    while ((numread = istream.read(b, 0, 65535)) > 0) {
      ostream.write(b, 0, numread);
    }
    istream.close();
    ostream.close();
  }

  @Override
  public NexusWorkItem createCompatibleWorkItem(NexusWorkItem item) {
    return new RWorkItem(item);
  }
}
