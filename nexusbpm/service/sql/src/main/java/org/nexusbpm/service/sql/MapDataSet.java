package org.nexusbpm.service.sql;

import java.util.Hashtable;
import java.util.Map;
import org.nexusbpm.common.DataVisitor;

public class MapDataSet implements DataSet{

  Map map;

  public MapDataSet(Map map) {
    this.map = map;
  }

  @Override
  public void accept(DataVisitor visitor) throws Exception {
    visitor.visitColumns(map.keySet().toArray());
    visitor.visitData(map.values().toArray());
  }
}
