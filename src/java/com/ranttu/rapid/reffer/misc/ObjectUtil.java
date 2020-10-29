/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.misc;

import com.ranttu.rapid.reffer.misc.asm.ClassReader;
import com.ranttu.rapid.reffer.misc.asm.util.TraceClassVisitor;
import lombok.experimental.UtilityClass;
import lombok.experimental.var;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * object utils
 *
 * @author rapid
 * @version $Id: Objects.java, v 0.1 2018年09月20日 10:29 PM rapid Exp $
 */
@UtilityClass
public class ObjectUtil {
    /**
     * cached fields
     */
    private final Map<Class<?>, List<Field>> classFields = new WeakHashMap<>();

    /**
     * check object is primitive object
     *
     * @param o any object
     * @return NOTE that null is treated as non-primitive
     */
    public boolean isPrimitive(Object o) {
        return o instanceof String
            || o instanceof Integer
            || o instanceof Boolean
            || o instanceof Byte
            || o instanceof Short
            || o instanceof Long
            || o instanceof Float
            || o instanceof Double
            || o instanceof Character;
    }

    /**
     * check the clazz is primitive clazz
     */
    public boolean isPrimitive(Class<?> clz) {
        return clz.isPrimitive()
            || clz == String.class
            || clz == Integer.class
            || clz == Boolean.class
            || clz == Byte.class
            || clz == Short.class
            || clz == Long.class
            || clz == Float.class
            || clz == Double.class
            || clz == Character.class;
    }

    /**
     * resolve all fields of a class
     *
     * @param cl class to be solved
     * @return fields
     */
    public List<Field> getAllFields(Class<?> cl) {
        if (cl == Object.class) {
            return new ArrayList<>();
        }

        return classFields.computeIfAbsent(cl, k -> {
            var fields = new ArrayList<Field>();
            Collections.addAll(fields, cl.getDeclaredFields());
            fields.addAll(getAllFields(cl.getSuperclass()));

            return fields;
        });
    }

    /**
     * whether class has default constructor
     */
    public boolean hasDefaultConstructor(Class<?> cl) {
        try {
            cl.getDeclaredConstructor();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }


    /**
     * print the class from byte code
     *
     * @param className the name of the class
     * @param bytes     the bytes
     */
    public void printClass(String className, byte[] bytes) {
        if (Boolean.parseBoolean(System.getProperty("reffer.printBC"))) {
            System.out.println("========Class: " + className);
            ClassReader reader = new ClassReader(bytes);
            reader.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
            System.out.println();
        }
    }
}