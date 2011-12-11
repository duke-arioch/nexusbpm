package org.nexusbpm.service.groovy;

import org.nexusbpm.service.NexusServiceRequest;


public class GroovyServiceRequest extends NexusServiceRequest {

  private static final long serialVersionUID = 1L;
  private String code;

  public String getCode() {
    return code;
  }

  public void setCode(final String code) {
    this.code = code;
  }
}
