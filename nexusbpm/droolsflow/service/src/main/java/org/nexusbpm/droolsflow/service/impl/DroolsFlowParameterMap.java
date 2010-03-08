package org.nexusbpm.droolsflow.service.impl;

import java.util.Map;
import org.drools.runtime.process.WorkItem;
import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterMap;
import org.nexusbpm.common.data.ParameterType;

/**
 *
 * @author Matthew Sandoz
 */
public class DroolsFlowParameterMap extends ParameterLinkedHashMap {

  public DroolsFlowParameterMap(WorkItem item, ParameterMap prototype) {
    super(prototype);
    super.setInstanceId(item.getProcessInstanceId());
    super.setNodeName(item.getName());
    super.setRequestId(Long.toString(item.getId()));
    Map<String, Object> itemMap = item.getParameters();
    for (Entry<String, Object> entry : itemMap.entrySet()) {
      Parameter parameter = get(entry.getKey());
      if (prototype.containsKey(entry.getKey())) {
        parameter.setValue(entry.getValue());
      } else {
        ParameterType type = ParameterType.getType(entry.getValue().getClass());
        Parameter p = new Parameter(entry.getKey(), type, entry.getValue(), false, Parameter.DIRECTION_INPUT_AND_OUTPUT);
      }
    }
  }
}
