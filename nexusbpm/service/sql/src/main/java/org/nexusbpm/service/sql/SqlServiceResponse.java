package org.nexusbpm.service.sql;

import org.nexusbpm.service.NexusServiceResponse;

public class SqlServiceResponse extends NexusServiceResponse {

  private static final long serialVersionUID = 1L;
  public static final String QUERY_SQL_TYPE = "query";
  public static final String DDL_SQL_TYPE = "ddl";
  public static final String DML_SQL_TYPE = "dml";
  private long recordCount;

  public long getRecordCount() {
    return recordCount;
  }

  public void setRecordCount(final long recordCount) {
    this.recordCount = recordCount;
  }
}
