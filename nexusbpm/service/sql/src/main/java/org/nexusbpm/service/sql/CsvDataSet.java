package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVParser;
import java.io.IOException;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

public class CsvDataSet implements DataSet {

  private transient final CSVParser parser;

  public CsvDataSet(final CSVParser parser) {
    this.parser = parser;
  }

  @Override
  public void accept(final DataVisitor visitor) throws DataVisitationException {
    try {
      final String[] columnData = parser.getLine();
      if (columnData != null) {
        visitor.visitColumns(columnData);
        for (String[] lineData = parser.getLine(); lineData != null; lineData = parser.getLine()) {
          visitor.visitData(lineData);
        }
      }
    } catch (IOException ioe) {
      throw new DataVisitationException(ioe);
    }
  }
}
