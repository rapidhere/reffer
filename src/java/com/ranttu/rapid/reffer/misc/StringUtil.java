/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.misc;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;

/**
 * @author rapid
 * @version $Id: StringUtil.java, v 0.1 2018Äê12ÔÂ03ÈÕ 5:40 PM rapid Exp $
 */
@UtilityClass
public class StringUtil {
    /**
     * capitalize first letter of a string
     */
    public String capFirst(String raw) {
        if (isEmpty(raw)) {
            return raw;
        }

        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    /**
     * de-capitalize first letter of a string
     */
    public String decapFirst(String raw) {
        if (isEmpty(raw)) {
            return raw;
        }

        return Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
    }

    /**
     * test if string is empty
     */
    public boolean isEmpty(String s) {
        return Strings.isNullOrEmpty(s);
    }
}