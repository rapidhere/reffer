/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.misc;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * common utils
 *
 * @author rapid
 * @version $Id: $.java, v 0.1 2018Äê09ÔÂ20ÈÕ 10:52 PM rapid Exp $
 */
@UtilityClass
public class $ {
    //~~~ unsafe
    private final Unsafe unsafe = initUnsafe();

    /**
     * get sun.misc.Unsafe
     */
    public Unsafe getUnsafe() {
        return unsafe;
    }

    @SneakyThrows
    private Unsafe initUnsafe() {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);

        return (Unsafe) f.get(null);
    }

    //~~~ asserts

    /**
     * this function is not supported by reffer lib yet
     */
    public <T> T notSupportedYet(String msg) {
        throw new UnsupportedOperationException(msg);
    }

    /**
     * object cannot be null
     */
    public <T> T notNull(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        return obj;
    }
}