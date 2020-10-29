/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.clone.unit;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ranttu.rapid.reffer.clone.Cloner;
import com.ranttu.rapid.reffer.test.util.CaseData;
import com.ranttu.rapid.reffer.test.util.TestUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author rapid
 * @version : ClonerUnitTestSupport.java, v 0.1 2020-10-29 9:41 PM rapid Exp $
 */
public class ClonerUnitTestSupport {
    protected Cloner cloner = new Cloner();

    protected void check(Object o) {
        Object cloned = cloner.deepClone(o);

        Assert.assertEquals(JSON.toJSON(cloned), JSON.toJSON(o));
    }

    @DataProvider(name = "load-from-yaml")
    protected Iterator<Object[]> loadFromYaml() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        List<CaseData> data = mapper.readValue(TestUtil.getTestResource(getClass(), "unittest/"),
            mapper.getTypeFactory().constructCollectionType(List.class, CaseData.class));

        Iterator<CaseData> iter = data.iterator();

        return new Iterator<Object[]>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Object[] next() {
                return new Object[]{iter.next()};
            }
        };
    }
}