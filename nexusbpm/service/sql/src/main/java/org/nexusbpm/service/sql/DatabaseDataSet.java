package org.nexusbpm.service.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

public class DatabaseDataSet implements DataSet {

  private final transient ResultSet resultSet;

  public DatabaseDataSet(final ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  public void accept(final DataVisitor visitor) throws DataVisitationException {
    try {
      Object[] data = new Object[resultSet.getMetaData().getColumnCount()];
      for (int i = 0; i < data.length; i++) {
        data[i] = resultSet.getMetaData().getColumnName(i + 1);
      }
      visitor.visitColumns(data);
      final int columnCount = resultSet.getMetaData().getColumnCount();
      final Object[] outData = new Object[columnCount];
      while (resultSet.next()) {
        for (int i = 0; i < data.length; i++) {
          outData[i] = resultSet.getObject(i + 1);
        }
        visitor.visitData(outData);
      }
    } catch (SQLException sqle) {
      throw new DataVisitationException(sqle);
    }
  }
}
