/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.misc;

import sun.misc.Unsafe;
import sun.reflect.MagicAccessorMark;

/**
 * ANY OBJECT INHERITED THIS OBJECT
 * WILL **NOT** BE CHECKED BY JVM BYTECODE PROTOCOLS
 *
 * @author rapid
 * @version $Id: BackdoorObject.java, v 0.1 2018年09月20日 10:12 PM rapid Exp $
 */
public class BackdoorObject extends MagicAccessorMark {
    private static final Unsafe unsafe = $.getUnsafe();

    //~~~ final set helpers
    @SuppressWarnings("unused")
    protected static void setByte(Object o, long offset, byte b) {
        unsafe.putByte(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setShort(Object o, long offset, short b) {
        unsafe.putShort(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setInt(Object o, long offset, int b) {
        unsafe.putInt(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setLong(Object o, long offset, long b) {
        unsafe.putLong(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setFloat(Object o, long offset, float b) {
        unsafe.putFloat(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setDouble(Object o, long offset, double b) {
        unsafe.putDouble(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setChar(Object o, long offset, char b) {
        unsafe.putChar(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setBoolean(Object o, long offset, boolean b) {
        unsafe.putBoolean(o, offset, b);
    }

    @SuppressWarnings("unused")
    protected static void setObject(Object o, long offset, Object b) {
        unsafe.putObject(o, offset, b);
    }
}