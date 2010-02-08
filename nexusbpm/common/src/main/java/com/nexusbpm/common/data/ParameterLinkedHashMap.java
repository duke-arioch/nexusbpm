package com.nexusbpm.common.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nexusbpm.common.util.ObjectConversionException;

public class ParameterLinkedHashMap extends LinkedHashMap<String, Parameter> implements ParameterMap {
    private static final long serialVersionUID = 1l;
    public static final String REQUEST_ID_FIELDNAME = "requestId";
    public static final String TOKEN_ID_FIELDNAME = "tokenId";
    public static final String INSTANCE_ID_FIELDNAME = "instanceId";
    public static final String NODE_NAME_FIELDNAME = "nodeName";
    public static final String PROCESS_NAME_FIELDNAME = "processName";
    public static final String PROCESS_VERSION_FIELDNAME = "processVersion";
    public static final String TRANSITION_NAME_FIELDNAME = "transitionName";
    public static final String AUTO_SIGNALLING_FIELDNAME = "autoSignalling";
    Parameter requestId = new Parameter(REQUEST_ID_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter tokenId = new Parameter(TOKEN_ID_FIELDNAME, null, null, ParameterType.LONG, null, true, Parameter.DIRECTION_INPUT);
    Parameter instanceId = new Parameter(INSTANCE_ID_FIELDNAME, null, null, ParameterType.LONG, null, true, Parameter.DIRECTION_INPUT);
    Parameter nodeName = new Parameter(NODE_NAME_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter processName = new Parameter(PROCESS_NAME_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter processVersion = new Parameter(PROCESS_VERSION_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter transitionName = new Parameter(TRANSITION_NAME_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter autoSignalling = new Parameter(AUTO_SIGNALLING_FIELDNAME, null, null, ParameterType.BOOLEAN, null, true, Parameter.DIRECTION_INPUT);
    
    public ParameterLinkedHashMap() {
        super();
        add(requestId);
        add(tokenId);
        add(instanceId);
        add(nodeName);
        add(processName);
        add(processVersion);
        add(transitionName);
        add(autoSignalling);
    }

    public ParameterLinkedHashMap(Map<? extends String, ? extends Parameter> m) {
        super(m);
        add(requestId);
        add(tokenId);
        add(instanceId);
        add(nodeName);
        add(processName);
        add(processVersion);
        add(transitionName);
        add(autoSignalling);
    }
    
    protected void add(Parameter param) {
        Parameter existing = get(param.getName());
        if(existing != null) {
            existing.setRequired(param.isRequired());
            if(!existing.isFile() && !existing.getType().equals(param.getType())) {
                existing.setType(param.getType());
                try {
                    existing.setValue(ObjectConverter.convert(existing.getValue(), param.getType()));
                } catch(ObjectConversionException e) {
                    e.printStackTrace();
                    existing.setValue(param.getValue());
                }
            }
        } else {
            put(param.getName(), param);
        }
    }
    
    protected void setValue(String name, Object value) {
        Parameter param = get(name);
        param.setValue(value);
    }
    
    protected Object getValue(String name) {
        Parameter param = get(name);
        return param.getValue();
    }

    public String getRequestId() {
        return (String) getValue(REQUEST_ID_FIELDNAME);
    }
    
    public void setRequestId(String requestId) {
        setValue(REQUEST_ID_FIELDNAME, requestId);
    }
    
    public void put(Parameter parm) {
        put(parm.getName(), parm);
    }
    
    public Long getTokenId() {
        return (Long) getValue(TOKEN_ID_FIELDNAME);
    }
    
    public void setTokenId(Long tokenId) {
        setValue(TOKEN_ID_FIELDNAME, tokenId);
    }
    
    public Long getInstanceId() {
        return (Long) getValue(INSTANCE_ID_FIELDNAME);
    }
    
    public void setInstanceId(Long instanceId) {
        setValue(INSTANCE_ID_FIELDNAME, instanceId);
    }
    
    public String getNodeName() {
        return (String) getValue(NODE_NAME_FIELDNAME);
    }
    
    public void setNodeName(String nodeName) {
        setValue(NODE_NAME_FIELDNAME, nodeName);
    }
    
    public void setProcessName(String processName) {
        setValue(PROCESS_NAME_FIELDNAME, processName);
    }
    
    public String getProcessName() {
        return (String) getValue(PROCESS_NAME_FIELDNAME);
    }
    
    public void setProcessVersion(String processVersion) {
        setValue(PROCESS_VERSION_FIELDNAME, processVersion);
    }
    
    public String getProcessVersion() {
        return (String) getValue(PROCESS_VERSION_FIELDNAME);
    }
    
    public void setTransitionName(String transitionName) {
        setValue(TRANSITION_NAME_FIELDNAME, transitionName);
    }
    
    public String getTransitionName() {
        return (String) getValue(TRANSITION_NAME_FIELDNAME);
    }
    
    public void setAutoSignalling(Boolean autoSignalling) {
        setValue(AUTO_SIGNALLING_FIELDNAME, autoSignalling);
    }
    
    public Boolean isAutoSignalling() {
        return (Boolean) getValue(AUTO_SIGNALLING_FIELDNAME);
    }
}
