package org.nexusbpm.activiti.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Defines the Script nexusbpm node.
 * 
 * @author Matthew Sandoz
 */
@Runtime(delegationClass = "org.nexusbpm.activiti.ScriptNexusJavaDelegation")
@Help(displayHelpShort = "Runs code using a scripting language interpreter")
public class ScriptNexusTask extends AbstractCustomServiceTask {

	@Property(type = PropertyType.BOOLEAN_CHOICE, displayName = "Skip header?", required = false, defaultValue = "true")
	@Help(displayHelpShort = "Should the header be skipped while filling?")
	private String skipHeader;

	@Property(type = PropertyType.TEXT, displayName = "Column Limit", required = false)
	@Help(displayHelpShort = "Max number of columns to populate")
	private String columnLimit;

	@Property(type = PropertyType.TEXT, displayName = "Anchor", required = true, defaultValue = "A1")
	@Help(displayHelpShort = "Row/Column indicating fill start position")
	private String anchor;

	@Property(type = PropertyType.TEXT, displayName = "Row Limit", required = false)
	@Help(displayHelpShort = "Max number of rows to populate")
	private String rowLimit;

	@Property(type = PropertyType.TEXT, displayName = "Sheet Name", defaultValue = "Sheet 1", required = false)
	@Help(displayHelpShort = "Name the sheet being populated")
	private String sheetName;

	@Property(type = PropertyType.TEXT, displayName = "Template File", required = false)
	@Help(displayHelpShort = "Name of the file resource to use as a template")
	private String templateFile;

	@Property(type = PropertyType.TEXT, displayName = "Data File", required = false)
	@Help(displayHelpShort = "Name of the file resource to use for data")
	private String dataFile;

	@Property(type = PropertyType.TEXT, displayName = "Output File", required = false)
	@Help(displayHelpShort = "Name of the file resource to use for output")
	private String outputFile;

	@Override
	public String contributeToPaletteDrawer() {
		return NexusConstants.NEXUS_PALETTE;
	}

	@Override
	public String getName() {
		return "Excel node";
	}

	@Override
	public String getSmallIconPath() {
		return "icons/script_enabled.gif";
	}

}
