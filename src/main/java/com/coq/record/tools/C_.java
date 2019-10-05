package com.coq.record.tools;

import com.coq.record.annotation.Record;

import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Common Tools
 * @author Quanyec
 */
public class C_ {
    public static boolean isBaseType(Object obj) {
        return obj instanceof Integer
                || obj instanceof Double
                || obj instanceof Long
                || obj instanceof Short
                || obj instanceof String
                || obj instanceof Character
                || obj instanceof Byte
                || obj instanceof Boolean;
    }

    public static boolean isBaseType(Class<?> type) {
        String typeName = type.getTypeName();
        return typeName.equals(Integer.class.getTypeName())
                || typeName.equals(Double.class.getTypeName())
                || typeName.equals(Long.class.getTypeName())
                || typeName.equals(Short.class.getTypeName())
                || typeName.equals(String.class.getTypeName())
                || typeName.equals(Character.class.getTypeName())
                || typeName.equals(Byte.class.getTypeName())
                || typeName.equals(Boolean.class.getTypeName());
    }

    public static boolean isRecord(Object obj) {
        Annotation[] annotations = obj.getClass().getDeclaredAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof Record) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRecord(Class<?> type) {
        Annotation[] annotations = type.getDeclaredAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof Record) {
                return true;
            }
        }
        return false;
    }

    public static void putArray(List<Object> list, Object arr, Object target, Field field) {
        int len = list.size();
        for (int i = 0; i < len; ++i) {
            Array.set(arr, i, list.get(i));
        }
        try {
            field.set(target, arr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
