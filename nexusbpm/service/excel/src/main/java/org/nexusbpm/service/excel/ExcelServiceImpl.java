package org.nexusbpm.service.excel;

import org.nexusbpm.common.data.ParameterMap;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Excel service reads data from a CSV file and then inserts part or all
 * of that data into an Excel file.
 *
 * @author Austin Chau
 * @author Daniel Gredler
 * @author Matthew Sandoz (Major revisions, 2007)
 * @author Nathan Rose
 * @created May 27, 2003
 */
public class ExcelServiceImpl implements NexusService {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger( ExcelServiceImpl.class );
    
    public ParameterMap execute(ParameterMap data) throws NexusServiceException {
        ExcelParameterMap exData = new ExcelParameterMap(data);
        
        FileObject template = exData.getTemplateFile();
        FileObject inputData = exData.getDataFile();
        FileObject output = exData.getOutputFile();
        
        // The Excel service must have a CSV file from which to draw data.
        if(inputData == null) {
            throw new NexusServiceException("There is no data to export.");
        }
        
        // The Excel service must have an output file to put the data in.
        if(output == null) {
            throw new NexusServiceException("There is no Excel file to insert data into.");
        }
        
        try {
            String sheetName = exData.getSheetName();
            
            // Initialize an object with a common interface, regardless of whether we
            // are using Extentech's ExtenXLS library or Apache's POI library.
            ExcelPOIObject excelObject = new ExcelPOIObject(template, sheetName);
            
            excelObject.setOutputStreamProvider(output);
            excelObject.initialize();
            
            String anchorString = exData.getAnchor();
            boolean skipHeader = exData.isSkipHeader() != null && exData.isSkipHeader().booleanValue();
            int rowLimit = -1;
            if(exData.getRowLimit() != null && exData.getRowLimit().intValue() > 0) {
                rowLimit = exData.getRowLimit().intValue();
            }
            int colLimit = -1;
            if(exData.getColLimit() != null && exData.getColLimit().intValue() > 0) {
                colLimit = exData.getColLimit().intValue();
            }
            
            logger.debug("Inserting into sheet " + sheetName + " at " + anchorString + " as " + output.getName().getURI());
            int rows = excelObject.insertTableAtAnchor(
                    inputData, anchorString, skipHeader, rowLimit, colLimit);
            excelObject.save();
            logger.debug("Finished adding " + rows + " rows into spreadsheet");
            
            return exData;
        }
        catch(Exception e) {
            throw new NexusServiceException(e, exData, false);
        }
    } //run()
}
