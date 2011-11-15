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

import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.nexusbpm.service.NexusServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSenderServiceImpl implements NexusService {

  public static final String MAILER = "NEXUS_EMAIL_SENDER";
  private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

  @Override
  public EmailSenderServiceResponse execute(final NexusServiceRequest inData) throws NexusServiceException {
    final EmailSenderServiceRequest eData;
    if (inData instanceof EmailSenderServiceRequest) {
      eData = (EmailSenderServiceRequest) inData;
    } else {
      throw new IllegalArgumentException("method only takes email sender service requests");
    }
    try {
      LOGGER.debug("Email Request:" + eData.toString());
      send(eData.getToAddress(),
              eData.getCcAddress(),
              eData.getBccAddress(),
              eData.getFromAddress(),
              eData.getUsername(),
              eData.getPassword(),
              eData.getSubject(),
              eData.getBody(),
              eData.getHost(),
              eData.getPort(),
              eData.isUseSSL(),
              eData.isHtml(),
              eData);
    } catch (Exception e) {
      LOGGER.error("Error sending email!", e);
      throw new NexusServiceException("Error sending email!", e);
    }
    return new EmailSenderServiceResponse();
  }

  //Get a session and send the email
  public void send(final String to, final String cc, final String bcc, final String from,
          final String user, final String password,
          final String subject, final String body, final String host, final int port, final boolean isSecure,
          final boolean html,
          final EmailSenderServiceRequest data)
          throws AddressException, MessagingException, IOException {
    final Session session = getSession(host, Integer.toString(port), isSecure, user, password);
    final Message message = new MimeMessage(session);
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

    final Multipart multipart = new MimeMultipart();

    final MimeBodyPart bodyPart = new MimeBodyPart();
    if (html) {
      bodyPart.setContent(body, "text/html");
    } else {
      bodyPart.setContent(body, "text/plain");
    }
    multipart.addBodyPart(bodyPart);

    attachFiles(multipart, data);

    message.setContent(multipart);

    Transport.send(message);
    LOGGER.debug("sent email to '" + to + "' with subject '" + subject + "'");
  }

  private Session getSession(final String host, final String port, final boolean isSecure, final String user, final String password) {
    final Properties properties = new Properties();
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", port);
//        properties.put("mail.debug", "true");
    if (isSecure) {
      properties.put("mail.smtp.socketFactory.class", SSL_FACTORY);
    }
    Session session;
    if (user != null && user.length() > 0) {
      properties.put("mail.user", user);
      properties.put("mail.password", password);
      properties.put("mail.smtp.auth", "true");
      if (password == null) {
        session = Session.getInstance(properties);
      } else {
        final Authenticator authenticator = new PasswordAuthenticator(user, password);
        session = Session.getInstance(properties, authenticator);
      }
    } else {
      session = Session.getInstance(properties);
    }
    return session;
  }

  private void attachFiles(final Multipart mp, final EmailSenderServiceRequest data) throws IOException, MessagingException {
    for (Object value : data.getInputVariables().values()) {
      if (value instanceof URI) {
        final FileObject file = VFS.getManager().resolveFile(((URI) value).toString());

        final FileObjectDataSource source = new FileObjectDataSource(file, false);

        final MimeBodyPart part = new MimeBodyPart();

        part.setDataHandler(new DataHandler(source));
        part.setFileName(part.getDataHandler().getName());

        mp.addBodyPart(part);
      }
    }
  }

  private static class PasswordAuthenticator extends Authenticator {

    private transient final PasswordAuthentication authentication;

    PasswordAuthenticator(final String username, final String password) {
      super();
      authentication = new PasswordAuthentication(username, password);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return authentication;
    }
  }

  private static class FileObjectDataSource implements DataSource {

    private final transient boolean asciiMode;
    private final transient FileObject provider;

    FileObjectDataSource(final FileObject provider, final boolean asciiMode) {
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
      throw new IOException(new UnsupportedOperationException("OutputStream not supported!"));
    }
  }
}
