package org.nexusbpm.service;

import org.nexusbpm.common.data.NexusWorkItem;

/**
 * Base interface that all Nexus Services extend.
 * 
 * @author Nathan Rose
 * @author Matthew Sandoz
 */
public interface NexusService {

  void execute(NexusWorkItem item) throws NexusServiceException;

  NexusWorkItem createCompatibleWorkItem(NexusWorkItem item);
}
