/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.valt;

import com.ranttu.rapid.reffer.misc.Property;
import com.ranttu.rapid.reffer.valt.annotations.ValueType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * a value type field
 *
 * @author rapid
 * @version $Id: ValueTypeField.java, v 0.1 2018Äê12ÔÂ02ÈÕ 11:12 PM rapid Exp $
 */
public class ValueTypeField extends Property {
    /**
     * field size, in bytes
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int size;

    /**
     * field index
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int fieldIndex;

    /**
     * field offset
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int fieldOffset;

    /**
     * the true type of this field
     */
    public Class<?> getFieldType() {
        Class<?> rawType = getGetterType();
        if (rawType.isArray()) {
            return rawType.getComponentType();
        } else {
            return rawType;
        }
    }

    /**
     * the raw type of this field
     */
    public Class<?> getType() {
        return getGetterType();
    }

    /**
     * is this a array field
     */
    public boolean isArray() {
        return getType().isArray();
    }

    /**
     * is this field value typed
     */
    public boolean isValueTyped() {
        return getFieldType().isAnnotationPresent(ValueType.class);
    }

    /**
     * is this field primitive
     */
    public boolean isPrimitive() {
        return getFieldType().isPrimitive();
    }

    //~~~ for popup
    @Override
    protected void setGetter(Method getter) {
        super.setGetter(getter);
    }

    @Override
    protected void setSetter(Method setter) {
        super.setSetter(setter);
    }

    @Override
    protected void setName(String name) {
        super.setName(name);
    }
}