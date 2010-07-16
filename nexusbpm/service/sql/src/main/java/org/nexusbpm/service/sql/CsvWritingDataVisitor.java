package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVPrinter;
import org.nexusbpm.common.DataVisitor;

/**
 *
 * @author Matthew Sandoz
 */
public class CsvWritingDataVisitor implements DataVisitor{

  private CSVPrinter printer;

  public CSVPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(final CSVPrinter printer) {
    this.printer = printer;
  }

  public void visitColumns(final Object[] columns) {
    final String[] toPrint = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      toPrint[i] = columns[i].toString();
    }
    printer.print(toPrint);
  }

  public void visitData(final Object[] data) {
    final String[] toPrint = new String[data.length];
    for (int i = 0; i < data.length; i++) {
      toPrint[i] = data[i].toString();
    }
    printer.print(toPrint);
  }

}
