package org.nexusbpm.droolsflow.service.impl;

import java.net.URI;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.nexusbpm.droolsflow.service.DroolsFlowExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Matthew Sandoz
 */
public class SimpleDroolsFlowExecutionService implements DroolsFlowExecutionService {

  @Resource private Map<String, WorkItemHandler> handlers;
  public static Logger LOGGER = LoggerFactory.getLogger(SimpleDroolsFlowExecutionService.class);

  public SimpleDroolsFlowExecutionService() {
  }

  public Map<String, WorkItemHandler> getHandlers() {
    return handlers;
  }

  public void setHandlers(Map<String, WorkItemHandler> handlers) {
    this.handlers = handlers;
  }


  private void registerHandlers(WorkItemManager manager) {
    for (Map.Entry<String, WorkItemHandler> entry : handlers.entrySet()) {
      manager.registerWorkItemHandler(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public long startProcess(URI processLocation, String resourceId, Map processVariables) throws Exception {
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    FileObject file = VFS.getManager().resolveFile(processLocation.toString());
    kbuilder.add(ResourceFactory.newInputStreamResource(file.getContent().getInputStream()), ResourceType.DRF);
    if (kbuilder.getErrors().size() > 0) {
      throw new Exception("REALLY THIS WILL HAVE TO BE CHANGED");
    }
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    registerHandlers(ksession.getWorkItemManager());

    ProcessInstance instance = ksession.startProcess(resourceId, processVariables);
    return instance.getId();
  }

}
