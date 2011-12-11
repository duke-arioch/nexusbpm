package org.nexusbpm.droolsflow.service.impl;

/**
 *
 * @author Matthew Sandoz
 */
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nexusbpm.service.NexusServiceRequest;
import org.nexusbpm.service.NexusServiceResponse;

public class NexusWorkItemHandler implements WorkItemHandler {

  public static final Logger LOGGER = LoggerFactory.getLogger(NexusWorkItemHandler.class);
  private NexusService service;
  private String serviceRequestClassName;
  private Map<String, String> propertyMap;
  public NexusWorkItemHandler() {
  }

  @Override
  public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
    wim.abortWorkItem(wi.getId());
  }

  @Override
  public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
    NexusServiceResponse response;
    try {
      NexusServiceRequest nexusServiceRequest = (NexusServiceRequest) Class.forName(serviceRequestClassName).newInstance();
      nexusServiceRequest.setRequestId(workItem.getName());
      Map<String, Object> values = new HashMap<String, Object>();
      Set<String> keys = workItem.getParameters().keySet();
      for (String key : keys) {
        String propertyName = propertyMap.get(key);
        try {
        BeanUtils.setProperty(nexusServiceRequest, propertyName, workItem.getParameter(key));
        LOGGER.info("POPULATING WORK ITEM WITH key:" + key + ", prop:" + propertyName + ", value:" + workItem.getParameter(key));
        } catch(Exception e) {
          LOGGER.info("POPULATING INPUT VAR MAP WITH key:" + key + ", value:" + workItem.getParameter(key));
          nexusServiceRequest.getInputVariables().put(key, workItem.getParameter(key));
        }
      }
      response = service.execute(nexusServiceRequest);
      workItemManager.completeWorkItem(workItem.getId(), response.getOutputVariables());
    } catch (NexusServiceException nexusServiceException) {
      LOGGER.error("Exception in service execution", nexusServiceException);
    } catch (IllegalAccessException nexusServiceException) {
      LOGGER.error("Exception in service execution", nexusServiceException);
    } catch (InstantiationException nexusServiceException) {
      LOGGER.error("Exception in service execution", nexusServiceException);
    } catch (ClassNotFoundException nexusServiceException) {
      LOGGER.error("Exception in service execution", nexusServiceException);
    }
  }

  public NexusService getService() {
    return service;
  }

  public void setService(NexusService service) {
    this.service = service;
  }

  public String getServiceRequestClassName() {
    return serviceRequestClassName;
  }

  public void setServiceRequestClassName(String serviceRequestClassName) {
    this.serviceRequestClassName = serviceRequestClassName;
  }

  public Map<String, String> getPropertyMap() {
    return propertyMap;
  }

  public void setPropertyMap(Map<String, String> propertyMap) {
    this.propertyMap = propertyMap;
  }
}
