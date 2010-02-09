package com.nexusbpm.services.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.nexusbpm.common.NexusTestCase;

//TODO 

public class SqlServiceTest extends NexusTestCase {
    public void testQuery() throws Exception {
        SqlServiceImpl c = new SqlServiceImpl();
        SqlParameterMap data = new SqlParameterMap();
        data.setJdbcDriverClass("org.postgresql.Driver");
        data.setJdbcUri("jdbc:postgresql:jbpm");
        data.setUserName("jbpm");
        data.setPassword("jbpm");
        data.setSqlCode("select * from jbpm_id_user");
        data.setProcessName("test1");
        data.setProcessVersion("1");
        data.setRequestId("1000");
        data.setCsvOutput(DataflowStreamProviderFactory.getInstance().getOutputProvider(
                "nexus/SQLService",
                "test.csv",
                data.getProcessName(),
                data.getProcessVersion(),
                data.getRequestId()));
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_QUERY);
        SqlParameterMap outputData = (SqlParameterMap) c.execute(data);
        System.out.println(outputData.getError());
        System.out.println(outputData.getCsvOutput().getURI());
    }
    
    public void testMultiQuery() throws Exception {
        SqlServiceImpl c = new SqlServiceImpl();
        SqlParameterMap map = new SqlParameterMap();
        map.setJdbcDriverClass("org.postgresql.Driver");
        map.setJdbcUri("jdbc:postgresql:jbpm");
        map.setUserName("jbpm");
        map.setPassword("jbpm");
        map.setSqlCode("select * from jbpm_log where id_ < 100;select * from jbpm_node;");
        ByteArrayOutputStreamProvider out = new ByteArrayOutputStreamProvider(
                ByteArrayOutputStreamProvider.ASCII_SUPPORTED);
        map.setCsvOutput(out);
        map.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_QUERY);
        SqlParameterMap output = (SqlParameterMap) c.execute(map);
        System.err.println(output.getError());
        System.out.println(new String(out.getBytes()));
    }
    
    public void testMultiQuery2() throws Exception {
        InputStream is = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStreamProvider provider = new ByteArrayOutputStreamProvider(
                    ByteArrayOutputStreamProvider.ASCII_UNIX);
            is = getClass().getResourceAsStream("/code2.sql");
            int b;
            while((b = is.read()) != -1) {
                out.write(b);
            }
            SqlServiceImpl c = new SqlServiceImpl();
            SqlParameterMap map = new SqlParameterMap();
            map.setJdbcDriverClass("org.postgresql.Driver");
            map.setJdbcUri("jdbc:postgresql:jbpm");
            map.setUserName("jbpm");
            map.setPassword("jbpm");
            map.setSqlCode(new String(out.toByteArray()));
            map.setCsvOutput(provider);
            map.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_QUERY);
            SqlParameterMap output = (SqlParameterMap) c.execute(map);
            System.err.println(output.getError());
            String expected = "key_,name_\n" +
                "1,Value number; 1\n" +
                "2,another value'\n" +
                "3,a third ' value\n" +
                "4,value four\n";
            String actual = new String(provider.getBytes());
            System.out.println(actual);
            assertEquals(expected, actual);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch(Exception e) {
                }
            }
        }
    }
    
    public void testDML() throws Exception {
        SqlServiceImpl c = new SqlServiceImpl();
        SqlParameterMap data = new SqlParameterMap();
        data.setJdbcDriverClass("org.postgresql.Driver");
        data.setJdbcUri("jdbc:postgresql:jbpm");
        data.setUserName("jbpm");
        data.setPassword("jbpm");
        data.setSqlCode("insert into jbpm_id_group values(-1, 'G', 'testgroup', 'organization', NULL)");
        data.setProcessName("test1");
        data.setProcessVersion("1");
        data.setRequestId("1000");
        data.setCsvOutput(DataflowStreamProviderFactory.getInstance().getOutputProvider(
                "nexus/SQLService",
                "test.csv",
                data.getProcessName(),
                data.getProcessVersion(),
                data.getRequestId()));
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_DML);
        SqlParameterMap outputData = (SqlParameterMap) c.execute(data);
        assertEquals(new Integer(1), outputData.getRecordCount());
        data.setSqlCode("delete from jbpm_id_group where ID_=-1");
        outputData = (SqlParameterMap) c.execute(data);
        assertEquals(new Integer(1), outputData.getRecordCount());
        System.out.println(outputData.getError());
        System.out.println(outputData.getCsvOutput().getURI());
    }
    
    public void testDDL() throws Exception {
        SqlServiceImpl c = new SqlServiceImpl();
        SqlParameterMap data = new SqlParameterMap();
        data.setJdbcDriverClass("org.postgresql.Driver");
        data.setJdbcUri("jdbc:postgresql:jbpm");
        data.setUserName("jbpm");
        data.setPassword("jbpm");
        data.setSqlCode("create table TEST_DROP (COL CHAR(1))");
        data.setProcessName("test1");
        data.setProcessVersion("1");
        data.setRequestId("1000");
        data.setCsvOutput(DataflowStreamProviderFactory.getInstance().getOutputProvider(
                "nexus/SQLService",
                "test.csv",
                data.getProcessName(),
                data.getProcessVersion(),
                data.getRequestId()));
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_DDL);
        SqlParameterMap outputData = (SqlParameterMap) c.execute(data);
        System.out.println(outputData.getError());
        System.out.println(outputData.getCsvOutput().getURI());
        assertTrue(outputData.getError() == null || outputData.getError().equals(""));
        data.setSqlCode("drop table TEST_DROP");
        outputData = (SqlParameterMap) c.execute(data);
        assertTrue(outputData.getError() == null || outputData.getError().equals(""));
    }
    
    public void xtestOracleRun() throws Exception {
        SqlParameterMap data = new SqlParameterMap();
        data.setJdbcDriverClass("oracle.jdbc.driver.OracleDriver");
        data.setJdbcUri(getProperty("test.sql.uri"));
        data.setUserName(getProperty("test.sql.user"));
        data.setPassword(getProperty("test.sql.password"));
        data.setSqlCode(getProperty("test.sql.select"));
        SqlServiceImpl c = new SqlServiceImpl();
        SqlParameterMap outputData = (SqlParameterMap) c.execute(data);
        System.out.println(outputData.getError());
        System.out.println(outputData.getCsvOutput());
    }
    
    public void testBatchInsert() throws Exception {
        SqlServiceImpl svc = new SqlServiceImpl();
        
        // drop the table if it exists (in case we're following a previous test case)
        SqlParameterMap data = getHypersonicParameterMap();
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_DDL);
        data.setSqlCode("DROP TABLE IF EXISTS testtable;");
        svc.execute(data);
        
        // create the table
        data = getHypersonicParameterMap();
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_DDL);
        data.setSqlCode("CREATE TABLE testtable (" +
                "id_ BIGINT NOT NULL, " +
                "date_ TIMESTAMP, " +
                "amt_ FLOAT, " +
                "name_ VARCHAR, " +
                "CONSTRAINT tbl_pk PRIMARY KEY(id_)" +
                ");");
        svc.execute(data);
        
        // insert the data
        data = getHypersonicParameterMap();
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_BATCH_INSERT);
        data.setTableName("testtable");
        data.setDataMappings("id,id_,date,date_,amount,amt_,name,name_");
        data.setCsvInput(new LocalResourceInputStreamProvider(this, "/data.csv"));
        
        svc.execute(data);
        
        // retrieve the data through a SQL query and put it in a byte array
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        data = getHypersonicParameterMap();
        data.setCsvOutput(new OutputDataflowStreamProvider() {
            public OutputStream getOutputStream(boolean asciiMode) throws IOException {
                if(asciiMode) {
                    return new EOLOutputStream(baos);
                }
                return baos;
            }
            public void setAppendMode(boolean isAppendMode) {
                throw new UnsupportedOperationException();
            }
            public boolean fileExists() throws IOException {
                return true;
            }
            public URI getURI() {
                return URI.create(baos.toString());
            }
            public boolean isLocal() {
                return false;
            }
        });
        data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_QUERY);
        data.setSqlCode("select id_, date_, amt_, name_ from testtable;");
        
        data = (SqlParameterMap) svc.execute(data);
        
        // turn the byte array into a string and check it
        byte[] a = baos.toByteArray();
        assertEquals("ID_,DATE_,AMT_,NAME_" +
                "1,2005-10-01 16:15:10.25,123.45,octoberfirst" +
                "2,1990-09-10 08:20:45.5,65.4321,septembertenth",
                new String(a).replace("\n", "").replace("\r", ""));
    }
    
    protected SqlParameterMap getHypersonicParameterMap() {
        SqlParameterMap map = new SqlParameterMap();
        
        map.setJdbcDriverClass("org.hsqldb.jdbcDriver");
        map.setJdbcUri("jdbc:hsqldb:mem:dbname");
        
        return map;
	}
}
