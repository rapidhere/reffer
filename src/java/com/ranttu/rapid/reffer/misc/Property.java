/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * bean property fields of a class
 *
 * @author rapid
 * @version $Id: Property.java, v 0.1 2018年11月06日 6:25 PM rapid Exp $
 */
public class Property {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Method getter;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Method setter;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String name;

    public Class getGetterType() {
        return getter.getReturnType();
    }

    public Class getSetterType() {
        return setter.getParameterTypes()[0];
    }
}