package org.nexusbpm.service.ftp;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

/**
 * @author Matthew Sandoz
 *
 */
public class FtpWorkItem extends NexusWorkItemImpl {

  private static final long serialVersionUID = 1l;
  public static final String FTP_INPUT_FIELDNAME = "input";
  public static final String FTP_OUTPUT_FIELDNAME = "output";
  public static final List<String> FTP_INPUT_FIELDS = Arrays.asList(
          FTP_INPUT_FIELDNAME,
          FTP_OUTPUT_FIELDNAME);
  public static final List<String> FTP_OUTPUT_FIELDS = Arrays.asList(
          WORKITEM_OUT_KEY,
          WORKITEM_ERR_KEY,
          WORKITEM_RETURN_CODE_KEY);

  public FtpWorkItem() {
    super();
  }

  public FtpWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredInputParameterNames() {
    return FTP_INPUT_FIELDS;
  }

  @Override
  public List<String> getRequiredOutputParameterNames() {
    return FTP_OUTPUT_FIELDS;
  }

  public URI getInput() {
    return (URI) getParameters().get(FTP_INPUT_FIELDNAME);
  }

  public void setInput(URI input) {
    getParameters().put(FTP_INPUT_FIELDNAME, input);
  }

  public URI getOutput() {
    return (URI) getParameters().get(FTP_OUTPUT_FIELDNAME);
  }

  public void setOutput(URI output) {
    getParameters().put(FTP_OUTPUT_FIELDNAME, output);
  }
}
