package org.nexusbpm.service.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

/**
 *
 * @author Matthew Sandoz
 */
public class QueryExecutingDataVisitor implements DataVisitor {

  private transient List<String> columns;
  private long affectedRecords = 0;
  private DataVisitor resultSetVisitor;
  private String sql;
  private Connection connection;

  public void visitColumns(final List columns) {
    this.columns = new ArrayList<String>();
    for (int i = 0; i < columns.size(); i++) {
      this.columns.add(columns.get(i).toString());
    }
  }

  @Override
  public void visitData(final List data) throws DataVisitationException{
    try {
      final Map<String, Object> values = new HashMap<String, Object>();
      for (int i = 0; i < data.size(); i++) {
        values.put(columns.get(i), data.get(i));
      }
      final PreparedStatement statement = DatabaseUtils.prepareStatement(connection, sql, values);
      boolean success = statement.execute();
      do {
        if (success) {
          final ResultSet resultSet = statement.getResultSet();
          DatabaseDataSet.getInstance(resultSet).accept(resultSetVisitor);
        } else {
          affectedRecords += statement.getUpdateCount();
        }
      } while (success = (statement.getMoreResults() || statement.getUpdateCount() != -1));
    } catch (IOException ex) {
      throw new DataVisitationException("Failed to visit data", ex);
    } catch (SQLException ex) {
      throw new DataVisitationException("Failed to visit data", ex);
    }
  }

  public long getAffectedRecords() {
    return affectedRecords;
  }

  public void setAffectedRecords(final long affectedRecords) {
    this.affectedRecords = affectedRecords;
  }

  public DataVisitor getResultSetVisitor() {
    return resultSetVisitor;
  }

  public void setResultSetVisitor(final DataVisitor resultSetVisitor) {
    this.resultSetVisitor = resultSetVisitor;
  }

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(final Connection connection) {
    this.connection = connection;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(final String sql) {
    this.sql = sql;
  }
}
