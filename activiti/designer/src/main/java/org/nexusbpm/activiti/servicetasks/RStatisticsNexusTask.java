package org.nexusbpm.activiti.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Defines the R Statistics nexusbpm node.
 * 
 * @author Matthew Sandoz
 */
@Runtime(delegationClass = "org.nexusbpm.activiti.RStatisticsNexusJavaDelegation")
@Help(displayHelpShort = "Invoke R Code")
public class RStatisticsNexusTask extends AbstractCustomServiceTask {

	@Property(type = PropertyType.MULTILINE_TEXT, displayName = "R Code", required = false)
	@Help(displayHelpShort = "R Code to execute")
	private String rCode;

	@Property(type = PropertyType.MULTILINE_TEXT, displayName = "Output")
	@Help(displayHelpShort = "R execution output")
	private String output; // do they support multi-out or will we have to start
							// a naming convention and extract the vals into the
							// context?

	@Property(type = PropertyType.MULTILINE_TEXT, displayName = "Error")
	@Help(displayHelpShort = "R execution errors")
	private String error;

	@Property(type = PropertyType.BOOLEAN_CHOICE, displayName = "Keep Session?", required = false)
	@Help(displayHelpShort = "Should we try to re-use existing sessions?")
	// may not be possible depending on state...
	private Boolean keepSessions;

	@Property(type = PropertyType.TEXT, displayName = "Server Address")
	@Help(displayHelpShort = "Name the sheet being populated")
	private String serverAddress;

	@Property(type = PropertyType.TEXT, displayName = "Session Object", required = false)
	// output variable - may not work...
	@Help(displayHelpShort = "If it works, a handle to the open session")
	private String session;

	@Property(type = PropertyType.TEXT, displayName = "Data File", required = false)
	// may require some work/thought...
	@Help(displayHelpShort = "Name of the file resource to use for data")
	private String dataFile;

	@Property(type = PropertyType.TEXT, displayName = "Output File", required = false)
	// may require some work/thought...
	@Help(displayHelpShort = "Name of the file resource to use for output")
	private String outputFile;

	@Override
	public String contributeToPaletteDrawer() {
		return NexusConstants.NEXUS_PALETTE;
	}

	@Override
	public String getName() {
		return "R Stats";
	}

	@Override
	public String getSmallIconPath() {
		return "icons/newrlogo.png";
	}

}
