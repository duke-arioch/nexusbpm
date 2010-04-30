package org.nexusbpm.droolsflow.service.impl;

/**
 *
 * @author Matthew Sandoz
 */
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

public class NexusWorkItemHandler implements WorkItemHandler {

  public static final Logger LOGGER = LoggerFactory.getLogger(NexusWorkItemHandler.class);
  private NexusService service;

  public NexusWorkItemHandler() {
  }

  @Override
  public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
    wim.abortWorkItem(wi.getId());
  }

  @Override
  public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
    NexusWorkItem nexusWorkItem = new NexusWorkItemImpl();
    nexusWorkItem.setName(workItem.getName());
    nexusWorkItem.setParameters(workItem.getParameters());
    nexusWorkItem.setProcessInstanceId(String.valueOf(workItem.getProcessInstanceId()));
    nexusWorkItem.setWorkItemId(String.valueOf(workItem.getId()));

    NexusWorkItem specificNexusWorkItem = service.createCompatibleWorkItem(nexusWorkItem);
    try {
      service.execute(specificNexusWorkItem);
      LOGGER.error(specificNexusWorkItem.getErr());
      workItemManager.completeWorkItem(workItem.getId(), specificNexusWorkItem.getResults());
    } catch (NexusServiceException nexusServiceException) {
      LOGGER.error("Exception in service execution", nexusServiceException);
    }
  }

  public NexusService getService() {
    return service;
  }

  public void setService(NexusService service) {
    this.service = service;
  }

}
