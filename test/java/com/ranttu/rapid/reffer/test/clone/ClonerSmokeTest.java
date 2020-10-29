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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rapid
 * @version $Id: ClonerUnitTest.java, v 0.1 2018年09月21日 2:14 AM rapid Exp $
 */
public class ClonerSmokeTest {
    private Cloner cloner = new Cloner();

    @Test
    public void test0() {
        Assert.assertEquals(1, (int) cloner.deepClone(1));
        Assert.assertEquals(1.0, cloner.deepClone(1.0));
        Assert.assertTrue(cloner.deepClone(true));
        Assert.assertEquals("123", cloner.deepClone("123"));
    }

    @Test
    public void test1() {
        Object1 a = new Object1();
        a.c.ca = "hello";
        a.c.cb = "world";

        a.d.ca = "ni";
        a.d.cb = "ma";

        check(a);
    }

    @Test
    public void test2() {
        Object1[] arr = new Object1[]{
            new Object1(), new Object1(), new Object1()
        };

        arr[0].c.ca = "1a";
        arr[0].c.cb = "1b";

        arr[1].c.ca = "2a";
        arr[1].c.cb = "2b";

        arr[2].c.ca = "3a";
        arr[2].c.cb = "3b";

        check(arr);
    }

    @Test
    public void test3() {
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("333");

        check(list);
    }

    @Test
    public void test4() {
        Map<String, String> m = new HashMap<>();
        m.put("a", "b");
        m.put("c", "d");

        check(m);
    }

    @Test
    public void test5() {
        Object3 obj = new Object3();
        obj.outer1 = "ni";
        obj.outer2.fill("ma");

        check(obj);
    }

    private void check(Object o) {
        Object cloned = cloner.deepClone(o);

        Assert.assertEquals(JSON.toJSON(cloned), JSON.toJSON(o));
    }
}

class Object1 {
    @Getter
    final private String a = "123";
    @Getter
    final private int aa = 123;
    @Getter
    private String b = "456";
    @Getter
    Object2 c = new Object2();
    @Getter
    final Object2 d = new Object2();
}

class Object2 {
    @Getter
    String ca;
    @Getter
    String cb;
}

class Object3 {
    String outer1;

    ObjectInner3 outer2 = new ObjectInner3();

    class ObjectInner3 {
        String inner1;

        String inner2;

        public void fill(String par) {
            inner1 = outer1;
            inner2 = par;
        }
    }
}