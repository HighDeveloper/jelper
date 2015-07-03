package refle;

import bean1.BeanTest1;
import bean1.BeanTest3;
import bean2.BeanTest2;
import bean2.BeanTest4;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class BeanProperty {

    private static final Set<Class<?>> PRIMITIVE_WRAPPER_CLASSES = new HashSet<Class<?>>();
    private static final Set<Class<?>> ARRAY_PRIMITIVE_CLASSES = new HashSet<Class<?>>();
    private static final Set<Class<?>> ARRAY_WRAPPER_CLASSES = new HashSet<Class<?>>();
    private static final Set<Class<?>> DATE_TIME_CLASSES = new HashSet<Class<?>>();
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    static {
        PRIMITIVE_WRAPPER_CLASSES.add(boolean.class);
        PRIMITIVE_WRAPPER_CLASSES.add(int.class);
        PRIMITIVE_WRAPPER_CLASSES.add(double.class);
        PRIMITIVE_WRAPPER_CLASSES.add(char.class);
        PRIMITIVE_WRAPPER_CLASSES.add(float.class);
        PRIMITIVE_WRAPPER_CLASSES.add(byte.class);
        PRIMITIVE_WRAPPER_CLASSES.add(short.class);
        PRIMITIVE_WRAPPER_CLASSES.add(long.class);
        PRIMITIVE_WRAPPER_CLASSES.add(String.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Boolean.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Integer.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Double.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Character.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Float.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Byte.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Short.class);
        PRIMITIVE_WRAPPER_CLASSES.add(Long.class);

        ARRAY_PRIMITIVE_CLASSES.add(boolean[].class);
        ARRAY_PRIMITIVE_CLASSES.add(int[].class);
        ARRAY_PRIMITIVE_CLASSES.add(double[].class);
        ARRAY_PRIMITIVE_CLASSES.add(char[].class);
        ARRAY_PRIMITIVE_CLASSES.add(float[].class);
        ARRAY_PRIMITIVE_CLASSES.add(byte[].class);
        ARRAY_PRIMITIVE_CLASSES.add(short[].class);
        ARRAY_PRIMITIVE_CLASSES.add(long[].class);

        ARRAY_WRAPPER_CLASSES.add(String[].class);
        ARRAY_WRAPPER_CLASSES.add(Boolean[].class);
        ARRAY_WRAPPER_CLASSES.add(Integer[].class);
        ARRAY_WRAPPER_CLASSES.add(Double[].class);
        ARRAY_WRAPPER_CLASSES.add(Character[].class);
        ARRAY_WRAPPER_CLASSES.add(Float[].class);
        ARRAY_WRAPPER_CLASSES.add(Byte[].class);
        ARRAY_WRAPPER_CLASSES.add(Short[].class);
        ARRAY_WRAPPER_CLASSES.add(Long[].class);

        DATE_TIME_CLASSES.add(Date.class);
        DATE_TIME_CLASSES.add(Timestamp.class);
        DATE_TIME_CLASSES.add(Time.class);
        DATE_TIME_CLASSES.add(java.sql.Date.class);
    }

    public static void copyBeanProperties(Object originBean, Object destinationBean) throws Exception {

        if (originBean == null || destinationBean == null) {
            return;
        }

        Field originFields[] = originBean.getClass().getDeclaredFields();
        Field destinationFields[];

        if (originFields != null && originFields.length > 0) {

            destinationFields = destinationBean.getClass().getDeclaredFields();

            if (destinationFields != null && destinationFields.length > 0) {

                for (Field originField : originFields) {

                    String originNameField = originField.getName();

                    if ("serialVersionUID".equals(originNameField)) {
                        continue;
                    }

                    for (Field destinationField : destinationFields) {

                        if ("serialVersionUID".equals(destinationField.getName())) {
                            continue;
                        }

                        if (originNameField.equals(destinationField.getName())) {

                            Class<?> originFieldClass = originField.getType();
                            Class<?> destinationFieldClass = destinationField.getType();
                            originField.setAccessible(true);
                            destinationField.setAccessible(true);

                            if (PRIMITIVE_WRAPPER_CLASSES.contains(originFieldClass) || ARRAY_PRIMITIVE_CLASSES.contains(originFieldClass)) {

                                Object genericObject = originField.get(originBean);

                                if (originFieldClass == String.class && DATE_TIME_CLASSES.contains(destinationFieldClass)) {

                                    if (genericObject != null) {

                                        String dateAsString = (String) genericObject;
                                        Date genericDate;
                                        if (dateAsString.length() > 11) {
                                            genericDate = DATE_TIME_FORMAT.parse(dateAsString);
                                        } else {
                                            genericDate = DATE_FORMAT.parse(dateAsString);
                                        }
                                        
                                        Object obj;
                                        if (destinationFieldClass == Timestamp.class) {
                                            obj = new Timestamp(genericDate.getTime());
                                        } else if (destinationFieldClass == Time.class) {
                                            obj = new Time(genericDate.getTime());
                                        } else if (destinationFieldClass == java.sql.Date.class) {
                                            obj = new java.sql.Date(genericDate.getTime());
                                        } else {
                                            obj = genericDate;
                                        }
                                        destinationField.set(destinationBean, obj);
                                    }
                                } else {

                                    destinationField.set(destinationBean, genericObject);
                                }

                            } else if (DATE_TIME_CLASSES.contains(originFieldClass)) {

                                Object genericObject = originField.get(originBean);

                                if (DATE_TIME_CLASSES.contains(destinationFieldClass)) {

                                    destinationField.set(destinationBean, genericObject);

                                } else if (destinationFieldClass == String.class) {

                                    if (genericObject != null) {

                                        String genericFormattedDate = DATE_TIME_FORMAT.format(genericObject);
                                        destinationField.set(destinationBean, genericFormattedDate);
                                    }
                                }

                            } else if (ARRAY_WRAPPER_CLASSES.contains(originFieldClass)) {

                                Object genericObject = originField.get(originBean);

                                if (ARRAY_WRAPPER_CLASSES.contains(destinationFieldClass)) {

                                    destinationField.set(destinationBean, genericObject);

                                } else if (destinationFieldClass == List.class) {

                                    if (genericObject != null) {
                                        Object[] genericArray = (Object[]) genericObject;
                                        destinationField.set(destinationBean, Arrays.asList(genericArray));
                                    }
                                } else if (destinationFieldClass == Set.class) {

                                    if (genericObject != null) {
                                        Object[] genericArray = (Object[]) genericObject;
                                        destinationField.set(destinationBean, new HashSet<Object>(Arrays.asList(genericArray)));
                                    }
                                }
                            } else if (originFieldClass == List.class || originFieldClass == Set.class) {

                                ParameterizedType originParameterizedField = (ParameterizedType) originField.getGenericType();
                                Class<?> originParameterizedFieldClass = (Class<?>) originParameterizedField.getActualTypeArguments()[0];

                                if (PRIMITIVE_WRAPPER_CLASSES.contains(originParameterizedFieldClass)) {

                                    if (destinationFieldClass == List.class || destinationFieldClass == Set.class) {
                                        destinationField.set(destinationBean, originField.get(originBean));
                                    } else if (ARRAY_WRAPPER_CLASSES.contains(destinationFieldClass)) {

                                        Object genericObject = originField.get(originBean);
                                        if (genericObject != null) {
                                            Collection<?> genericCollection = (Collection<?>) genericObject;
                                            Object[] genericArray = (Object[]) Array.newInstance(destinationFieldClass.getComponentType(), genericCollection.size());
                                            destinationField.set(destinationBean, genericCollection.toArray(genericArray));
                                        }
                                    }
                                } else {
                                    Object genericObject = originField.get(originBean);

                                    if (destinationFieldClass == List.class || destinationFieldClass == Set.class) {

                                        if (genericObject != null) {

                                            ParameterizedType destinationParameterizedField = (ParameterizedType) destinationField.getGenericType();
                                            Class<?> destinationParameterizedFieldClass = (Class<?>) destinationParameterizedField.getActualTypeArguments()[0];
                                            Collection<?> genericCollection = (Collection<?>) genericObject;
                                            Collection<Object> destinationFieldCollection = (Collection<Object>) (destinationFieldClass == List.class ? new ArrayList<Object>() : new HashSet<Object>());

                                            for (Object obj : genericCollection) {
                                                Object destinationFieldObjectCollection = destinationParameterizedFieldClass.newInstance();
                                                destinationFieldCollection.add(destinationFieldObjectCollection);
                                                copyBeanProperties(obj, destinationFieldObjectCollection);
                                            }
                                            destinationField.set(destinationBean, destinationFieldCollection);
                                        }
                                    } else if (destinationFieldClass.isArray()) {

                                        if (genericObject != null) {

                                            Class<?> destinationParameterizedFieldClass = destinationFieldClass.getComponentType();
                                            Collection<?> genericCollection = (Collection<?>) genericObject;
                                            Object[] genericArray = (Object[]) Array.newInstance(destinationParameterizedFieldClass, genericCollection.size());
                                            int index = 0;
                                            for (Object originObj : genericCollection) {
                                                Object destinationObj = destinationParameterizedFieldClass.newInstance();
                                                genericArray[index] = destinationObj;
                                                copyBeanProperties(originObj, destinationObj);
                                                index++;
                                            }
                                            destinationField.set(destinationBean, genericArray);
                                        }
                                    }
                                }

                            } else if (originFieldClass.isArray()) {

                                Object genericObject = originField.get(originBean);

                                if (destinationFieldClass.isArray()) {

                                    if (genericObject != null) {

                                        Object[] genericArray = (Object[]) genericObject;
                                        Class<?> destinationParameterizedFieldClass = destinationFieldClass.getComponentType();
                                        Object[] destinationFieldArray = (Object[]) Array.newInstance(destinationParameterizedFieldClass, genericArray.length);

                                        for (int i = 0; i < genericArray.length; i++) {
                                            Object destinationObj = destinationParameterizedFieldClass.newInstance();
                                            destinationFieldArray[i] = destinationObj;
                                            copyBeanProperties(genericArray[i], destinationObj);
                                        }
                                        destinationField.set(destinationBean, destinationFieldArray);
                                    }
                                } else if (destinationFieldClass == List.class || destinationFieldClass == Set.class) {

                                    if (genericObject != null) {

                                        Object[] genericArray = (Object[]) genericObject;
                                        ParameterizedType destinationParameterizedField = (ParameterizedType) destinationField.getGenericType();
                                        Class<?> destinationParameterizedFieldClass = (Class<?>) destinationParameterizedField.getActualTypeArguments()[0];
                                        Collection<Object> destinationFieldCollection = (Collection<Object>) (destinationFieldClass == List.class ? new ArrayList<Object>() : new HashSet<Object>());

                                        for (Object obj : genericArray) {
                                            Object destinationFieldObjectCollection = destinationParameterizedFieldClass.newInstance();
                                            destinationFieldCollection.add(destinationFieldObjectCollection);
                                            copyBeanProperties(obj, destinationFieldObjectCollection);
                                        }
                                        destinationField.set(destinationBean, destinationFieldCollection);
                                    }
                                }
                            } else {
                                Object genericObject = destinationField.get(destinationBean);
                                if (genericObject == null) {
                                    genericObject = destinationFieldClass.newInstance();
                                    destinationField.set(destinationBean, genericObject);
                                }
                                copyBeanProperties(originField.get(originBean), destinationField.get(destinationBean));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        BeanTest1 bean1 = new BeanTest1();

        bean1.setName("aaaaaa");
        bean1.setCode(5);

        BeanTest3 bean3 = new BeanTest3();
        bean3.setText("hola333");
        bean3.setFlag(true);
        bean1.setBean3(bean3);

        Integer[] nums = new Integer[3];
        nums[0] = 5;
        nums[1] = 4;
        nums[2] = 3;
        bean1.setNumbers(nums);
        List<Character> list = new ArrayList<Character>();
        list.add('X');
        list.add('Y');
        Set<String> set = new HashSet<String>();
        set.add("TTWW");
        set.add("GGKK");
        bean1.setLetterList(list);
        bean1.setLetterSet(set);
        List<Integer> numlist = new ArrayList<Integer>();
        numlist.add(9);
        numlist.add(8);
        numlist.add(7);
        bean1.setNumList(numlist);
        List<BeanTest3> bean3List = new ArrayList<BeanTest3>();
        BeanTest3 b3_1 = new BeanTest3();
        BeanTest3 b3_2 = new BeanTest3();
        b3_1.setText("9999999999999999999");
        b3_1.setFlag(true);
        b3_2.setText("1111111111111111111");
        b3_2.setFlag(false);
        bean3List.add(b3_1);
        bean3List.add(b3_2);
        bean1.setBean4List(bean3List);
        //"2014-11-25 11:09:44"
        bean1.setFecha1(new java.sql.Date(new Date().getTime()));

        BeanTest2 bean2 = new BeanTest2();
        try {
            copyBeanProperties(bean1, bean2);
        } catch (Exception ex) {
            Logger.getLogger(RefleTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        //TESTS method
        System.out.println(bean2.getName() + " " + bean2.getCode());
        System.out.println(bean2.getBean3().getText() + " " + bean2.getBean3().isFlag());
        System.out.println(bean2.getNumbers()[0] + " " + bean2.getNumbers()[1] + " " + bean2.getNumbers()[2]);
        System.out.println(bean2.getLetterList()[0] + " " + bean2.getLetterList()[1]);
        System.out.println(bean2.getLetterSet()[0] + " " + bean2.getLetterSet()[1]);
        System.out.println(bean2.getNumList().get(0) + " " + bean2.getNumList().get(1) + " " + bean2.getNumList().get(2));
        System.out.println(bean2.getFecha1());

        for (BeanTest4 b4 : bean2.getBean4List()) {
            System.out.println(b4.getText() + " " + b4.isFlag());
        }
    }

}
