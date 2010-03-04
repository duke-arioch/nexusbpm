package org.nexusbpm.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterMap;

public class NexusServiceException extends Exception {
    private static final long serialVersionUID = 2l;
    private ParameterMap outputData;
    private String causeStacktrace;
    
    public NexusServiceException() {
        this(null, null, null, true);
    }
    public NexusServiceException(ParameterMap outputData) {
        this(null, outputData, true);
    }
    public NexusServiceException(String message, Throwable cause) {
        this(message, cause, null, true);
    }
    public NexusServiceException(String message, Throwable cause, ParameterMap outputData) {
        this(message, cause, outputData, true);
    }
    public NexusServiceException(String message) {
        this(message, null, null, true);
    }
    public NexusServiceException(String message, ParameterMap outputData) {
        this(message, null, outputData, true);
    }
    public NexusServiceException(Throwable cause) {
        this(cause, null, true);
    }
    public NexusServiceException(Throwable cause, ParameterMap outputData) {
        this(cause, outputData, true);
    }
    public NexusServiceException(String message, Throwable cause, boolean serializeException) {
        this(message, cause, null, serializeException);
    }
    public NexusServiceException(Throwable cause, boolean serializeException) {
        this(cause, null, serializeException);
    }
    public NexusServiceException(Throwable cause, ParameterMap outputData, boolean serializeException) {
        super(serializeException ? cause : null);
        setOutputData(outputData);
        if(!serializeException) {
            setCauseStacktrace(cause);
        }
    }
    public NexusServiceException(String message, Throwable cause, ParameterMap outputData, boolean serializeException) {
        super(message, serializeException ? cause : null);
        setOutputData(outputData);
        if(!serializeException) {
            setCauseStacktrace(cause);
        }
    }
    
    public ParameterMap getOutputData() {
        return outputData;
    }
    public void setOutputData(ParameterMap outputData) {
        if(outputData != null) {
            this.outputData = new ParameterLinkedHashMap(outputData);
        } else {
            this.outputData = null;
        }
    }
    
    public String getCauseStacktrace() {
        return causeStacktrace;
    }
    public void setCauseStacktrace(Throwable cause) {
        if(cause == null) {
            causeStacktrace = null;
        } else {
            StringWriter sw = new StringWriter();
            cause.printStackTrace(new PrintWriter(sw));
            causeStacktrace = sw.toString();
        }
    }
    
}
