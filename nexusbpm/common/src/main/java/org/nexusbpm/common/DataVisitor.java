package org.nexusbpm.common;

import java.util.List;

/**
 *
 * @author Matthew Sandoz
 */
public interface DataVisitor {

  void visitData(List data) throws DataVisitationException;
  void visitColumns(List columns) throws DataVisitationException;

}
