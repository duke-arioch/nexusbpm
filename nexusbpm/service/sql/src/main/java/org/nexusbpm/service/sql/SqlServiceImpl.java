package org.nexusbpm.service.sql;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import java.util.logging.Level;
import org.nexusbpm.service.NexusServiceException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceRequest;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SqlServiceImpl implements NexusService {

  public static final Logger LOGGER = LoggerFactory.getLogger(SqlServiceImpl.class);

  @Override
  public SqlServiceResponse execute(final NexusServiceRequest genericWorkItem) throws NexusServiceException {
    final SqlServiceResponse retval = new SqlServiceResponse();
    final SqlServiceRequest workItem = (SqlServiceRequest) genericWorkItem;
    /*
     * process:
     *
     * 1. split compound sql into simple sqls
     * 2. inputcsv or data ==> context
     * 3. apply templating to each sql, creating a parameter list and '?' rendered parms from context
     * 4. execute sql ==> output csv
     *
     */

    CSVParser parser = null;
    CSVPrinter printer = null;
    Connection connection;
    try {
      connection = DatabaseUtils.getConnection(workItem);
    } catch (Exception ex) {
      retval.setErr(ex.getMessage());
      LOGGER.error("Error in sql connection", ex);
      throw new NexusServiceException("Error in SQL service!", ex);
    }
    try {
      parser = getParser(workItem);
      printer = getPrinter(workItem);
      final CsvWritingDataVisitor outvisitor = new CsvWritingDataVisitor();
      outvisitor.setPrinter(printer);
      final DataSet dataSet = (parser == null) ? new MapDataSet(new HashMap()) : new CsvDataSet(parser);
      final QueryExecutingDataVisitor visitor = new QueryExecutingDataVisitor();
      visitor.setConnection(connection);
      visitor.setResultSetVisitor(outvisitor);
      final String[] statements = DatabaseUtils.parse(workItem.getSqlCode());
      for (String sql : statements) {
        visitor.setSql(sql);
        dataSet.accept(visitor);
      }
    } catch (Exception e) {
      retval.setErr(e.getMessage());
      LOGGER.error("Error in sql statement", e);
      throw new NexusServiceException("Error in SQL service!", e);
    } finally {
      DbUtils.commitAndCloseQuietly(connection);
      try {
        if (parser != null) {
          parser.close();
        }
      } catch (IOException e) {
        LOGGER.warn("couldnt close parser file");
      }
      try {
        if (printer != null) {
          printer.close();
        }
      } catch (IOException e) {
        LOGGER.warn("couldnt close printer file");
      }
    }
    return retval;
  }

  protected CSVPrinter getPrinter(final SqlServiceRequest workItem) throws FileSystemException {
    final FileObject file = VFS.getManager().resolveFile(workItem.getCsvOutputUri().toString());
    final OutputStream ostream = file.getContent().getOutputStream();
    return new CSVPrinter(ostream);
  }

  protected CSVParser getParser(final SqlServiceRequest workItem) throws FileSystemException {
    CSVParser retval = null;
    if (workItem.getCsvInputUri() != null) {
      final FileObject file = VFS.getManager().resolveFile(workItem.getCsvInputUri().toString());
      final InputStream istream = file.getContent().getInputStream();
      retval = new CSVParser(istream);
    }
    return retval;
  }

  protected void executeIgnoringErrors(final Connection connection, final SqlServiceRequest workItem, final SqlServiceResponse response) throws SQLException {
    Statement statement = null;
    final String[] statements = DatabaseUtils.parse(workItem.getSqlCode());
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);

    try {
      connection.setAutoCommit(true);
      statement = connection.createStatement();

      int affectedRows = 0;

      // loop through and execute the statements
      for (int index = 0; index < statements.length; index++) {
        try {
          final boolean success = statement.execute(statements[index]);

          if (!success) {
            final int count = statement.getUpdateCount();
            if (count > 0) {
              affectedRows += count;
            }
          }
        } catch (Exception e) {
          printWriter.println("Error ignored executing SQL statement:");
          printWriter.println(statements[index]);
          printWriter.println(e.toString());
          printWriter.println();
          LOGGER.debug("Error in SQL statement ignored", e);
        }
      }
      response.setErr(stringWriter.toString());
      response.setRecordCount(affectedRows);
    } finally {
      statement.close();
      DbUtils.commitAndCloseQuietly(connection);
    }
  }
}
