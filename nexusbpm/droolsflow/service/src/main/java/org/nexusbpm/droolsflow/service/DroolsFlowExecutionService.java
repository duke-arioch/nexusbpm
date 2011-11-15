package org.nexusbpm.droolsflow.service;

import java.net.URI;
import java.util.Map;

/**
 *
 * @author Matthew Sandoz
 */
public interface DroolsFlowExecutionService {

  long startProcess(URI processLocation, String processId, Map processVariables) throws Exception;

}
