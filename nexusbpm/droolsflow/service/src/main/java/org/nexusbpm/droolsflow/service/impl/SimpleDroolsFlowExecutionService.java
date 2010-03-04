package org.nexusbpm.droolsflow.service.impl;

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
import org.nexusbpm.droolsflow.service.DroolsFlowExecutionService;

/**
 *
 * @author Matthew Sandoz
 */
public class SimpleDroolsFlowExecutionService implements DroolsFlowExecutionService {

  public void run(String resourceId) throws Exception {
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    FileObject file = VFS.getManager().resolveFile(resourceId);
    kbuilder.add(ResourceFactory.newInputStreamResource(file.getContent().getInputStream()), ResourceType.DRF);
    if (kbuilder.getErrors().size() > 0) {
      throw new Exception("REALLY THIS WILL HAVE TO BE CHANGED");
    }
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    ksession.getWorkItemManager().registerWorkItemHandler("Email Sender",
            new WorkItemHandler() {
              @Override
              public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.completeWorkItem(workItem.getId(), null);
              }
              @Override
              public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}
            });
    ksession.getWorkItemManager().registerWorkItemHandler("Script", new WorkItemHandler() {
      @Override
      public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.completeWorkItem(workItem.getId(), null);
      }
      @Override
      public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}
    });

  }
}
