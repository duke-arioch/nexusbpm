package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVPrinter;
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

  public void visitColumns(final Object[] columns) {
    for (int i = 0; i < columns.length; i++) {
      printer.print(columns[i].toString());
    }
  }

  public void visitData(final Object[] data) {
    for (int i = 0; i < data.length; i++) {
      printer.print(data[i].toString());
    }
  }
}
