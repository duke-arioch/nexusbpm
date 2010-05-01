package org.nexusbpm.service.groovy;

import java.util.Arrays;
import java.util.List;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;


public class GroovyWorkItem extends NexusWorkItemImpl {

  private static final long serialVersionUID = 1L;
  public static final String GROOVY_CODE_FIELDNAME = "code";
  public static final List<String> fields = Arrays.asList(
          GROOVY_CODE_FIELDNAME,
          WORKITEM_OUT_KEY,
          WORKITEM_ERR_KEY,
          WORKITEM_RETURN_CODE_KEY);

  public GroovyWorkItem() {
    super();
  }

  public GroovyWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredParameterNames() {
    return fields;
  }

  public String getCode() {
    return (String) getParameters().get(GROOVY_CODE_FIELDNAME);
  }

  public void setCode(String code) {
    getParameters().put(GROOVY_CODE_FIELDNAME, code);
  }
}
