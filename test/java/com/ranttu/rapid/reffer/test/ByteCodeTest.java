/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.test;

/**
 * @author rapid
 * @version $Id: ByteCodeTest.java, v 0.1 2018Äê09ÔÂ21ÈÕ 1:23 AM rapid Exp $
 */
public class ByteCodeTest {
    public static void main(String args[]) {
        int[][] a = new int[][]{
            {1, 2},
            {3, 4}
        };
        int[][] b = new int[2][2];

        System.arraycopy(a, 0, b, 0, 2);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.println(b[i][j]);
            }
        }
    }
}