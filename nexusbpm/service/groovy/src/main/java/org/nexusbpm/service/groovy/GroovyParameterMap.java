package org.nexusbpm.service.groovy;

import java.util.Map;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterType;

public class GroovyParameterMap extends ParameterLinkedHashMap {

	private static final long serialVersionUID = 1L;
	String GROOVY_CODE_FIELDNAME = "code";
	String GROOVY_OUTPUT_FIELDNAME = "output";
	String GROOVY_ERROR_FIELDNAME = "error";
	Parameter code = new Parameter(GROOVY_CODE_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
	Parameter output = new Parameter(GROOVY_OUTPUT_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_OUTPUT);
	Parameter error = new Parameter(GROOVY_ERROR_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_OUTPUT);
	private Parameter[] parms = {code, output, error};
	
	public GroovyParameterMap() {
		super();
		for (Parameter p: parms) add(p);
	}

	public GroovyParameterMap(Map<? extends String, ? extends Parameter> m) {
		super(m);
        for (Parameter p: parms) add(p);
	} 

	public String getCode() {
		return (String) getValue(GROOVY_CODE_FIELDNAME);
	}

	public void setCode(String code) {
		setValue(GROOVY_CODE_FIELDNAME, code);
	}

	public String getError() {
		return (String) getValue(GROOVY_ERROR_FIELDNAME);
	}

	public void setError(String error) {
		setValue(GROOVY_ERROR_FIELDNAME, error);
	}

	public String getOutput() {
		return (String) getValue(GROOVY_OUTPUT_FIELDNAME);
	}

	public void setOutput(String output) {
		setValue(GROOVY_OUTPUT_FIELDNAME, output);
	}
	
	
}
