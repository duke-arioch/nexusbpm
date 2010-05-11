package org.nexusbpm.service;

import java.util.HashMap;
import java.util.Map;

public class NexusServiceRequest {

  public static final long serialVersionUID = 1L;
  private String requestId;
  private Map<String, Object> inputVariables = new HashMap<String, Object>();

  public Map<String, Object> getInputVariables() {
    return inputVariables;
  }

  public void setInputVariables(Map<String, Object> inputVariables) {
    this.inputVariables = inputVariables;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
