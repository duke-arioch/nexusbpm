package org.nexusbpm.activiti.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Defines the Email Sender nexusbpm node.
 * 
 * @author Matthew Sandoz
 */
@Runtime(delegationClass = "org.nexusbpm.activiti.EmailSenderNexusJavaDelegation")
@Help(displayHelpShort = "Create an Email from other data")
public class EmailSenderNexusTask extends AbstractCustomServiceTask {

	@Property(type = PropertyType.TEXT, displayName = "To", required = false)
	@Help(displayHelpShort = "Email address of recipient(s)")
	private String sendTo;

	@Property(type = PropertyType.TEXT, displayName = "CC", required = false)
	@Help(displayHelpShort = "Email address of CC recipient(s)")
	private String sendCc;

	@Property(type = PropertyType.TEXT, displayName = "BCC", required = false)
	@Help(displayHelpShort = "Email address of BCC recipient(s)")
	private String sendBcc;

	@Property(type = PropertyType.TEXT, displayName = "From", required = true)
	@Help(displayHelpShort = "Email address of sender")
	private String sendFrom;

	@Property(type = PropertyType.TEXT, displayName = "Subject", required = false)
	@Help(displayHelpShort = "Email subject")
	private String subject;

	@Property(type = PropertyType.MULTILINE_TEXT, displayName = "Body", required = false)
	@Help(displayHelpShort = "Text of the email")
	private String body;

	@Property(type = PropertyType.TEXT, displayName = "Email Host", required = true)
	@Help(displayHelpShort = "Email server address")
	private String host;

	@Property(type = PropertyType.TEXT, displayName = "Port", required = true)
	@Help(displayHelpShort = "Email server port")
	private String port;

	@Property(type = PropertyType.BOOLEAN_CHOICE, displayName = "SSL", required = true)
	@Help(displayHelpShort = "Use SSL?")
	private Boolean useSsl;

	@Property(type = PropertyType.TEXT, displayName = "user", required = true)
	@Help(displayHelpShort = "Email server user account")
	private String user;

	@Property(type = PropertyType.TEXT, displayName = "password", required = true)
	@Help(displayHelpShort = "Email server user password")
	private String password;

	@Property(type = PropertyType.BOOLEAN_CHOICE, displayName = "use HTML?", required = true)
	@Help(displayHelpShort = "Email server user password")
	private Boolean html;

	@Property(type = PropertyType.TEXT, displayName = "Attachments", required = true)
	// change this to a grid later
	@Help(displayHelpShort = "Files to attach to the email")
	private String attachments;

	@Override
	public String contributeToPaletteDrawer() {
		return NexusConstants.NEXUS_PALETTE;
	}

	@Override
	public String getName() {
		return "Send Email";
	}

	@Override
	public String getSmallIconPath() {
		return "icons/mail_forward.png";
	}

}
