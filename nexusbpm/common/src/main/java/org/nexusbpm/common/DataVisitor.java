package org.nexusbpm.common;

/**
 *
 * @author Matthew Sandoz
 */
public interface DataVisitor {

  void visitData(Object[] data) throws DataVisitationException;
  void visitColumns(Object[] columns) throws DataVisitationException;

}
