package org.nexusbpm.service.sql;

import java.util.ArrayList;
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
    visitor.visitColumns(new ArrayList(map.keySet()));
    visitor.visitData(new ArrayList(map.values()));
  }
}
