package org.nexusbpm.service.r;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.lang.ArrayUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.Rserve.RSession;

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

  public static Object convertREXP(REXP rexp) throws REXPMismatchException {
    Object retval = null;
    if (rexp.isInteger() && rexp.isVector()) {
      retval = rexp.asIntegers();
    } else if (rexp.isInteger() && rexp.length() != 1) {
      retval = new Integer(rexp.asInteger());
    } else if (rexp.isNumeric() && rexp.length() == 1) {
      retval = new Double(rexp.asDouble());
    } else if (rexp.isNumeric() && rexp.length() != 1) {
      retval = rexp.asDoubles();
    } else if (rexp.isString() && rexp.length() == 1) {
      retval = rexp.asString();
    } else if (rexp.isString() && rexp.length() != 1) {
      retval = rexp.asStrings();
    } else if (rexp.length() != 1) {
      retval = rexp.asStrings();
    } else {
      retval = rexp.asString();
    }
    return retval;
  }

  public static REXP convertToREXP(Object source) throws REXPMismatchException {
    REXP retval = null;
    if (source == null) {
      retval = new REXPNull();
    } else if (source instanceof Integer) {
      retval = new REXPInteger((Integer) source);
    } else if (source instanceof Double) {
      retval = new REXPDouble((Double) source);
    } else if (source instanceof String) {
      retval = new REXPString((String) source);
    } else if (source instanceof String[]) {
      retval = new REXPString((String[]) source);
    } else if (source instanceof Integer[]) {
      retval = new REXPInteger(ArrayUtils.toPrimitive((Integer[]) source));
    } else if (source instanceof Double[]) {
      retval = new REXPDouble(ArrayUtils.toPrimitive((Double[]) source));
    }
    return retval;
  }

  public static RSession bytesToSession(byte[] sessionBytes) {
    ByteArrayInputStream byteStream = new ByteArrayInputStream(sessionBytes);
    RSession session;
    try {
      ObjectInputStream stream = new ObjectInputStream(byteStream);
      session = (RSession) stream.readObject();
    } catch (IOException exception) {
      return null;
    } catch (ClassNotFoundException exception) {
      return null;
    }
    return session;
  }

  public static byte[] sessionToBytes(RSession outSession) {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    byte[] retval;
    try {
      ObjectOutputStream stream = new ObjectOutputStream(byteStream);
      stream.writeObject(outSession);
      stream.flush();
      retval = byteStream.toByteArray();
    } catch (IOException exception) {
      retval = null;
    }
    return retval;
  }
}
