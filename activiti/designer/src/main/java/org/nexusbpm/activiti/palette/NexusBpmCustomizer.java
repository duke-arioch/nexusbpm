package org.nexusbpm.activiti.palette;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.integration.palette.AbstractDefaultPaletteCustomizer;
import org.activiti.designer.integration.palette.PaletteEntry;

public class NexusBpmCustomizer extends AbstractDefaultPaletteCustomizer {

	@Override 
	public List<PaletteEntry> disablePaletteEntries() {
		List<PaletteEntry> result = new ArrayList<PaletteEntry>();
		//Disable the SAS task?
//		result.add(PaletteEntry.BUSINESSRULE_TASK);
		return result;
	}

}
