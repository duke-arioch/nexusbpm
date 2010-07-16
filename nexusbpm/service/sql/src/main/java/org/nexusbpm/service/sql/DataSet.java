package org.nexusbpm.service.sql;

import org.nexusbpm.common.DataVisitationException;
import org.nexusbpm.common.DataVisitor;

public interface DataSet {

  void accept(DataVisitor visitor) throws DataVisitationException;

}
