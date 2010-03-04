package org.nexusbpm.service.sql;

import java.util.Map;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterLinkedHashMap;
import org.nexusbpm.common.data.ParameterType;
import java.net.URI;

public class SqlParameterMap extends ParameterLinkedHashMap {
    private static final long serialVersionUID = 1L;
    
    static String SQL_JDBC_DRIVER_CLASS_FIELDNAME = "jdbcDriverClass";
    static String SQL_JDBC_URI_FIELDNAME = "jdbcUri";
    static String SQL_USER_NAME_FIELDNAME = "username";
    static String SQL_PASSWORD_FIELDNAME = "password";
    static String SQL_SQL_CODE_FIELDNAME = "sqlCode";
    static String SQL_STATEMENT_TYPE_FIELDNAME = "statementType";
    static String SQL_DATA_MAPPINGS = "dataMappings";
    static String SQL_TABLE_NAME = "tableName";
    static String SQL_CSV_INPUT_FIELDNAME = "csvInput";
    static String SQL_CSV_OUTPUT_FIELDNAME = "csvOutput";
    static String SQL_RECORD_COUNT_FIELDNAME = "recordCount";
    static String SQL_ERROR_FIELDNAME = "error";
    
    Parameter jdbcDriverClass = new Parameter(SQL_JDBC_DRIVER_CLASS_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter jdbcUri = new Parameter(SQL_JDBC_URI_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter username = new Parameter(SQL_USER_NAME_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter password = new Parameter(SQL_PASSWORD_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter sqlCode = new Parameter(SQL_SQL_CODE_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter statementType = new Parameter(SQL_STATEMENT_TYPE_FIELDNAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT);
    Parameter dataMappings = new Parameter(SQL_DATA_MAPPINGS, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT_AND_OUTPUT);
    Parameter tableName = new Parameter(SQL_TABLE_NAME, null, null, ParameterType.STRING, null, true, Parameter.DIRECTION_INPUT_AND_OUTPUT);
    Parameter csvInput = new Parameter(SQL_CSV_INPUT_FIELDNAME, null, null, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_INPUT);
    Parameter csvOutput = new Parameter(SQL_CSV_OUTPUT_FIELDNAME, null, null, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_OUTPUT);
    Parameter recordCount = new Parameter(SQL_RECORD_COUNT_FIELDNAME, null, null, ParameterType.INT, null, true, Parameter.DIRECTION_OUTPUT);
    Parameter error = new Parameter(SQL_ERROR_FIELDNAME, null, null, ParameterType.STRING, "", true, Parameter.DIRECTION_OUTPUT);
    
    private Parameter[] parms = {
        jdbcDriverClass,
        jdbcUri,
        username,
        password,
        sqlCode,
        statementType,
        dataMappings,
        tableName,
        csvInput,
        csvOutput,
        recordCount,
        error
    };
    
    public SqlParameterMap() {
        super();
        for(Parameter p : parms)
            add(p);
    }
    
    public SqlParameterMap(Map<? extends String, ? extends Parameter> m) {
        super(m);
        for(Parameter p : parms)
            add(p);
    }
    
    public String getError() {
        return (String) getValue(SQL_ERROR_FIELDNAME);
    }
    
    public void setError(String error) {
        setValue(SQL_ERROR_FIELDNAME, error);
    }
    
    public String getJdbcDriverClass() {
        return (String) getValue(SQL_JDBC_DRIVER_CLASS_FIELDNAME);
    }
    
    public void setJdbcDriverClass(String jdbcDriverClass) {
        setValue(SQL_JDBC_DRIVER_CLASS_FIELDNAME, jdbcDriverClass);
    }
    
    public String getJdbcUri() {
        return (String) getValue(SQL_JDBC_URI_FIELDNAME);
    }
    
    public void setJdbcUri(String jdbcUri) {
        setValue(SQL_JDBC_URI_FIELDNAME, jdbcUri);
    }
    
    public String getPassword() {
        return (String) getValue(SQL_PASSWORD_FIELDNAME);
    }
    
    public void setPassword(String password) {
        setValue(SQL_PASSWORD_FIELDNAME, password);
    }
    
    public Integer getRecordCount() {
        return (Integer) getValue(SQL_RECORD_COUNT_FIELDNAME);
    }
    
    public void setRecordCount(Integer recordCount) {
        setValue(SQL_RECORD_COUNT_FIELDNAME, recordCount);
    }
    
    public String getSqlCode() {
        return (String) getValue(SQL_SQL_CODE_FIELDNAME);
    }
    
    public void setSqlCode(String sqlCode) {
        setValue(SQL_SQL_CODE_FIELDNAME, sqlCode);
    }
    
    public String getStatementType() {
        return (String) getValue(SQL_STATEMENT_TYPE_FIELDNAME);
    }
    
    public void setStatementType(String statementType) {
        setValue(SQL_STATEMENT_TYPE_FIELDNAME, statementType);
    }
    
    public String getTableName() {
        return (String) getValue(SQL_TABLE_NAME);
    }
    
    public void setTableName(String tableName) {
        setValue(SQL_TABLE_NAME, tableName);
    }
    
    public String getDataMappings() {
        return (String) getValue(SQL_DATA_MAPPINGS);
    }
    
    public void setDataMappings(String dataMappings) {
        setValue(SQL_DATA_MAPPINGS, dataMappings);
    }
    
    public URI getCsvInput() {
        return (URI) getValue(SQL_CSV_INPUT_FIELDNAME);
    }
    
    public void setCsvInput(URI csvInput) {
        setValue(SQL_CSV_INPUT_FIELDNAME, csvInput);
    }
    
    public URI getCsvOutput() {
        return (URI) getValue(SQL_CSV_OUTPUT_FIELDNAME);
    }
    
    public void setCsvOutput(URI csvOutput) {
        setValue(SQL_CSV_OUTPUT_FIELDNAME, csvOutput);
    }
    
    public String getUserName() {
        return (String) getValue(SQL_USER_NAME_FIELDNAME);
    }
    
    public void setUserName(String userName) {
        setValue(SQL_USER_NAME_FIELDNAME, userName);
    }
    
}
