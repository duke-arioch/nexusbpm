package com.nexusbpm.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NexusTestCase {
    
    protected boolean hasProperty(String name) {
        return getProperties().containsKey(name);
    }
    
	protected Properties getProperties() {
		Properties p = new Properties();
		try {
		    InputStream stream = getClass().getResourceAsStream("/test.properties");
		    if(stream != null) {
		        p.load(stream);
		    } else {
		        System.err.println("No test.properties file found!");
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	protected String getProperty(String key) {
		return getProperties().getProperty(key);
	}
	
}
