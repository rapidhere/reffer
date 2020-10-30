/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone;

import com.ranttu.rapid.reffer.clone.custom.CustomFastClonerSupport;
import com.ranttu.rapid.reffer.clone.custom.PropertiesFastCloner;
import com.ranttu.rapid.reffer.misc.ObjectUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * deep clone configs
 *
 * @author rapid
 * @version $Id: CloneConfig.java, v 0.1 2018年09月20日 10:41 PM rapid Exp $
 */
@Getter
@ToString
final public class CloneConfig {
    /**
     * fast clone, enabled by default
     */
    private boolean fastCloneEnabled;

    /**
     * should ignore transient field, disabled by default
     */
    private boolean ignoreTransient;

    /**
     * dump fast clone info in fast cloners
     */
    private boolean dumpFcInfo;

    /**
     * user defined fast cloner
     */
    private Map<Class<?>, FastCloner> userDefinedFastCloners = new HashMap<>();

    /**
     * no constructor, use builder
     */
    private CloneConfig() {
    }

    /**
     * builder method
     */
    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    /**
     * should this omitted for clone
     */
    public boolean shouldIgnore(Object o) {
        return o == null || ObjectUtil.isPrimitive(o);
    }

    /**
     * should this type of clazz ignored for deep clone
     */
    public boolean shouldIgnore(Class<?> clz) {
        return ObjectUtil.isPrimitive(clz);
    }

    /**
     * find defined fast cloner
     */
    public FastCloner getDefinedFastCloner(Class<?> clz) {
        return userDefinedFastCloners.get(clz);
    }

    //~~~ config builder
    public static class ConfigBuilder {
        private CloneConfig cloneConfig = new CloneConfig();

        public ConfigBuilder withDefault() {
            return fastCloneEnabled(true)
                .ignoreTransient(false)
                .dumpFcInfo(true)
                .withDefaultFastCloner();
        }

        public ConfigBuilder fastCloneEnabled(boolean flag) {
            cloneConfig.fastCloneEnabled = flag;
            return this;
        }

        public ConfigBuilder ignoreTransient(boolean flag) {
            cloneConfig.ignoreTransient = flag;
            return this;
        }

        public ConfigBuilder dumpFcInfo(boolean flag) {
            cloneConfig.dumpFcInfo = flag;
            return this;
        }

        public ConfigBuilder registerFastCloner(Class<?> type, FastCloner fc) {
            cloneConfig.userDefinedFastCloners.put(type, fc);
            return this;
        }

        public ConfigBuilder registerFastCloner(CustomFastClonerSupport<?> fc) {
            registerFastCloner(fc.getType(), fc);
            return this;
        }

        public ConfigBuilder withDefaultFastCloner() {
            registerFastCloner(new PropertiesFastCloner());
            return this;
        }

        public CloneConfig build() {
            return cloneConfig;
        }
    }
}