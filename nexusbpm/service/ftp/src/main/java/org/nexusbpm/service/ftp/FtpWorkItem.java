/**
 * 
 */
package org.nexusbpm.service.ftp;

import java.util.Map;

import org.nexusbpm.common.data.Parameter;
import org.nexusbpm.common.data.ParameterType;
import java.net.URI;
import org.nexusbpm.common.data.NexusWorkItem;
import org.nexusbpm.common.data.NexusWorkItemImpl;

/**
 * @author Matthew Sandoz
 *
 */
public class FtpWorkItem extends NexusWorkItemImpl {
    private static final long serialVersionUID = 1l;

    public static final String FTP_INPUT_FIELDNAME = "input";
    public static final String FTP_OUTPUT_FIELDNAME = "output";
    
    Parameter input = new Parameter(FTP_INPUT_FIELDNAME, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_INPUT);
    Parameter output = new Parameter(FTP_OUTPUT_FIELDNAME, ParameterType.ASCII_FILE, null, true, Parameter.DIRECTION_OUTPUT);
    private Parameter[] parms = {input, output};

    public FtpWorkItem() {
        super();
    }

    public FtpWorkItem(NexusWorkItem item) {
        super(item);
    }

    public URI getInput() {
        return (URI) getParameters().get(FTP_INPUT_FIELDNAME);
    }

    public void setInput(URI input) {
        getParameters().put(FTP_INPUT_FIELDNAME, input);
    } 

    public URI getOutput() {
        return (URI) getParameters().get(FTP_OUTPUT_FIELDNAME);
    }

    public void setOutput(URI output) {
        getParameters().put(FTP_OUTPUT_FIELDNAME, output);
    } 
}
