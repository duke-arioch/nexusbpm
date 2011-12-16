package org.nexusbpm.activiti.delegate;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:nexusbpm-test-context.xml")
public class NexusDelegateTest {
  
  @Autowired
  private RuntimeService runtimeService;
  
  @Autowired
  private TaskService taskService;
  
  @Autowired
  @Rule
  public ActivitiRule activitiSpringRule;
    
 @Test
  @Deployment
  public void simpleProcessTest() {
    ProcessInstance p = runtimeService.startProcessInstanceByKey("simpleProcess", CollectionUtil.singletonMap("input", "Activiti BPM Engine"));
    
//    Task task = taskService.createTaskQuery().singleResult();
//    assertEquals("My Task", task.getName());
  
//    taskService.complete(task.getId());
//    assertEquals(0, runtimeService.createProcessInstanceQuery().count());
   
  }
  
  
  
}
