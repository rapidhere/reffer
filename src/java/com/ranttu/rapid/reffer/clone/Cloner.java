/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone;

import com.ranttu.rapid.reffer.misc.$;
import lombok.experimental.var;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * deep cloner
 *
 * @author rapid
 * @version $Id: Cloner.java, v 0.1 2018Äê09ÔÂ20ÈÕ 10:19 PM rapid Exp $
 */
public class Cloner {
    /**
     * clone configurations
     */
    private CloneConfig config = new CloneConfig();

    /**
     * factory to generate fast cloner
     */
    private FastClonerFactory fastClonerFactory = new FastClonerFactory(config);


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
    private Object cloneInternal(Object obj, Map<Object, Object> cloned, ClassLoader cl) {
        // omitted objects
        if (config.shouldIgnore(obj)) {
            return obj;
        }

        // cloned before, just return
        var clonedBefore = cloned.get(obj);
        if (clonedBefore != null) {
            return clonedBefore;
        }

        // try fast clone
        var fastCloned = fastClone(obj, cloned, cl);
        if (fastCloned != null) {
            cloned.put(obj, fastCloned);
            return fastCloned;
        }

        // default impl, current not support :)
        return $.notSupportedYet("clones without FAST CLONE ENABLED is not supported currently");
    }

    /**
     * invoke fast clone impl!
     */
    private Object fastClone(Object obj, Map<Object, Object> cloned, ClassLoader cl) {
        // find fast cloner
        var fc = findFastCloner(obj);
        if (fc != null) {
            return fc.clone(obj, cloned, cl, this);
        } else {
            return null;
        }
    }

    /**
     * find matched fast cloner
     */
    private FastCloner findFastCloner(Object obj) {
        $.notNull(obj);

        // use pre defined cloner first
        var fc = config.getDefinedFastCloner(obj);
        if (fc != null) {
            return fc;
        }

        // if fast clone is not enabled, return
        if (!config.isFastCloneEnabled()) {
            return null;
        }

        // else , find and generate fast cloner
        return fastClonerFactory.getFastCloner(obj.getClass());
    }
}