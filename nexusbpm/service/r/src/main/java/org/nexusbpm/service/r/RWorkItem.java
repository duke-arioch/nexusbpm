package org.nexusbpm.service.r;

import java.util.Arrays;
import java.util.List;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterType;

public class RWorkItem extends NexusWorkItemImpl {

  private static final long serialVersionUID = 1L;
  static String R_CODE_FIELDNAME = "code";
  static String R_KEEP_SESSION_FIELDNAME = "keepSession";
  static String R_SERVER_ADDRESS_FIELDNAME = "serverAddress";
  static String R_SESSION_FIELDNAME = "session";
  public static final List<String> R_INPUT_FIELDS = Arrays.asList(
          R_CODE_FIELDNAME,
          R_KEEP_SESSION_FIELDNAME,
          R_SERVER_ADDRESS_FIELDNAME,
          R_SESSION_FIELDNAME);
  public static final List<String> R_OUTPUT_FIELDS = Arrays.asList(
          R_SESSION_FIELDNAME,
          WORKITEM_OUT_KEY,
          WORKITEM_ERR_KEY,
          WORKITEM_RETURN_CODE_KEY);

  public RWorkItem() {
    super();
  }

  public RWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredInputParameterNames() {
    return R_INPUT_FIELDS;
  }

  @Override
  public List<String> getRequiredOutputParameterNames() {
    return R_OUTPUT_FIELDS;
  }

  @Override
  public boolean isRequiredParameter(String name) {
    return R_INPUT_FIELDS.contains(name);
  }

  @Override
  public boolean isRequiredResult(String name) {
    return R_OUTPUT_FIELDS.contains(name);
  }

  public String getCode() {
    return (String) getParameters().get(R_CODE_FIELDNAME);
  }

  public void setCode(String code) {
    getParameters().put(R_CODE_FIELDNAME, code);
  }

  public String getServerAddress() {
    return (String) getParameters().get(R_SERVER_ADDRESS_FIELDNAME);
  }

  public void setServerAddress(String serverAddress) {
    getParameters().put(R_SERVER_ADDRESS_FIELDNAME, serverAddress);
  }

  public byte[] getSession() {
    return (byte[]) getParameters().get(R_SESSION_FIELDNAME);
  }

  public void setSession(byte[] sessionIn) {
    getParameters().put(R_SESSION_FIELDNAME, sessionIn);
  }

  public Boolean isKeepSession() {
    return (Boolean) getParameters().get(R_KEEP_SESSION_FIELDNAME);
  }

  public void setKeepSession(Boolean keepSession) {
    getParameters().put(R_KEEP_SESSION_FIELDNAME, keepSession);
  }

}
