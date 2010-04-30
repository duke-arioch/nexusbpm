package org.nexusbpm.service.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matthew Sandoz
 */
public class QueryExecutingDataVisitor implements DataVisitor {

  private String[] columns;
  private long affectedRecords = 0;
  private DataVisitor resultSetVisitor;
  private String sql;
  private Connection connection;
  private static final Logger logger = LoggerFactory.getLogger(QueryExecutingDataVisitor.class);

  public void visitColumns(Object[] columns) {
    this.columns = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      this.columns[i] = columns[i].toString();
    }
  }

  @Override
  public void visitData(Object[] data) throws DataVisitationException{
    try {
      Map<String, Object> values = new HashMap<String, Object>();
      for (int i = 0; i < data.length; i++) {
        values.put(columns[i], data[i]);
      }
      PreparedStatement statement = DatabaseUtils.prepareStatement(connection, sql, values);
      boolean b = statement.execute();
      do {
        if (b) {
          ResultSet rs = statement.getResultSet();
          DatabaseDataSet ds = new DatabaseDataSet(rs);
          ds.accept(resultSetVisitor);
        } else {
          affectedRecords += statement.getUpdateCount();
        }
      } while (b = (statement.getMoreResults() || statement.getUpdateCount() != -1));
    } catch (IOException ex) {
      throw new DataVisitationException("Failed to visit data", ex);
    } catch (SQLException ex) {
      throw new DataVisitationException("Failed to visit data", ex);
    }
  }

  public long getAffectedRecords() {
    return affectedRecords;
  }

  public void setAffectedRecords(long affectedRecords) {
    this.affectedRecords = affectedRecords;
  }

  public DataVisitor getResultSetVisitor() {
    return resultSetVisitor;
  }

  public void setResultSetVisitor(DataVisitor resultSetVisitor) {
    this.resultSetVisitor = resultSetVisitor;
  }

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
