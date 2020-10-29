/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test.clone.unit;

import com.ranttu.rapid.reffer.test.util.CaseData;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author rapid
 * @version : ArrayCloneUnitTest.java, v 0.1 2020-10-29 9:58 PM rapid Exp $
 */
public class ArrayCloneUnitTest extends PrimitivesCloneUnitTest {
    @Test(dataProvider = "load-from-yaml")
    public void test(CaseData caseData) {
        if (caseData.skip) {
            throw new SkipException("skipped by case data mark");
        }

        List<?> obj = (List<?>) caseData.obj;
        Object[] arrObj = obj.toArray();

        check(arrObj);
    }
}