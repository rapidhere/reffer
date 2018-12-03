/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.valt;

import com.ranttu.rapid.reffer.valt.ValueTypeEnvironment;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: ValueTypedUnitTest.java, v 0.1 2018Äê12ÔÂ02ÈÕ 7:44 PM rapid Exp $
 */
public class ValueTypedUnitTest {
    private ValueTypeEnvironment valueTypeEnvironment = new ValueTypeEnvironment();

    @Test
    public void test0() {
        ValtCounter valtCounter = valueTypeEnvironment.allocate(ValtCounter.class);

        valtCounter.setCount(10);
        Assert.assertEquals(valtCounter.getCount(), 10);
        Assert.assertEquals(valtCounter.incrementAndGet(), 11);

        valtCounter.setType(0xdeadbeef);
        Assert.assertEquals(valtCounter.getCount(), 11);
        Assert.assertEquals(valtCounter.getType(), 0xdeadbeef);
        Assert.assertEquals(valtCounter.incrementAndGet(), 12);
        Assert.assertEquals(valtCounter.getType(), 0xdeadbeef);
    }
}