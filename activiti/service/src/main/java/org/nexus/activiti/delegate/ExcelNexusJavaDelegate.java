package org.nexus.activiti.delegate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.beanutils.BeanUtils;
import org.nexusbpm.service.NexusServiceResponse;
import org.nexusbpm.service.excel.ExcelServiceImpl;
import org.nexusbpm.service.excel.ExcelServiceRequest;

public class ExcelNexusJavaDelegate extends ExcelServiceImpl implements
		JavaDelegate {

	public static final Map<String, String> INPUT_MAP, OUTPUT_MAP;
	
	static {
		Map<String, String> inMap = new HashMap<String, String>();
		inMap.put("skipHeader", "skipHeader");
		inMap.put("rowLimit", "rowLimit");
		inMap.put("columnLimit", "columnLimit");
		inMap.put("excelAnchor", "excelAnchor");
		inMap.put("sheetName", "templateFile");
		inMap.put("dataFile", "dataFile");
		INPUT_MAP = Collections.unmodifiableMap(inMap);

		Map<String, String> outMap = new HashMap<String, String>();
		outMap.put("outputFile", "outputFileVariableName");
		outMap.put("returnCode", "status");
		OUTPUT_MAP = Collections.unmodifiableMap(outMap);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ExcelServiceRequest request = new ExcelServiceRequest();
		Map<String, Object> vars = execution.getVariables();
		for (Entry<String, String> entry : INPUT_MAP.entrySet()) {
			if (vars.containsKey(entry.getValue())) {
				BeanUtils.setProperty(request, entry.getKey(), vars.get(entry.getValue()));			
			}
		}
		NexusServiceResponse response = super.execute(request);
		for (Entry<String, String> entry : OUTPUT_MAP.entrySet()) {
			Object output = BeanUtils.getProperty(response, entry.getKey());
			execution.setVariable(entry.getValue(), output);
		}
	}
}
