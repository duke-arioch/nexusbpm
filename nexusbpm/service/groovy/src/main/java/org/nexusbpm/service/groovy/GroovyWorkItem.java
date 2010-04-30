package org.nexusbpm.service.groovy;

import java.util.Map;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterType;

public class GroovyWorkItem extends NexusWorkItemImpl {

	private static final long serialVersionUID = 1L;
	public static final String GROOVY_CODE_FIELDNAME = "code";
	
	public GroovyWorkItem() {
		super();
	}

	public GroovyWorkItem(NexusWorkItem item) {
		super(item);
	} 

	public String getCode() {
		return (String) getParameters().get(GROOVY_CODE_FIELDNAME);
	}

	public void setCode(String code) {
		getParameters().put(GROOVY_CODE_FIELDNAME, code);
	}
	
}
