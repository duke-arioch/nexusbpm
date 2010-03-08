package org.nexusbpm.common.data;

import java.io.Serializable;
import java.util.Map;

public interface ParameterMap extends Map<String, Parameter>, Serializable {
    String getRequestId();
    void setRequestId(String requestId);
    Long getTokenId();
    void setTokenId(Long tokenId);
    Long getInstanceId();
    void setInstanceId(Long instanceId);
    String getNodeName();
    void setNodeName(String nodeName);
    String getProcessName();
    void setProcessName(String processName);
    String getProcessVersion();
    void setProcessVersion(String processVersion);
    String getTransitionName();
    void setTransitionName(String transitionName);
    Boolean isAutoSignalling();
    void setAutoSignalling(Boolean autoSignalling);
    ParameterMap toInputMap();
    ParameterMap toOutputMap();
    Map<String, Object> toObjectMap();
}
