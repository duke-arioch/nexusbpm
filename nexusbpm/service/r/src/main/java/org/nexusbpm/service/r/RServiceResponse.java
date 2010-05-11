package org.nexusbpm.service.r;

import org.nexusbpm.service.NexusServiceResponse;

public class RServiceResponse extends NexusServiceResponse{

  public static final long serialVersionUID = 1L;
  private boolean keepSession;
  private byte[] session;

  public boolean isKeepSession() {
    return keepSession;
  }

  public void setKeepSession(boolean keepSession) {
    this.keepSession = keepSession;
  }

  public byte[] getSession() {
    return session;
  }

  public void setSession(byte[] session) {
    this.session = session;
  }

}
