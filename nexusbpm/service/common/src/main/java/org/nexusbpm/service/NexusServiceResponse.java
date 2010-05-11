package org.nexusbpm.service;

import java.util.HashMap;
import java.util.Map;

public class NexusServiceResponse {

  public static final long serialVersionUID = 1L;
  private String out;
  private String err;
  private String returnCode;
  private Map<String, Object> outputVariables = new HashMap<String, Object>();

  public String getErr() {
    return err;
  }

  public void setErr(String err) {
    this.err = err;
  }

  public String getOut() {
    return out;
  }

  public void setOut(String out) {
    this.out = out;
  }

  public Map<String, Object> getOutputVariables() {
    return outputVariables;
  }

  public void setOutputVariables(Map<String, Object> outputVariables) {
    this.outputVariables = outputVariables;
  }

  public String getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(String returnCode) {
    this.returnCode = returnCode;
  }

}
