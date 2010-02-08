package com.nexusbpm.common;

import java.io.IOException;

import com.nexusbpm.common.data.Parameter;
import com.nexusbpm.common.data.ParameterType;
import com.nexusbpm.common.data.ObjectConverter;

public class NexusXmlTestCase extends NexusTestCase {
    
//    public Parameter getParameterFromNode(org.dom4j.Element element) throws IOException {
//        Parameter p = new Parameter();
//        p.setName(element.attributeValue("name"));
//        p.setSourceNode(element.attributeValue("sourceNode"));
//        p.setSourceVariable(element.attributeValue("sourceVariable"));
//        p.setRequired(Boolean.valueOf(element.attributeValue("required")).booleanValue());
//        p.setDirection(element.attributeValue("direction"));
//        String type = element.attributeValue("variableType");
//        if(type == null) type = "string";
//        p.setType(ParameterType.getType(type));
//        try {
//            p.setValue(ObjectConverter.convert(element.element("dfvalue").getText(), p.getType()));
//        } catch (Exception e) {
//            throw new IOException("Unable to unmarshal parameter " + p);
//        }
//        return p;
//    }
}
