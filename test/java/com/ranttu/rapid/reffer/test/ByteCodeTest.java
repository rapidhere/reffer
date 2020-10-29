/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test;

import java.util.Map;

/**
 * @author rapid
 * @version $Id: ByteCodeTest.java, v 0.1 2018年09月21日 1:23 AM rapid Exp $
 */
public class ByteCodeTest {
    ByteCodeTest() throws InstantiationException {
        Map<String, String> a = null;

        a.put("1", "2");
    }
}

//// class version 52.0 (52)
//// access flags 0x21
//public class com/ranttu/rapid/reffer/test/ByteCodeTest {
//
//// compiled from: ByteCodeTest.java
//
//// access flags 0x0
//<init>()V
//    L0
//    LINENUMBER 14 L0
//    ALOAD 0
//    INVOKESPECIAL java/lang/Object.<init> ()V
//    L1
//    LINENUMBER 15 L1
//    ACONST_NULL
//    LCONST_0
//    ICONST_0
//    INVOKESTATIC com/ranttu/rapid/reffer/misc/BackdoorObject.setByte (Ljava/lang/Object;JB)V
//    L2
//    LINENUMBER 16 L2
//    RETURN
//    L3
//    LOCALVARIABLE this Lcom/ranttu/rapid/reffer/test/ByteCodeTest; L0 L3 0
//    MAXSTACK = 4
//    MAXLOCALS = 1
//}
//
