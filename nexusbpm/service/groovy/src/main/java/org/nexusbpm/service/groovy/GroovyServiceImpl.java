package org.nexusbpm.service.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import java.util.Map;
import org.nexusbpm.common.data.NexusWorkItem;

public class GroovyServiceImpl implements NexusService {

  @Override
  public void execute(NexusWorkItem data) throws NexusServiceException {
    GroovyWorkItem jData = (GroovyWorkItem) data;
    StringWriter out = new StringWriter();
    StringWriter err = new StringWriter();
    PrintWriter outputWriter = new PrintWriter(out);
    PrintWriter errWriter = new PrintWriter(err);
    Exception ex = null;

    try {
      // create the interpreter
      Binding binding = new Binding();
      GroovyShell shell = new GroovyShell(binding);

      // get a copy of the dynamic variables
      Map<String, Object> variables = jData.getParameters();
      // Process dynamic attributes (put attribute values into the interpreter).
      for (Map.Entry<String, Object> entry : variables.entrySet()) {
        binding.setVariable(entry.getKey(), entry.getValue());
      }

      // Execute the code.
      binding.setVariable("out", outputWriter);
      binding.setVariable("err", errWriter);
      shell.evaluate(jData.getCode());

      // Process dynamic attributes (get attribute values out of the interpreter).
      data.getResults().putAll(binding.getVariables());
    } catch (Exception e) {
      if (errWriter.toString().length() > 0) {
        errWriter.write("\n");
      }
      e.printStackTrace(new PrintWriter(errWriter));
      e.printStackTrace(System.err);
      ex = e;
    }

    jData.setOut(out.getBuffer().toString());
    jData.setErr(err.getBuffer().toString());
    if (ex != null) {
      throw new NexusServiceException("groovy exception", ex);
    }
  }

  @Override
  public NexusWorkItem createCompatibleWorkItem(NexusWorkItem item) {
    return new GroovyWorkItem(item);
  }
}
