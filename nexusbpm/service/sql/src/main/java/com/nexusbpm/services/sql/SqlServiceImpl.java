package com.nexusbpm.services.sql;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import com.nexusbpm.common.data.ParameterMap;
import com.nexusbpm.common.io.interfaces.OutputDataflowStreamProvider;
import com.nexusbpm.common.util.ObjectConversionException;
import com.nexusbpm.common.util.ObjectConverter;
import com.nexusbpm.services.AbstractNexusService;
import com.nexusbpm.services.NexusServiceException;

public class SqlServiceImpl extends AbstractNexusService {
    public static final String SQL_STATEMENT_TYPE_AUTO_COMMIT_IGNORE_ERRORS = "Auto Commit and Ignore Errors";
    public static final String SQL_STATEMENT_TYPE_QUERY = "Query";
    public static final String SQL_STATEMENT_TYPE_DML = "Insert, Update, or Delete";
    public static final String SQL_STATEMENT_TYPE_DDL = "Data Definition Language (DDL)";
    public static final String SQL_STATEMENT_TYPE_BATCH_INSERT = "Batch Insert";
    
    public ParameterMap execute(ParameterMap data) throws NexusServiceException {
        Connection connection = null;
        SqlParameterMap sData = new SqlParameterMap(data);
        try {
            Class cl = Class.forName(sData.getJdbcDriverClass());
            Driver driver = (Driver) cl.newInstance();
            Properties properties = new Properties();
            if(sData.getUserName() != null && sData.getUserName().length() > 0) {
                properties.setProperty("user", sData.getUserName());
            }
            if(sData.getPassword() != null && sData.getPassword().length() > 0) {
                properties.setProperty("password", sData.getPassword());
            }
            connection = driver.connect(sData.getJdbcUri(), properties);
            
            if(sData.getStatementType().equals(SQL_STATEMENT_TYPE_BATCH_INSERT)) {
                executeInsert(connection, sData);
            } else if(sData.getStatementType().equals(SQL_STATEMENT_TYPE_AUTO_COMMIT_IGNORE_ERRORS)) {
                executeIgnoringErrors(connection, sData);
            } else {
                execute(connection, sData);
            }
            
//            connection = java.sql.DriverManager.getConnection(
//                    sData.getJdbcUri(), sData.getUserName(), sData.getPassword());
            
//            if (sData.getStatementType().equals(SQL_STATEMENT_TYPE_QUERY)) {
//                executeQuery(connection, sData);
//            } else if (sData.getStatementType().equals(SQL_STATEMENT_TYPE_DML)) {
//                executeUpdate(connection, sData);
//            } else if (sData.getStatementType().equals(SQL_STATEMENT_TYPE_DDL)) {
//                executeDDL(connection, sData);
//            } else if(sData.getStatementType().equals(SQL_STATEMENT_TYPE_BATCH_INSERT)) {
//                executeInsert(connection, sData);
//            }
            
//        } catch (NexusServiceException e) {
//            throw e;
        } catch (Exception e) {
            sData.setError(e.getMessage());
            LOG.error("Error in sql statement", e);
            throw new NexusServiceException("Error in SQL service!", e, sData, false);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(SQLException e) {
                    LOG.debug("Error closing SQL connection", e);
                }
            }
        }
        
        return sData;
    }
    
    protected String[] parse(String sql) {
        List<String> results = new ArrayList<String>();
        StringBuilder b = new StringBuilder();
        int index = 0;
        
        String[] states = {"code", "line comment", "literal string", "c-style comment"};
        final int STATE_CODE = 0;
        final int STATE_LINE_COMMENT = 1;
        final int STATE_STRING = 2;
        final int STATE_C_COMMENT = 3;
        int state = STATE_CODE;
        int depth = 0;
        
        while(index < sql.length()) {
            char c = sql.charAt(index);
            switch(state) {
                // for code: append characters, checking for literal strings, comments, and semicolons
                case STATE_CODE:
                    if(c == '-' && index + 1 < sql.length() && sql.charAt(index + 1) == '-') {
                        // start of a line comment
                        state = STATE_LINE_COMMENT;
                        index += 1;
                    } else if(c == '/' && index + 1 < sql.length() && sql.charAt(index + 1) == '*') {
                        // start of a C-style comment
                        state = STATE_C_COMMENT;
                        index += 1;
                        depth = 1;
                    } else if(c == '\'') {
                        // start of a literal string
                        b.append(c);
                        state = STATE_STRING;
                    } else if(c == ';') {
                        // end of a statement
                        if(b.toString().trim().length() > 0) {
                            results.add(b.toString().trim());
                            b.delete(0, b.length());
                        }
                    } else {
                        b.append(c);
                    }
                    break;
                    
                // for line comments: ignore everything until the end of the line
                case STATE_LINE_COMMENT:
                    if(c == '\r' || c == '\n') {
                        state = STATE_CODE;
                        b.append(c);
                    }
                    break;
                    
                // for strings: read a string looking for escape sequences
                case STATE_STRING:
                    if(c == '\\') {
                        // some form of escape sequence
                        if(index + 1 >= sql.length()) {
                            throw new RuntimeException("End of SQL code reached while parsing literal string");
                        }
                        char c2 = sql.charAt(index + 1);
                        if(c2 >= '0' && c2 <= '9') {
                            // octal escape of the form '\xxx'
                            b.append(sql.substring(index, index + 4));
                            index += 3;
                        } else {
                            // some other escape
                            b.append(c).append(c2);
                            index += 1;
                        }
                    } else if(c == '\'') {
                        // an apostrophe: either a double-apostrophe (which is left alone and interpreted
                        // as a single apostrophe by SQL) or the end of the literal string
                        if(index + 1 < sql.length() && sql.charAt(index + 1) == '\'') {
                            // two adjacent apostrophes are left intact
                            b.append(c).append(c);
                            index += 1;
                        } else {
                            b.append(c);
                            state = STATE_CODE;
                        }
                    } else {
                        // some other character in the literal string
                        b.append(c);
                    }
                    break;
                    
                // for c comments: count nesting depth and ignore until matching end
                case STATE_C_COMMENT:
                    if(c == '/' && index + 1 < sql.length() && sql.charAt(index + 1) == '*') {
                        depth += 1;
                        index += 1;
                    } else if(c == '*' && index + 1 < sql.length() && sql.charAt(index + 1) == '/') {
                        depth -= 1;
                        index += 1;
                    }
                    if(depth == 0) {
                        state = STATE_CODE;
                    }
                    break;
            }
            index += 1;
        }
        
        if(state != STATE_CODE && state != STATE_LINE_COMMENT) {
            throw new RuntimeException("Reached the end of the SQL code while parsing a " + states[state]);
        } else if(b.toString().trim().length() > 0) {
            results.add(b.toString().trim());
        }
        
        while(results.contains("")) {
            results.remove("");
        }
        
        return results.toArray(new String[results.size()]);
    }
    
    protected void executeIgnoringErrors(Connection connection, SqlParameterMap sData) throws SQLException {
        Statement statement = null;
        String[] statements = parse(sData.getSqlCode());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        try {
            connection.setAutoCommit(true);
            statement = connection.createStatement();
            
            int affectedRows = 0;
            
            // loop through and execute the statements
            for(int index = 0; index < statements.length; index++) {
                try {
                    boolean b = statement.execute(statements[index]);
                    
                    if(!b) {
                        int count = statement.getUpdateCount();
                        if(count > 0) {
                            affectedRows += count;
                        }
                    }
                } catch(Exception e) {
                    pw.println("Error ignored executing SQL statement:");
                    pw.println(statements[index]);
                    pw.println(e.toString());
                    pw.println();
                    LOG.debug("Error in SQL statement ignored", e);
                }
            }
            sData.setError(sw.toString());
            sData.setRecordCount(Integer.valueOf(affectedRows));
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch(Exception e) {
                }
            }
        }
    }
    
    protected void execute(Connection connection, SqlParameterMap sData) throws SQLException, IOException {
        Statement statement = null;
        String[] statements = parse(sData.getSqlCode());
        
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.setFetchSize(255);
            
            // if the statement type is a query, then we'll save the first statement that returns a
            // result set, otherwise we sum up the update counts of the statements that return
            // no result sets
            boolean awaitingResults = sData.getStatementType().equals(SQL_STATEMENT_TYPE_QUERY);
            int affectedRows = 0;
            
            // loop through and execute the statements
            for(int index = 0; index < statements.length; index++) {
                boolean b = statement.execute(statements[index]);
                
                if(b) {
                    if(awaitingResults) {
                        // if this statement returned a result set and we're still waiting to save one
                        // then save the result set to a CSV
                        awaitingResults = false;
                        ResultSet rs = null;
                        try {
                            rs = statement.getResultSet();
                            saveResultSet(rs, sData);
                        } finally {
                            if(rs != null) {
                                try {
                                    rs.close();
                                } catch(Exception e) {
                                }
                            }
                        }
                    }
                } else {
                    int count = statement.getUpdateCount();
                    if(count > 0) {
                        affectedRows += count;
                    }
                }
            }
            if(!sData.getStatementType().equals(SQL_STATEMENT_TYPE_QUERY) || awaitingResults) {
                sData.setRecordCount(Integer.valueOf(affectedRows));
            }
            connection.commit();
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch(Exception e) {
                }
            }
        }
    }
    
    protected void saveResultSet(ResultSet results, SqlParameterMap sData) throws SQLException, IOException {
        com.Ostermiller.util.CSVPrinter csvPrinter = null;
        OutputStream ostream = null;
//        ResultSet results = statement.executeQuery(sData.getSqlCode());
        ResultSetMetaData rsmd = results.getMetaData();
//        String filename = sData.getCsvOutput().toASCIIString();
//        while(filename.indexOf('/') >= 0) {
//            filename = filename.substring(filename.indexOf('/') + 1);
//        }
//      OutputDataflowStreamProvider provider = getOutputStreamProvider(sData, filename);
        OutputDataflowStreamProvider provider = sData.getCsvOutput();
        String columnNames[] = new String[rsmd.getColumnCount()];
        for (int column = 1; column <= rsmd.getColumnCount(); column++) {
            columnNames[column - 1] = rsmd.getColumnName(column);
        }
        try {
            ostream = provider.getOutputStream(true);
            csvPrinter = new CSVPrinter(new BufferedWriter(new OutputStreamWriter(ostream)));
            csvPrinter.println(columnNames);
            String columnValues[] = new String[rsmd.getColumnCount()];
            int recordCount = 0;
            while (results.next()) {
                for (int column = 1; column <= rsmd.getColumnCount(); column++) {
                    Object value = null;
                    switch(rsmd.getColumnType(column)) {
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
                    if(value instanceof Date || value instanceof URI) {
                        try {
                            Object o = ObjectConverter.convert(value, String.class);
                            if(o != null) {
                                value = o;
                            }
                        } catch(ObjectConversionException e) {
                            LOG.debug("error converting " + value, e);
                        }
                    }
                    columnValues[column - 1] = value == null ? "" : value.toString();
                }
                csvPrinter.println(columnValues);
                recordCount++;
            }
            sData.setRecordCount(Integer.valueOf(recordCount));
        } finally {
            if(csvPrinter != null) {
                try {
                    csvPrinter.flush();
                } catch(Exception e) {
                }
                try {
                    csvPrinter.close();
                } catch(Exception e) {
                }
            }
            if(ostream != null) {
                try {
                    ostream.close();
                } catch(Exception e) {
                }
            }
        }
    }
    
    protected void executeInsert(Connection connection, SqlParameterMap data) throws SQLException, IOException, ObjectConversionException {
        Map<String, String> mappings = new HashMap<String, String>();
        Map<String, Class> types =  new HashMap<String, Class>();
        String[] csvColumns;
        CSVParser parser = null;
        
        // first read in the data mappings
        StringBuffer buffer = new StringBuffer(data.getDataMappings());
        
        while(buffer.length() > 0) {
            String csvName = unescape(buffer);
            String dbName = unescape(buffer);
            
            if(csvName == null || dbName == null ||
                    csvName.length() == 0 || dbName.length() == 0) {
                continue;
            }
            
            mappings.put(csvName, dbName);
        }
        
        try {
            // read in the column names from the CSV
            parser = new CSVParser(data.getCsvInput().getInputStream(true));
            csvColumns = parser.getLine();
            
            String sql = "insert into " + data.getTableName() + " (";
            String values = ") values (";
            
            for(int index = 0; index < csvColumns.length; index++) {
                if(mappings.containsKey(csvColumns[index])) {
                    sql += mappings.get(csvColumns[index]) + ", ";
                    values += "?, ";
                }
            }
            
            if(sql.endsWith(", ")) {
                sql = sql.substring(0, sql.length() - 2);
                values = values.substring(0, values.length() - 2);
            } else {
                throw new RuntimeException("No column name mappings matched the input CSV!");
            }
            
            connection.setAutoCommit(false);
            
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("select * from " + data.getTableName() + " where 1 = 2");
            ResultSetMetaData metadata = r.getMetaData();
            for(int index = 1; index <= metadata.getColumnCount(); index++) {
                String name = metadata.getColumnName(index);
                String cname = null;
                switch(metadata.getColumnType(index)) {
                    case Types.DATE:
                        cname = "java.sql.Date";
                        break;
                    case Types.TIME:
                        cname = "java.sql.Time";
                        break;
                    case Types.TIMESTAMP:
                        cname = "java.sql.Timestamp";
                        break;
                    default:
                        cname = metadata.getColumnClassName(index);
                }
                try {
                    Class c = Class.forName(cname);
                    types.put(name, c);
                } catch(Exception e) {
                    LOG.debug("Column " + index + " (" + name + ") is of type " + cname, e);
                }
            }
            
            PreparedStatement stmt = connection.prepareStatement(sql + values + ")");
            
            String[] row = parser.getLine();
            
            while(row != null) {
                int column = 1;
                for(int index = 0; index < csvColumns.length; index++) {
                    String dbcolumn = mappings.get(csvColumns[index]);
                    if(dbcolumn != null) {
                        Class type = getType(dbcolumn, types);
                        if(type == null) {
                            throw new ObjectConversionException("CSV column '" + csvColumns[index] +
                                    "' maps to database column '" + dbcolumn +
                                    "' but the database column does not exist on table '" +
                                    data.getTableName() + "'");
                        }
                        Object value = row.length > index ? row[index] : null;
                        stmt.setObject(column, ObjectConverter.convert(value, type));
                        column += 1;
                    }
                }
                stmt.execute();
                
                row = parser.getLine();
            }
            
            connection.commit();
        } finally {
            if(parser != null) {
                try {
                    parser.close();
                } catch(Exception e) {
                }
            }
        }
    }
    
    public Class getType(String key, Map<String, Class> map) {
        Class type = map.get(key);
        if(type == null) {
            for(String k : map.keySet()) {
                if(k.equalsIgnoreCase(key)) {
                    type = map.get(k);
                    break;
                }
            }
        }
        return type;
    }
    
    public String unescape(StringBuffer buffer) {
        if(buffer.length() < 1) {
        } else if(buffer.charAt(0) == ',') {
            buffer.deleteCharAt(0);
        } else {
            StringBuffer result = new StringBuffer();
            
            while(buffer.length() > 0 && buffer.charAt(0) != ',') {
                if(buffer.charAt(0) == '\\') {
                    buffer.deleteCharAt(0);
                }
                if(buffer.length() == 0) {
                    break;
                }
                result.append(buffer.charAt(0));
                buffer.deleteCharAt(0);
            }
            if(buffer.length() > 0) {
                buffer.deleteCharAt(0);
            }
            
            return result.toString();
        }
        return null;
    }
}
