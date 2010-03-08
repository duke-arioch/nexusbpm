package org.nexusbpm.service;

import org.nexusbpm.common.data.ParameterMap;

/**
 * Base interface that all Nexus Services extend.
 * 
 * @author Nathan Rose
 */
public interface NexusService {
	ParameterMap execute(ParameterMap data) throws NexusServiceException;
  ParameterMap getMinimalParameterMap();
}
