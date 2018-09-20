/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone;

import java.util.Map;

/**
 * fast cloner interface
 *
 * @author rapid
 * @version $Id: FastCloner.java, v 0.1 2018Äê09ÔÂ20ÈÕ 10:59 PM rapid Exp $
 */
public interface FastCloner {
    /**
     * clone a object
     */
    Object clone(Object obj, Map<Object, Object> cloned, ClassLoader cl, Cloner cloner);
}