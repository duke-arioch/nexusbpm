package org.nexusbpm.service.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

public class DatabaseDataSet implements DataSet {

  ResultSet rs;

  public DatabaseDataSet(ResultSet rs) {
    this.rs = rs;
  }

  public void accept(DataVisitor visitor) throws SQLException, DataVisitationException {
    Object[] data = new Object[rs.getMetaData().getColumnCount()];
    for (int i = 0; i < data.length; i++) {
      data[i] = rs.getMetaData().getColumnName(i + 1);
    }
    visitor.visitColumns(data);
    Object[] outData = new Object[rs.getMetaData().getColumnCount()];
    while (rs.next()) {
      for (int i = 0; i < data.length; i++) {
        outData[i] = rs.getObject(i + 1);
      }
      visitor.visitData(outData);
    }
  }
}
