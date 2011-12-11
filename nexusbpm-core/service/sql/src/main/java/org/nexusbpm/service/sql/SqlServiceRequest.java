package org.nexusbpm.service.sql;

import java.net.URI;
import org.nexusbpm.service.NexusServiceRequest;

public class SqlServiceRequest extends NexusServiceRequest {

  private static final long serialVersionUID = 1L;

  public static final String QUERY_SQL_TYPE = "query";
  public static final String DDL_SQL_TYPE = "ddl";
  public static final String DML_SQL_TYPE = "dml";

  private String jdbcDriverClass;
  public URI jdbcUri;
  private String userName;
  private String password;
  private String sqlCode;
  private String statementType;
  private String dataMappings;
  private String tableName;
  private URI csvInputUri;
  private URI csvOutputUri;

  public URI getCsvInputUri() {
    return csvInputUri;
  }

  public void setCsvInputUri(final URI csvInputUri) {
    this.csvInputUri = csvInputUri;
  }

  public URI getCsvOutputUri() {
    return csvOutputUri;
  }

  public void setCsvOutputUri(final URI csvOutputUri) {
    this.csvOutputUri = csvOutputUri;
  }

  public String getDataMappings() {
    return dataMappings;
  }

  public void setDataMappings(final String dataMappings) {
    this.dataMappings = dataMappings;
  }

  public String getJdbcDriverClass() {
    return jdbcDriverClass;
  }

  public void setJdbcDriverClass(final String jdbcDriverClass) {
    this.jdbcDriverClass = jdbcDriverClass;
  }

  public URI getJdbcUri() {
    return jdbcUri;
  }

  public void setJdbcUri(final URI jdbcUri) {
    this.jdbcUri = jdbcUri;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getSqlCode() {
    return sqlCode;
  }

  public void setSqlCode(final String sqlCode) {
    this.sqlCode = sqlCode;
  }

  public String getStatementType() {
    return statementType;
  }

  public void setStatementType(final String statementType) {
    this.statementType = statementType;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }

}
