/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.misc;

import com.google.common.collect.ImmutableMap;
import com.ranttu.rapid.reffer.misc.asm.MethodVisitor;
import lombok.experimental.UtilityClass;

import java.util.Map;

import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ALOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ARETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ASTORE;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.DLOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.DRETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.DSTORE;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.FLOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.FRETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.FSTORE;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ILOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.IRETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ISTORE;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.LLOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.LRETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.LSTORE;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.RETURN;

/**
 * @author rapid
 * @version $Id: BytecodeUtil.java, v 0.1 2018年12月03日 5:49 PM rapid Exp $
 */
@UtilityClass
public class BytecodeUtil {
    public void load(MethodVisitor mv, Class<?> type, int idx) {
        mv.visitVarInsn(matchOpc(type, OPS.LOAD), idx);
    }

    public void ret(MethodVisitor mv, Class<?> type) {
        mv.visitInsn(matchOpc(type, OPS.RET));
    }

    public int matchOpc(Class<?> type, OPS op) {
        int opc;
        if (PRIMITIVE_OPCS.containsKey(type)) {
            opc = PRIMITIVE_OPCS.get(type)[op.ordinal()];
        } else {
            opc = PRIMITIVE_OPCS.get(Object.class)[op.ordinal()];
        }

        $.should(opc > 0, "illegal op: " + type + "@" + op);
        return opc;
    }

    private final Map<Class, int[]> PRIMITIVE_OPCS;

    private enum OPS {
        LOAD,
        STORE,
        RET,
    }

    static {
        // ~~~ primitive opcs
        PRIMITIVE_OPCS = ImmutableMap.<Class, int[]>builder()
            .put(int.class, opcs(ILOAD, ISTORE, IRETURN))
            .put(boolean.class, opcs(ILOAD, ISTORE, IRETURN))
            .put(byte.class, opcs(ILOAD, ISTORE, IRETURN))
            .put(short.class, opcs(ILOAD, ISTORE, IRETURN))
            .put(char.class, opcs(ILOAD, ISTORE, IRETURN))
            .put(long.class, opcs(LLOAD, LSTORE, LRETURN))
            .put(float.class, opcs(FLOAD, FSTORE, FRETURN))
            .put(double.class, opcs(DLOAD, DSTORE, DRETURN))
            .put(void.class, opcs(-1, -1, RETURN))
            .put(Object.class, opcs(ALOAD, ASTORE, ARETURN))
            .build();
    }

    private int[] opcs(int opLoad, int opStore, int opRet) {
        return new int[]{opLoad, opStore, opRet};
    }
}