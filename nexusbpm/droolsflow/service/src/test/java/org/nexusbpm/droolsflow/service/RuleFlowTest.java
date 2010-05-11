package org.nexusbpm.droolsflow.service;

/**
 *
 * @author Matthew Sandoz
 */
import com.dumbster.smtp.SimpleSmtpServer;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nexusbpm.droolsflow.service.impl.SimpleDroolsFlowExecutionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test.context.xml"})
public class RuleFlowTest {

  @Resource
  private SimpleDroolsFlowExecutionService executionService;
  private SimpleSmtpServer server;

  @Before
  public void setUp() throws Exception {
    server = SimpleSmtpServer.start();
  }

  @After
  public void tearDown() {
    server.stop();
  }

  @Test
  public void testAFlow() throws Exception {
    HashMap map = new HashMap<String, Object>();
    map.put("names", Arrays.asList(new String[]{"Bill", "John"}));
    //rewrite the flow to move the map into the process node variables. until then, they will show up empty.
    long processId = executionService.startProcess(new URI("res:rule/ruleflow.rf"), "com.sample.ruleflow", map);
    assertThat("should at least run the service and give back an id", Long.valueOf(processId), not(equalTo(Long.valueOf(0))));
  }
}
