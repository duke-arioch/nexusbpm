package org.nexusbpm.common.data;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

public final class ParameterType implements Serializable {
    private static final long serialVersionUID = 1l;
//    public String marshal(Object o);
//    public Object unmarshal(String e);
    
    public static final ParameterType STRING = new ParameterType(String.class, "string");
    public static final ParameterType INT = new ParameterType(Integer.class, "int");
    public static final ParameterType LONG = new ParameterType(Long.class, "long");
    public static final ParameterType FLOAT = new ParameterType(Double.class, "float");
    public static final ParameterType BOOLEAN = new ParameterType(Boolean.class, "boolean");
    public static final ParameterType ASCII_FILE = new ParameterType(URI.class, "asciifile");
    public static final ParameterType BINARY_FILE = new ParameterType(URI.class, "binaryfile");
    public static final ParameterType BINARY = new ParameterType(byte[].class, "binary");
    public static final ParameterType OBJECT = new ParameterType(Object.class, "object");
    public static final ParameterType DATE_TIME = new ParameterType(Date.class, "date");
    
    public static final ParameterType[] TYPES = {
        STRING,
        INT,
        LONG,
        FLOAT,
        BOOLEAN,
        ASCII_FILE,
        BINARY_FILE,
        BINARY,
        OBJECT,
        DATE_TIME
    };
    
    public static final ParameterType getType(String name) {
        for(int index = 0; index < TYPES.length; index++) {
            if(TYPES[index].getName().equals(name)) {
                return TYPES[index];
            }
        }
        throw new IllegalArgumentException("Type " + name + " does not exist!");
    }
    
    public static final ParameterType getType(Class type) {
        for(int index = 0; index < TYPES.length; index++) {
            if(TYPES[index].getJavaClass().equals(type)) {
                return TYPES[index];
            }
        }
        throw new IllegalArgumentException("No type for class " + type + " exists!");
    }
    
    private Class type;
    private String name;
    
    private ParameterType(Class type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public Class getJavaClass() {
        return type;
    }
    public String getName() {
        return name;
    }
    
    public boolean equals(Object obj) {
        if(obj instanceof ParameterType) {
            ParameterType type = (ParameterType) obj;
            return type.getJavaClass().equals(this.type) && type.getName().equals(getName());
        }
        return false;
    }
}
