package com.nexusbpm.services;

import com.nexusbpm.common.data.ParameterMap;

/**
 * Base interface that all Nexus Services extend.
 * 
 * @author Nathan Rose
 */
public interface NexusService {
	public ParameterMap execute(ParameterMap data) throws NexusServiceException;
//    public String getTransitionName();
//    public boolean isAutoSignalling();
}
