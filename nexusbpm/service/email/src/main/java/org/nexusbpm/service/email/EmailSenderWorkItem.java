package org.nexusbpm.service.email;

import java.util.Arrays;
import java.util.List;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

public class EmailSenderWorkItem extends NexusWorkItemImpl {

  private static final long serialVersionUID = 1L;
  public static final String EMAIL_SENDER_TO_ADDRESS_FIELDNAME = "toAddress";
  public static final String EMAIL_SENDER_CC_ADDRESS_FIELDNAME = "ccAddress";
  public static final String EMAIL_SENDER_BCC_ADDRESS_FIELDNAME = "bccAddress";
  public static final String EMAIL_SENDER_FROM_ADDRESS_FIELDNAME = "fromAddress";
  public static final String EMAIL_SENDER_SUBJECT_FIELDNAME = "subject";
  public static final String EMAIL_SENDER_BODY_FIELDNAME = "body";
  public static final String EMAIL_SENDER_HOST_FIELDNAME = "host";
  public static final String EMAIL_SENDER_PORT_FIELDNAME = "port";
  public static final String EMAIL_SENDER_USE_SSL_FIELDNAME = "useSSL";
  public static final String EMAIL_SENDER_USERNAME_FIELDNAME = "username";
  public static final String EMAIL_SENDER_PASSWORD_FIELDNAME = "password";
  public static final String EMAIL_SENDER_HTML_FIELDNAME = "html";
  public static final List<String> EMAIL_INPUT_FIELDS = Arrays.asList(
          EMAIL_SENDER_TO_ADDRESS_FIELDNAME,
          EMAIL_SENDER_CC_ADDRESS_FIELDNAME,
          EMAIL_SENDER_BCC_ADDRESS_FIELDNAME,
          EMAIL_SENDER_FROM_ADDRESS_FIELDNAME,
          EMAIL_SENDER_SUBJECT_FIELDNAME,
          EMAIL_SENDER_BODY_FIELDNAME,
          EMAIL_SENDER_HOST_FIELDNAME,
          EMAIL_SENDER_PORT_FIELDNAME,
          EMAIL_SENDER_USE_SSL_FIELDNAME,
          EMAIL_SENDER_USERNAME_FIELDNAME,
          EMAIL_SENDER_PASSWORD_FIELDNAME,
          EMAIL_SENDER_HTML_FIELDNAME);

  public EmailSenderWorkItem() {
    super();
  }

  public EmailSenderWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredInputParameterNames() {
    return EMAIL_INPUT_FIELDS;
  }

  @Override
  public List<String> getRequiredOutputParameterNames() {
    return NexusWorkItemImpl.NEXUS_OUTPUT_FIELDS;
  }

  public String getBccAddress() {
    return (String) this.getParameters().get(EMAIL_SENDER_BCC_ADDRESS_FIELDNAME);
  }

  public void setBccAddress(String bccAddress) {
    this.getParameters().put(EMAIL_SENDER_BCC_ADDRESS_FIELDNAME, bccAddress);
  }

  public String getBody() {
    return (String) this.getParameters().get(EMAIL_SENDER_BODY_FIELDNAME);
  }

  public void setBody(String body) {
    this.getParameters().put(EMAIL_SENDER_BODY_FIELDNAME, body);
  }

  public String getCcAddress() {
    return (String) this.getParameters().get(EMAIL_SENDER_CC_ADDRESS_FIELDNAME);
  }

  public void setCcAddress(String ccAddress) {
    this.getParameters().put(EMAIL_SENDER_CC_ADDRESS_FIELDNAME, ccAddress);
  }

  public String getFromAddress() {
    return (String) this.getParameters().get(EMAIL_SENDER_FROM_ADDRESS_FIELDNAME);
  }

  public void setFromAddress(String fromAddress) {
    this.getParameters().put(EMAIL_SENDER_FROM_ADDRESS_FIELDNAME, fromAddress);
  }

  public String getHost() {
    return (String) this.getParameters().get(EMAIL_SENDER_HOST_FIELDNAME);
  }

  public void setHost(String host) {
    this.getParameters().put(EMAIL_SENDER_HOST_FIELDNAME, host);
  }

  public String getPort() {
    String retval = (String) this.getParameters().get(EMAIL_SENDER_PORT_FIELDNAME);
    return retval == null ? "25" : retval;
  }

  public void setPort(String port) {
    this.getParameters().put(EMAIL_SENDER_PORT_FIELDNAME, port);
  }

  public Boolean useSSL() {
    return (Boolean) this.getParameters().get(EMAIL_SENDER_USE_SSL_FIELDNAME);
  }

  public void setSecure(Boolean useSSL) {
    this.getParameters().put(EMAIL_SENDER_USE_SSL_FIELDNAME, useSSL);
  }

  public String getSubject() {
    return (String) this.getParameters().get(EMAIL_SENDER_SUBJECT_FIELDNAME);
  }

  public void setSubject(String subject) {
    this.getParameters().put(EMAIL_SENDER_SUBJECT_FIELDNAME, subject);
  }

  public String getToAddress() {
    return (String) this.getParameters().get(EMAIL_SENDER_TO_ADDRESS_FIELDNAME);
  }

  public void setToAddress(String toAddress) {
    this.getParameters().put(EMAIL_SENDER_TO_ADDRESS_FIELDNAME, toAddress);
  }

  public String getUsername() {
    return (String) this.getParameters().get(EMAIL_SENDER_USERNAME_FIELDNAME);
  }

  public void setUsername(String username) {
    this.getParameters().put(EMAIL_SENDER_USERNAME_FIELDNAME, username);
  }

  public String getPassword() {
    return (String) this.getParameters().get(EMAIL_SENDER_PASSWORD_FIELDNAME);
  }

  public void setPassword(String password) {
    this.getParameters().put(EMAIL_SENDER_PASSWORD_FIELDNAME, password);
  }

  public Boolean getHTML() {
    Boolean retval = (Boolean) this.getParameters().get(EMAIL_SENDER_HTML_FIELDNAME);
    return retval == null ? Boolean.TRUE : retval;
  }

  public void setHTML(Boolean html) {
    this.getParameters().put(EMAIL_SENDER_HTML_FIELDNAME, html);
  }
}
