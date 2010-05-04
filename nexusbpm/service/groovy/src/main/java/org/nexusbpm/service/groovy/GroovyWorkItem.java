package org.nexusbpm.service.groovy;

import java.util.Arrays;
import java.util.List;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;


public class GroovyWorkItem extends NexusWorkItemImpl {

  private static final long serialVersionUID = 1L;
  public static final String GROOVY_CODE_FIELDNAME = "code";
  public static final List<String> GROOVY_INPUT_FIELDS = Arrays.asList(
          GROOVY_CODE_FIELDNAME);

  public GroovyWorkItem() {
    super();
  }

  public GroovyWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredInputParameterNames() {
    return GROOVY_INPUT_FIELDS;
  }

  @Override
  public List<String> getRequiredOutputParameterNames() {
    return NexusWorkItemImpl.NEXUS_OUTPUT_FIELDS;
  }

  public String getCode() {
    return (String) getParameters().get(GROOVY_CODE_FIELDNAME);
  }

  public void setCode(String code) {
    getParameters().put(GROOVY_CODE_FIELDNAME, code);
  }
}
