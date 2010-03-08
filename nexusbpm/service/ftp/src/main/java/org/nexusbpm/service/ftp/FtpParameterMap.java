/**
 * 
 */
package org.nexusbpm.service.ftp;

import java.util.Map;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterType;
import java.net.URI;

/**
 * @author Matthew Sandoz
 *
 */
public class FtpParameterMap extends ParameterLinkedHashMap {
    private static final long serialVersionUID = 1l;

    public static final String FTP_OPERATION_FIELDNAME = "operation";
    public static final String FTP_REMOTE_DIR_FIELDNAME = "remoteDirectory";
    public static final String FTP_REMOTE_FILE_SPEC_FIELDNAME = "remoteFileSpecification";
    public static final String FTP_REMOTE_HOST_FIELDNAME = "remoteHost";
    public static final String FTP_USER_ID_FIELDNAME = "username";
    public static final String FTP_USER_PASSWORD_FIELDNAME = "password";
    public static final String FTP_INPUT_FIELDNAME = "input";
    public static final String FTP_OUTPUT_FIELDNAME = "output";
    
    Parameter operation = new Parameter(FTP_OPERATION_FIELDNAME, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter remoteDir = new Parameter(FTP_REMOTE_DIR_FIELDNAME, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter remoteFileSpec = new Parameter(FTP_REMOTE_FILE_SPEC_FIELDNAME, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter remoteHost = new Parameter(FTP_REMOTE_HOST_FIELDNAME, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter userId = new Parameter(FTP_USER_ID_FIELDNAME, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter userPassword = new Parameter(FTP_USER_PASSWORD_FIELDNAME, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter input = new Parameter(FTP_INPUT_FIELDNAME, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_INPUT);
    Parameter output = new Parameter(FTP_OUTPUT_FIELDNAME, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_OUTPUT);
    private Parameter[] parms = {operation, remoteDir, remoteFileSpec, remoteHost, userId, userPassword, input, output};

    public FtpParameterMap() {
        super();
        for (Parameter p: parms) add(p);
    }

    public FtpParameterMap(Map<? extends String, ? extends Parameter> m) {
        super(m);
        for (Parameter p: parms) add(p);
    }

    public String getOperation() {
        return (String) getValue(FTP_OPERATION_FIELDNAME);
    }

    public void setOperation(String operation) {
        setValue(FTP_OPERATION_FIELDNAME, operation);
    }

    public String getRemoteDir() {
        return (String) getValue(FTP_REMOTE_DIR_FIELDNAME);
    }

    public void setRemoteDir(String remoteDir) {
        setValue(FTP_REMOTE_DIR_FIELDNAME, remoteDir);
    }

    public String getRemoteFileSpec() {
        return (String) getValue(FTP_REMOTE_FILE_SPEC_FIELDNAME);
    }

    public void setRemoteFileSpec(String remoteFileSpec) {
        setValue(FTP_REMOTE_FILE_SPEC_FIELDNAME, remoteFileSpec);
    }

    public String getRemoteHost() {
        return (String) getValue(FTP_REMOTE_HOST_FIELDNAME);
    }

    public void setRemoteHost(String remoteHost) {
        setValue(FTP_REMOTE_HOST_FIELDNAME, remoteHost);
    }

    public String getUserId() {
        return (String) getValue(FTP_USER_ID_FIELDNAME);
    }

    public void setUserId(String userId) {
        setValue(FTP_USER_ID_FIELDNAME, userId);
    }

    public String getUserPassword() {
        return (String) getValue(FTP_USER_PASSWORD_FIELDNAME);
    }

    public void setUserPassword(String userPassword) {
        setValue(FTP_USER_PASSWORD_FIELDNAME, userPassword);
    }

    public URI getInput() {
        return (URI) getValue(FTP_INPUT_FIELDNAME);
    }

    public void setInput(URI input) {
        setValue(FTP_INPUT_FIELDNAME, input);
    } 

    public URI getOutput() {
        return (URI) getValue(FTP_OUTPUT_FIELDNAME);
    }

    public void setOutput(URI output) {
        setValue(FTP_OUTPUT_FIELDNAME, output);
    } 
}
