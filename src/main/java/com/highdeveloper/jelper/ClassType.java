package com.highdeveloper.jelper;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author carlos.silva
 */
public class ClassType {

  public static final Set<Class> PRIMITIVE_CLASSES = new HashSet<Class>();
  public static final Set<Class> WRAPPER_CLASSES = new HashSet<Class>();
  public static final Set<Class> PRIMITIVE_ARRAY_CLASSES = new HashSet<Class>();
  public static final Set<Class> WRAPPER_ARRAY_CLASSES = new HashSet<Class>();
  public static final Set<Class> DATE_TIME_CLASSES = new HashSet<Class>();

  static {
    PRIMITIVE_CLASSES.add(boolean.class);
    PRIMITIVE_CLASSES.add(int.class);
    PRIMITIVE_CLASSES.add(double.class);
    PRIMITIVE_CLASSES.add(char.class);
    PRIMITIVE_CLASSES.add(float.class);
    PRIMITIVE_CLASSES.add(byte.class);
    PRIMITIVE_CLASSES.add(short.class);
    PRIMITIVE_CLASSES.add(long.class);

    WRAPPER_CLASSES.add(String.class);
    WRAPPER_CLASSES.add(Boolean.class);
    WRAPPER_CLASSES.add(Integer.class);
    WRAPPER_CLASSES.add(Double.class);
    WRAPPER_CLASSES.add(Character.class);
    WRAPPER_CLASSES.add(Float.class);
    WRAPPER_CLASSES.add(Byte.class);
    WRAPPER_CLASSES.add(Short.class);
    WRAPPER_CLASSES.add(Long.class);

    PRIMITIVE_ARRAY_CLASSES.add(boolean[].class);
    PRIMITIVE_ARRAY_CLASSES.add(int[].class);
    PRIMITIVE_ARRAY_CLASSES.add(double[].class);
    PRIMITIVE_ARRAY_CLASSES.add(char[].class);
    PRIMITIVE_ARRAY_CLASSES.add(float[].class);
    PRIMITIVE_ARRAY_CLASSES.add(byte[].class);
    PRIMITIVE_ARRAY_CLASSES.add(short[].class);
    PRIMITIVE_ARRAY_CLASSES.add(long[].class);

    WRAPPER_ARRAY_CLASSES.add(String[].class);
    WRAPPER_ARRAY_CLASSES.add(Boolean[].class);
    WRAPPER_ARRAY_CLASSES.add(Integer[].class);
    WRAPPER_ARRAY_CLASSES.add(Double[].class);
    WRAPPER_ARRAY_CLASSES.add(Character[].class);
    WRAPPER_ARRAY_CLASSES.add(Float[].class);
    WRAPPER_ARRAY_CLASSES.add(Byte[].class);
    WRAPPER_ARRAY_CLASSES.add(Short[].class);
    WRAPPER_ARRAY_CLASSES.add(Long[].class);

    DATE_TIME_CLASSES.add(Date.class);
    DATE_TIME_CLASSES.add(Timestamp.class);
    DATE_TIME_CLASSES.add(Time.class);
    DATE_TIME_CLASSES.add(java.sql.Date.class);
  }
}
