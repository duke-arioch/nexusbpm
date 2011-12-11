package org.nexusbpm.activiti.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Defines the FTP Sender nexusbpm node.
 * 
 * @author Matthew Sandoz
 */
@Runtime(delegationClass = "org.nexusbpm.activiti.FtpSenderNexusJavaDelegation")
@Help(displayHelpShort = "Send files via FTP")
public class FTPSenderNexusTask extends AbstractCustomServiceTask {

	@Property(type = PropertyType.TEXT, displayName = "Operation", required = true, defaultValue = "get")
	// change to dropdown
	@Help(displayHelpShort = "Should we get or put or other?")
	private String operation;

	@Property(type = PropertyType.TEXT, displayName = "Remote Directory", required = false)
	@Help(displayHelpShort = "Remote directory for selected files")
	private String remoteDirectory;

	@Property(type = PropertyType.TEXT, displayName = "Remote Host", required = true)
	@Help(displayHelpShort = "Remote host server address")
	private String remoteHost;

	@Property(type = PropertyType.TEXT, displayName = "Remote File", required = false)
	@Help(displayHelpShort = "Remote file")
	private String remoteFile;

	@Property(type = PropertyType.TEXT, displayName = "User ID", required = false)
	@Help(displayHelpShort = "User ID on the remote server")
	private String user;

	@Property(type = PropertyType.TEXT, displayName = "Password", required = false)
	@Help(displayHelpShort = "User Password on the remote server")
	private String password;

	@Property(type = PropertyType.TEXT, displayName = "Input File", required = false)
	@Help(displayHelpShort = "Name of the file resource to retrieve")
	private String inputFile;

	@Property(type = PropertyType.TEXT, displayName = "Output File", required = false)
	@Help(displayHelpShort = "Name of the file resource to send")
	private String outputFile;

	@Override
	public String contributeToPaletteDrawer() {
		return NexusConstants.NEXUS_PALETTE;
	}

	@Override
	public String getName() {
		return "File Transfer";
	}

	@Override
	public String getSmallIconPath() {
		return "icons/folder_documents.png";
	}

}
