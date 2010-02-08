package com.nexusbpm.services.r;

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

import org.rosuda.JRclient.REXP;
import org.rosuda.JRclient.RSession;
import org.rosuda.JRclient.RSrvException;
import org.rosuda.JRclient.Rconnection;

import com.nexusbpm.common.data.Parameter;
import com.nexusbpm.common.data.ParameterMap;
import com.nexusbpm.common.io.interfaces.InputDataflowStreamProvider;
import com.nexusbpm.common.io.interfaces.OutputDataflowStreamProvider;
import com.nexusbpm.common.util.ObjectConverter;
import com.nexusbpm.services.AbstractNexusService;
import com.nexusbpm.services.NexusServiceException;

public class RServiceImpl extends AbstractNexusService {
    public ParameterMap execute(ParameterMap data) throws NexusServiceException {
        StringBuilder result = new StringBuilder("R service call results:\n");
        RParameterMap rData = new RParameterMap(data);
        RSession session = null;
        Rconnection c = null;
        Exception ex = null;
        try {
            rData.setOutput("");
            result.append("Session Attachment: \n");
            byte[] sessionBytes = rData.getSession();
            if(sessionBytes != null && sessionBytes.length > 0) {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(sessionBytes);
                ObjectInputStream stream = new ObjectInputStream(byteStream);
                session = (RSession) stream.readObject();
            }
            if(session != null) {
                result.append("  attaching to " + session + "\n");
                c = session.attach();
            } else {
                result.append("  creating new session\n");
            }
            // connect to Rserve if we didn't attach to a session yet
            if(c == null)
                c = new Rconnection(rData.getServerAddress());
            // assign any necessary data from incoming attributes
            result.append("Input Parameters: \n");
            for(String attributeName : rData.keySet()) {
                Parameter parameter = rData.get(attributeName);
                if(!parameter.isRequired()) {
                    if(parameter.isFile()) {
//                        DataflowStreamProviderFactory factory = DataflowStreamProviderFactory.getInstance();
                        if(parameter.isInput()) {
                            InputDataflowStreamProvider provider = (InputDataflowStreamProvider) parameter.getValue();
//                            InputStream istream = factory.getInputProvider((URI) parameter.getValue()).getInputStream(parameter.isAsciiFile());
                            InputStream istream = provider.getInputStream(parameter.isAsciiFile());
//                            String outname = new File(factory.getLocalFilename((URI) provider.getURI())).getName();
                            URI uri = provider.getURI();
                            String outname = null;
                            if(uri.getPath() != null && uri.getPath().length() > 0) {
                                outname = uri.getPath();
                            } else {
                                outname = uri.toString();
                            }
                            if(outname.indexOf('/') >= 0) {
                                outname = outname.substring(outname.lastIndexOf('/') + 1);
                            }
                            if(outname.length() == 0) {
                                throw new IllegalArgumentException(parameter.toString() + " (" + uri + ")");
                            }
                            OutputStream ostream = c.createFile(outname);
                            copyStream(istream, ostream);
                            result.append("  " + parameter.getType().getName() + " " + attributeName + "="
                                    + parameter.getValue() + " mapped to " + outname + "\n");
                            c.assign(attributeName, outname);
                        } else {
                            OutputDataflowStreamProvider provider = (OutputDataflowStreamProvider) parameter.getValue();
//                            String outname = parameter.getValue().toString();
//                            String outname = new File(factory.getLocalFilename((URI) provider.getURI())).getName();
                            String outname = provider.getURI().toString();
                            if(outname.indexOf('/') > -1) {
                                outname = outname.substring(outname.lastIndexOf('/') + 1);
                            }
                            c.assign(attributeName, outname);
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
            }
            REXP x = c.eval(RUtils.wrapCode(rData.getCode().replace('\r', '\n')));
            result.append("Execution results:\n" + x.asString() + "\n");
            if(x.getAttribute() != null || x.asString().startsWith("Error")) {
                // only error has an attribute (the class)
                rData.setError(x.asString());
                // what should we do after an error?
                throw new NexusServiceException("R error: " + x.asString());
            }
            result.append("Output Parameters:\n");
            // process dynamic attributes: (storing attributes)
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
                        InputStream istream = c.openFile(var.getContent().toString());
//                        DataflowStreamPr oviderFactory factory = DataflowStreamProviderFactory.getInstance();
//                        OutputDataflowStreamProvider oprovider = factory.getOutputProvider(R_SERVICE_NAME, rData
//                                .getProcessName(), rData.getProcessVersion(), rData.getRequestId(), var.getContent()
//                                .toString());
                        OutputDataflowStreamProvider oprovider = (OutputDataflowStreamProvider) parameter.getValue();
                        OutputStream ostream = oprovider.getOutputStream(parameter.isAsciiFile());
                        copyStream(istream, ostream);
                        rData.get(attributeName).setValue(oprovider.getURI());
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
        } catch(RSrvException re) {
            rData.setError(re.getRequestErrorDescription());
            ex = re;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            rData.setError(sw.toString());
            // e.printStackTrace();
            ex = e;
        } finally {
            result.append("Session Detachment:\n");
            if(c != null) {
                RSession outSession = null;
                if (rData.isKeepSession() != null &&
                        rData.isKeepSession().booleanValue()) {
                    try {
                        outSession = c.detach();
                    } catch(RSrvException e) {
                        LOG.debug("Error detaching R session", e);
                    }
                }
                boolean close = outSession == null;
                if(outSession != null) {
                    try {
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream stream = new ObjectOutputStream(byteStream);
                        stream.writeObject(outSession);
                        stream.flush();
                        rData.setSession(byteStream.toByteArray());
                        result.append("  Session: " + session + "\n");
                        result.append("  suspended session for later use\n");
                    } catch(IOException e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        if(rData.getError() != null && rData.getError().length() > 0) {
                            pw.println(rData.getError());
                            pw.println();
                        }
                        pw.println("Error detaching session!");
                        e.printStackTrace(pw);
                        rData.setError(sw.toString());
                        result.append("  Error detaching session!\n");
                        close = true;
                    }
                }
                if(close) {
                    c.close();
                    rData.setSession(null);
                    result.append("  session closed.\n");
                }
            }
        }
        rData.setOutput(result.toString());
        if (ex != null) {
            throw new NexusServiceException(ex, rData, false);
        }
        return rData;
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
}
