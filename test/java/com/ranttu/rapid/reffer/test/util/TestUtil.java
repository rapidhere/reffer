/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.util;

import lombok.experimental.UtilityClass;

import java.io.InputStream;

/**
 * @author rapid
 * @version : TestUtil.java, v 0.1 2020-10-29 9:49 PM rapid Exp $
 */
@UtilityClass
public class TestUtil {
    public static InputStream getTestResource(Class<?> klass, String basePath) {
        String className = klass.getSimpleName();

        return klass.getClassLoader().getResourceAsStream(basePath + className + ".yaml");
    }
}