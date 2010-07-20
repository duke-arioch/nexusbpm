package org.nexusbpm.service.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.nexusbpm.service.NexusService;
import org.nexusbpm.service.NexusServiceException;
import java.util.Map;
import org.nexusbpm.service.NexusServiceRequest;

public class GroovyServiceImpl implements NexusService {

  @Override
  public GroovyServiceResponse execute(final NexusServiceRequest data) throws NexusServiceException {
    final GroovyServiceResponse retval = new GroovyServiceResponse();
    final GroovyServiceRequest jData = (GroovyServiceRequest) data;
    final StringWriter out = new StringWriter();
    final StringWriter err = new StringWriter();
    final PrintWriter outputWriter = new PrintWriter(out);
    final PrintWriter errWriter = new PrintWriter(err);
    Exception exception = null;

    try {
      // create the interpreter
      final Binding binding = new Binding();
      final GroovyShell shell = new GroovyShell(binding);

      // get a copy of the dynamic variables
      final Map<String, Object> variables = jData.getInputVariables();
      // Process dynamic attributes (put attribute values into the interpreter).
      for (Map.Entry<String, Object> entry : variables.entrySet()) {
        binding.setVariable(entry.getKey(), entry.getValue());
      }

      // Execute the code.
      binding.setVariable("out", outputWriter);
      binding.setVariable("err", errWriter);
      shell.evaluate(jData.getCode());

      // Process dynamic attributes (get attribute values out of the interpreter).
      retval.getOutputVariables().putAll(binding.getVariables());
    } catch (Exception e) {
      if (errWriter.toString().length() > 0) {
        errWriter.write("\n");
      }
      e.printStackTrace(new PrintWriter(errWriter));
      e.printStackTrace(System.err);
      exception = e;
    } finally {
      retval.setOut(out.getBuffer().toString());
      retval.setErr(err.getBuffer().toString());
      if (exception != null) {
        throw new NexusServiceException("groovy exception", exception);
      }
    }
    return retval;
  }
}
