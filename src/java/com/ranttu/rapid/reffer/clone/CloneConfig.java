/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone;

import com.ranttu.rapid.reffer.misc.ObjectUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * deep clone configs
 *
 * @author rapid
 * @version $Id: CloneConfig.java, v 0.1 2018��09��20�� 10:41 PM rapid Exp $
 */
public class CloneConfig {
    @Getter
    @Setter
    /** fast clone, enabled by default */
    private boolean fastCloneEnabled = true;

    /**
     * should this omitted for clone
     */
    public boolean shouldIgnore(Object o) {
        return o == null || ObjectUtil.isPrimitive(o);
    }

    /**
     * should this type of clazz ignored for deep clone
     */
    public boolean shouldIgnore(Class clz) {
        return ObjectUtil.isPrimitive(clz);
    }

    /**
     * find defined fast cloner
     */
    public FastCloner getDefinedFastCloner(Object o) {
        // TODO
        return null;
    }
}