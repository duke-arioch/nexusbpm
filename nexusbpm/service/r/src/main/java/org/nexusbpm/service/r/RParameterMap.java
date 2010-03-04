package org.nexusbpm.service.r;

import java.util.Map;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterType;

public class RParameterMap extends ParameterLinkedHashMap {

	private static final long serialVersionUID = 1L;
	static String R_CODE_FIELDNAME = "code";
	static String R_OUTPUT_FIELDNAME = "output";
	static String R_ERROR_FIELDNAME = "error";
	static String R_KEEP_SESSION_FIELDNAME = "keepSession";
	static String R_SERVER_ADDRESS_FIELDNAME = "serverAddress";
	static String R_SESSION_FIELDNAME = "session";

	Parameter code = new Parameter(R_CODE_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
	Parameter output = new Parameter(R_OUTPUT_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_OUTPUT);
	Parameter error = new Parameter(R_ERROR_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_OUTPUT);
	Parameter keepSession = new Parameter(R_KEEP_SESSION_FIELDNAME, null, null, ParameterType.BOOLEAN, Boolean.FALSE, true, Parameter.DIRECTION_INPUT);
	Parameter serverAddress = new Parameter(R_SERVER_ADDRESS_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
	Parameter session = new Parameter(R_SESSION_FIELDNAME, null, null, ParameterType.BINARY, null, true, Parameter.DIRECTION_INPUT_AND_OUTPUT);
	private Parameter[] parms = {code, output, error, keepSession, serverAddress, session};
	
	public RParameterMap() {
		super();
		for (Parameter p: parms) add(p);
	}

	public RParameterMap(Map<? extends String, ? extends Parameter> m) {
		super(m);
        for (Parameter p: parms) add(p);
	} 

	public String getCode() {
		return (String) getValue(R_CODE_FIELDNAME);
	}

	public void setCode(String code) {
		setValue(R_CODE_FIELDNAME, code);
	}

	public String getError() {
		return (String) getValue(R_ERROR_FIELDNAME);
	}

	public void setError(String error) {
		setValue(R_ERROR_FIELDNAME, error);
	}

	public String getOutput() {
		return (String) getValue(R_OUTPUT_FIELDNAME);
	}

	public void setOutput(String output) {
		setValue(R_OUTPUT_FIELDNAME, output);
	}

	public String getServerAddress() {
		return (String) getValue(R_SERVER_ADDRESS_FIELDNAME);
	}

	public void setServerAddress(String serverAddress) {
		setValue(R_SERVER_ADDRESS_FIELDNAME, serverAddress);
	}

	public byte[] getSession() {
		return (byte[]) getValue(R_SESSION_FIELDNAME);
	}

	public void setSession(byte[] sessionIn) {
		setValue(R_SESSION_FIELDNAME, sessionIn);
	}
	
	public Boolean isKeepSession() {
		return (Boolean) getValue(R_KEEP_SESSION_FIELDNAME);
	}

	public void setKeepSession(Boolean keepSession) {
		setValue(R_KEEP_SESSION_FIELDNAME, keepSession);
	}
}
