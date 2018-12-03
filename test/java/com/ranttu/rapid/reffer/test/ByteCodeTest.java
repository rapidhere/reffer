/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test;

import java.nio.ByteBuffer;

/**
 * @author rapid
 * @version $Id: ByteCodeTest.java, v 0.1 2018Äê09ÔÂ21ÈÕ 1:23 AM rapid Exp $
 */
public class ByteCodeTest {
    private ByteBuffer bb = ByteBuffer.allocate(100000);

    ByteCodeTest(boolean ss) {
        bb.putChar((ss + "").charAt(0));
    }
}