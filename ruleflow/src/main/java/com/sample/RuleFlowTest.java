package com.sample;

import java.util.Arrays;
import java.util.HashMap;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * This is a sample file to launch a process.
 */
public class RuleFlowTest {


	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			// start a new process instance
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
			ksession.getWorkItemManager().registerWorkItemHandler(
				    "Email Sender", new WorkItemHandler() {
				    	@Override
				    	public void executeWorkItem(WorkItem workItem,
				    			WorkItemManager manager) {
				    		System.out.println("email invoked:" + workItem.getParameters());
				    		manager.completeWorkItem(workItem.getId(), null);
				    	}

						@Override
						public void abortWorkItem(WorkItem arg0,
								WorkItemManager arg1) {
						}
				    });
			ksession.getWorkItemManager().registerWorkItemHandler(
				    "Script", new WorkItemHandler() {
				    	@Override
				    	public void executeWorkItem(WorkItem workItem,
				    			WorkItemManager manager) {
				    		System.out.println("script invoked:" + workItem.getParameters());
				    		manager.completeWorkItem(workItem.getId(), null);
				    	}

						@Override
						public void abortWorkItem(WorkItem arg0,
								WorkItemManager arg1) {
							// TODO Auto-generated method stub
							
						}
				    });
			HashMap h = new HashMap<String, Object>();
			h.put("names", Arrays.asList((new String[] {"Bill", "John"})));
			ProcessInstance pi = ksession.startProcess("com.sample.ruleflow", h);
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("rules/ruleflow.rf"), ResourceType.DRF);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

}