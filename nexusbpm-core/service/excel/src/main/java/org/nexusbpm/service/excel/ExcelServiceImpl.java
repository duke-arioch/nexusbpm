package org.nexusbpm.service.excel;

import java.net.URI;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.nexusbpm.service.NexusServiceRequest;
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
  private static final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);

  @Override
  public ExcelServiceResponse execute(final NexusServiceRequest inData) throws NexusServiceException {
    final ExcelServiceRequest exData = (ExcelServiceRequest) inData;

    final URI template = exData.getTemplateFile();
    final URI inputData = exData.getDataFile();
    final URI output = exData.getOutputFile();
    try {
    final FileSystemManager fsManager = VFS.getManager();

    // The Excel service must have a CSV file from which to draw data.
    if (inputData == null) {
      throw new NexusServiceException("There is no data to export.");
    }

    // The Excel service must have an output file to put the data in.
    if (output == null) {
      throw new NexusServiceException("There is no Excel file to insert data into.");
    }

      final String sheetName = exData.getSheetName();

      // Initialize an object with a common interface, regardless of whether we
      // are using Extentech's ExtenXLS library or Apache's POI library.
      final ExcelPOIObject excelObject = new ExcelPOIObject(fsManager.resolveFile(template.toString()), sheetName);

      excelObject.setOutputStreamProvider(fsManager.resolveFile(template.toString()));
      excelObject.initialize();

      final String anchorString = exData.getExcelAnchor();
      final boolean skipHeader = exData.isSkipHeader();
      final int rowLimit = exData.getRowLimit();
      final int colLimit = exData.getColumnLimit();

      LOGGER.debug("Inserting into sheet " + sheetName + " at " + anchorString + " as " + output.toString());
      final int rows = excelObject.insertTableAtAnchor(
              fsManager.resolveFile(inputData.toString()), anchorString, skipHeader, rowLimit, colLimit);
      excelObject.save();
      LOGGER.debug("Finished adding " + rows + " rows into spreadsheet");

    } catch (Exception e) {
      throw new NexusServiceException("Exception in Excel service", e);
    }
    return new ExcelServiceResponse();
  } //run()
}
