package org.nexusbpm.service.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.junit.Test;
	
public class GroovyServiceImplTest{

	@Test
    public void testGroovy() {
        Binding binding = new Binding();
        binding.setVariable("foo", Integer.valueOf(2));
        binding.setVariable("out", System.out);
        binding.setVariable("err", System.err);
        
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate("println 'Hello World!'; x = 123; err.println('hi');return foo * 10");
        assert value.equals(Integer.valueOf(20));
        assert binding.getVariable("x").equals(Integer.valueOf(123));
    }
}
