/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.clone;

import com.alibaba.fastjson.JSON;
import com.ranttu.rapid.reffer.clone.Cloner;
import lombok.Getter;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: ClonerUnitTest.java, v 0.1 2018Äê09ÔÂ21ÈÕ 2:14 AM rapid Exp $
 */
@SuppressWarnings("unused")
public class ClonerUnitTest {
    private Cloner cloner = new Cloner();

    @Test
    public void test0() {
        Assert.assertEquals(1, (int) cloner.deepClone(1));
        Assert.assertEquals(1.0, cloner.deepClone(1.0));
        Assert.assertEquals(true, (boolean) cloner.deepClone(true));
        Assert.assertEquals("123", cloner.deepClone("123"));
    }

    @Test
    public void test1() {
        Object1 a = new Object1();
        a.c.ca = "hello";
        a.c.cb = "world";

        check(new Object1());
    }

    private void check(Object o) {
        Object cloned = cloner.deepClone(o);

        Assert.assertEquals(JSON.toJSON(cloned), JSON.toJSON(o));
    }
}

class Object1 {
    @Getter
    private String a = "123";
    @Getter
    private String b = "456";
    @Getter
    Object2 c = new Object2();
}

class Object2 {
    @Getter
    String ca;
    @Getter
    String cb;
}