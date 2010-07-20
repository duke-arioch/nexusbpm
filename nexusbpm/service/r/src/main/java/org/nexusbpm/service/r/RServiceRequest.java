package org.nexusbpm.service.r;

import org.nexusbpm.service.NexusServiceRequest;

public class RServiceRequest extends NexusServiceRequest{

  public static final long serialVersionUID = 1L;
  private String code;
  private String serverAddress;
  private byte[] session;
  private boolean keepSession;

  public String getCode() {
    return code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public String getServerAddress() {
    return serverAddress;
  }

  public void setServerAddress(final String serverAddress) {
    this.serverAddress = serverAddress;
  }

  public byte[] getSession() {
    return session;
  }

  public void setSession(final byte[] sessionIn) {
    this.session = sessionIn;
  }

  public boolean isKeepSession() {
    return keepSession;
  }

  public void setKeepSession(final boolean keepSession) {
    this.keepSession = keepSession;
  }

}
