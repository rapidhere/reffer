/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone;

import com.ranttu.rapid.reffer.clone.inner.FastClonerFactory;
import com.ranttu.rapid.reffer.misc.$;
import lombok.experimental.var;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * deep cloner
 *
 * @author rapid
 * @version $Id: Cloner.java, v 0.1 2018年09月20日 10:19 PM rapid Exp $
 */
public class Cloner {
    /**
     * clone configurations
     */
    private final CloneConfig config;

    /**
     * factory to generate fast cloner
     */
    private final FastClonerFactory fastClonerFactory;

    public Cloner() {
        this(CloneConfig.defaultConfig());
    }

    public Cloner(CloneConfig config) {
        this.config = config;
        this.fastClonerFactory = new FastClonerFactory(config);
    }

    /**
     * deep clone a object, use context class loader
     *
     * @param obj object to clone
     * @return a cloned object
     */
    public <T> T deepClone(T obj) {
        return deepClone(obj, Thread.currentThread().getContextClassLoader());
    }

    /**
     * deep clone a object
     *
     * @param obj object to clone
     * @param cl  class loader for new cloned object
     * @return a cloned object
     */
    public <T> T deepClone(T obj, ClassLoader cl) {
        @SuppressWarnings("unchecked")
        T result = (T) cloneInternal(obj, new IdentityHashMap<>(), cl);
        return result;
    }

    /**
     * clone internal impl
     */
    public Object cloneInternal(Object obj, Map<Object, Object> cloned, ClassLoader cl) {
        // omitted objects
        if (config.shouldIgnore(obj)) {
            return obj;
        }

        // cloned before, just return
        var clonedBefore = cloned.get(obj);
        if (clonedBefore != null) {
            return clonedBefore;
        }
        Class<?> clz = obj.getClass();

        // deal with array
        if (clz.isArray()) {
            return cloneArray(obj, clz, cloned, cl);
        }

        // try fast clone
        var fastCloned = fastClone(obj, clz, cloned, cl);
        if (fastCloned != null) {
            return fastCloned;
        }

        // default impl, current not support :)
        return $.notSupportedYet("clones without FAST CLONE ENABLED is not supported currently");
    }

    /**
     * clone a array
     */
    private Object cloneArray(Object obj, Class<?> clz, Map<Object, Object> cloned, ClassLoader cl) {
        var arrLength = Array.getLength(obj);

        Object useSysArrCopy = null;
        Class<?> componentType = null;

        if (obj instanceof int[]) {
            useSysArrCopy = new int[arrLength];
        } else if (obj instanceof String[]) {
            useSysArrCopy = new String[arrLength];
        } else if (obj instanceof boolean[]) {
            useSysArrCopy = new boolean[arrLength];
        } else if (obj instanceof byte[]) {
            useSysArrCopy = new byte[arrLength];
        } else if (obj instanceof short[]) {
            useSysArrCopy = new short[arrLength];
        } else if (obj instanceof long[]) {
            useSysArrCopy = new long[arrLength];
        } else if (obj instanceof float[]) {
            useSysArrCopy = new float[arrLength];
        } else if (obj instanceof double[]) {
            useSysArrCopy = new double[arrLength];
        } else if (obj instanceof char[]) {
            useSysArrCopy = new char[arrLength];
        } else {
            componentType = clz.getComponentType();
            if (config.shouldIgnore(componentType)) {
                useSysArrCopy = Array.newInstance(componentType, arrLength);
            }
        }

        // use system array copy
        if (useSysArrCopy != null) {
            cloned.put(obj, useSysArrCopy);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(obj, 0, useSysArrCopy, 0, arrLength);
            return useSysArrCopy;
        }

        // else use iter deep clone
        var result = Array.newInstance(componentType, arrLength);
        cloned.put(obj, result);

        if (arrLength == 0) {
            return result;
        }

        var arrScale = $.getUnsafe().arrayIndexScale(clz);
        var arrBase = $.getUnsafe().arrayBaseOffset(clz);
        for (var i = 0; i < arrLength; i++) {
            long offset = arrBase + arrScale * i;
            // clone element
            Object element = $.getUnsafe().getObject(obj, offset);
            element = cloneInternal(element, cloned, cl);

            // set element
            $.getUnsafe().putObject(result, offset, element);
        }
        return result;
    }

    /**
     * invoke fast clone impl!
     */
    private Object fastClone(Object obj, Class<?> clz, Map<Object, Object> cloned, ClassLoader cl) {
        // find fast cloner
        var fc = findFastCloner(clz, cl);
        if (fc != null) {
            return fc.clone(obj, cloned, cl, this);
        } else {
            return null;
        }
    }

    /**
     * find matched fast cloner
     */
    private FastCloner findFastCloner(Class<?> clz, ClassLoader cl) {
        // use pre defined cloner first
        var fc = config.getDefinedFastCloner(clz);
        if (fc != null) {
            return fc;
        }

        // if fast clone is not enabled, return
        if (!config.isFastCloneEnabled()) {
            return null;
        }

        // else , find and generate fast cloner
        return fastClonerFactory.getFastCloner(clz, cl);
    }
}