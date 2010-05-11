package org.nexusbpm.service.sql;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import org.nexusbpm.service.NexusServiceException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.velocity.app.VelocityEngine;
import org.nexusbpm.common.data.ObjectConversionException;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceRequest;
import org.nexusbpm.service.NexusServiceResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SqlServiceImpl implements NexusService {

  public static final Logger logger = LoggerFactory.getLogger(SqlServiceImpl.class);
  public VelocityEngine velocityEngine;

  @Override
  public SqlServiceResponse execute(NexusServiceRequest genericWorkItem) throws NexusServiceException {
    SqlServiceResponse retval = new SqlServiceResponse();
    SqlServiceRequest workItem = (SqlServiceRequest) genericWorkItem;
    /*
     * process:
     *
     * 1. split compound sql into simple sqls
     * 2. inputcsv or data ==> context
     * 3. apply templating to each sql, creating a parameter list and '?' rendered parms from context
     * 4. execute sql ==> output csv
     *
     */

    Connection connection = null;
    CSVParser parser = null;
    CSVPrinter printer = null;
    try {
      parser = getParser(workItem);
      printer = getPrinter(workItem);
      connection = DatabaseUtils.getConnection(workItem);
      CsvWritingDataVisitor outvisitor = new CsvWritingDataVisitor();
      outvisitor.setPrinter(printer);
      String[] line = null;
      DataSet ds;
      if (parser == null) {
        ds = new MapDataSet(new Hashtable());
      } else {
        ds = new CsvDataSet(parser);
      }
      QueryExecutingDataVisitor visitor = new QueryExecutingDataVisitor();
      visitor.setConnection(connection);
      visitor.setResultSetVisitor(outvisitor);
      String[] statements = DatabaseUtils.parse(workItem.getSqlCode());
      for (String sql : statements) {
        visitor.setSql(sql);
        ds.accept(visitor);
      }
    } catch (Exception e) {
      retval.setErr(e.getMessage());
      logger.error("Error in sql statement", e);
      throw new NexusServiceException("Error in SQL service!", e);
    } finally {
      DbUtils.commitAndCloseQuietly(connection);
      try {
        if (parser != null) {
          parser.close();
        }
      } catch (IOException e) {
        logger.warn("couldnt close parser file");
      }
      try {
        if (printer != null) {
          printer.close();
        }
      } catch (IOException e) {
        logger.warn("couldnt close printer file");
      }
      return retval;
    }

  }

  protected CSVPrinter getPrinter(SqlServiceRequest workItem) throws FileSystemException {
    CSVPrinter retval = null;
      FileObject file = VFS.getManager().resolveFile(workItem.getCsvOutputUri().toString());
      OutputStream ostream = file.getContent().getOutputStream();
      retval = new CSVPrinter(ostream);
    return retval;
  }

  protected CSVParser getParser(SqlServiceRequest workItem) throws FileSystemException {
    CSVParser retval = null;
    if (workItem.getCsvInputUri() != null) {
      FileObject file = VFS.getManager().resolveFile(workItem.getCsvInputUri().toString());
      InputStream istream = file.getContent().getInputStream();
      retval = new CSVParser(istream);
    }
    return retval;
  }

  protected void executeIgnoringErrors(Connection connection, SqlServiceRequest workItem, SqlServiceResponse response) throws SQLException {
    Statement statement = null;
    String[] statements = DatabaseUtils.parse(workItem.getSqlCode());
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    try {
      connection.setAutoCommit(true);
      statement = connection.createStatement();

      int affectedRows = 0;

      // loop through and execute the statements
      for (int index = 0; index < statements.length; index++) {
        try {
          boolean b = statement.execute(statements[index]);

          if (!b) {
            int count = statement.getUpdateCount();
            if (count > 0) {
              affectedRows += count;
            }
          }
        } catch (Exception e) {
          pw.println("Error ignored executing SQL statement:");
          pw.println(statements[index]);
          pw.println(e.toString());
          pw.println();
          logger.debug("Error in SQL statement ignored", e);
        }
      }
      response.setErr(sw.toString());
      response.setRecordCount(affectedRows);
    } finally {
      DbUtils.commitAndCloseQuietly(connection);
    }
  }

//  protected void execute(PreparedStatement statement, String type, SqlServiceRequest workItem, SqlServiceResponse response) throws SQLException, IOException, ObjectConversionException {
//    try {
//      // if the statement type is a query, then we'll save the first statement that returns a
//      // result set, otherwise we sum up the update counts of the statements that return
//      // no result sets
//      boolean awaitingResults = type.equals(SqlServiceRequest.QUERY_SQL_TYPE);
//      int affectedRows = 0;
//
//      // loop through and execute the statements
//      boolean b = statement.execute();
//
//      if (b) {
//        if (awaitingResults) {
//          // if this statement returned a result set and we're still waiting to save one
//          // then save the result set to a CSV
//          awaitingResults = false;
//          ResultSet rs = null;
//          rs = statement.getResultSet();
////            DatabaseUtils.saveResultSet(rs, sData);
//        }
//      } else {
//        int count = statement.getUpdateCount();
//        if (count > 0) {
//          affectedRows += count;
//        }
//      }
//      response.setRecordCount(affectedRows);
//    } finally {
//    }
//  }

//  protected void executeInsert(PreparedStatement statement, CSVParser parser, List<String> parameters) throws SQLException, IOException, ObjectConversionException {
//    String[] csvColumns;
//    try {
//      if (parser != null) {
//        csvColumns = parser.getLine();
//        if (csvColumns.length < 1) {
//          return;
//        }
//        String[] row = parser.getLine();
//        while (row != null) {
//          for (int index = 0; index < csvColumns.length; index++) {
//            Object value = row.length > index ? row[index] : null;
//            statement.setObject(index, value);
//          }
//          statement.execute();
//          row = parser.getLine();
//        }
//      } else {
//        statement.execute();
//      }
//    } finally {
//      if (parser != null) {
//        try {
//          parser.close();
//        } catch (Exception e) {
//        }
//      }
//      return;
//    }
//  }

  public void setVelocityEngine(VelocityEngine velocityEngine) {
    this.velocityEngine = velocityEngine;
  }
}
