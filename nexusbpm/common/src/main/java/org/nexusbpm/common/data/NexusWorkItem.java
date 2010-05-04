package org.nexusbpm.common.data;

import java.util.List;
import java.util.Map;

public interface NexusWorkItem {

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

  List<String> getRequiredInputParameterNames();

  List<String> getRequiredOutputParameterNames();

  boolean isRequiredParameter(String name);

  boolean isRequiredResult(String name);
}
