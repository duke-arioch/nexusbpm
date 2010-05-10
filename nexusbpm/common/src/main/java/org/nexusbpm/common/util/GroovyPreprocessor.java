package org.nexusbpm.common.util;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.GroovyException;
import org.codehaus.groovy.runtime.typehandling.GroovyCastException;

import java.net.URI;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Preprocesses inline groovy code in Nexus Service data before
 * the data gets passed to the services.
 * 
 * @author Nathan Rose
 */
public class GroovyPreprocessor {

  private static final Logger LOG = LoggerFactory.getLogger(GroovyPreprocessor.class);
  /**
   * The delimiter indicating the starting point where a dynamic attribute
   * is inserted.
   */
  public static final String START_DELIMITER = "<<<";
  /**
   * The delimiter indicating the starting point where a dynamic attribute
   * is inserted.
   */
  public static final String END_DELIMITER = ">>>";
  /**
   * The maximum number of passes to make when processing the variables.
   */
  public static final int MAXIMUM_PASSES = 100;
  protected Binding binding;
  protected GroovyShell shell;
  protected List<String> variables;
  protected Map<String, String> initialValues;
  protected Map<String, String> processedValues;
  protected ClassLoader classLoader;

  /**
   * Creates the preprocessor and determines which variables need to be processed.
   */
//  public GroovyPreprocessor(ParameterMap data, ClassLoader classLoader) {
//    this.classLoader = classLoader;
//    variables = new ArrayList<String>();
//    initialValues = new HashMap<String, String>();
//    processedValues = new HashMap<String, String>();
//
//    List<String> vars = new ArrayList<String>(data.keySet());
//
//    for (Iterator<String> iter = vars.iterator(); iter.hasNext();) {
//      String name = iter.next();
//      variables.add(name);
//      boolean isString =
//              (data.get(name).getType().equals(ParameterType.STRING)
//              && data.get(name).getValue() != null)
//              || data.get(name).getValue() instanceof String;
//      if (isString && data.get(name).getValue().toString().indexOf(START_DELIMITER) != -1) {
//        initialValues.put(name, data.get(name).getValue().toString());
//      }
//    }
//  }

  /**
   * Preprocesses the variables that need to be preprocessed.
   */
//  public void evaluate(ParameterMap data) {
//    // working queue
//    List<String> queue = new LinkedList<String>();
//    // queue of things that still need work
//    List<String> recheckQueue = new LinkedList<String>(variables);
//    int passes = 0;
//    Set<String> visitedSet = new HashSet<String>();
//    Set<String> includeVars = new HashSet<String>();
//
//    // first find all the variables that don't need to be evaluated
//    for (Iterator<String> iter = recheckQueue.iterator(); iter.hasNext();) {
//      String name = iter.next();
//      if (!initialValues.containsKey(name)) {
//        includeVars.add(name);
//        iter.remove();
//      }
//    }
//
//    if (recheckQueue.size() == 0) {
//      return;
//    }
//
//    boolean evalMore = true;
//
//    StringWriter writer = new StringWriter();
//
//    // for each pass...
//    while (recheckQueue.size() > 0 && passes < MAXIMUM_PASSES && evalMore) {
//      passes += 1;
//
//      evalMore = false;
//
//      // move everything that needs to be worked on into the working queue
//      while (recheckQueue.size() > 0) {
//        queue.add(recheckQueue.remove(0));
//      }
//
//      // then process each item in the working queue once
//      while (queue.size() > 0) {
//        String name = queue.remove(0);
//        String val = (String) data.get(name).getValue();
//
//        boolean evalFail = false;
//
//        // first evaluate
//        try {
//          val = eval(data, val, includeVars);
//
//          data.get(name).setValue(val);
//        } catch (GroovyException e) {
//          evalFail = true;
//          writer.write("Error evaluating variable '" + name + "':\n"
//                  + indent("\t", new BufferedReader(new StringReader(e.toString()))));
//        } catch (GroovyRuntimeException e) {
//          evalFail = true;
//          writer.write("Error evaluating variable '" + name + "':\n"
//                  + indent("\t", new BufferedReader(new StringReader(e.toString()))));
//        } catch (GroovyCastException e) {
//          evalFail = true;
//          writer.write("Error evaluating variable '" + name + "':\n"
//                  + indent("\t", new BufferedReader(new StringReader(e.toString()))));
//        }
//
//        processedValues.put(name, val);
//        if (evalFail) {
//          // this variable either depends on itself or on a variable that
//          // hasn't completed yet
//          recheckQueue.add(name);
//        } else if (val == null || val.indexOf(START_DELIMITER) == -1 || visitedSet.contains(val)) {
//          // the variable evaluation is finished
//          includeVars.add(name);
//          evalMore = true;
//        } else {
//          // this descriptor couldn't be completely evaluated
//          visitedSet.add(val);
//          recheckQueue.add(name);
//          evalMore = true;
//        }
//      }
//    }
//
//    if (!evalMore) {
//      // an error prevented the processor from making any further passes
//      StringBuffer b = new StringBuffer();
//      b.append("Error pre-processing inline Groovy!\n");
//      b.append("Successfully processed variables:\n");
//      for (Iterator<String> iter = includeVars.iterator(); iter.hasNext();) {
//        b.append(iter.next());
//        if (iter.hasNext()) {
//          b.append(", ");
//        }
//      }
//      b.append("\nUnsuccessfully processed variables:\n");
//      for (Iterator<String> iter = initialValues.keySet().iterator(); iter.hasNext();) {
//        String name = iter.next();
//        if (!includeVars.contains(name)) {
//          b.append(name);
//          if (iter.hasNext()) {
//            b.append(", ");
//          }
//        }
//      }
//      b.append("\nErrors encountered on the last pass:\n");
//      b.append(indent("\t", new BufferedReader(new StringReader(writer.toString()))));
//      LOG.debug(b.toString());
//    }
//  }

  protected String indent(String tab, BufferedReader r) {
    String s;
    String ret = "";

    try {
      while ((s = r.readLine()) != null) {
        ret += tab + s + "\n";
      }
    } catch (IOException e) {
      // shouldn't happen, since we're reading from strings and not a file or anything
      // but print it out, just in case
      LOG.error("IOException shouldn't happen during preprocessing!", e);
    }
    return ret;
  }

  /**
   * Restores the values that haven't been changed since the preprocessor processed them to their original values
   * before they were preprocessed.
   */
//  public void restore(ParameterMap data) {
//    for (Iterator<String> iter = initialValues.keySet().iterator(); iter.hasNext();) {
//      String name = iter.next();
//      try {
//        String processedValue = processedValues.get(name);
//        Object finalValue = data.get(name).getValue();
//        if (finalValue == processedValue
//                || (finalValue != null && processedValue != null
//                && data.get(name).getType().equals(ParameterType.STRING)
//                && finalValue.equals(processedValue))) {
//          // if the value wasn't changed by the component then restore the value
//          data.get(name).setValue(initialValues.get(name));
//        }
//      } catch (Exception e) {
//        LOG.error("Error restoring variable in post-processing:" + name, e);
//      }
//    }
//  }

  /**
   * Processes inline Groovy code in the given string, returning the resulting string.
   * Returns <tt>null</tt> if <tt>s</tt> is <tt>null</tt>.
   *
   * @param s the string to process
   * @param includeVars the names of the variables to include in the interpreter's context
   * @return the result of processing the given string.
   */
//  protected String eval(ParameterMap data, String s, Set<String> includeVars) throws GroovyException {
//    String result = null;
//    if (s != null) {
//      // find delimited string blocks denoted by "START_DELIMITER
//      // <code/variables> END_DELIMITER" and evaluate
//      // each one. Return final result with the string blocks replaced by
//      // its evaluated return value.
//      String[] startSubstrings = s.split(START_DELIMITER);
//      result = "";
//      int startIndex = 0;
//      if (s.indexOf(START_DELIMITER) != 0) {
//        result = startSubstrings[0];
//        startIndex = 1;
//      }
//      for (int i = startIndex; i < startSubstrings.length; i++) {
//        String startString = startSubstrings[i];
//        String[] segments = startString.split(END_DELIMITER);
//        String code = segments[0];
//        if (code != null && (!code.equals(""))) {
//          if (includeVars.contains(code)) {
//            result += convert(data.get(code).getValue());
//          } else if (data.containsKey(code)) {
//            // if we know it's a direct reference to an un-processed
//            // variable, break out early
//            throw new GroovyException("not yet processed: " + code);
//          } else {
//            setupInterpreter(data, includeVars);
//            result += interpret(code);
//          }
//        }
//        if (segments.length > 1) {
//          result += segments[1];
//        }
//      }
//      // component.setErrorOutput( component.getErrorOutput() + errorString );
//    }
//    return result;
//  }

//  protected void setupInterpreter(ParameterMap data, Set<String> includeVars) {
//    binding = new Binding();
//    shell = new GroovyShell(/*classLoader,*/binding);
//    for (Iterator<String> iter = includeVars.iterator(); iter.hasNext();) {
//      String name = iter.next();
////            if(data.get(name).isFile()) {
////                binding.setVariable(name, data.get(name));
////            } else {
//      binding.setVariable(name, data.get(name).getValue());
////            }
//    }
//  }

  /**
   * Interprets the given Groovy code and returns the result.
   * @param s the string of Groovy code to interpret.
   * @return the result of interpreting the given Groovy code.
   */
  protected String interpret(String s) throws GroovyException {
//        StringWriter outputWriter = new StringWriter();
//        StringWriter errWriter = new StringWriter();
//        interpreter.setOut(outputWriter);
//        interpreter.setErr(errWriter);
    Object obj = shell.evaluate(s);

    return convert(obj);
  }

  protected String convert(Object value) {
    String result;
//        if(pyObj instanceof PyJavaInstance) {
//            Object o = pyObj.__tojava__(Object.class);
    if (value instanceof URI) {
      result = ((URI) value).toString();
    } else if (value == null) {
      result = "null";
    } else {
      result = value.toString();
    }
//            if(obj instanceof Parameter) {
//                Parameter p = (Parameter) obj;
//                if(p.isFile()) {
//                    return ((DataflowStreamProvider) p.getValue()).getURI().toString();
////                    try {
////                        Object value = p.getValue();
////                        if(value != null) {
////                            InputDataflowStreamProvider provider = DataflowStreamProviderFactory.getInstance().getInputProvider((URI) value);
////                            BufferedReader r = new BufferedReader(new InputStreamReader(provider.getInputStream(p
////                                    .isAsciiFile())));
////                            StringWriter sw = new StringWriter();
////                            PrintWriter writer = new PrintWriter(sw);
////                            String line;
////                            while((line = r.readLine()) != null) {
////                                writer.println(line);
////                            }
////                            return sw.toString();
////                        }
////                    } catch(IOException e) {
////                        e.printStackTrace();
////                    }
//                }
//            }
//        }
    return result;
  }
}
