/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.clone;

import com.ranttu.rapid.reffer.misc.$;
import com.ranttu.rapid.reffer.misc.BackdoorObject;
import com.ranttu.rapid.reffer.misc.ObjectUtil;
import com.ranttu.rapid.reffer.misc.asm.ClassWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_PUBLIC;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_SUPER;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_SYNTHETIC;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ALOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ARETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.DUP;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.GETFIELD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.INVOKESPECIAL;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.INVOKEVIRTUAL;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.NEW;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.PUTFIELD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.V1_6;
import static com.ranttu.rapid.reffer.misc.asm.Type.getDescriptor;
import static com.ranttu.rapid.reffer.misc.asm.Type.getInternalName;

/**
 * auto-gen fast cloner factory
 *
 * @author rapid
 * @version $Id: FastClonerFactory.java, v 0.1 2018-09-20 11:03 PM rapid Exp $
 */
@RequiredArgsConstructor
final public class FastClonerFactory {
    /**
     * clone config
     */
    private final CloneConfig cloneConfig;

    /**
     * generated cloner
     */
    private final Map<Class<?>, FastCloner> clonerMap = new IdentityHashMap<>(64);

    /**
     * lock
     */
    private final Object CLONER_GEN_LOCK = new Object();

    /**
     * number of clazz generated
     */
    private final static AtomicInteger clazzCount = new AtomicInteger();

    /**
     * get or generate a fast cloner
     * <p>
     * this is generated by asm lib, don't use this fastCloner to do other things than clone, or u fucked up
     */
    public FastCloner getFastCloner(Class<?> cl) {
        FastCloner fc;
        if ((fc = clonerMap.get(cl)) == null) {
            synchronized (CLONER_GEN_LOCK) {
                if ((fc = clonerMap.get(cl)) == null) {
                    fc = generateCloner(cl);
                    clonerMap.put(cl, fc);
                }
            }
        }

        return fc;
    }

    /**
     * generate a fast cloner
     */
    private FastCloner generateCloner(Class<?> clz) {
        // TODO: can use super.clone
        // TODO: can use expanded clone
        // TODO: bytecode verify
        // TODO: support array
        // TODO: support in-place instance with cl

        var fcClzName = generateFcClazzName(clz);
        var fcClzInternalName = fcClzName.replace('.', '/');

        // start define
        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_6,
            ACC_SYNTHETIC + ACC_SUPER + ACC_PUBLIC,
            fcClzInternalName,
            null,
            getInternalName(BackdoorObject.class),
            new String[]{getInternalName(FastCloner.class)});
        cw.visitSource("<reffer-generated-cloner>", null);

        //~~~ constructor method
        //        var mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        //        mv.visitCode();
        //        mv.visitVarInsn(ALOAD, 0);
        //        mv.visitMethodInsn(INVOKESPECIAL, getInternalName(Object.class), "<init>", "()V", false);
        //        mv.visitInsn(RETURN);
        //        mv.visitMaxs(0, 0);
        //        mv.visitEnd();

        //~~~ clone method
        visitCloneMethod(cw, clz);

        // generate bytecode
        cw.visitEnd();

        var bc = cw.toByteArray();
        ObjectUtil.printClass(fcClzName, bc);

        try {
            // TODO, define class with clz
            return (FastCloner) $.getUnsafe().allocateInstance(defineClass(FastClonerFactory.class, bc));
        } catch (Throwable e) {
            throw new RuntimeException("failed to generate accessor class!", e);
        }
    }

    /**
     * generate the clone method
     */
    private void visitCloneMethod(ClassWriter cw, Class<?> clz) {
        val interClzName = getInternalName(clz);

        // index of parameters and variables
        val V_OBJ = 1;          // object to be cloned
        val V_CLONED = 2;       // cloned objects
        val V_CL = 3;           // class loader
        val V_CLONER = 4;       // cloner

        var mv = cw.visitMethod(ACC_PUBLIC,
            "clone",
            "(Ljava/lang/Object;"
                + "Ljava/util/Map;"
                + "Ljava/lang/ClassLoader;"
                + "Lcom/ranttu/rapid/reffer/clone/Cloner;"
                + ")Ljava/lang/Object;",
            null,
            null);

        // method content
        mv.visitCode();

        //~~~ new instance
        // TODO: support arged constructor
        mv.visitTypeInsn(NEW, interClzName);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, interClzName, "<init>", "()V", false);

        //~~~ set fields
        Set<String> visitedFieldNames = new HashSet<>();
        for (Field f : ObjectUtil.getAllFields(clz)) {
            // for same name fields, we only clone the very sub class field
            if (visitedFieldNames.contains(f.getName())) {
                return;
            }

            // don't clone static field
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }

            // ignore transient field on need
            if (cloneConfig.isIgnoreTransient() && Modifier.isTransient(f.getModifiers())) {
                continue;
            }

            var fieldClz = f.getType();

            // as visited
            visitedFieldNames.add(f.getName());

            // copy field
            mv.visitInsn(DUP);

            // for primitive field, copy directly
            if (cloneConfig.shouldIgnore(fieldClz)) {
                mv.visitVarInsn(ALOAD, V_OBJ);
                mv.visitFieldInsn(GETFIELD, interClzName, f.getName(), getDescriptor(fieldClz));
            }
            // other field, use cloner's copy
            else {
                mv.visitVarInsn(ALOAD, V_CLONER);

                // obj.field
                mv.visitVarInsn(ALOAD, V_OBJ);
                mv.visitFieldInsn(GETFIELD, interClzName, f.getName(), getDescriptor(fieldClz));

                mv.visitVarInsn(ALOAD, V_CLONED);
                mv.visitVarInsn(ALOAD, V_CL);

                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    getInternalName(Cloner.class),
                    "cloneInternal",
                    "(Ljava/lang/Object;Ljava/util/Map;Ljava/lang/ClassLoader;)Ljava/lang/Object;",
                    false);
            }

            // set field
            mv.visitFieldInsn(PUTFIELD, interClzName, f.getName(), getDescriptor(fieldClz));
        }

        //~~~ return clone target
        mv.visitInsn(DUP);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /**
     * generate fc clazz name
     */
    private String generateFcClazzName(Class<?> clz) {
        return "com.ranttu.rapid.reffer.clone.GENFC_" + clz.getSimpleName() + "$" + clazzCount.getAndIncrement();
    }

    /**
     * define the fast cloner class
     */
    private Class<?> defineClass(Class<?> clz, byte[] byteCodes) {
        return $.getUnsafe().defineAnonymousClass(clz, byteCodes, new Object[0]);
    }
}