package org.nexusbpm.service.r;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.protocol.REXPFactory;

public class RUtils {

	/**
	 * quotes a given string such that it can be passed to R in R code
	 * 
	 * @param s
	 *            the string to quote
	 * @return quoted string
	 */
	public static String quoteString(String s) {
		int i = s.indexOf('\\');
		while (i >= 0) {
			s = s.substring(0, i + 1) + s.substring(i, s.length());
			i = s.indexOf('\\', i + 2);
		}
		i = s.indexOf('\"');
		while (i >= 0) {
			s = s.substring(0, i) + "\\" + s.substring(i, s.length());
			i = s.indexOf('\"', i + 2);
		}
		i = s.indexOf('\n');
		while (i >= 0) {
			if (i >= s.length() - 1) {
				s = s.substring(0, i) + "\\n";
				break;
			}
			s = s.substring(0, i) + "\\n" + s.substring(i + 1, s.length());
			i = s.indexOf('\n', i + 2);
		}
		return "\"" + s + "\"";
	}

	// wrap the code to run
	public static String wrapCode(String code) {
		return "{ .output<-capture.output(.result<-try({ "
				+ code
				+ " },silent=FALSE)); if (any(class(.result)=='try-error')) .result else paste(.output, collapse='\n') }";
	}

	public static Object convertREXP(REXP rexp, Class toClass) throws REXPMismatchException{
		Object retval = null;
                if (rexp.isInteger() && rexp.isList()) {
                        retval = new Integer(rexp.asInteger());
                } else if (rexp.isInteger() && !rexp.isList()) {
                        retval = new Integer(rexp.asInteger());
                } else if (rexp.isNumeric() && !rexp.isList()) {
                        retval = new Double(rexp.asDouble());
                } else if (rexp.isNumeric() && rexp.isList()) {
                        retval = new Double(rexp.asDouble());
                } else if (rexp.isString() && rexp.isList()) {
                        retval = rexp.asString();
                } else if (rexp.isNumeric() && rexp.isList()) {
                        retval = rexp.asString();
                }
		return retval;
	}


}
