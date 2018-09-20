/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test;

/**
 * @author rapid
 * @version $Id: ByteCodeTest.java, v 0.1 2018Äê09ÔÂ21ÈÕ 1:23 AM rapid Exp $
 */
public class ByteCodeTest {
    int a;

    public void f(int b) {
        ByteCodeTest a = new ByteCodeTest();

        a.a = 1;

        new Object() {
            String a = "123";
            String b = "456";
            Object c = new Object() {
                String ca = "789";
                String cb = "789";
            };
        };
    }
}