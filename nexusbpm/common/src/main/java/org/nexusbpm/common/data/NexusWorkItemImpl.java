package org.nexusbpm.common.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class NexusWorkItemImpl implements NexusWorkItem {

  private static final long serialVersionUID = 1l;
  private String name;
  private String workItemId;
  private String processInstanceId;
  private Map<String, Object> parameters;
  private Map<String, Object> results;

  public NexusWorkItemImpl(NexusWorkItem item) {
    super();
    this.setName(item.getName());
    this.setParameters(item.getParameters());
    this.setProcessInstanceId(item.getProcessInstanceId());
    this.setResults(item.getResults());
    this.setWorkItemId(item.getWorkItemId());
  }

  public NexusWorkItemImpl() {
    super();
    this.setParameters(new LinkedHashMap<String, Object>());
    this.setResults(new LinkedHashMap<String, Object>());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Map<String, Object> getParameters() {
    return parameters;
  }

  @Override
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  @Override
  public String getProcessInstanceId() {
    return processInstanceId;
  }

  @Override
  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  @Override
  public Map<String, Object> getResults() {
    return results;
  }

  @Override
  public void setResults(Map<String, Object> results) {
    this.results = results;
  }

  @Override
  public String getWorkItemId() {
    return workItemId;
  }

  @Override
  public void setWorkItemId(String workItemId) {
    this.workItemId = workItemId;
  }

  @Override
  public String getReturnCode() {
    return (String) this.getResults().get(WORKITEM_RETURN_CODE_KEY);
  }

  @Override
  public void setReturnCode(String returnCode) {
    this.getResults().put(WORKITEM_RETURN_CODE_KEY, returnCode);
  }

  @Override
  public String getOut() {
    return (String) this.getResults().get(WORKITEM_OUT_KEY);
  }

  @Override
  public void setOut(String out) {
    this.getResults().put(WORKITEM_OUT_KEY, out);
  }

  @Override
  public String getErr() {
    return (String) this.getResults().get(WORKITEM_ERR_KEY);
  }

  @Override
  public void setErr(String err) {
    this.getResults().put(WORKITEM_ERR_KEY, err);
  }
}
