package com.highdeveloper.jelper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author carlos.silva
 */
public class ObjectProperty {

  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

  public static void copyPropertyValues(Object originObject, Object destinationObject) throws Exception {

    if (originObject == null || destinationObject == null) {
      
      throw new IllegalArgumentException("Origin and destination objects cannot be null.");
    }
    
    Field allOriginFields[];
    Field allDestinationFields[];

    Field originFields[] = originObject.getClass().getDeclaredFields();
    Field destinationFields[] = destinationObject.getClass().getDeclaredFields();
    
    if (originObject.getClass().getSuperclass() != null) {

      Field originSuperFields[] = originObject.getClass().getSuperclass().getDeclaredFields();
      allOriginFields = new Field[originFields.length + originSuperFields.length];
      
      System.arraycopy(originFields, 0, allOriginFields, 0, originFields.length);
      System.arraycopy(originSuperFields, 0, allOriginFields, originFields.length, originSuperFields.length);
    }
    else{
      allOriginFields = originFields;
    }

    if (destinationObject.getClass().getSuperclass() != null) {

      Field destinationSuperFields[] = destinationObject.getClass().getSuperclass().getDeclaredFields();
      allDestinationFields = new Field[destinationFields.length + destinationSuperFields.length];
      
      System.arraycopy(destinationFields, 0, allDestinationFields, 0, destinationFields.length);
      System.arraycopy(destinationSuperFields, 0, allDestinationFields, destinationFields.length, destinationSuperFields.length);
    }
    else{
      allDestinationFields =  destinationFields;
    }

    if (allOriginFields != null && allDestinationFields != null && allOriginFields.length > 0 && allDestinationFields.length > 0) {

      for (Field originField : allOriginFields) {

        if ("serialVersionUID".equals(originField.getName())) {
          continue;
        }

        for (Field destinationField : allDestinationFields) {

          if ("serialVersionUID".equals(destinationField.getName())) {
            continue;
          }

          if (originField.getName().equals(destinationField.getName())) {

            Class<?> originFieldClass = originField.getType();
            Class<?> destinationFieldClass = destinationField.getType();
            originField.setAccessible(true);
            destinationField.setAccessible(true);

            if (ClassType.PRIMITIVE_CLASSES.contains(originFieldClass) || ClassType.WRAPPER_CLASSES.contains(originFieldClass)
                    || ClassType.PRIMITIVE_ARRAY_CLASSES.contains(originFieldClass)) {

              Object genericObject = originField.get(originObject);

              if (originFieldClass == String.class && ClassType.DATE_TIME_CLASSES.contains(destinationFieldClass)) {

                if (genericObject != null) {

                  String dateAsString = (String) genericObject;
                  Date genericDate;
                  if (dateAsString.length() > 11) {
                    genericDate = DATE_TIME_FORMAT.parse(dateAsString);
                  }
                  else {
                    genericDate = DATE_FORMAT.parse(dateAsString);
                  }

                  Object obj;
                  if (destinationFieldClass == Timestamp.class) {
                    obj = new Timestamp(genericDate.getTime());
                  }
                  else if (destinationFieldClass == Time.class) {
                    obj = new Time(genericDate.getTime());
                  }
                  else if (destinationFieldClass == java.sql.Date.class) {
                    obj = new java.sql.Date(genericDate.getTime());
                  }
                  else {
                    obj = genericDate;
                  }
                  destinationField.set(destinationObject, obj);
                }
              }
              else {

                destinationField.set(destinationObject, genericObject);
              }

            }
            else if (ClassType.DATE_TIME_CLASSES.contains(originFieldClass)) {

              Object genericObject = originField.get(originObject);

              if (ClassType.DATE_TIME_CLASSES.contains(destinationFieldClass)) {

                destinationField.set(destinationObject, genericObject);

              }
              else if (destinationFieldClass == String.class) {

                if (genericObject != null) {

                  String genericFormattedDate = DATE_TIME_FORMAT.format(genericObject);
                  destinationField.set(destinationObject, genericFormattedDate);
                }
              }

            }
            else if (ClassType.WRAPPER_ARRAY_CLASSES.contains(originFieldClass)) {

              Object genericObject = originField.get(originObject);

              if (ClassType.WRAPPER_ARRAY_CLASSES.contains(destinationFieldClass)) {

                destinationField.set(destinationObject, genericObject);

              }
              else if (destinationFieldClass == List.class) {

                if (genericObject != null) {
                  Object[] genericArray = (Object[]) genericObject;
                  destinationField.set(destinationObject, Arrays.asList(genericArray));
                }
              }
              else if (destinationFieldClass == Set.class) {

                if (genericObject != null) {
                  Object[] genericArray = (Object[]) genericObject;
                  destinationField.set(destinationObject, new HashSet<Object>(Arrays.asList(genericArray)));
                }
              }
            }
            else if (originFieldClass == List.class || originFieldClass == Set.class) {

              ParameterizedType originParameterizedField = (ParameterizedType) originField.getGenericType();
              Class<?> originParameterizedFieldClass = (Class<?>) originParameterizedField.getActualTypeArguments()[0];

              if (ClassType.PRIMITIVE_CLASSES.contains(originParameterizedFieldClass) || ClassType.WRAPPER_CLASSES.contains(originParameterizedFieldClass)) {

                if (destinationFieldClass == List.class || destinationFieldClass == Set.class) {
                  destinationField.set(destinationObject, originField.get(originObject));
                }
                else if (ClassType.WRAPPER_ARRAY_CLASSES.contains(destinationFieldClass)) {

                  Object genericObject = originField.get(originObject);
                  if (genericObject != null) {
                    Collection<?> genericCollection = (Collection<?>) genericObject;
                    Object[] genericArray = (Object[]) Array.newInstance(destinationFieldClass.getComponentType(), genericCollection.size());
                    destinationField.set(destinationObject, genericCollection.toArray(genericArray));
                  }
                }
              }
              else {
                Object genericObject = originField.get(originObject);

                if (destinationFieldClass == List.class || destinationFieldClass == Set.class) {

                  if (genericObject != null) {

                    ParameterizedType destinationParameterizedField = (ParameterizedType) destinationField.getGenericType();
                    Class<?> destinationParameterizedFieldClass = (Class<?>) destinationParameterizedField.getActualTypeArguments()[0];
                    Collection<?> genericCollection = (Collection<?>) genericObject;
                    Collection<Object> destinationFieldCollection = (Collection<Object>) (destinationFieldClass == List.class ? new ArrayList<Object>() : new HashSet<Object>());

                    for (Object obj : genericCollection) {
                      Object destinationFieldObjectCollection = destinationParameterizedFieldClass.newInstance();
                      destinationFieldCollection.add(destinationFieldObjectCollection);
                      copyPropertyValues(obj, destinationFieldObjectCollection);
                    }
                    destinationField.set(destinationObject, destinationFieldCollection);
                  }
                }
                else if (destinationFieldClass.isArray()) {

                  if (genericObject != null) {

                    Class<?> destinationParameterizedFieldClass = destinationFieldClass.getComponentType();
                    Collection<?> genericCollection = (Collection<?>) genericObject;
                    Object[] genericArray = (Object[]) Array.newInstance(destinationParameterizedFieldClass, genericCollection.size());
                    int index = 0;
                    for (Object originObj : genericCollection) {
                      Object destinationObj = destinationParameterizedFieldClass.newInstance();
                      genericArray[index] = destinationObj;
                      copyPropertyValues(originObj, destinationObj);
                      index++;
                    }
                    destinationField.set(destinationObject, genericArray);
                  }
                }
              }

            }
            else if (originFieldClass.isArray()) {

              Object genericObject = originField.get(originObject);

              if (destinationFieldClass.isArray()) {

                if (genericObject != null) {

                  Object[] genericArray = (Object[]) genericObject;
                  Class<?> destinationParameterizedFieldClass = destinationFieldClass.getComponentType();
                  Object[] destinationFieldArray = (Object[]) Array.newInstance(destinationParameterizedFieldClass, genericArray.length);

                  for (int i = 0; i < genericArray.length; i++) {
                    Object destinationObj = destinationParameterizedFieldClass.newInstance();
                    destinationFieldArray[i] = destinationObj;
                    copyPropertyValues(genericArray[i], destinationObj);
                  }
                  destinationField.set(destinationObject, destinationFieldArray);
                }
              }
              else if (destinationFieldClass == List.class || destinationFieldClass == Set.class) {

                if (genericObject != null) {

                  Object[] genericArray = (Object[]) genericObject;
                  ParameterizedType destinationParameterizedField = (ParameterizedType) destinationField.getGenericType();
                  Class<?> destinationParameterizedFieldClass = (Class<?>) destinationParameterizedField.getActualTypeArguments()[0];
                  Collection<Object> destinationFieldCollection = (Collection<Object>) (destinationFieldClass == List.class ? new ArrayList<Object>() : new HashSet<Object>());

                  for (Object obj : genericArray) {
                    Object destinationFieldObjectCollection = destinationParameterizedFieldClass.newInstance();
                    destinationFieldCollection.add(destinationFieldObjectCollection);
                    copyPropertyValues(obj, destinationFieldObjectCollection);
                  }
                  destinationField.set(destinationObject, destinationFieldCollection);
                }
              }
            }
            else {
              Object genericObject = destinationField.get(destinationObject);
              if (genericObject == null) {
                genericObject = destinationFieldClass.newInstance();
                destinationField.set(destinationObject, genericObject);
              }
              copyPropertyValues(originField.get(originObject), destinationField.get(destinationObject));
            }
          }
        }
      }
    }

  }

}
