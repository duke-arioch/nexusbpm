package org.nexusbpm.service.ftp;

import java.net.URI;
import org.nexusbpm.service.NexusServiceRequest;

/**
 * @author Matthew Sandoz
 *
 */
public class FtpServiceRequest extends NexusServiceRequest {

  public URI input;
  public URI output;

  public URI getInput() {
    return input;
  }

  public void setInput(URI input) {
    this.input = input;
  }

  public URI getOutput() {
    return output;
  }

  public void setOutput(URI output) {
    this.output = output;
  }
  
}
