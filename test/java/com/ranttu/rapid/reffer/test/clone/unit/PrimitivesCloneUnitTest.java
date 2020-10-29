/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.clone.unit;

import org.testng.annotations.Test;

/**
 * @author rapid
 * @version : PrimitivesCloneUnitTest.java, v 0.1 2020-10-29 9:41 PM rapid Exp $
 */
public class PrimitivesCloneUnitTest extends ClonerUnitTestSupport {
    @Test
    public void testIntegers() {
        check(0);
        check(1);
        check(-1);
        check(Integer.MAX_VALUE);
        check(Integer.MIN_VALUE);
    }

    @Test
    public void testFloats() {
        check(0.0);
        check(0.1);
        check(-0.1);
        check(Double.MAX_VALUE);
        check(Double.MIN_VALUE);
    }

    @Test
    public void checkBooleans() {
        check(true);
        check(false);
    }

    @Test
    public void checkChars() {
        check('a');
        check('b');
        check('\0');
    }
}