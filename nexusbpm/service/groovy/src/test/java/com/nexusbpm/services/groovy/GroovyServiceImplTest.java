package com.nexusbpm.services.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.junit.Test;
	
public class GroovyServiceImplTest{

	@Test
    public void testGroovy() {
        Binding binding = new Binding();
        binding.setVariable("foo", new Integer(2));
        binding.setVariable("out", System.out);
        binding.setVariable("err", System.err);
        
        GroovyShell shell = new GroovyShell(binding);
        Object value = shell.evaluate("println 'Hello World!'; x = 123; err.println('hi');return foo * 10");
        assert value.equals(new Integer(20));
        assert binding.getVariable("x").equals(new Integer(123));
    }
}
