package org.nexusbpm.service.r;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.Rserve.RSession;

public final class RUtils {

  private static final RUtils INSTANCE = new RUtils();

  private RUtils() {
  }

  public static RUtils getInstance() {
    return INSTANCE;
  }

  /**
   * quotes a given string such that it can be passed to R in R code
   *
   * @param source
   *            the string to quote
   * @return quoted string
   */



  public String quoteString(final String aSource) {
    String source = aSource;
    int index = source.indexOf('\\');
    while (index >= 0) {
      source = source.substring(0, index + 1) + source.substring(index, source.length());
      index = source.indexOf('\\', index + 2);
    }
    index = source.indexOf('\"');
    while (index >= 0) {
      source = source.substring(0, index) + "\\" + source.substring(index, source.length());
      index = source.indexOf('\"', index + 2);
    }
    index = source.indexOf('\n');
    while (index >= 0) {
      if (index >= source.length() - 1) {
        source = source.substring(0, index) + "\\n";
        break;
      }
      source = source.substring(0, index) + "\\n" + source.substring(index + 1, source.length());
      index = source.indexOf('\n', index + 2);
    }
    return "\"" + source + "\"";
  }

  // wrap the code to run
  public String wrapCode(final String code) {
    return "{ .output<-capture.output(.result<-try({ "
            + code
            + " },silent=FALSE)); if (any(class(.result)=='try-error')) .result else paste(.output, collapse='\n') }";
  }

  public Object convertREXP(final REXP rexp) throws REXPMismatchException {
    final Object retval;
    if (rexp.isInteger() && rexp.isVector()) {
      retval = rexp.asIntegers();
    } else if (rexp.isInteger() && rexp.length() != 1) {
      retval = Integer.valueOf(rexp.asInteger());
    } else if (rexp.isNumeric() && rexp.length() == 1) {
      retval = new Double(rexp.asDouble());
    } else if (rexp.isNumeric() && rexp.length() != 1) {
      retval = rexp.asDoubles();
    } else if (rexp.isString() && rexp.length() == 1) {
      retval = rexp.asString();
    } else if (rexp.isString() && rexp.length() != 1) {
      retval = rexp.asStrings();
    } else if (rexp.length() > 1) {
      retval = rexp.asStrings();
    } else {
      retval = rexp.asString();
    }
    return retval;
  }

  public REXP convertToREXP(final Object source) throws REXPMismatchException {
    REXP retval;
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
    } else {
      retval = null;
    }
    return retval;
  }

  public RSession bytesToSession(final byte[] sessionBytes) throws IOException, ClassNotFoundException {
    final ByteArrayInputStream byteStream = new ByteArrayInputStream(sessionBytes);
    final ObjectInputStream stream = new ObjectInputStream(byteStream);
    return (RSession) stream.readObject();
  }

  public byte[] sessionToBytes(final RSession outSession) {
    ObjectOutputStream stream;
    byte[] retval;
    try {
      final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      stream = new ObjectOutputStream(byteStream);
      stream.writeObject(outSession);
      stream.flush();
      retval = byteStream.toByteArray();
    } catch (IOException ex) {
      Logger.getLogger(RUtils.class.getName()).log(Level.SEVERE, null, ex);
      retval = null;
    }
    return retval;
  }
}
