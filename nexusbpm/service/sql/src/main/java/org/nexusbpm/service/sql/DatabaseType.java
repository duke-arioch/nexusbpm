package org.nexusbpm.service.sql;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author msandoz
 */
public final class DatabaseType {

  private static final Map<Integer, Class> MAP = new HashMap<Integer, Class>();

  private DatabaseType() {
  }

  static {
    MAP.put(java.sql.Types.NUMERIC, java.math.BigDecimal.class);
    MAP.put(java.sql.Types.BIT, java.lang.Boolean.class);
    MAP.put(java.sql.Types.VARCHAR, java.lang.String.class);
    MAP.put(java.sql.Types.DATE, java.sql.Date.class);
    MAP.put(java.sql.Types.DECIMAL, java.math.BigDecimal.class);
    MAP.put(java.sql.Types.DOUBLE, java.math.BigDecimal.class);
    MAP.put(java.sql.Types.FLOAT, java.lang.Float.class);
    MAP.put(java.sql.Types.INTEGER, java.lang.Integer.class);
    MAP.put(java.sql.Types.NUMERIC, java.math.BigDecimal.class);
    MAP.put(java.sql.Types.REAL, java.math.BigDecimal.class);
    MAP.put(java.sql.Types.SMALLINT, java.lang.Integer.class);
    MAP.put(java.sql.Types.TIMESTAMP, java.sql.Timestamp.class);
    MAP.put(java.sql.Types.TIME, java.sql.Time.class);
    MAP.put(java.sql.Types.TINYINT, java.lang.Integer.class);
  }

  public static Class getClass(final Integer key) {
    return MAP.get(key);
  }
}
