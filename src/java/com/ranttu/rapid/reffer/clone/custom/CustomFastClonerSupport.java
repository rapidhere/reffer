/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone.custom;

import com.ranttu.rapid.reffer.clone.Cloner;
import com.ranttu.rapid.reffer.clone.FastCloner;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.var;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * base custom fast cloner
 *
 * @author rapid
 * @version : CustomFastClonerSupport.java, v 0.1 2020-10-30 4:18 PM rapid Exp $
 */
abstract public class CustomFastClonerSupport<T> implements FastCloner {
    /**
     * cloner target type
     */
    @Getter
    final private Class<?> type;

    protected CustomFastClonerSupport() {
        var type = (ParameterizedType) this.getClass().getGenericSuperclass();
        //noinspection unchecked
        this.type = (Class<T>) type.getActualTypeArguments()[0];
    }

    /**
     * @see FastCloner#clone(Object, Map, ClassLoader, Cloner)
     */
    @Override
    @SneakyThrows
    public Object clone(Object obj, Map<Object, Object> cloned, ClassLoader cl, Cloner cloner) {
        @SuppressWarnings("unchecked") var source = (T) obj;
        var target = newInstance(source, cl, cloner);
        cloned.put(source, target);

        fillInstance(source, target, cloned, cl, cloner);
        return target;
    }

    /**
     * create a new instance
     */
    abstract protected T newInstance(T source, ClassLoader cl, Cloner cloner) throws Throwable;

    /**
     * fill instance info
     */
    abstract protected void fillInstance(
        T source, T target, Map<Object, Object> cloned, ClassLoader cl, Cloner cloner) throws Throwable;
}