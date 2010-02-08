package com.nexusbpm.common.data;

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
    private String sourceNode;
    private String sourceVariable;
    private ParameterType type = ParameterType.STRING;
    private Object value;
    private boolean required = false;
    private String direction = "inout";

    public Parameter() {
    }
    
    public Parameter(String name, String sourceNode, String sourceVariable, ParameterType type, Object value, boolean required, String direction) {
        super();
        this.name = name;
        this.sourceVariable = sourceVariable;
        this.sourceNode = sourceNode;
        this.type = type;
        this.value = value;
        this.required = required;
        this.direction = direction;
    }
    
    public String getSourceVariable() {
        return sourceVariable;
    }
    public void setSourceVariable(String sourceVariable) {
        this.sourceVariable = sourceVariable;
    }
    
    public String getSourceNode() {
        return sourceNode;
    }
    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
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
        retval.setSourceNode(this.getSourceNode());
        retval.setSourceVariable(this.getSourceVariable());
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
        if (sourceNode != null && sourceNode.length() != 0) val = "<<<" + sourceNode + "." + value + ">>>";
        
        return (required == true ? "required" : "optional") + " " + direction + " var " + type.getName() + " " + name + " = " + val;
    }
    protected boolean isInitialValue() {
    	return getSourceNode() == null 
    	|| "".equals(getSourceNode())
    	|| SOURCE_INITIAL_VALUE.equals(getSourceNode())
    	;
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
    public boolean isFromProcessSource() {
    	return SOURCE_PROCESS_FILE.equals(getSourceNode());
    }
    public boolean isFromDataTransfer() {
    	return isInput() && !isFromProcessSource() && !isInitialValue();
    }
    public boolean isInputFile() {
    	return isInput() && isFile();
    }
    public boolean isOutputFile() {
    	return isOutput() && isFile();
    }
    public boolean shouldReadContents(Object sourceValue) {
    	return (isFromProcessSource() || sourceValue instanceof URI) && !isFile();
    }
}
