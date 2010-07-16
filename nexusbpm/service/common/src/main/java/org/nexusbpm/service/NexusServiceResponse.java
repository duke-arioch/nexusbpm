package org.nexusbpm.service;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public class NexusServiceResponse {

  public static final long serialVersionUID = 1L;
  private String out;
  private String err;
  private String returnCode;
  private Map<String, Object> outputVariables = new HashMap<String, Object>();

  public String getErr() {
    return err;
  }

  public void setErr(final String err) {
    this.err = err;
  }

  public String getOut() {
    return out;
  }

  public void setOut(final String out) {
    this.out = out;
  }

  public Map<String, Object> getOutputVariables() {
    return outputVariables;
  }

  public void setOutputVariables(final Map<String, Object> outputVariables) {
    this.outputVariables = outputVariables;
  }

  public String getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(final String returnCode) {
    this.returnCode = returnCode;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
