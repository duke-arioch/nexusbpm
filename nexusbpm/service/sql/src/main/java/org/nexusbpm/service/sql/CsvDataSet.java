package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
        final List columns = Arrays.asList(columnData);
        visitor.visitColumns(columns);
        for (String[] lineData = parser.getLine(); lineData != null; lineData = parser.getLine()) {
          final List values = Arrays.asList(lineData);
          visitor.visitData(values);
        }
      }
    } catch (IOException ioe) {
      throw new DataVisitationException(ioe);
    }
  }
}
