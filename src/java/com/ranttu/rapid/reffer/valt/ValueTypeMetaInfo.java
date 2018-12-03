/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.valt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * meta info for value type
 *
 * @author rapid
 * @version $Id: ValueTypeMetaInfo.java, v 0.1 2018Äê12ÔÂ02ÈÕ 10:41 PM rapid Exp $
 */
public class ValueTypeMetaInfo<T> {
    /**
     * the env of this valt
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private ValueTypeEnvironment env;

    /**
     * the class where the value type derived from
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Class<T> originalClass;

    /**
     * value typed properties of this value type
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Map<String, ValueTypeField> fields;

    /**
     * the size of this value type, in bytes
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int size;

    /**
     * the value typed class
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Class<? extends T> valtClass;
}