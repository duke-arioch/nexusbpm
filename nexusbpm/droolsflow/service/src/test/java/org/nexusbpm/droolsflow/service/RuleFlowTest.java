package org.nexusbpm.droolsflow.service;

/**
 *
 * @author Matthew Sandoz
 */
import java.util.Arrays;
import java.util.HashMap;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test.context.xml")
public class RuleFlowTest {

  private int script = 0;
  private int email = 0;

  @Test
  public void testAFlow() throws Exception {
    // load the knowledge base
    KnowledgeBase kbase = readKnowledgeBase();
    StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    // start a new process instance
    ksession.getWorkItemManager().registerWorkItemHandler("Email Sender", new WorkItemHandler() {

      @Override
      public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        RuleFlowTest.this.email++;
        manager.completeWorkItem(workItem.getId(), null);
      }

      @Override
      public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
      }
    });
    ksession.getWorkItemManager().registerWorkItemHandler("Script", new WorkItemHandler() {

      @Override
      public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        RuleFlowTest.this.script++;
        manager.completeWorkItem(workItem.getId(), null);
      }

      @Override
      public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
      }
    });
    HashMap map = new HashMap<String, Object>();
    map.put("names", Arrays.asList(new String[]{"Bill", "John"}));
    ksession.startProcess("com.sample.ruleflow", map);
    assertThat("script should be invoked twice", script, equalTo(2));
    assertThat("email should be invoked once", email, equalTo(1));
  }

  private static KnowledgeBase readKnowledgeBase() throws Exception {
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    kbuilder.add(ResourceFactory.newClassPathResource("rule/ruleflow.rf"), ResourceType.DRF);
    if (kbuilder.getErrors().size() > 0) {
      fail("Could not parse knowledge.");
    }
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    return kbase;
  }
}
