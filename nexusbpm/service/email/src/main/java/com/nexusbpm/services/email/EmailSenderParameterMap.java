package com.nexusbpm.services.email;

import java.util.Map;

import com.nexusbpm.common.data.Parameter;
import com.nexusbpm.common.data.ParameterLinkedHashMap;
import com.nexusbpm.common.data.ParameterType;

public class EmailSenderParameterMap extends ParameterLinkedHashMap {

    private static final long serialVersionUID = 1L;

    String EMAIL_SENDER_TO_ADDRESS_FIELDNAME = "toAddress";
    String EMAIL_SENDER_CC_ADDRESS_FIELDNAME = "ccAddress";
    String EMAIL_SENDER_BCC_ADDRESS_FIELDNAME = "bccAddress";
    String EMAIL_SENDER_FROM_ADDRESS_FIELDNAME = "fromAddress";
    String EMAIL_SENDER_SUBJECT_FIELDNAME = "subject";
    String EMAIL_SENDER_BODY_FIELDNAME = "body";
    String EMAIL_SENDER_HOST_FIELDNAME = "host";
    String EMAIL_SENDER_PORT_FIELDNAME = "port";
    String EMAIL_SENDER_USE_SSL_FIELDNAME = "useSSL";
    String EMAIL_SENDER_USERNAME_FIELDNAME = "username";
    String EMAIL_SENDER_PASSWORD_FIELDNAME = "password";
    String EMAIL_SENDER_HTML_FIELDNAME = "html";
    Parameter toAddress = new Parameter(EMAIL_SENDER_TO_ADDRESS_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter ccAddress = new Parameter(EMAIL_SENDER_CC_ADDRESS_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter bccAddress = new Parameter(EMAIL_SENDER_BCC_ADDRESS_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter fromAddress = new Parameter(EMAIL_SENDER_FROM_ADDRESS_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter subject = new Parameter(EMAIL_SENDER_SUBJECT_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter body = new Parameter(EMAIL_SENDER_BODY_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter host = new Parameter(EMAIL_SENDER_HOST_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter port = new Parameter(EMAIL_SENDER_PORT_FIELDNAME, null, null, ParameterType.STRING, "25", true, Parameter.DIRECTION_INPUT);
    Parameter useSSL = new Parameter(EMAIL_SENDER_USE_SSL_FIELDNAME, null, null, ParameterType.BOOLEAN, Boolean.FALSE, true, Parameter.DIRECTION_INPUT);
    Parameter username = new Parameter(EMAIL_SENDER_USERNAME_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter password = new Parameter(EMAIL_SENDER_PASSWORD_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_INPUT);
    Parameter html = new Parameter(EMAIL_SENDER_HTML_FIELDNAME, null, null, ParameterType.BOOLEAN, Boolean.TRUE, true, Parameter.DIRECTION_INPUT);
    private Parameter[] parms = {toAddress, ccAddress, bccAddress, fromAddress, subject, body, host, port, useSSL, username, password, html};
    
    public EmailSenderParameterMap() {
        super();
        for (Parameter p: parms) add(p);
    }

    public EmailSenderParameterMap(Map<? extends String, ? extends Parameter> m) {
        super(m);
        for (Parameter p: parms) add(p);
    } 

    public String getBccAddress() {
        return (String) getValue(EMAIL_SENDER_BCC_ADDRESS_FIELDNAME);
    }

    public void setBccAddress(String bccAddress) {
        setValue(EMAIL_SENDER_BCC_ADDRESS_FIELDNAME, bccAddress);
    }

    public String getBody() {
        return (String) getValue(EMAIL_SENDER_BODY_FIELDNAME);
    }

    public void setBody(String body) {
        setValue(EMAIL_SENDER_BODY_FIELDNAME, body);
    }

    public String getCcAddress() {
        return (String) getValue(EMAIL_SENDER_CC_ADDRESS_FIELDNAME);
    }

    public void setCcAddress(String ccAddress) {
        setValue(EMAIL_SENDER_CC_ADDRESS_FIELDNAME, ccAddress);
    }

    public String getFromAddress() {
        return (String) getValue(EMAIL_SENDER_FROM_ADDRESS_FIELDNAME);
    }

    public void setFromAddress(String fromAddress) {
        setValue(EMAIL_SENDER_FROM_ADDRESS_FIELDNAME, fromAddress);
    }

    public String getHost() {
        return (String) getValue(EMAIL_SENDER_HOST_FIELDNAME);
    }

    public void setHost(String host) {
        setValue(EMAIL_SENDER_HOST_FIELDNAME, host);
    }

    public String getPort() {
        return (String) getValue(EMAIL_SENDER_PORT_FIELDNAME);
    }

    public void setPort(String port) {
        setValue(EMAIL_SENDER_PORT_FIELDNAME, port);
    }

    public Boolean useSSL() {
        return (Boolean) getValue(EMAIL_SENDER_USE_SSL_FIELDNAME);
    }

    public void setSecure(Boolean useSSL) {
        setValue(EMAIL_SENDER_USE_SSL_FIELDNAME, useSSL);
    }

    public String getSubject() {
        return (String) getValue(EMAIL_SENDER_SUBJECT_FIELDNAME);
    }

    public void setSubject(String subject) {
        setValue(EMAIL_SENDER_SUBJECT_FIELDNAME, subject);
    }

    public String getToAddress() {
        return (String) getValue(EMAIL_SENDER_TO_ADDRESS_FIELDNAME);
    }

    public void setToAddress(String toAddress) {
        setValue(EMAIL_SENDER_TO_ADDRESS_FIELDNAME, toAddress);
    }

    public String getUsername() {
        return (String) getValue(EMAIL_SENDER_USERNAME_FIELDNAME);
    }
    
    public void setUsername(String username) {
        setValue(EMAIL_SENDER_USERNAME_FIELDNAME, username);
    }
    
    public String getPassword() {
        return (String) getValue(EMAIL_SENDER_PASSWORD_FIELDNAME);
    }
    
    public void setPassword(String password) {
        setValue(EMAIL_SENDER_PASSWORD_FIELDNAME, password);
    }
    
    public Boolean getHTML() {
        return (Boolean) getValue(EMAIL_SENDER_HTML_FIELDNAME);
    }
    
    public void setHTML(Boolean html) {
        setValue(EMAIL_SENDER_HTML_FIELDNAME, html);
    }
}
