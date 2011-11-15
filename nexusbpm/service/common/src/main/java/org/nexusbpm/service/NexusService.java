package org.nexusbpm.service;

/**
 * Base interface that all Nexus Services extend.
 * 
 * @author Nathan Rose
 * @author Matthew Sandoz
 */
public interface NexusService {

  NexusServiceResponse execute(NexusServiceRequest request) throws NexusServiceException;
}
