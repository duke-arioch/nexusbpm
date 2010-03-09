package org.nexusbpm.droolsflow.service.impl;

/**
 *
 * @author Matthew Sandoz
 */
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.nexusbpm.common.data.ParameterMap;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

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
    ParameterMap serviceParameterMap = service.getMinimalParameterMap();
    DroolsFlowParameterMap map = new DroolsFlowParameterMap(workItem, serviceParameterMap);
    try {
      Map<String, Object> outmap = service.execute(map).toOutputMap().toObjectMap();
      workItemManager.completeWorkItem(workItem.getId(), outmap);
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
