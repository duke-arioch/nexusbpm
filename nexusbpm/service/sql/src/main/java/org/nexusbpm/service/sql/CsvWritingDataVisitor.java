package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVPrinter;
import java.util.List;
import org.nexusbpm.common.DataVisitor;

/**
 *
 * @author Matthew Sandoz
 */
public class CsvWritingDataVisitor implements DataVisitor {

  private CSVPrinter printer;

  public CSVPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(final CSVPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void visitColumns(final List columns) {
    for (int i = 0; i < columns.size(); i++) {
      printer.print(columns.get(i).toString());
    }
  }

  @Override
  public void visitData(final List data) {
    for (int i = 0; i < data.size(); i++) {
      printer.print(data.get(i).toString());
    }
  }
}
