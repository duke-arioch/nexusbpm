package org.nexusbpm.activiti.delegate;

import java.net.URI;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.Expression;

public class NexusJavaDelegate {

  protected String evaluateToString(Expression expression, DelegateExecution execution) {
    return evaluate(expression, execution).toString();
  }

  protected Boolean evaluateToBoolean(Expression expression, DelegateExecution execution) {
    return Boolean.valueOf(evaluate(expression, execution).toString());
  }

  protected Integer evaluateToInt(Expression expression, DelegateExecution execution) {
    return Integer.valueOf(evaluate(expression, execution).toString());
  }

  protected URI evaluateToUri(Expression expression, DelegateExecution execution) {
    return URI.create(evaluate(expression, execution).toString());
  }

  protected Object evaluate(Expression expression, DelegateExecution execution) {
    return Context.getProcessEngineConfiguration().getExpressionManager().createExpression(expression.getExpressionText()).getValue(execution);
  }
}
