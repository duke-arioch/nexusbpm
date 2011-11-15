/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nexusbpm.common;

/**
 *
 * @author msandoz
 */
public class DataVisitationException extends Exception {

  public DataVisitationException(final Throwable cause) {
    super(cause);
  }

  public DataVisitationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DataVisitationException(final String message) {
    super(message);
  }

  public DataVisitationException() {
    super();
  }

}
