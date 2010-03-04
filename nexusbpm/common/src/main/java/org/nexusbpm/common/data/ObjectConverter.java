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
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import org.nexusbpm.common.util.ObjectConversionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

public class ObjectConverter {
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO8601_TIME_FORMAT = "HH:mm:ss.SSSZ";
    public static final String ISO8601_DATETIME_FORMAT =  ISO8601_DATE_FORMAT + "'T'" + ISO8601_TIME_FORMAT;
    
    public static final String JDBC_DATE_FORMAT = ISO8601_DATE_FORMAT;
    public static final String JDBC_TIME_FORMAT = "HH:mm:ss";
    
    // SimpleDateFormat is not sufficient for JDBC Datetime format: the following
    // string will recognize a JDBC timestamp value, but will leave off the nanoseconds
    public static final String JDBC_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    public static final String JDBC_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String JDBC_TIME_REGEX = "\\d{2}:\\d{2}:\\d{2}";
    public static final String JDBC_DATETIME_REGEX =
        JDBC_DATE_REGEX + " " + JDBC_TIME_REGEX + "([.]\\d{1,9})?";
    
    private static final ThreadLocal<SimpleDateFormat> LOCAL_ISO8601_DATE_FORMAT =
        new SimpleDateFormatThreadLocal(ISO8601_DATE_FORMAT);
    private static final ThreadLocal<SimpleDateFormat> LOCAL_ISO8601_TIME_FORMAT =
        new SimpleDateFormatThreadLocal(ISO8601_TIME_FORMAT);
    private static final ThreadLocal<SimpleDateFormat> LOCAL_ISO8601_DATETIME_FORMAT =
        new SimpleDateFormatThreadLocal(ISO8601_DATETIME_FORMAT);
    
    private static final ThreadLocal<SimpleDateFormat> LOCAL_JDBC_TIME_FORMAT =
        new SimpleDateFormatThreadLocal(JDBC_TIME_FORMAT);
    private static final ThreadLocal<SimpleDateFormat> LOCAL_JDBC_DATETIME_FORMAT =
        new SimpleDateFormatThreadLocal(JDBC_DATETIME_FORMAT);
    
    private static final Pattern LOCAL_JDBC_DATE_REGEX = Pattern.compile(JDBC_DATE_REGEX);
    private static final Pattern LOCAL_JDBC_TIME_REGEX = Pattern.compile(JDBC_TIME_REGEX);
    private static final Pattern LOCAL_JDBC_DATETIME_REGEX = Pattern.compile(JDBC_DATETIME_REGEX);
    
    private static class SimpleDateFormatThreadLocal extends ThreadLocal<SimpleDateFormat> {
        private final String format;
        public SimpleDateFormatThreadLocal(String format) {
            this.format = format;
        }
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(format);
        }
    }
    
    public static Object convert(Object source, ParameterType type) throws ObjectConversionException {
        return convert(source, type.getJavaClass());
    }
    
    public static Object convert(Object source, Class dest) throws ObjectConversionException {
        if (dest == null) {
            throw new ObjectConversionException("Unable to convert to a null type!");
        }
        try {
            Object result = null;
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
            } else if(dest.equals(java.sql.Time.class)) {
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
        } catch(Exception e) {
            throw new ObjectConversionException("Unable to convert to type " + dest.getSimpleName(), e);
        }
    }
    
    public static Object convert(Object source) {
        return convert(source, false);
    }
    
    /**
     * @param sql whether SQL Date/Time classes should be preferred for
     *            parsing over java.util.Date
     */
    public static Object convert(Object source, boolean sql) {
        // try to convert the value if it's not null, but leave certain object types alone
        if(source != null &&
            !(source instanceof URI ||
                source instanceof Number ||
                source instanceof Date ||
                source instanceof Boolean)) {
            if("".equals(source)) {
                return source;
            }
            
            // first see if it's some sort of serialized object
            try {
                Object value = convertToObject(source);
                if(value != null && value != source) {
                    return value;
                }
            } catch(Exception e) {
            }
            
            // convert the string and trimmed string ahead of time so
            // that they can be reused without having to be recomputed
            String sourceString = null;
            String sourceStringTrimmed = null;
            try {
                if(!source.getClass().isArray()) {
                    sourceString = source.toString();
                    sourceStringTrimmed = sourceString.trim();
                }
            } catch(Exception e) {
            }
            
            // try to turn it into an integer value, preferring the smallest
            // type that can hold the value
            try {
                if(sourceStringTrimmed != null) {
                    BigInteger bigint = new BigInteger(sourceStringTrimmed);
                    long value = bigint.longValue();
                    if(bigint.compareTo(BigInteger.valueOf(value)) != 0) {
                        return bigint;
                    } else if(value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                        return Long.valueOf(value);
                    } else if(value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                        return Integer.valueOf((int) value);
                    } else if(value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                        return Short.valueOf((short) value);
                    } else {
                        return Byte.valueOf((byte) value);
                    }
                }
            } catch(Exception e) {
            }
            
            // try to turn it into a floating point value, preferring the
            // smallest type that can hold the value
            try {
                if(sourceStringTrimmed != null) {
                    BigDecimal bigdec = new BigDecimal(sourceStringTrimmed);
                    double value = bigdec.doubleValue();
                    if(value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY ||
                            bigdec.compareTo(BigDecimal.valueOf(value)) != 0) {
                        return bigdec;
                    }
                    float fvalue = bigdec.floatValue();
                    if(fvalue == Float.POSITIVE_INFINITY || fvalue == Float.NEGATIVE_INFINITY ||
                            bigdec.compareTo(BigDecimal.valueOf(fvalue)) != 0) {
                        return Double.valueOf(value);
                    } else {
                        return Float.valueOf(fvalue);
                    }
                }
            } catch(Exception e) {
            }
            
            // if there is a preference for SQL date/time types, try those
            if(sql) {
                try {
                    if(LOCAL_JDBC_DATETIME_REGEX.matcher(sourceStringTrimmed).matches()) {
                        return java.sql.Timestamp.valueOf(sourceStringTrimmed);
                    }
                } catch(Exception e) {
                }
                try {
                    if(LOCAL_JDBC_TIME_REGEX.matcher(sourceStringTrimmed).matches()) {
                        return java.sql.Time.valueOf(sourceStringTrimmed);
                    }
                } catch(Exception e) {
                }
                try {
                    if(LOCAL_JDBC_DATE_REGEX.matcher(sourceStringTrimmed).matches()) {
                        return java.sql.Date.valueOf(sourceStringTrimmed);
                    }
                } catch(Exception e) {
                }
            }
            
            // try to parse a date using the ISO format
            ParsePosition position = new ParsePosition(0);
            try {
                return parseDate(
                        LOCAL_ISO8601_DATETIME_FORMAT.get(),
                        sourceStringTrimmed,
                        position);
            } catch(Exception e) {
            }
            try {
                return parseDate(
                        LOCAL_ISO8601_TIME_FORMAT.get(),
                        sourceStringTrimmed,
                        position);
            } catch(Exception e) {
            }
            try {
                return parseDate(
                        LOCAL_ISO8601_DATE_FORMAT.get(),
                        sourceStringTrimmed,
                        position);
            } catch(Exception e) {
            }
            
            // if there isn't a preference for SQL date/time types, we still need to
            // check if the value was formatted as a SQL date/time type
            if(!sql) {
                // SQL date is the same as the ISO date, so we can skip that one
                try {
                    return parseDate(
                            LOCAL_JDBC_DATETIME_FORMAT.get(),
                            sourceStringTrimmed,
                            position);
                } catch(Exception e) {
                }
                try {
                    return parseDate(
                            LOCAL_JDBC_TIME_FORMAT.get(),
                            sourceStringTrimmed,
                            position);
                } catch(Exception e) {
                }
            }
            
            if(sourceStringTrimmed != null) {
                // see if it's a boolean in string format
                if(sourceStringTrimmed.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                } else if(sourceStringTrimmed.equalsIgnoreCase("false")) {
                    return Boolean.FALSE;
                }
            }
        }
        return source;
    }
    
    protected static Date parseDate(SimpleDateFormat format, String text, ParsePosition position)
            throws ObjectConversionException {
        int index = position.getIndex();
        Date d = format.parse(text, position);
        if(d == null || position.getIndex() < text.length()) {
            position.setIndex(index);
            throw new ObjectConversionException("Could not parse '" + text + "'");
        }
        return d;
    }
    
    public static Double convertToDouble(Object source) throws ObjectConversionException {
        Double result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (Double) toObject(base64Decode((String) source));
                } catch(Exception e) {
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
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Double.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Double.");
        }
        return result;
    }
    
    public static Float convertToFloat(Object source) throws ObjectConversionException {
        Float result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (Float) toObject(base64Decode((String) source));
                } catch(Exception e) {
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
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Float.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Float.");
        }
        return result;
    }
    
    public static Long convertToLong(Object source) throws ObjectConversionException {
        Long result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (Long) toObject(base64Decode((String) source));
                } catch(Exception e) {
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
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Long.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Long."); 
        }
        return result;
    }
    
    public static Integer convertToInteger(Object source) throws ObjectConversionException {
        Integer result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (Integer) toObject(base64Decode((String) source));
                } catch(Exception e) {
                    result = Integer.valueOf((String) source);
                }
            }
        } else if (source instanceof Number) {
            result = new Integer(new Long(Math.round(((Number) source).doubleValue())).toString());
        } else if (source instanceof Boolean) {
            result = new Integer(((Boolean) source).booleanValue() ? 1 : 0);
        } else if (source instanceof Date) {
            result = new Integer(new Long(((Date) source).getTime()).toString());
        } else if (source instanceof byte[]) {
            try {
                result = (Integer) toObject((byte[]) source);
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to an Integer.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to an Integer."); 
        }
        return result;
    }
    
    public static BigInteger convertToBigInteger(Object source) throws ObjectConversionException {
        BigInteger result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (BigInteger) toObject(base64Decode((String) source));
                } catch(Exception e) {
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
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a BigInteger.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a BigInteger.");
        }
        return result;
    }
    
    public static BigDecimal convertToBigDecimal(Object source) throws ObjectConversionException {
        BigDecimal result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (BigDecimal) toObject(base64Decode((String) source));
                } catch(Exception e) {
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
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a BigInteger.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a BigInteger.");
        }
        return result;
    }
    
    public static Boolean convertToBoolean(Object source) throws ObjectConversionException {
        Boolean result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = Boolean.FALSE;
            } else {
                try {
                    result = (Boolean) toObject(base64Decode((String) source));
                } catch(Exception e) {
                    result = Boolean.valueOf((String) source);
                }
            }
        } else if (source instanceof Number) {
            result = Boolean.valueOf(((Number) source).intValue() != 0);
        } else if (source instanceof byte[]) {
            try {
                result = (Boolean) toObject((byte[]) source);
            } catch(Exception e) {
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Boolean.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a Boolean."); 
        }
        return result;
    }

    public static Date convertToDate(Object source) throws ObjectConversionException {
        Date result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (Date) toObject(base64Decode((String) source));
                } catch(Exception e) {
                    try {
                        result = LOCAL_ISO8601_DATETIME_FORMAT.get().parse((String) source);
                    } catch(Exception ex) {
                        try {
                            result = LOCAL_ISO8601_TIME_FORMAT.get().parse((String) source);
                        } catch(Exception exc) {
                        }
                        if(result == null) {
                            try {
                                result = new Date(java.sql.Timestamp.valueOf((String) source).getTime());
                            } catch(Exception exc) {
                            }
                        }
                        if(result == null) {
                            try {
                                result = new Date(java.sql.Time.valueOf((String) source).getTime());
                            } catch(Exception exc) {
                            }
                        }
                        if(result == null) {
                            try {
                                result = LOCAL_ISO8601_DATE_FORMAT.get().parse((String) source);
                            } catch(Exception exc) {
                                throw new ObjectConversionException(
                                        "Unable to convert String to Date using String's date format:" +
                                        source.toString() + ". Instead use " + ISO8601_DATETIME_FORMAT, ex);
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
            } catch(Exception e) {
                throw new ObjectConversionException(
                        "Unable to convert a " + source.getClass().getName() + " to a Date.", e);
            }
        } else if (source instanceof Date) {
            result = new Date(((Date) source).getTime());
        } else {
            throw new ObjectConversionException(
                    "Unable to convert a " + source.getClass().getName() + " to a Date."); 
        }
        return result;
    }
    
    public static java.sql.Date convertToSQLDate(Object source) throws ObjectConversionException {
        java.sql.Date result = null;
        if(source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (java.sql.Date) toObject(base64Decode((String) source));
                } catch(Exception e) {
                    try {
                        result = java.sql.Date.valueOf((String) source);
                    } catch(Exception ex) {
                        try {
                            result = new java.sql.Date(convertToDate(source).getTime());
                        } catch(Exception exc) {
                            throw new ObjectConversionException(
                                    "Unable to convert String to SQL Date using JDBC Date format:" +
                                    source.toString() + ". Instead use " + JDBC_DATE_FORMAT, ex);
                        }
                    }
                }
            }
        } else if(source instanceof Number) {
            result = new java.sql.Date(((Number) source).longValue());
        } else if(source instanceof byte[]) {
            try {
                result = (java.sql.Date) toObject((byte[]) source);
            } catch(Exception e) {
                throw new ObjectConversionException(
                        "Unable to convert a " + source.getClass().getName() + " to an SQL Date.", e);
            }
        } else if(source instanceof Date) {
            result = new java.sql.Date(((Date) source).getTime());
        } else {
            throw new ObjectConversionException(
                    "Unable to convert a " + source.getClass().getName() + " to an SQL Date.");
        }
        return result;
    }
    
    public static java.sql.Time convertToSQLTime(Object source) throws ObjectConversionException {
        java.sql.Time result = null;
        if(source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (java.sql.Time) toObject(base64Decode((String) source));
                } catch(Exception e) {
                    try {
                        result = java.sql.Time.valueOf((String) source);
                    } catch(Exception ex) {
                        try {
                            result = new java.sql.Time(convertToDate(source).getTime());
                        } catch(Exception exc) {
                            throw new ObjectConversionException(
                                    "Unable to convert String to SQL Time using JDBC Time format:" +
                                    source.toString() + ". Instead use " + JDBC_TIME_FORMAT, ex);
                        }
                    }
                }
            }
        } else if(source instanceof Number) {
            result = new java.sql.Time(((Number) source).longValue());
        } else if(source instanceof byte[]) {
            try {
                result = (java.sql.Time) toObject((byte[]) source);
            } catch(Exception e) {
                throw new ObjectConversionException(
                        "Unable to convert a " + source.getClass().getName() + " to an SQL Time.", e);
            }
        } else if(source instanceof Date) {
            result = new java.sql.Time(((Date) source).getTime());
        } else {
            throw new ObjectConversionException(
                    "Unable to convert a " + source.getClass().getName() + " to an SQL Time.");
        }
        return result;
    }
    
    public static java.sql.Timestamp convertToSQLTimestamp(Object source) throws ObjectConversionException {
        java.sql.Timestamp result = null;
        if(source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (java.sql.Timestamp) toObject(base64Decode((String) source));
                } catch(Exception e) {
                    try {
                        result = java.sql.Timestamp.valueOf((String) source);
                    } catch(Exception ex) {
                        try {
                            result = new java.sql.Timestamp(convertToDate(source).getTime());
                        } catch(Exception exc) {
                            throw new ObjectConversionException(
                                    "Unable to convert String to SQL Date using JDBC Timestamp format:" +
                                    source.toString() + ". Instead use " + JDBC_DATETIME_FORMAT, ex);
                        }
                    }
                }
            }
        } else if(source instanceof Number) {
            result = new java.sql.Timestamp(((Number) source).longValue());
        } else if(source instanceof byte[]) {
            try {
                result = (java.sql.Timestamp) toObject((byte[]) source);
            } catch(Exception e) {
                throw new ObjectConversionException(
                        "Unable to convert a " + source.getClass().getName() + " to an SQL Timestamp.", e);
            }
        } else if(source instanceof Date) {
            result = new java.sql.Timestamp(((Date) source).getTime());
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to an SQL Timestamp.");
        }
        return result;
    }

    public static URI convertToURI(Object source) throws ObjectConversionException {
        URI result = null;
        if (source instanceof String) {
            if(((String) source).length() == 0) {
                result = null;
            } else {
                try {
                    result = (URI) toObject(base64Decode((String) source));
                } catch(Exception e) {
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
                throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a URI.", e);
            }
        } else {
            throw new ObjectConversionException("Unable to convert a " + source.getClass().getName() + " to a URI."); 
        }
        return result;
    }

    public static String convertToString(Object source) throws IOException {
        String result = null;
        if (source instanceof Date) {
            if (source instanceof java.sql.Date ||
                    source instanceof java.sql.Time ||
                    source instanceof java.sql.Timestamp) {
                result = source.toString();
            } else {
                SimpleDateFormat sdf = LOCAL_ISO8601_DATETIME_FORMAT.get();
                result = sdf.format((Date) source);
            }
        } else if (source instanceof URI) {
            FileSystemManager manager = VFS.getManager();
            FileObject fileObject = manager.resolveFile(((URI)source).toString()); //may need to do toAsciiString
            InputStream istream = fileObject.getContent().getInputStream();
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
    
    public static byte[] convertToBinary(Object source) throws ObjectConversionException {
        try {
            if (source instanceof URI) {
            FileSystemManager manager = VFS.getManager();
            FileObject fileObject = manager.resolveFile(((URI)source).toString()); //may need to do toAsciiString
            InputStream istream = fileObject.getContent().getInputStream();
            return IOUtils.toByteArray(istream);
            }
        } catch (IOException e1) {
            throw new ObjectConversionException("Unable to read contents of file into binary variable", e1);
        }
        if(source instanceof String && ((String) source).length() == 0) {
            return null;
        } else {
            try {
                return toByteArray(source);
            } catch(Exception e) {
                throw new ObjectConversionException();
            }
        }
    }
    
    public static Object convertToObject(Object source) throws ObjectConversionException {
        Object result = null;
        if (source instanceof byte[]) {
            if(((byte[]) source).length == 0) {
                result = null;
            } else {
                try {
                    result = toObject((byte[]) source);
                } catch(Exception e) {
                    throw new ObjectConversionException();
                }
            }
        } else if (source instanceof String) {
            try {
                result = toObject(base64Decode((String) source));
            } catch(Exception e) {
                result = null;
            }
        } else {
            result = source;
        }
        return result;
    }
    
    public static String base64Encode(byte[] data) {
        BASE64Encoder e = new BASE64Encoder();
        return data == null ? null : e.encode(data);
    }
    
    public static byte[] base64Decode(String s) {
        BASE64Decoder d = new BASE64Decoder();
        try {
            return s == null ? null : d.decodeBuffer(s);
        } catch(IOException e) {
            return null;
        }
    }
    
    public static byte[] toByteArray(Object object) throws IOException {
        byte[] retval = null;
        if(object != null) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(object);
            
            retval = bout.toByteArray();
        }
        return retval;
    }
    
    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object retval = null;
        if(bytes != null) {
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bin);
            
            retval = in.readObject();
        }
        return retval;
    }
}
