/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nexusbpm.service.sql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author msandoz
 */
public class DatabaseType {

  private static Map map;

  static {
    map = new HashMap();
    map.put(java.sql.Types.NUMERIC, java.math.BigDecimal.class);
    map.put(java.sql.Types.BIT, java.lang.Boolean.class);
    map.put(java.sql.Types.VARCHAR, java.lang.String.class);
    map.put(java.sql.Types.DATE, java.sql.Date.class);
    map.put(java.sql.Types.DECIMAL, java.math.BigDecimal.class);
    map.put(java.sql.Types.DOUBLE, java.math.BigDecimal.class);
    map.put(java.sql.Types.FLOAT, java.lang.Float.class);
    map.put(java.sql.Types.INTEGER, java.lang.Integer.class);
    map.put(java.sql.Types.NUMERIC, java.math.BigDecimal.class);
    map.put(java.sql.Types.REAL, java.math.BigDecimal.class);
    map.put(java.sql.Types.SMALLINT, java.lang.Integer.class);
    map.put(java.sql.Types.TIMESTAMP, java.sql.Timestamp.class);
    map.put(java.sql.Types.TIME, java.sql.Time.class);
    map.put(java.sql.Types.TINYINT, java.lang.Integer.class);
  }

  public static Class getClass(short key) {
    return (Class) map.get(map);
  }
}
