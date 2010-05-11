package org.nexusbpm.service.email;

import org.nexusbpm.service.NexusServiceRequest;

public class EmailSenderServiceRequest extends NexusServiceRequest {

  private static final long serialVersionUID = 1L;
  private String toAddress;
  private String ccAddress;
  private String bccAddress;
  private String fromAddress;
  private String subject;
  private String body;
  private String host;
  private int port;
  private boolean useSSL;
  private String username;
  private String password;
  private boolean html;

  public String getBccAddress() {
    return bccAddress;
  }

  public void setBccAddress(String bccAddress) {
    this.bccAddress = bccAddress;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getCcAddress() {
    return ccAddress;
  }

  public void setCcAddress(String ccAddress) {
    this.ccAddress = ccAddress;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public boolean isHtml() {
    return html;
  }

  public void setHtml(boolean html) {
    this.html = html;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getPort() {
    return port == 0 ? 25 : port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getToAddress() {
    return toAddress;
  }

  public void setToAddress(String toAddress) {
    this.toAddress = toAddress;
  }

  public boolean isUseSSL() {
    return useSSL;
  }

  public void setUseSSL(boolean useSSL) {
    this.useSSL = useSSL;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
