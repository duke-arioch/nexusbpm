package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import java.io.IOException;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

public class CsvDataSet implements DataSet {

  CSVParser parser;

  public CsvDataSet(CSVParser parser) {
    this.parser = parser;
  }

  @Override
  public void accept(DataVisitor visitor) throws IOException, DataVisitationException {
    String[] data = parser.getLine();
    if (data != null) {
      visitor.visitColumns(data);
      while ((data = parser.getLine()) != null) {
        visitor.visitData(data);
      }
    }

  }
}
