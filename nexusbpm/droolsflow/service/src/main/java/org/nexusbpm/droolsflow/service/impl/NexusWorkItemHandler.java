package org.nexusbpm.droolsflow.service.impl;

/**
 *
 * @author Matthew Sandoz
 */
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.nexusbpm.common.data.ParameterMap;

public class NexusWorkItemHandler implements WorkItemHandler {

  public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
        wim.completeWorkItem(wi.getId(), null);
  }

  
}
