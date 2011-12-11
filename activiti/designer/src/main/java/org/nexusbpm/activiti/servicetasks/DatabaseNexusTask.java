package org.nexusbpm.activiti.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Defines the Database nexusbpm node.
 * 
 * @author Matthew Sandoz
 */
@Runtime(delegationClass = "org.nexusbpm.activiti.DatabaseNexusJavaDelegation")
@Help(displayHelpShort = "Execute a database action")
public class DatabaseNexusTask extends AbstractCustomServiceTask {

	@Property(type = PropertyType.TEXT, displayName = "JDBC Driver", required = true)
	@Help(displayHelpShort = "Name of the driver class to use")
	private String jdbcDriver;

	@Property(type = PropertyType.TEXT, displayName = "JDBC URI", required = false, defaultValue = "jdbc:")
	@Help(displayHelpShort = "URI for connecting to the database")
	private String jdbcUri;

	@Property(type = PropertyType.TEXT, displayName = "User Name", required = false)
	@Help(displayHelpShort = "Database user name")
	private String user;

	@Property(type = PropertyType.TEXT, displayName = "Password", required = false)
	@Help(displayHelpShort = "Database user password")
	private String password;

	@Property(type = PropertyType.MULTILINE_TEXT, displayName = "SQL Code", required = true)
	@Help(displayHelpShort = "SQL code to execute against the database")
	private String sqlCode;

	@Property(type = PropertyType.TEXT, displayName = "Statement Type", required = true)
	@Help(displayHelpShort = "Type of statement to execute - ddl, dml, etc")
	private String statementType;

	@Property(type = PropertyType.TEXT, displayName = "CSV Input", required = false)
	@Help(displayHelpShort = "name of file containing data")
	private String csvInput;

	@Property(type = PropertyType.TEXT, displayName = "CSV Output", required = false)
	@Help(displayHelpShort = "write results to this destination")
	private String csvOutput;

	@Property(type = PropertyType.TEXT, displayName = "Record Count", required = false)
	@Help(displayHelpShort = "Name of the file resource to use for output")
	private String recordCount;

	@Override
	public String contributeToPaletteDrawer() {
		return NexusConstants.NEXUS_PALETTE;
	}

	@Override
	public String getName() {
		return "Database Ops";
	}

	@Override
	public String getSmallIconPath() {
		return "icons/database.png";
	}

}
