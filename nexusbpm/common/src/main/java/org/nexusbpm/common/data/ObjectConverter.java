package org.nexusbpm.common.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

public class ObjectConverter {

  private ObjectConverter() {}

  public static final String ISO8601_DF = "yyyy-MM-dd";
  public static final String ISO8601_TF = "HH:mm:ss.SSSZ";
  public static final String ISO8601_DTF = ISO8601_DF + "'T'" + ISO8601_TF;
  public static final String JDBC_DF = ISO8601_DF;
  public static final String JDBC_TF = "HH:mm:ss";
  // SimpleDateFormat is not sufficient for JDBC Datetime format: the following
  // string will recognize a JDBC timestamp value, but will leave off the nanoseconds
  public static final String JDBC_DTF = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String JDBC_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
  public static final String JDBC_TIME_REGEX = "\\d{2}:\\d{2}:\\d{2}";
  public static final String JDBC_DATETIME_REGEX =
          JDBC_DATE_REGEX + " " + JDBC_TIME_REGEX + "([.]\\d{1,9})?";
  public static final String TO_A_BIGINTEGER = " to a BigInteger.";
  public static final String UNABLE_TO_CONVERT_A_ = "Unable to convert a ";
  public static final String _INSTEAD_USE_ = ". Instead use ";
  private static final ThreadLocal<SimpleDateFormat> LOCAL_ISO8601_DF =
          new SimpleDateFormatThreadLocal(ISO8601_DF);
  private static final ThreadLocal<SimpleDateFormat> LOCAL_ISO8601_TF =
          new SimpleDateFormatThreadLocal(ISO8601_TF);
  private static final ThreadLocal<SimpleDateFormat> LOCAL_ISO8601_DTF =
          new SimpleDateFormatThreadLocal(ISO8601_DTF);
  private static final ThreadLocal<SimpleDateFormat> LOCAL_JDBC_TF =
          new SimpleDateFormatThreadLocal(JDBC_TF);
  private static final ThreadLocal<SimpleDateFormat> LOCAL_JDBC_DTF =
          new SimpleDateFormatThreadLocal(JDBC_DTF);
  private static final Pattern LOCAL_JDBC_DATE_REGEX = Pattern.compile(JDBC_DATE_REGEX);
  private static final Pattern LOCAL_JDBC_TIME_REGEX = Pattern.compile(JDBC_TIME_REGEX);
  private static final Pattern LOCAL_JDBC_DATETIME_REGEX = Pattern.compile(JDBC_DATETIME_REGEX);

  private static class SimpleDateFormatThreadLocal extends ThreadLocal<SimpleDateFormat> {

    private transient final String format;

    public SimpleDateFormatThreadLocal(final String format) {
      super();
      this.format = format;
    }

    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat(format, Locale.US);
    }
  }

  public static Object convert(final Object source, final Class dest) throws ObjectConversionException {
    if (dest == null) {
      throw new ObjectConversionException("Unable to convert to a null type!");
    }
    try {
      final Object result;
      if (source == null) {
        result = null;
      } else if (source.getClass().equals(dest)) {
        result = source;
      } else if (dest.equals(String.class)) {
        result = convertToString(source);
      } else if (dest.equals(URI.class)) {
        result = convertToURI(source);
      } else if (dest.equals(Double.class)) {
        result = convertToDouble(source);
      } else if (dest.equals(Long.class)) {
        result = convertToLong(source);
      } else if (dest.equals(Integer.class)) {
        result = convertToInteger(source);
      } else if (dest.equals(Float.class)) {
        result = convertToFloat(source);
      } else if (dest.equals(BigInteger.class)) {
        result = convertToBigInteger(source);
      } else if (dest.equals(BigDecimal.class)) {
        result = convertToBigDecimal(source);
      } else if (dest.equals(java.sql.Timestamp.class)) {
        result = convertToSQLTimestamp(source);
      } else if (dest.equals(java.sql.Date.class)) {
        result = convertToSQLDate(source);
      } else if (dest.equals(java.sql.Time.class)) {
        result = convertToSQLTime(source);
      } else if (dest.equals(Date.class)) {
        result = convertToDate(source);
      } else if (dest.equals(Boolean.class)) {
        result = convertToBoolean(source);
      } else if (dest.equals(byte[].class)) {
        result = convertToBinary(source);
      } else if (dest.equals(Object.class)) {
        result = convertToObject(source);
      } else {
        throw new IllegalArgumentException("Type " + dest.getName() + " not supported!");
      }
      return result;
    } catch (IOException e) {
      throw new ObjectConversionException("Unable to convert to type " + dest.getSimpleName(), e);
    }
  }

  /**
   * @param sql whether SQL Date/Time classes should be preferred for
   *            parsing over java.util.Date
   */
  public static Object convert(final Object source, final boolean sql) {
    // try to convert the value if it's not null, but leave certain object types alone
    if (source != null
            && !(source instanceof URI
            || source instanceof Number
            || source instanceof Date
            || source instanceof Boolean)) {
      if ("".equals(source)) {
        return source;
      }

      // first see if it's some sort of serialized object
      try {
        final Object value = convertToObject(source);
        if (value != null && !value.equals(source)) {
          return value;
        }
      } catch (Exception e) {
      }

      // convert the string and trimmed string ahead of time so
      // that they can be reused without having to be recomputed
      final String sourceString;
      final String sourceStringTrimmed;
      if (!source.getClass().isArray()) {
        sourceString = source.toString();
        sourceStringTrimmed = sourceString.trim();
      } else {
        sourceStringTrimmed = null;
      }

      // try to turn it into an integer value, preferring the smallest
      // type that can hold the value
      try {
        if (sourceStringTrimmed != null) {
          final BigInteger bigint = new BigInteger(sourceStringTrimmed);
          final long value = bigint.longValue();
          if (bigint.compareTo(BigInteger.valueOf(value)) != 0) {
            return bigint;
          } else if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            return Long.valueOf(value);
          } else if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            return Integer.valueOf((int) value);
          }
        }
      } catch (Exception e) {
      }

      // try to turn it into a floating point value, preferring the
      // smallest type that can hold the value
      try {
        if (sourceStringTrimmed != null) {
          final BigDecimal bigdec = new BigDecimal(sourceStringTrimmed);
          final double value = bigdec.doubleValue();
          if (value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY
                  || bigdec.compareTo(BigDecimal.valueOf(value)) != 0) {
            return bigdec;
          }
          final float fvalue = bigdec.floatValue();
          if (fvalue == Float.POSITIVE_INFINITY || fvalue == Float.NEGATIVE_INFINITY
                  || bigdec.compareTo(BigDecimal.valueOf(fvalue)) != 0) {
            return Double.valueOf(value);
          } else {
            return Float.valueOf(fvalue);
          }
        }
      } catch (Exception e) {
      }

      // if there is a preference for SQL date/time types, try those
      if (sql) {
        try {
          if (LOCAL_JDBC_DATETIME_REGEX.matcher(sourceStringTrimmed).matches()) {
            return java.sql.Timestamp.valueOf(sourceStringTrimmed);
          }
        } catch (Exception e) {
        }
        try {
          if (LOCAL_JDBC_TIME_REGEX.matcher(sourceStringTrimmed).matches()) {
            return java.sql.Time.valueOf(sourceStringTrimmed);
          }
        } catch (Exception e) {
        }
        try {
          if (LOCAL_JDBC_DATE_REGEX.matcher(sourceStringTrimmed).matches()) {
            return java.sql.Date.valueOf(sourceStringTrimmed);
          }
        } catch (Exception e) {
        }
      }

      // try to parse a date using the ISO format
      final ParsePosition position = new ParsePosition(0);
      try {
        return parseDate(
                LOCAL_ISO8601_DTF.get(),
                sourceStringTrimmed,
                position);
      } catch (Exception e) {
      }
      try {
        return parseDate(
                LOCAL_ISO8601_TF.get(),
                sourceStringTrimmed,
                position);
      } catch (Exception e) {
      }
      try {
        return parseDate(
                LOCAL_ISO8601_DF.get(),
                sourceStringTrimmed,
                position);
      } catch (Exception e) {
      }

      // if there isn't a preference for SQL date/time types, we still need to
      // check if the value was formatted as a SQL date/time type
      if (!sql) {
        // SQL date is the same as the ISO date, so we can skip that one
        try {
          return parseDate(
                  LOCAL_JDBC_DTF.get(),
                  sourceStringTrimmed,
                  position);
        } catch (Exception e) {
        }
        try {
          return parseDate(
                  LOCAL_JDBC_TF.get(),
                  sourceStringTrimmed,
                  position);
        } catch (Exception e) {
        }
      }

      if (sourceStringTrimmed != null) {
        // see if it's a boolean in string format
        if (sourceStringTrimmed.equalsIgnoreCase("true")) {
          return Boolean.TRUE;
        } else if (sourceStringTrimmed.equalsIgnoreCase("false")) {
          return Boolean.FALSE;
        }
      }
    }
    return source;
  }

  protected static Date parseDate(final SimpleDateFormat format, final String text, final ParsePosition position)
          throws ObjectConversionException {
    final int index = position.getIndex();
    final Date date = format.parse(text, position);
    if (date == null || position.getIndex() < text.length()) {
      position.setIndex(index);
      throw new ObjectConversionException("Could not parse '" + text + "'");
    }
    return date;
  }

  public static Double convertToDouble(final Object source) throws ObjectConversionException {
    Double result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (Double) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = Double.valueOf((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = Double.valueOf(source.toString());
    } else if (source instanceof Boolean) {
      result = Double.valueOf(((Boolean) source).booleanValue() ? 1.0 : 0.0);
    } else if (source instanceof Date) {
      result = Double.valueOf(((Date) source).getTime());
    } else if (source instanceof byte[]) {
      try {
        result = (Double) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Double.", e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Double.");
    }
    return result;
  }

  public static Float convertToFloat(final Object source) throws ObjectConversionException {
    Float result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (Float) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = Float.valueOf((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = Float.valueOf(source.toString());
    } else if (source instanceof Boolean) {
      result = Float.valueOf(((Boolean) source).booleanValue() ? 1.0f : 0.0f);
    } else if (source instanceof Date) {
      result = Float.valueOf(((Date) source).getTime());
    } else if (source instanceof byte[]) {
      try {
        result = (Float) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Float.", e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Float.");
    }
    return result;
  }

  public static Long convertToLong(final Object source) throws ObjectConversionException {
    Long result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (Long) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = Long.valueOf((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = Long.valueOf(((Number) source).longValue());
    } else if (source instanceof Boolean) {
      result = Long.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
    } else if (source instanceof Date) {
      result = Long.valueOf(((Date) source).getTime());
    } else if (source instanceof byte[]) {
      try {
        result = (Long) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Long.", e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Long.");
    }
    return result;
  }

  public static Integer convertToInteger(final Object source) throws ObjectConversionException {
    Integer result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (Integer) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = Integer.valueOf((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = Integer.parseInt(Long.valueOf(Math.round(((Number) source).doubleValue())).toString());
    } else if (source instanceof Boolean) {
      result = ((Boolean) source).booleanValue() ? 1 : 0;
    } else if (source instanceof Date) {
      result = Integer.parseInt(Long.valueOf(((Date) source).getTime()).toString());
    } else if (source instanceof byte[]) {
      try {
        result = (Integer) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an Integer.", e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an Integer.");
    }
    return result;
  }

  public static BigInteger convertToBigInteger(final Object source) throws ObjectConversionException {
    BigInteger result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (BigInteger) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = new BigInteger((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = new BigInteger(source.toString());
    } else if (source instanceof Boolean) {
      result = BigInteger.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
    } else if (source instanceof Date) {
      result = BigInteger.valueOf(((Date) source).getTime());
    } else if (source instanceof byte[]) {
      try {
        result = (BigInteger) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + TO_A_BIGINTEGER, e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + TO_A_BIGINTEGER);
    }
    return result;
  }

  public static BigDecimal convertToBigDecimal(final Object source) throws ObjectConversionException {
    BigDecimal result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (BigDecimal) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = new BigDecimal((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = new BigDecimal(source.toString());
    } else if (source instanceof Boolean) {
      result = BigDecimal.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
    } else if (source instanceof Date) {
      result = BigDecimal.valueOf(((Date) source).getTime());
    } else if (source instanceof byte[]) {
      try {
        result = (BigDecimal) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + TO_A_BIGINTEGER, e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + TO_A_BIGINTEGER);
    }
    return result;
  }

  public static Boolean convertToBoolean(final Object source) throws ObjectConversionException {
    Boolean result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = Boolean.FALSE;
      } else {
        try {
          result = (Boolean) toObject(base64Decode((String) source));
        } catch (Exception e) {
          result = Boolean.valueOf((String) source);
        }
      }
    } else if (source instanceof Number) {
      result = Boolean.valueOf(((Number) source).intValue() != 0);
    } else if (source instanceof byte[]) {
      try {
        result = (Boolean) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Boolean.", e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Boolean.");
    }
    return result;
  }

  public static Date convertToDate(final Object source) throws ObjectConversionException {
    Date result = null;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (Date) toObject(base64Decode((String) source));
        } catch (Exception e) {
          try {
            result = LOCAL_ISO8601_DTF.get().parse((String) source);
          } catch (Exception ex) {
            try {
              result = LOCAL_ISO8601_TF.get().parse((String) source);
            } catch (Exception exc) {
            }
            if (result == null) {
              try {
                result = new Date(java.sql.Timestamp.valueOf((String) source).getTime());
              } catch (Exception exc) {
              }
            }
            if (result == null) {
              try {
                result = new Date(java.sql.Time.valueOf((String) source).getTime());
              } catch (Exception exc) {
              }
            }
            if (result == null) {
              try {
                result = LOCAL_ISO8601_DF.get().parse((String) source);
              } catch (Exception exc) {
                throw new ObjectConversionException(
                        "Unable to convert String to Date using String's date format:"
                        + source.toString() + _INSTEAD_USE_ + ISO8601_DTF, exc);
              }
            }
          }
        }
      }
    } else if (source instanceof Number) {
      result = new Date(((Number) source).longValue());
    } else if (source instanceof byte[]) {
      try {
        result = (Date) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(
                UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Date.", e);
      }
    } else if (source instanceof Date) {
      result = new Date(((Date) source).getTime());
    } else {
      throw new ObjectConversionException(
              UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a Date.");
    }
    return result;
  }

  public static java.sql.Date convertToSQLDate(final Object source) throws ObjectConversionException {
    java.sql.Date result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (java.sql.Date) toObject(base64Decode((String) source));
        } catch (Exception e) {
          try {
            result = java.sql.Date.valueOf((String) source);
          } catch (Exception ex) {
            try {
              result = new java.sql.Date(convertToDate(source).getTime());
            } catch (Exception exc) {
              throw new ObjectConversionException(
                      "Unable to convert String to SQL Date using JDBC Date format:"
                      + source.toString() + _INSTEAD_USE_ + JDBC_DF, exc);
            }
          }
        }
      }
    } else if (source instanceof Number) {
      result = new java.sql.Date(((Number) source).longValue());
    } else if (source instanceof byte[]) {
      try {
        result = (java.sql.Date) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(
                UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an SQL Date.", e);
      }
    } else if (source instanceof Date) {
      result = new java.sql.Date(((Date) source).getTime());
    } else {
      throw new ObjectConversionException(
              UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an SQL Date.");
    }
    return result;
  }

  public static java.sql.Time convertToSQLTime(final Object source) throws ObjectConversionException {
    java.sql.Time result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (java.sql.Time) toObject(base64Decode((String) source));
        } catch (Exception e) {
          try {
            result = java.sql.Time.valueOf((String) source);
          } catch (Exception ex) {
            try {
              result = new java.sql.Time(convertToDate(source).getTime());
            } catch (Exception exc) {
              throw new ObjectConversionException(
                      "Unable to convert String to SQL Time using JDBC Time format:"
                      + source.toString() + _INSTEAD_USE_ + JDBC_TF, exc);
            }
          }
        }
      }
    } else if (source instanceof Number) {
      result = new java.sql.Time(((Number) source).longValue());
    } else if (source instanceof byte[]) {
      try {
        result = (java.sql.Time) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(
                UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an SQL Time.", e);
      }
    } else if (source instanceof Date) {
      result = new java.sql.Time(((Date) source).getTime());
    } else {
      throw new ObjectConversionException(
              UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an SQL Time.");
    }
    return result;
  }

  public static java.sql.Timestamp convertToSQLTimestamp(final Object source) throws ObjectConversionException {
    java.sql.Timestamp result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (java.sql.Timestamp) toObject(base64Decode((String) source));
        } catch (Exception e) {
          try {
            result = java.sql.Timestamp.valueOf((String) source);
          } catch (Exception ex) {
            try {
              result = new java.sql.Timestamp(convertToDate(source).getTime());
            } catch (Exception exc) {
              throw new ObjectConversionException(
                      "Unable to convert String to SQL Date using JDBC Timestamp format:"
                      + source.toString() + _INSTEAD_USE_ + JDBC_DTF, exc);
            }
          }
        }
      }
    } else if (source instanceof Number) {
      result = new java.sql.Timestamp(((Number) source).longValue());
    } else if (source instanceof byte[]) {
      try {
        result = (java.sql.Timestamp) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(
                UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an SQL Timestamp.", e);
      }
    } else if (source instanceof Date) {
      result = new java.sql.Timestamp(((Date) source).getTime());
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to an SQL Timestamp.");
    }
    return result;
  }

  public static URI convertToURI(final Object source) throws ObjectConversionException {
    URI result;
    if (source instanceof String) {
      if (((String) source).length() == 0) {
        result = null;
      } else {
        try {
          result = (URI) toObject(base64Decode((String) source));
        } catch (Exception e) {
          try {
            result = new URI((String) source);
          } catch (URISyntaxException ex) {
            throw new ObjectConversionException("Unable to convert String to URI:" + source.toString(), ex);
          }
        }
      }
    } else if (source instanceof byte[]) {
      try {
        result = (URI) toObject((byte[]) source);
      } catch (Exception e) {
        throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a URI.", e);
      }
    } else {
      throw new ObjectConversionException(UNABLE_TO_CONVERT_A_ + source.getClass().getName() + " to a URI.");
    }
    return result;
  }

  public static String convertToString(final Object source) throws IOException {
    String result;
    if (source instanceof Date) {
      if (source instanceof java.sql.Date
              || source instanceof java.sql.Time
              || source instanceof java.sql.Timestamp) {
        result = source.toString();
      } else {
        final SimpleDateFormat sdf = LOCAL_ISO8601_DTF.get();
        result = sdf.format((Date) source);
      }
    } else if (source instanceof URI) {
      final FileSystemManager manager = VFS.getManager();
      final FileObject fileObject = manager.resolveFile(((URI) source).toString()); //may need to do toAsciiString
      final InputStream istream = fileObject.getContent().getInputStream();
      result = IOUtils.toString(istream);
    } else if (source instanceof Number || source instanceof Boolean) {
      result = source.toString();
    } else if (source instanceof byte[]) {
      result = base64Encode((byte[]) source);
    } else {
      result = base64Encode(toByteArray(source));
    }
    return result;
  }

  public static byte[] convertToBinary(final Object source) throws ObjectConversionException {
    try {
      if (source instanceof URI) {
        final FileSystemManager manager = VFS.getManager();
        final FileObject fileObject = manager.resolveFile(((URI) source).toString()); //may need to do toAsciiString
        final InputStream istream = fileObject.getContent().getInputStream();
        return IOUtils.toByteArray(istream);
      }
    } catch (IOException e1) {
      throw new ObjectConversionException("Unable to read contents of file into binary variable", e1);
    }
    if (source instanceof String && ((String) source).length() == 0) {
      return null;
    } else {
      try {
        return toByteArray(source);
      } catch (Exception e) {
        throw new ObjectConversionException();
      }
    }
  }

  public static Object convertToObject(final Object source) throws ObjectConversionException {
    Object result = null;
    if (source instanceof byte[]) {
      if (((byte[]) source).length == 0) {
        result = null;
      } else {
        try {
          result = toObject((byte[]) source);
        } catch (Exception e) {
          throw new ObjectConversionException();
        }
      }
    } else if (source instanceof String) {
      try {
        result = toObject(base64Decode((String) source));
      } catch (Exception e) {
        result = null;
      }
    } else {
      result = source;
    }
    return result;
  }

  public static String base64Encode(final byte[] data) {
    return data == null ? null : Base64.encodeBase64String(data);
  }

  public static byte[] base64Decode(final String string) {
    return string == null ? null : Base64.decodeBase64(string);
  }

  public static byte[] toByteArray(final Object object) throws IOException {
    byte[] retval = null;
    if (object != null) {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream();
      final ObjectOutputStream out = new ObjectOutputStream(bout);
      out.writeObject(object);

      retval = bout.toByteArray();
    }
    return retval;
  }

  public static Object toObject(final byte[] bytes) throws IOException, ClassNotFoundException {
    Object retval = null;
    if (bytes != null) {
      final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
      final ObjectInputStream inputStream = new ObjectInputStream(bin);

      retval = inputStream.readObject();
    }
    return retval;
  }
}
