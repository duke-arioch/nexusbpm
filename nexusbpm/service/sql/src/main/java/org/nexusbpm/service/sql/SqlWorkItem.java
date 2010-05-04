package org.nexusbpm.service.sql;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.nexusbpm.common.data.NexusWorkItemImpl;
import org.nexusbpm.common.data.NexusWorkItem;

public class SqlWorkItem extends NexusWorkItemImpl {

  private static final long serialVersionUID = 1L;

  public static final String QUERY_SQL_TYPE = "query";
  public static final String DDL_SQL_TYPE = "ddl";
  public static final String DML_SQL_TYPE = "dml";

  public static final String SQL_JDBC_DRIVER_CLASS_NAME_KEY = "jdbcDriverClass";
  public static final String SQL_JDBC_URI_KEY = "jdbcUri";
  public static final String SQL_USER_NAME_KEY = "userName";
  public static final String SQL_PASSWORD_KEY = "password";
  public static final String SQL_SQL_CODE_KEY = "sqlCode";
  public static final String SQL_STATEMENT_TYPE_KEY = "statementType";
  public static final String SQL_DATA_MAPPINGS = "dataMappings";
  public static final String SQL_TABLE_NAME = "tableName";
  public static final String SQL_CSV_INPUT_KEY = "csvInputUri";
  public static final String SQL_CSV_OUTPUT_KEY = "csvOutputUri";
  public static final String SQL_RECORD_COUNT_KEY = "recordCount";
  public static final List<String> SQL_INPUT_FIELDS = Arrays.asList(
          SQL_JDBC_DRIVER_CLASS_NAME_KEY,
          SQL_JDBC_URI_KEY,
          SQL_USER_NAME_KEY,
          SQL_PASSWORD_KEY,
          SQL_SQL_CODE_KEY,
          SQL_STATEMENT_TYPE_KEY,
          SQL_DATA_MAPPINGS,
          SQL_TABLE_NAME,
          SQL_CSV_INPUT_KEY);

  public static final List<String> SQL_OUTPUT_FIELDS = Arrays.asList(
          SQL_CSV_OUTPUT_KEY,
          SQL_RECORD_COUNT_KEY,
          WORKITEM_OUT_KEY,
          WORKITEM_ERR_KEY,
          WORKITEM_RETURN_CODE_KEY);

  public SqlWorkItem() {
    super();
  }

  public SqlWorkItem(NexusWorkItem item) {
    super(item);
  }

  @Override
  public List<String> getRequiredInputParameterNames() {
    return SQL_INPUT_FIELDS;
  }

  @Override
  public List<String> getRequiredOutputParameterNames() {
    return SQL_OUTPUT_FIELDS;
  }

  public String getJdbcDriverClassName() {
    return (String) this.getParameters().get(SQL_JDBC_DRIVER_CLASS_NAME_KEY);
  }

  public void setJdbcDriverClassName(String jdbcDriverClass) {
    this.getParameters().put(SQL_JDBC_DRIVER_CLASS_NAME_KEY, jdbcDriverClass);
  }

  public URI getJdbcUri() {
    return (URI) this.getParameters().get(SQL_JDBC_URI_KEY);
  }

  public void setJdbcUri(URI jdbcUri) {
    this.getParameters().put(SQL_JDBC_URI_KEY, jdbcUri);
  }

  public String getUserName() {
    return (String) this.getParameters().get(SQL_USER_NAME_KEY);
  }

  public void setUserName(String userName) {
    this.getParameters().put(SQL_USER_NAME_KEY, userName);
  }

  public String getPassword() {
    return (String) this.getParameters().get(SQL_PASSWORD_KEY);
  }

  public void setPassword(String password) {
    this.getParameters().put(SQL_PASSWORD_KEY, password);
  }

  public String getSqlCode() {
    return (String) this.getParameters().get(SQL_SQL_CODE_KEY);
  }

  public void setSqlCode(String sqlCode) {
    this.getParameters().put(SQL_SQL_CODE_KEY, sqlCode);
  }

  public String getStatementType() {
    return (String) this.getParameters().get(SQL_STATEMENT_TYPE_KEY);
  }

  public void setStatementType(String statementType) {
    this.getParameters().put(SQL_STATEMENT_TYPE_KEY, statementType);
  }

  public URI getCsvInputUri() {
    return (URI) this.getParameters().get(SQL_CSV_INPUT_KEY);
  }

  public void setCsvInputUri(URI csvInputUri) {
    this.getParameters().put(SQL_CSV_INPUT_KEY, csvInputUri);
  }

  public URI getCsvOutputUri() {
    return (URI) this.getParameters().get(SQL_CSV_OUTPUT_KEY);
  }

  public void setCsvOutputUri(URI csvOutputUri) {
    this.getParameters().put(SQL_CSV_OUTPUT_KEY, csvOutputUri);
  }

  public Long getAffectedRecordCount() {
    return (Long) this.getResults().get(SQL_RECORD_COUNT_KEY);
  }

  public void setAffectedRecordCount(Long count) {
    this.getResults().put(SQL_RECORD_COUNT_KEY, count);
  }

}
