package org.nexusbpm.service.sql;

import com.Ostermiller.util.CSVPrinter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.nexusbpm.common.data.ObjectConversionException;
import org.nexusbpm.common.data.ObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Nathan Rose
 * @author Matthew Sandoz
 */
public final class DatabaseUtils {

  private static final String[] states = {"code", "line comment", "literal string", "c-style comment"};
  private static final int STATE_CODE = 0;
  private static final int STATE_LINE_COMMENT = 1;
  private static final int STATE_STRING = 2;
  private static final int STATE_C_COMMENT = 3;
  public static final Logger LOGGER = LoggerFactory.getLogger(SqlServiceImpl.class);

  private DatabaseUtils() {
  }

  public static PreparedStatement prepareStatement(final Connection connection, final String sqlStatement, final Map values) throws IOException, SQLException {
    final EventCartridge cartridge = new EventCartridge();
    final SqlReferenceInsertionEventHandler handler = new SqlReferenceInsertionEventHandler();

    cartridge.addReferenceInsertionEventHandler(handler);
    final VelocityContext context = new VelocityContext(values);
    cartridge.attachToContext(context);

    final StringWriter writer = new StringWriter();
    final boolean result = Velocity.evaluate(context, writer, "SQL Statement", sqlStatement);
    if (result) {
      final PreparedStatement statement = connection.prepareStatement(writer.toString());
      LOGGER.debug("sql>>" + writer.toString());
      for (int i = 0; i < handler.getVariables().size(); i++) {
        statement.setObject(i + 1, handler.getVariables().get(i));
        LOGGER.debug("var>>" + i + ":" + handler.getVariables().get(i));
      }
      return statement;
    } else {
      throw new SQLException("Unable to create template for " + writer.toString());
    }
  }

  public static Connection getConnection(final SqlServiceRequest data) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
    final Class clazz = Class.forName(data.getJdbcDriverClass());
    final Driver driver = (Driver) clazz.newInstance();
    final Properties properties = new Properties();
    if (data.getUserName() != null && data.getUserName().length() > 0) {
      properties.setProperty("user", data.getUserName());
    }
    if (data.getPassword() != null && data.getPassword().length() > 0) {
      properties.setProperty("password", data.getPassword());
    }
    final Connection connection = driver.connect(data.getJdbcUri().toString(), properties);
    connection.setAutoCommit(false);
    return connection;
  }

  public static String[] parse(final String sql) {
    final List<String> results = new ArrayList<String>();
    final StringBuilder builder = new StringBuilder();
    int index = 0;
    int depth = 0;
    int state = STATE_CODE;

    while (index < sql.length()) {
      final char c = sql.charAt(index);
      switch (state) {
        // for code: append characters, checking for literal strings, comments, and semicolons
        case STATE_CODE:
          if (c == '-' && index + 1 < sql.length() && sql.charAt(index + 1) == '-') {
            // start of a line comment
            state = STATE_LINE_COMMENT;
            index += 1;
          } else if (c == '/' && index + 1 < sql.length() && sql.charAt(index + 1) == '*') {
            // start of a C-style comment
            state = STATE_C_COMMENT;
            index += 1;
            depth = 1;
          } else if (c == '\'') {
            // start of a literal string
            builder.append(c);
            state = STATE_STRING;
          } else if (c == ';') {
            // end of a statement
            if (builder.toString().trim().length() > 0) {
              results.add(builder.toString().trim());
              builder.delete(0, builder.length());
            }
          } else {
            builder.append(c);
          }
          break;

        // for line comments: ignore everything until the end of the line
        case STATE_LINE_COMMENT:
          if (c == '\r' || c == '\n') {
            state = STATE_CODE;
            builder.append(c);
          }
          break;

        // for strings: read a string looking for escape sequences
        case STATE_STRING:
          if (c == '\\') {
            // some form of escape sequence
            if (index + 1 >= sql.length()) {
              throw new RuntimeException("End of SQL code reached while parsing literal string");
            }
            final char c2 = sql.charAt(index + 1);
            if (c2 >= '0' && c2 <= '9') {
              // octal escape of the form '\xxx'
              builder.append(sql.substring(index, index + 4));
              index += 3;
            } else {
              // some other escape
              builder.append(c).append(c2);
              index += 1;
            }
          } else if (c == '\'') {
            // an apostrophe: either a double-apostrophe (which is left alone and interpreted
            // as a single apostrophe by SQL) or the end of the literal string
            if (index + 1 < sql.length() && sql.charAt(index + 1) == '\'') {
              // two adjacent apostrophes are left intact
              builder.append(c).append(c);
              index += 1;
            } else {
              builder.append(c);
              state = STATE_CODE;
            }
          } else {
            // some other character in the literal string
            builder.append(c);
          }
          break;

        // for c comments: count nesting depth and ignore until matching end
        case STATE_C_COMMENT:
          if (c == '/' && index + 1 < sql.length() && sql.charAt(index + 1) == '*') {
            depth += 1;
            index += 1;
          } else if (c == '*' && index + 1 < sql.length() && sql.charAt(index + 1) == '/') {
            depth -= 1;
            index += 1;
          }
          if (depth == 0) {
            state = STATE_CODE;
          }
          break;
      }
      index += 1;
    }

    if (state != STATE_CODE && state != STATE_LINE_COMMENT) {
      throw new RuntimeException("Reached the end of the SQL code while parsing a " + states[state]);
    } else if (builder.toString().trim().length() > 0) {
      results.add(builder.toString().trim());
    }

    while (results.contains("")) {
      results.remove("");
    }

    return results.toArray(new String[results.size()]);
  }

  public static String unescape(final StringBuffer buffer) {
    if (buffer.length() <= 1 && buffer.charAt(0) == ',') {
      buffer.deleteCharAt(0);
    } else {
      final StringBuffer result = new StringBuffer();

      while (buffer.length() > 0 && buffer.charAt(0) != ',') {
        if (buffer.charAt(0) == '\\') {
          buffer.deleteCharAt(0);
        }
        if (buffer.length() == 0) {
          break;
        }
        result.append(buffer.charAt(0));
        buffer.deleteCharAt(0);
      }
      if (buffer.length() > 0) {
        buffer.deleteCharAt(0);
      }

      return result.toString();
    }
    return null;
  }

/*  private static void saveResultSet(final ResultSet results, final SqlServiceRequest sData, final SqlServiceResponse response) throws SQLException, IOException, ObjectConversionException {
    com.Ostermiller.util.CSVPrinter csvPrinter = null;
    OutputStream ostream = null;
    final ResultSetMetaData rsmd = results.getMetaData();
    final FileObject file = VFS.getManager().resolveFile(sData.getRequestId() + ".csv");
    String columnNames[] = new String[rsmd.getColumnCount()];
    for (int column = 1; column <= rsmd.getColumnCount(); column++) {
      columnNames[column - 1] = rsmd.getColumnName(column);
    }
    try {
      ostream = file.getContent().getOutputStream();
      csvPrinter = new CSVPrinter(new BufferedWriter(new OutputStreamWriter(ostream)));
      csvPrinter.println(columnNames);
      String columnValues[] = new String[rsmd.getColumnCount()];
      int recordCount = 0;
      while (results.next()) {
        for (int column = 1; column <= rsmd.getColumnCount(); column++) {
          Object value = null;
          switch (rsmd.getColumnType(column)) {
            case Types.DATE:
              value = results.getDate(column);
              break;
            case Types.TIME:
              value = results.getTime(column);
              break;
            case Types.TIMESTAMP:
              value = results.getTimestamp(column);
              break;
            default:
              value = results.getObject(column);
          }
          if (value instanceof Date || value instanceof URI) {
            final Object o = ObjectConverter.convert(value, String.class);
            if (o != null) {
              value = o;
            }
          }
          columnValues[column - 1] = value == null ? "" : value.toString();
        }
        csvPrinter.println(columnValues);
        recordCount++;
      }
      response.setRecordCount(recordCount);
    } finally {
      if (csvPrinter != null) {
        try {
          csvPrinter.flush();
        } catch (Exception e) {
        }
        try {
          csvPrinter.close();
        } catch (Exception e) {
        }
      }
      if (ostream != null) {
        try {
          ostream.close();
        } catch (Exception e) {
        }
      }
    }
  }*/
}
