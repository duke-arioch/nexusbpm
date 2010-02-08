package com.nexusbpm.services.email;

import java.net.InetAddress;
import java.net.URI;

import com.nexusbpm.common.NexusTestCase;
import com.nexusbpm.common.data.Parameter;
import com.nexusbpm.common.data.ParameterType;
import org.junit.Before;
import org.junit.Test;

public class EmailServiceTest extends NexusTestCase {
//    private boolean perform = false;

    private Parameter attachment;
    protected EmailSenderParameterMap data = new EmailSenderParameterMap();

    @Before
    public void setUp() throws Exception {
        data.setToAddress(getProperty("test.email.recipient"));
        data.setBody("Body for <i>Email</i>ServiceTest");
        data.setCcAddress("");
        data.setHTML(Boolean.TRUE); 
        data.setProcessName("test");
        data.setRequestId("1-100");
        data.setSubject("test from EmailServiceTest");
        URI path = URI.create("ftp://" + InetAddress.getLocalHost().getHostAddress() + "/jbpm/work/local-in.csv");
//        InputDataflowStreamProvider provider = DataflowStreamProviderFactory.getInstance().getInputProvider(path);
        attachment = new Parameter("myfile", "", "", ParameterType.ASCII_FILE, path, false, Parameter.DIRECTION_INPUT);
    }

    @Test
    public void testIntranetSend() throws Exception {//only works from our intranet
        data.setBody("Body for <i>Email</i>ServiceTest");
        data.setFromAddress(getProperty("test.email.intranet.fromAddress"));
        data.setHost(getProperty("test.email.intranet.server"));

        data.setSubject("test from EmailServiceTest");
        data.setToAddress(getProperty("test.email.recipient"));
        data.put(attachment);
        EmailSenderServiceImpl service = new EmailSenderServiceImpl();
        service.execute(data);
    }

    @Test
    public void testGmail() throws Exception { //usually wont work in a firewall
        data.setFromAddress(getProperty("test.email.gmail.user"));
        data.setHost("smtp.gmail.com");
        data.setPort("465");
        data.setUsername(getProperty("test.email.gmail.user"));
        data.setPassword(getProperty("test.email.gmail.password"));
        data.setSecure(Boolean.TRUE);
        data.put(attachment);
        EmailSenderServiceImpl service = new EmailSenderServiceImpl();
        service.execute(data);
    }

    @Test
    public void testGoDaddy() throws Exception {//only works with a godaddy account
        data.setHost("smtpout.secureserver.net");
        data.setPort("3535");
        data.setUsername(getProperty("test.email.godaddy.user"));
        data.setPassword(getProperty("test.email.godaddy.password"));
        data.setSecure(Boolean.FALSE);
        data.setFromAddress(getProperty("test.email.godaddy.fromAddress"));
        data.put(attachment);
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
