/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone.custom;

import com.ranttu.rapid.reffer.clone.Cloner;
import lombok.experimental.var;

import java.util.Map;
import java.util.Properties;

/**
 * fast cloner for java.util.Properties
 *
 * @author rapid
 * @version : PropertiesFastCloner.java, v 0.1 2020-10-30 4:37 PM rapid Exp $
 */
public class PropertiesFastCloner extends CustomFastClonerSupport<Properties> {
    /**
     * @see CustomFastClonerSupport#newInstance(Object, ClassLoader, Cloner)
     */
    @Override
    protected Properties newInstance(Properties source, ClassLoader cl, Cloner cloner) {
        return new Properties();
    }

    /**
     * @see CustomFastClonerSupport#fillInstance(Object, Object, Map, ClassLoader, Cloner)
     */
    @Override
    protected void fillInstance(Properties source, Properties target,
                                Map<Object, Object> cloned, ClassLoader cl, Cloner cloner) {
        for (var e : source.entrySet()) {
            var key = cloner.cloneInternal(e.getKey(), cloned, cl);
            var value = cloner.cloneInternal(e.getValue(), cloned, cl);

            target.put(key, value);
        }
    }
}