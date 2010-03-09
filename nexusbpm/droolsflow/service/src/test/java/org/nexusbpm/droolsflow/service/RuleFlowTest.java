package org.nexusbpm.droolsflow.service;

/**
 *
 * @author Matthew Sandoz
 */
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nexusbpm.droolsflow.service.impl.SimpleDroolsFlowExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test.context.xml" })
public class RuleFlowTest {

  @Resource
  public SimpleDroolsFlowExecutionService executionService;
  public static Logger LOGGER = LoggerFactory.getLogger(RuleFlowTest.class);

  @Test
  public void testAFlow() throws Exception {
    assertThat("autowired service can't be null", executionService, notNullValue());
    HashMap map = new HashMap<String, Object>();
    map.put("names", Arrays.asList(new String[]{"Bill", "John"}));
    long id = executionService.startProcess(new URI("res:rule/ruleflow.rf"), "com.sample.ruleflow", map);
    id = executionService.startProcess(new URI("res:rule/ruleflow.rf"), "com.sample.ruleflow", map);
    assertThat("should at least run the service and give back an id", Long.valueOf(id), not(equalTo(Long.valueOf(0))));
  }

  public SimpleDroolsFlowExecutionService getExecutionService() {
    return executionService;
  }

  public void setExecutionService(SimpleDroolsFlowExecutionService executionService) {
    this.executionService = executionService;
  }


}
