package com.dev.tool.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtils {


    /**
     * 修改field的值，返回旧值
     *
     * @param filedName
     * @param object
     * @return
     * @throws Exception
     */
    public static <R> R modifyField(String filedName, Object object, R r) throws Exception {
        Class clazz = null;
        if (object instanceof Class) {
            clazz = (Class) object;
        } else {
            clazz = object.getClass();
        }

        //修改访问权限
        Field field = getField(filedName, clazz);
        boolean originAccessible = field.isAccessible();
        int originModifiers = field.getModifiers();
        Field modifiersField = null;
        try {
            //将属性设置可见
            field.setAccessible(true);

            //将属性的修改为非final约束
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            Object oldValue = field.get(clazz);
            field.set(object, r);
            return (R) oldValue;
        } finally {
            modifiersField.setInt(field, originModifiers);//还原访问控制
            modifiersField.setAccessible(false);//Field -- private int modifiers  还原
            field.setAccessible(originAccessible);
        }
    }


    public static Field getField(String filedName, Class clazz) throws Exception {
        return clazz.getDeclaredField(filedName);
    }
}
