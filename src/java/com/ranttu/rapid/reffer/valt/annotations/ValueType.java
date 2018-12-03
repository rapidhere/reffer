/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.valt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotate the class is value typed
 *
 * @author rapid
 * @version $Id: ValueType.java, v 0.1 2018Äê11ÔÂ02ÈÕ 10:56 PM rapid Exp $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueType {
    /**
     * use heap off memory or not
     */
    boolean heapOff() default false;
}