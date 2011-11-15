package org.nexusbpm.service.sql;

import java.util.ArrayList;
import java.util.List;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public class SqlReferenceInsertionEventHandler implements ReferenceInsertionEventHandler {

  private final transient List variables = new ArrayList();

  @Override
  public Object referenceInsert(final String text, final Object replacement) {
    variables.add(replacement);
    return "?";
  }

  public List getVariables() {
    return variables;
  }
};
