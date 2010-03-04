package org.nexusbpm.droolsflow.service.impl;

import org.drools.runtime.process.WorkItem;
import org.nexusbpm.common.data.ParameterLinkedHashMap;

/**
 *
 * @author Matthew Sandoz
 */
public class GenericDroolsFlowParameterMap extends ParameterLinkedHashMap {

  public GenericDroolsFlowParameterMap(WorkItem item) {
    super.setInstanceId(item.getProcessInstanceId());
    super.setNodeName(item.getName());
    super.setRequestId(Long.toString(item.getId()));
    //next we have to convert everything to parameters...not as easy as just calling the below...
    //super.putAll(item.getParameters());
  }

}
