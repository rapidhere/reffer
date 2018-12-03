/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.valt;

import com.ranttu.rapid.reffer.valt.annotations.ValueType;

/**
 * @author rapid
 * @version $Id: ValtCounter.java, v 0.1 2018Äê12ÔÂ02ÈÕ 7:48 PM rapid Exp $
 */
@ValueType
abstract public class ValtCounter {
    public int incrementAndGet() {
        setCount(getCount() + 1);

        return getCount();
    }

    abstract public int getCount();

    abstract public void setCount(int content);

    abstract public int getType();

    abstract public void setType(int type);
}