package org.nexusbpm.common.data;

import java.io.Serializable;
import java.net.URI;

//basic type to hold nexus parameters
public final class Parameter implements Cloneable, Serializable {
    private static final long serialVersionUID = 1l;
    public static final String DIRECTION_INPUT = "in";
    public static final String DIRECTION_OUTPUT = "out";
    public static final String DIRECTION_INPUT_AND_OUTPUT = "inout";
    public static final String SOURCE_PROCESS_FILE = "process file";
    public static final String SOURCE_INITIAL_VALUE = "initial value";

    private String name;
    private ParameterType type = ParameterType.STRING;
    private Object value;
    private boolean required = false;
    private String direction = "inout";

    public Parameter() {
    }
    
    public Parameter(String name, ParameterType type, Object value, boolean required, String direction) {
        super();
        this.name = name;
        this.type = type;
        this.value = value;
        this.required = required;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public ParameterType getType() {
        return type;
    }
    public void setType(ParameterType type) {
        this.type = type == null ? ParameterType.STRING: type;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public Object clone() {
        Parameter retval = new Parameter();
        retval.setName(this.getName());
        retval.setRequired(this.isRequired());
        retval.setValue(this.getValue());
        retval.setType(this.getType());
        retval.setDirection(this.getDirection());
        return retval;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction == null ? "inout" : direction;
    }
    @Override
    public String toString() {
        String val = null;
        if (value != null) val = "'" + value.toString() + "'";
        return (required == true ? "required" : "optional") + " " + direction + " var " + type.getName() + " " + name + " = " + val;
    }

    public boolean isOfType(ParameterType type) {
    	return getType().equals(type);
    }
    public boolean isInput() {
        return !getDirection().equals(DIRECTION_OUTPUT);
    }
    public boolean isOutput() {
        return !getDirection().equals(DIRECTION_INPUT);
    }
    public boolean isFile() {
        return isBinaryFile() || isAsciiFile();
    }
    public boolean isBinaryFile() {
    	return getType().equals(ParameterType.BINARY_FILE);
    }
    public boolean isAsciiFile() {
    	return getType().equals(ParameterType.ASCII_FILE);	
    }
    public boolean isInputFile() {
    	return isInput() && isFile();
    }
    public boolean isOutputFile() {
    	return isOutput() && isFile();
    }
}
