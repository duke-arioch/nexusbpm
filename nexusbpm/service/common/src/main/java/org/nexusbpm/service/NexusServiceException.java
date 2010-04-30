package org.nexusbpm.service;

public class NexusServiceException extends Exception {
    private static final long serialVersionUID = 1l;

  public NexusServiceException(Throwable cause) {
    super(cause);
  }

  public NexusServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public NexusServiceException(String message) {
    super(message);
  }

  public NexusServiceException() {
  }
   
}
