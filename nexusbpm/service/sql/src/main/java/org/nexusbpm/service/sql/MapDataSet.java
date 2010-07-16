package org.nexusbpm.service.sql;

import java.util.Map;
import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

public class MapDataSet implements DataSet{

  private transient final Map map;

  public MapDataSet(final Map map) {
    this.map = map;
  }

  @Override
  public void accept(final DataVisitor visitor) throws DataVisitationException {
    visitor.visitColumns(map.keySet().toArray());
    visitor.visitData(map.values().toArray());
  }
}
