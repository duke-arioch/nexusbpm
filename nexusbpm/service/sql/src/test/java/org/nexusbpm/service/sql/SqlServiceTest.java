package org.nexusbpm.service.sql;

import java.io.IOException;
import java.util.Map;
import org.apache.velocity.app.event.EventCartridge;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.VFS;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.junit.Ignore;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

//@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:n"})
public class SqlServiceTest {

  @Test
  public void testMultiQuery() throws Exception {

    SqlServiceImpl c = new SqlServiceImpl();
    SqlWorkItem data = new SqlWorkItem();
    data.setJdbcDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
    data.setJdbcUri(URI.create("jdbc:derby:memory:unit-testing;create=true"));
    data.setUserName("");
    data.setPassword("");
    data.setSqlCode("#set ($apple = \"'RED'\") \r\nSELECT CURRENT_TIMESTAMP FROM SYSIBM.SYSDUMMY1 WHERE 'RED' = ${apple};#set ($apple = \"'GREEN'\") \r\nSELECT CURRENT_TIMESTAMP FROM SYSIBM.SYSDUMMY1 WHERE 'GREEN' = ${apple}");
    data.setCsvOutputUri(new URI("tmp:///temp1.csv"));
    data.setStatementType(SqlWorkItem.QUERY_SQL_TYPE);
    c.execute(data);
    FileContent content = VFS.getManager().resolveFile(data.getCsvOutputUri().toString()).getContent();
    InputStream stream = content.getInputStream();
    String result = org.apache.commons.io.IOUtils.toString(stream);
    stream.close();
    content.close();
    System.out.println(result);
    System.out.println(data.getOut());
    System.err.println(data.getErr());
  }

  @Test
  public void testDDL() throws Exception {
    SqlServiceImpl c = new SqlServiceImpl();
    SqlWorkItem data = new SqlWorkItem();
    data.setJdbcDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
    data.setJdbcUri(URI.create("jdbc:derby:memory:unit-testing;create=true"));
    data.setUserName("");
    data.setPassword("");
    data.setSqlCode("create table TEST_DROP (COL CHAR(1))");
    data.setCsvOutputUri(new URI("tmp:///temp2.csv"));
    data.setStatementType(SqlWorkItem.DDL_SQL_TYPE);
    c.execute(data);
    System.out.println(data.getErr());
    System.out.println(data.getCsvOutputUri());
    assertTrue(data.getErr() == null || data.getErr().equals(""));
    data.setSqlCode("drop table TEST_DROP");
    c.execute(data);
    assertTrue(data.getErr() == null || data.getErr().equals(""));
  }

  @Ignore @Test //needs to be refactored for Derby
  public void testBatchInsert() throws Exception {
    SqlServiceImpl svc = new SqlServiceImpl();

    // drop the table if it exists (in case we're following a previous test case)
    SqlWorkItem data = new SqlWorkItem();
    data.setJdbcDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
    data.setJdbcUri(URI.create("jdbc:derby:memory:unit-testing;create=true"));
    data.setStatementType(SqlWorkItem.DDL_SQL_TYPE);
    data.setSqlCode("DROP TABLE testtable;");
    svc.execute(data);

    // create the table
    data = new SqlWorkItem();
    data.setJdbcDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
    data.setJdbcUri(URI.create("jdbc:derby:memory:unit-testing;create=true"));
    data.setStatementType(SqlWorkItem.DDL_SQL_TYPE);
    data.setSqlCode("CREATE TABLE testtable ("
            + "id_ BIGINT NOT NULL, "
            + "date_ TIMESTAMP, "
            + "amt_ FLOAT, "
            + "name_ VARCHAR, "
            + "CONSTRAINT tbl_pk PRIMARY KEY(id_)"
            + ");");
    svc.execute(data);

    // insert the data
//    data = getHypersonicParameterMap();
//    data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_BATCH_INSERT);
//    data.setTableName("testtable");
//    data.setDataMappings("id,id_,date,date_,amount,amt_,name,name_");
//    data.setCsvInput(new URI("res:data.csv"));
//    svc.execute(data);
//
//    // retrieve the data through a SQL query and put it in a byte array
//    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    data = getHypersonicParameterMap();
//    data.setCsvOutput(new URI("tmp://temp.csv"));
//    data.setStatementType(SqlServiceImpl.SQL_STATEMENT_TYPE_QUERY);
//    data.setSqlCode("select id_, date_, amt_, name_ from testtable;");
//
//    data = (SqlParameterMap) svc.execute(data);
//
//    // turn the byte array into a string and check it
//    byte[] a = baos.toByteArray();
//    assertEquals("ID_,DATE_,AMT_,NAME_"
//            + "1,2005-10-01 16:15:10.25,123.45,octoberfirst"
//            + "2,1990-09-10 08:20:45.5,65.4321,septembertenth",
//            new String(a).replace("\n", "").replace("\r", ""));
  }

}
