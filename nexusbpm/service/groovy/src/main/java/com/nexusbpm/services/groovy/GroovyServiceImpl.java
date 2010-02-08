package com.nexusbpm.services.groovy;


import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import com.nexusbpm.common.data.Parameter;
import com.nexusbpm.common.data.ParameterMap;
import com.nexusbpm.services.NexusService;
import com.nexusbpm.services.NexusServiceException;
import java.net.URI;

public class GroovyServiceImpl implements NexusService {
    public ParameterMap execute( ParameterMap data) throws NexusServiceException {
        GroovyParameterMap jData = new GroovyParameterMap(data); 
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
            Collection variables = jData.values();
            
            // Process dynamic attributes (put attribute values into the Jython interpreter).
            // remove the non-dynamic variables
            for( Iterator iter = variables.iterator(); iter.hasNext(); ) {
                Parameter param = (Parameter) iter.next();
                
                set( binding, param, jData );
            }
            
            // Execute the code.
            binding.setVariable("out", outputWriter);
            binding.setVariable("err", errWriter);
                shell.evaluate( jData.getCode() );
                        
            // Process dynamic attributes (get attribute values out of the Jython interpreter).
            for( Iterator iter = variables.iterator(); iter.hasNext(); ) {
                Parameter param = (Parameter) iter.next();
                
                get(binding, param, jData, errWriter);
            }
        }
        catch( Exception e ) {
            if( errWriter.toString().length() > 0 ) {
                errWriter.write( "\n" );
            }
            e.printStackTrace( new PrintWriter( errWriter ) );
            e.printStackTrace( System.err );
            ex = e;
        }
        
        jData.setOutput(out.getBuffer().toString());
        jData.setError(err.getBuffer().toString());
        if(ex != null) {
            throw new NexusServiceException(ex, jData, false);
        }
        return jData;
    }
    
    private void set(Binding binding, Parameter param, GroovyParameterMap data) throws IOException {
        Object value = param.getValue();
        if(param.isFile()) {
            if (value instanceof URI) {
                URI provider = (URI) value;
                binding.setVariable( param.getName(), provider );
            }
//            if(param.getDirection().equals("in")) {
//              if (value instanceof DataflowStreamProvider) {
//                InputDataflowStreamProvider provider = null;
//                if(value != null) {
//                    provider = DataflowStreamProviderFactory.getInstance().getInputProvider((URI) value);
//                }
//            } else /* assume an output variable*/ {
//                OutputDataflowStreamProvider provider = null;
//                if(value != null) {
//                    String id = data.getRequestId();
//                    String[] parts = id.split("-");
//                    provider = DataflowStreamProviderFactory.getInstance().getOutputProvider(
//                          GROOVY_SERVICE_NAME,
//                            data.getProcessName(),
//                            data.getProcessVersion(),
//                            parts[1],
//                            (URI) value);
//                }
//                
//                binding.setVariable( param.getName(), provider );
//            }
        } else if(!param.isRequired() && !param.getDirection().equals("out")) {
            binding.setVariable( param.getName(), value );
        }
    }
    
    private void get(Binding binding, Parameter param,
            GroovyParameterMap data, PrintWriter errWriter) {
        String name = param.getName();
        if( !param.isRequired() && !param.getDirection().equals( "in" ) ) {
            try {
                Object value = binding.getVariable( name );
                if(value instanceof URI) {
                    URI provider = (URI) value;
                    param.setValue(provider);
//                    try {
//                        // try to close the output stream, in case the Jython code didn't
//                        provider.closeOutput();
//                    } catch(Exception e) {
//                        // ignore any exceptions... the stream may already be closed, etc
//                    }
                } else {
                    Object o = (Object) value;
                    param.setValue(o);
                }
            }
            catch( Throwable t ) {
                errWriter.write( "Error retrieving value of dynamic variable '" + name +
                        "' from the Groovy interpreter!\n" );
                t.printStackTrace( new PrintWriter( errWriter ) );
            }
        }
    }

    public static void main(String[] args) {
        Binding binding = new Binding();
        binding.setVariable("foo", new Integer(2));
        binding.setVariable("out", System.err);
        
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate("println 'Hello World!'; x = 123; err.println('hi');return foo * 10");
        assert value.equals(new Integer(20));
        assert binding.getVariable("x").equals(new Integer(123));
    }
}
