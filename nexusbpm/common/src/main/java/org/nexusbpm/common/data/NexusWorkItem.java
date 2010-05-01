package org.nexusbpm.common.data;

import java.util.List;
import java.util.Map;

public interface NexusWorkItem {

  public static final String WORKITEM_RETURN_CODE_KEY = "returnCode";
  public static final String WORKITEM_OUT_KEY = "out";
  public static final String WORKITEM_ERR_KEY = "err";

  String getName();

  Map<String, Object> getParameters();

  String getProcessInstanceId();

  Map<String, Object> getResults();

  String getWorkItemId();

  String getErr();

  String getOut();

  String getReturnCode();

  void setName(String name);

  void setParameters(Map<String, Object> parameters);

  void setProcessInstanceId(String processInstanceId);

  void setResults(Map<String, Object> results);

  void setWorkItemId(String workItemId);

  void setErr(String err);

  void setOut(String out);

  void setReturnCode(String returnCode);

  List<String> getRequiredParameterNames();

}
