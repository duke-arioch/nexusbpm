package org.nexusbpm.service.email;

import com.dumbster.smtp.SimpleSmtpServer;
import java.net.URI;
import org.junit.After;

import org.nexusbpm.common.NexusTestCase;
import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EmailServiceTest extends NexusTestCase {

  private Parameter attachment;
  protected EmailSenderWorkItem data = new EmailSenderWorkItem();
  SimpleSmtpServer server;

  @Before
  public void setUp() throws Exception {
    data.setFromAddress("someone@somewhere.com");
    data.setToAddress("someone@somewhere.com");
    data.setBody("Body for <i>Email</i>ServiceTest");
    data.setCcAddress("");
    data.setHTML(Boolean.TRUE);
    data.setName("test");
    data.setWorkItemId("1-100");
    data.setSubject("test from EmailServiceTest");
    URI path = URI.create("res:testfile.xml");
    attachment = new Parameter("myfile", ParameterType.ASCII_FILE, path, false, Parameter.DIRECTION_INPUT);
    data.getParameters().put("attachment1", attachment);
    server = SimpleSmtpServer.start();
  }

  @After
  public void tearDown() {
    server.stop();
  }

  @Test
  public void testIntranetSend() throws Exception {//only works from our intranet
    data.setHost("localhost");
    data.setUsername("dummy");
    data.setPassword("!@&*(%$");
    EmailSenderServiceImpl service = new EmailSenderServiceImpl();
    service.execute(data);
    assertThat("should receive one email.", server.getReceivedEmailSize(), equalTo(1));
  }

  @Ignore
  public void testGmail() throws Exception { //usually wont work in a firewall
    data.setFromAddress(getProperty("test.email.gmail.user"));
    data.setHost("smtp.gmail.com");
    data.setPort("465");
    data.setUsername("!!!");
    data.setPassword("!!!");
    data.setSecure(Boolean.TRUE);
    data.setToAddress("!!!");
    data.getParameters().put("attachment", attachment);
    EmailSenderServiceImpl service = new EmailSenderServiceImpl();
    service.execute(data);
  }

  @Ignore
  public void testGoDaddy() throws Exception {//only works with a godaddy account
    data.setHost("smtpout.secureserver.net");
    data.setPort("3535");
    data.setUsername("!!!");
    data.setPassword("!!!");
    data.setSecure(Boolean.FALSE);
    data.setFromAddress("!!!");
    data.getParameters().put("attachment", attachment);
    EmailSenderServiceImpl service = new EmailSenderServiceImpl();
    service.execute(data);
  }
  /* save these as a reminder of some of the settings I ran into should we need
   * to do further work...
  private static final String SMTP_HOST_NAME = "smtp.gmail.com";
  private static final String SMTP_PORT = "465";
  private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
  props.put("mail.smtp.host", SMTP_HOST_NAME);
  props.put("mail.smtp.auth", "true");
  props.put("mail.debug", "true");
  props.put("mail.smtp.port", SMTP_PORT);
  props.put("mail.smtp.socketFactory.port", SMTP_PORT);
  props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
   */
}
