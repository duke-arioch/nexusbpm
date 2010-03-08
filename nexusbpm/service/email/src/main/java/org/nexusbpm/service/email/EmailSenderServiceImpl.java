package org.nexusbpm.service.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterMap;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSenderServiceImpl implements NexusService {

  public static final String MAILER = "NEXUS_EMAIL_SENDER";
  private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

  public ParameterMap execute(ParameterMap data) throws NexusServiceException {
    String to = null;
    String cc = null;
    String bcc = null;
    String from = null;
    String username = null;
    String password = null;
    String subject = null;
    String body = null;
    String host = null;
    String port = null;
    boolean useSSL = false;
    boolean html = false;
    StringBuffer b = new StringBuffer();
    EmailSenderParameterMap eData = new EmailSenderParameterMap(data);
    try {
      to = eData.getToAddress();
      cc = eData.getCcAddress();
      bcc = eData.getBccAddress();
      from = eData.getFromAddress();
      username = eData.getUsername();
      password = eData.getPassword();
      subject = eData.getSubject();
      body = eData.getBody();
      host = eData.getHost();
      port = eData.getPort();
      useSSL = eData.useSSL() != null && eData.useSSL().booleanValue();
      html = eData.getHTML().booleanValue();

      b = b.append("to: ").append(to).append(", cc: ").append(cc).append(", bcc: ").append(bcc).append(", from: ").append(from).append(", username: ").append(username).append(", subject: ").append(subject).append(", host: ").append(host);

      LOGGER.debug(b.toString());

      send(to, cc, bcc, from, username, password, subject, body, host, port, useSSL, html, eData);
    } catch (Exception e) {
      LOGGER.error("Error sending email!\n" + b.toString(), e);
      throw new NexusServiceException("Error sending email!", e, eData, false);
    }

    return eData;
  }

  //Get a session and send the email
  public void send(String to, String cc, String bcc, String from,
          String user, String password,
          String subject, String body, String host, String port, boolean isSecure,
          boolean html,
          EmailSenderParameterMap data)
          throws AddressException, MessagingException, IOException {
    Message message = null;
    Session session = getSession(host, port, isSecure, user, password, from);
    message = new MimeMessage(session);
    message.setHeader("X-Mailer", MAILER);
    message.setSentDate(new Date());
    message.setFrom(new InternetAddress(from));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
    if (cc != null) {
      message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
    }//if
    if (bcc != null) {
      message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
    }//if
    message.setSubject(subject);

    Multipart mp = new MimeMultipart();

    MimeBodyPart bodyPart = new MimeBodyPart();
    if (html) {
      bodyPart.setContent(body, "text/html");
    } else {
      bodyPart.setContent(body, "text/plain");
    }
    mp.addBodyPart(bodyPart);

    attachFiles(mp, data);

    message.setContent(mp);

    Transport.send(message);
    LOGGER.debug("sent email to '" + to + "' with subject '" + subject + "'");
  }

  private Session getSession(String host, String port, boolean isSecure, String user, String password, String fromAddress) {
    Properties properties = new Properties();
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", port);
//        properties.put("mail.debug", "true");
    if (isSecure) {
      properties.put("mail.smtp.socketFactory.class", SSL_FACTORY);
    }
    Authenticator authenticator = null;
    Session session;
    if (user != null && user.length() > 0) {
      properties.put("mail.user", user);
      properties.put("mail.password", password);
      properties.put("mail.smtp.auth", "true");
      if (password != null) {
        authenticator = new PasswordAuthenticator(user, password);
      }
    }
    if (authenticator != null) {
      session = Session.getInstance(properties, authenticator);
    } else {
      session = Session.getInstance(properties);
    }
    return session;
  }

  private void attachFiles(Multipart mp, EmailSenderParameterMap data) throws IOException, MessagingException {
    for (Parameter param : data.values()) {
      Object value = param.getValue();
      if (!param.isRequired()
              && !param.getDirection().equals("out")
              && param.isFile()
              && value != null
              && value instanceof URI
              && value.toString().length() > 0) {
        FileObject file = VFS.getManager().resolveFile(((URI) value).toString());

        FileObjectDataSource source = new FileObjectDataSource(file, param.isAsciiFile());

        MimeBodyPart part = new MimeBodyPart();

        part.setDataHandler(new DataHandler(source));
        part.setFileName(part.getDataHandler().getName());

        mp.addBodyPart(part);
      }
    }
  }

  public ParameterMap getMinimalParameterMap() {
    return new EmailSenderParameterMap();
  }

  private class PasswordAuthenticator extends Authenticator {

    PasswordAuthentication authentication;

    PasswordAuthenticator(String username, String password) {
      authentication = new PasswordAuthentication(username, password);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return authentication;
    }
  }

  private class FileObjectDataSource implements DataSource {

    private boolean asciiMode;
    private FileObject provider;

    FileObjectDataSource(FileObject provider, boolean asciiMode) {
      this.provider = provider;
      this.asciiMode = asciiMode;
    }

    public String getContentType() {
      return "application/octet-stream";
    }

    public InputStream getInputStream() throws IOException {
      return provider.getContent().getInputStream();
    }

    public String getName() {
      return provider.getName().getBaseName();
    }

    public OutputStream getOutputStream() throws IOException {
      IOException e = new IOException();
      e.initCause(new UnsupportedOperationException("OutputStream not supported!"));
      throw e;
    }
  }
}
