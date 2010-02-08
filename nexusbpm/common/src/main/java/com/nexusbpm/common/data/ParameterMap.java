package com.nexusbpm.common.data;

import java.io.Serializable;
import java.util.Map;

public interface ParameterMap extends Map<String, Parameter>, Serializable {
    public String getRequestId();
    public void setRequestId(String requestId);
    public Long getTokenId();
    public void setTokenId(Long tokenId);
    public Long getInstanceId();
    public void setInstanceId(Long instanceId);
    public String getNodeName();
    public void setNodeName(String nodeName);
    public String getProcessName();
    public void setProcessName(String processName);
    public String getProcessVersion();
    public void setProcessVersion(String processVersion);
    public String getTransitionName();
    public void setTransitionName(String transitionName);
    public Boolean isAutoSignalling();
    public void setAutoSignalling(Boolean autoSignalling);
}
