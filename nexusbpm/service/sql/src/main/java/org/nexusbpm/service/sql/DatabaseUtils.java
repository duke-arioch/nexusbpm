package org.nexusbpm.service.sql;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Nathan Rose
 * @author Matthew Sandoz
 */
public final class DatabaseUtils {

  private static final String[] STATES = {"code", "line comment", "literal string", "c-style comment"};
  private static final int ST_CODE = 0;
  private static final int ST_LINE_COMMENT = 1;
  private static final int ST_STRING = 2;
  private static final int ST_C_COMMENT = 3;
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
    int state = ST_CODE;

    while (index < sql.length()) {
      final char chr = sql.charAt(index);
      switch (state) {
        // for code: append characters, checking for literal strings, comments, and semicolons
        case ST_CODE:
          if (chr == '-' && index + 1 < sql.length() && sql.charAt(index + 1) == '-') {
            // start of a line comment
            state = ST_LINE_COMMENT;
            index += 1;
          } else if (chr == '/' && index + 1 < sql.length() && sql.charAt(index + 1) == '*') {
            // start of a C-style comment
            state = ST_C_COMMENT;
            index += 1;
            depth = 1;
          } else if (chr == '\'') {
            // start of a literal string
            builder.append(chr);
            state = ST_STRING;
          } else if (chr == ';') {
            // end of a statement
            if (builder.toString().trim().length() > 0) {
              results.add(builder.toString().trim());
              builder.delete(0, builder.length());
            }
          } else {
            builder.append(chr);
          }
          break;

        // for line comments: ignore everything until the end of the line
        case ST_LINE_COMMENT:
          if (chr == '\r' || chr == '\n') {
            state = ST_CODE;
            builder.append(chr);
          }
          break;

        // for strings: read a string looking for escape sequences
        case ST_STRING:
          if (chr == '\\') {
            // some form of escape sequence
            if (index + 1 >= sql.length()) {
              throw new RuntimeException("End of SQL code reached while parsing literal string");
            }
            final char chr2 = sql.charAt(index + 1);
            if (chr2 >= '0' && chr2 <= '9') {
              // octal escape of the form '\xxx'
              builder.append(sql.substring(index, index + 4));
              index += 3;
            } else {
              // some other escape
              builder.append(chr).append(chr2);
              index += 1;
            }
          } else if (chr == '\'') {
            // an apostrophe: either a double-apostrophe (which is left alone and interpreted
            // as a single apostrophe by SQL) or the end of the literal string
            if (index + 1 < sql.length() && sql.charAt(index + 1) == '\'') {
              // two adjacent apostrophes are left intact
              builder.append(chr).append(chr);
              index += 1;
            } else {
              builder.append(chr);
              state = ST_CODE;
            }
          } else {
            // some other character in the literal string
            builder.append(chr);
          }
          break;

        // for c comments: count nesting depth and ignore until matching end
        case ST_C_COMMENT:
          if (chr == '/' && index + 1 < sql.length() && sql.charAt(index + 1) == '*') {
            depth += 1;
            index += 1;
          } else if (chr == '*' && index + 1 < sql.length() && sql.charAt(index + 1) == '/') {
            depth -= 1;
            index += 1;
          }
          if (depth == 0) {
            state = ST_CODE;
          }
          break;
        default: throw new IllegalArgumentException("Invalid parsing");
      }
      index += 1;
    }

    if (state != ST_CODE && state != ST_LINE_COMMENT) {
      throw new RuntimeException("Reached the end of the SQL code while parsing a " + STATES[state]);
    } else if (builder.toString().trim().length() > 0) {
      results.add(builder.toString().trim());
    }

    while (results.contains("")) {
      results.remove("");
    }

    return results.toArray(new String[results.size()]);
  }
  
}
