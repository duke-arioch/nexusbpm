package org.nexusbpm.service.email;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.io.OutputStream;
import java.net.URI;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class EmailServiceTest {

//  private Parameter attachment;
  protected EmailSenderServiceRequest data = new EmailSenderServiceRequest();
  SimpleSmtpServer server;

  @Before
  public void setUp() throws Exception {
    data.setFromAddress("someone@somewhere.com");
    data.setToAddress("someone@somewhere.com");
    data.setBody("Body for <i>Email</i>ServiceTest");
    data.setCcAddress("");
    data.setHtml(true);
    data.setUsername("test");
    data.setRequestId("1-100");
    data.setSubject("test from EmailServiceTest");
    URI path = URI.create("res:testfile.xml");
    FileObject fileObject = VFS.getManager().resolveFile(path.toString());
    fileObject.createFile();
    OutputStream ostream = fileObject.getContent().getOutputStream();
    ostream.write("HI THERE".getBytes());
    ostream.flush();
    ostream.close();
    data.getInputVariables().put("attachment1", path);
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
    SmtpMessage message = (SmtpMessage) server.getReceivedEmail().next();
    assertThat("email must have an attachment", message.getBody().contains("attachment; filename=testfile.xml"), is(true));
    assertThat("attachment must contain HI THERE", message.getBody().contains("HI THERE"), is(true));
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
