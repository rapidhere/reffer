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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * object utils
 *
 * @author rapid
 * @version $Id: Objects.java, v 0.1 2018��09��20�� 10:29 PM rapid Exp $
 */
@UtilityClass
public class ObjectUtil {
    /**
     * cached fields
     */
    private final Map<Class, List<Field>> classFields = new WeakHashMap<>();

    /**
     * cached properties
     */
    private final Map<Class, Map<String, Property>> classProperties = new WeakHashMap<>();

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
    public boolean isPrimitive(Class clz) {
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
    public List<Field> getAllFields(Class cl) {
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
     * resolve all properties of a class
     *
     * @param cl class to be solved
     * @return properties
     */
    public Map<String, Property> getAllProperties(Class cl) {
        if (cl == Object.class) {
            return Collections.emptyMap();
        }

        return classProperties.computeIfAbsent(cl, k -> {
            var properties = new HashMap<String, Property>();
            properties.putAll(getAllProperties(cl.getSuperclass()));
            if (cl.getInterfaces().length != 0) {
                for (Class inter : cl.getInterfaces()) {
                    properties.putAll(getAllProperties(inter));
                }
            }

            for (Method method : cl.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                // TODO: support static methods
                if (Modifier.isStatic(modifiers)
                    || Modifier.isNative(modifiers)
                    || !Modifier.isAbstract(modifiers)) {
                    continue;
                }

                // get getter/setter name
                boolean isSetter = false;
                String propertyName = null;
                if (method.getName().startsWith("is")) {
                    propertyName = StringUtil.decapFirst(method.getName().substring(2));
                } else if (method.getName().startsWith("get")) {
                    propertyName = StringUtil.decapFirst(method.getName().substring(3));
                } else if (method.getName().startsWith("set")) {
                    propertyName = StringUtil.decapFirst(method.getName().substring(3));
                    isSetter = true;
                }

                if (!StringUtil.isEmpty(propertyName)) {
                    // construct property
                    Property property = properties.computeIfAbsent(propertyName, name -> {
                        var prop = new Property();
                        prop.setName(name);
                        return prop;
                    });

                    if (isSetter) {
                        property.setSetter(method);
                    } else {
                        property.setGetter(method);
                    }
                }
            }

            return properties;
        });
    }


    /**
     * print the class from byte code
     *
     * @param className the name of the class
     * @param bytes     the bytes
     */
    public void printClass(String className, byte[] bytes) {
        if (Boolean.valueOf(System.getProperty("reffer.printBC"))) {
            System.out.println("========Class: " + className);
            ClassReader reader = new ClassReader(bytes);
            reader.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
            System.out.println();
        }
    }
}