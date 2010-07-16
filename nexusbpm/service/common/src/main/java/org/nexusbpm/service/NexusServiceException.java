package org.nexusbpm.service;

public class NexusServiceException extends Exception {
    private static final long serialVersionUID = 1l;

  public NexusServiceException(final Throwable cause) {
    super(cause);
  }

  public NexusServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public NexusServiceException(final String message) {
    super(message);
  }

  public NexusServiceException() {
    super();
  }
   
}
