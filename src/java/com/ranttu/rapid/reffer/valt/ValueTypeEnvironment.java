/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.reffer.valt;

import com.ranttu.rapid.reffer.misc.$;
import com.ranttu.rapid.reffer.misc.BytecodeUtil;
import com.ranttu.rapid.reffer.misc.ObjectUtil;
import com.ranttu.rapid.reffer.misc.StringUtil;
import com.ranttu.rapid.reffer.misc.asm.ClassWriter;
import com.ranttu.rapid.reffer.valt.annotations.ValueType;
import lombok.SneakyThrows;
import lombok.experimental.var;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_PRIVATE;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_PUBLIC;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_SUPER;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ACC_SYNTHETIC;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.ALOAD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.GETFIELD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.INVOKESPECIAL;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.INVOKESTATIC;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.INVOKEVIRTUAL;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.PUTFIELD;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.RETURN;
import static com.ranttu.rapid.reffer.misc.asm.Opcodes.V1_6;
import static com.ranttu.rapid.reffer.misc.asm.Type.getDescriptor;
import static com.ranttu.rapid.reffer.misc.asm.Type.getInternalName;
import static com.ranttu.rapid.reffer.misc.asm.Type.getMethodDescriptor;
import static java.util.function.Function.identity;

/**
 * value types facade
 *
 * @author rapid
 * @version $Id: ValueTypeEnvironment.java, v 0.1 2018Äê11ÔÂ02ÈÕ 10:42 PM rapid Exp $
 */
final public class ValueTypeEnvironment {
    /**
     * cached factories
     */
    private final Map<Class, ValueTypeMetaInfo> metaInfos = new IdentityHashMap<>(64);

    /**
     * lock
     */
    private final Object GEN_LOCK = new Object();

    /**
     * number of clazz generated
     */
    private final static AtomicInteger clazzCount = new AtomicInteger();

    /**
     * allocate a value typed object
     */
    @SneakyThrows
    public <T> T allocate(Class<T> clz) {
        $.should(clz.isAnnotationPresent(ValueType.class),
            "must annotated with " + ValueType.class.getSimpleName());

        ValueTypeMetaInfo<T> metaInfo = getMetaInfo(clz);
        // TODO: refine performance
        // TODO: support arguments constructor
        @SuppressWarnings("unchecked") T res = metaInfo.getValtClass().newInstance();
        return res;
    }

    /**
     * find value type factory
     */
    public <T> ValueTypeMetaInfo<T> getMetaInfo(Class<T> clz) {
        $.should(clz.isAnnotationPresent(ValueType.class),
            "must annotated with " + ValueType.class.getSimpleName());

        ValueTypeMetaInfo res;
        if ((res = metaInfos.get(clz)) == null) {
            synchronized (GEN_LOCK) {
                if ((res = metaInfos.get(clz)) == null) {
                    res = composeMetaInfo(clz);
                    metaInfos.put(clz, res);
                }
            }
        }

        @SuppressWarnings("unchecked") ValueTypeMetaInfo<T> tmp = res;
        return tmp;
    }

    private int getPrimitiveTypeSize(Class clz) {
        $.should(clz.isPrimitive(), "should be primitive type");

        if (clz == int.class) {
            return 4;
        }
        // TODO: refine for boolean
        else if (clz == boolean.class) {
            return 1;
        } else if (clz == byte.class) {
            return 1;
        } else if (clz == short.class) {
            return 2;
        } else if (clz == long.class) {
            return 8;
        } else if (clz == float.class) {
            return 4;
        } else if (clz == double.class) {
            return 8;
        } else if (clz == char.class) {
            return 2;
        } else {
            return $.notSupportedYet(clz.getSimpleName() + "for primitive types");
        }
    }

    /**
     * compose the meta info
     */
    @SuppressWarnings("unchecked")
    private ValueTypeMetaInfo composeMetaInfo(Class clz) {
        var metaInfo = new ValueTypeMetaInfo();
        metaInfo.setOriginalClass(clz);
        metaInfo.setEnv(this);

        // find fields
        // TODO: refactor the properties parser method
        Map<String, ValueTypeField> fields =
            ObjectUtil.getAllProperties(clz).values().stream().filter((prop) -> {
                // only accept abstract method
                var modifiers = prop.getGetter().getModifiers();
                if (Modifier.isStatic(modifiers)
                    || Modifier.isNative(modifiers)
                    || !Modifier.isAbstract(modifiers)) {
                    return false;
                }

                modifiers = prop.getSetter().getModifiers();
                if (Modifier.isStatic(modifiers)
                    || Modifier.isNative(modifiers)
                    || !Modifier.isAbstract(modifiers)) {
                    return false;
                }

                // getter type and setter type must much
                $.should(prop.getGetterType() == prop.getSetterType(),
                    "getter and setter must have same type: " + prop.getName() + ", " + clz.getName());

                return true;
            }).map((prop) -> {
                var field = new ValueTypeField();
                field.setGetter(prop.getGetter());
                field.setSetter(prop.getSetter());
                field.setName(prop.getName());

                return field;
            }).collect(Collectors.toMap(ValueTypeField::getName, identity()));
        metaInfo.setFields(fields);

        // field size calculate
        calculateSize(metaInfo);

        // dispatch indexes
        fillFieldIndexes(metaInfo);

        // generate valt class
        metaInfo.setValtClass(generateValtClass(metaInfo));

        return metaInfo;
    }

    private void calculateSize(ValueTypeMetaInfo<?> metaInfo) {
        int totSize = metaInfo.getFields().values().stream().mapToInt(field -> {
            // TODO
            if (field.isArray()) {
                $.notSupportedYet("array for valt " + field.getType().getSimpleName());
            }

            int sz;
            if (field.isPrimitive()) {
                sz = getPrimitiveTypeSize(field.getType());
            } else if (field.isValueTyped()) {
                var fieldMetaInfo = getMetaInfo(field.getFieldType());
                sz = fieldMetaInfo.getSize();
            } else {
                // a pointer for just 8 size
                // TODO: ?
                sz = 8;
            }

            field.setSize(sz);
            return sz;
        }).sum();

        metaInfo.setSize(totSize);
    }

    private void fillFieldIndexes(ValueTypeMetaInfo<?> metaInfo) {
        // 0 -> idx, 1 -> offset
        final int[] counter = {0, 0};
        metaInfo.getFields().values().stream()
            .sorted(Comparator.comparing(ValueTypeField::getName))
            .forEach(f -> {
                f.setFieldIndex(counter[0]);
                f.setFieldOffset(counter[1]);

                counter[0]++;
                counter[1] += f.getSize();
            });
    }

    private Class generateValtClass(ValueTypeMetaInfo<?> metaInfo) {
        var clz = metaInfo.getOriginalClass();
        var className = generateClassName(clz);
        var internalClassName = className.replace('.', '/');

        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_6,
            ACC_SYNTHETIC + ACC_SUPER + ACC_PUBLIC,
            internalClassName,
            null,
            getInternalName(clz),
            new String[0]);
        cw.visitSource("<reffer-generated>", null);

        // byte buffer array
        cw.visitField(ACC_PRIVATE, "buff",
            getDescriptor(ByteBuffer.class), null, null
        ).visitEnd();

        // <init> method
        buildInitMethod(cw, internalClassName, metaInfo);

        // accessors
        buildAccessors(cw, internalClassName, metaInfo);

        cw.visitEnd();
        var bc = cw.toByteArray();
        ObjectUtil.printClass(className, bc);

        try {
            return $.getUnsafe().defineAnonymousClass(clz, bc, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException("failed to generate accessor class!", e);
        }
    }

    private void buildInitMethod(ClassWriter cw, String internalClassName, ValueTypeMetaInfo<?> metaInfo) {
        // TODO: support constructor arguments
        var mv = cw.visitMethod(
            ACC_PUBLIC,
            "<init>",
            "()V",
            null,
            null);
        mv.visitCode();

        // call super
        // TODO: support constructor arguments
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL,
            getInternalName(metaInfo.getOriginalClass()),
            "<init>", "()V", false);

        // init byte buffer
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(metaInfo.getSize());
        mv.visitMethodInsn(INVOKESTATIC, "java/nio/ByteBuffer", "allocate",
            "(I)Ljava/nio/ByteBuffer;", false);
        mv.visitFieldInsn(PUTFIELD, internalClassName, "buff", "Ljava/nio/ByteBuffer;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }


    private void buildAccessors(ClassWriter cw, String internalClassName, ValueTypeMetaInfo<?> metaInfo) {
        metaInfo.getFields().forEach((name, field) -> {
            processAccessor(cw, internalClassName, field, field.getGetter(), true);
            processAccessor(cw, internalClassName, field, field.getSetter(), false);
        });
    }

    private void processAccessor(ClassWriter cw, String internalClassName,
                                 ValueTypeField field, Method method, boolean isGetter) {
        // TODO: support non-primitive types
        if (!field.isPrimitive()) {
            $.notSupportedYet("only support primitive types now");
        }

        // TODO: access flag should inherit from parent
        // TODO: exception signature should inherit from parent
        var mv = cw.visitMethod(ACC_PUBLIC,
            method.getName(),
            getMethodDescriptor(method),
            null,
            null);
        mv.visitCode();

        // get buff method info
        var methodNameSuffix = StringUtil.capFirst(field.getFieldType().getSimpleName());
        // there is no xxxBoolean method
        if ("Boolean".equals(methodNameSuffix)) {
            methodNameSuffix = "";
        }
        // for xxxByte method, there is no suffix
        else if ("Byte".equals(methodNameSuffix)) {
            methodNameSuffix = "";
        }

        var buffMethodName = (isGetter ? "get" : "put") + methodNameSuffix;
        var typeDesc = getDescriptor(field.getFieldType());
        var buffMethodDesc = String.format("(I%s)%s",
            (isGetter ? "" : typeDesc),
            (isGetter ? typeDesc : "Ljava/nio/ByteBuffer;"));

        // en-stack
        // buff on stack
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalClassName, "buff", "Ljava/nio/ByteBuffer;");

        // offset on stack
        mv.visitLdcInsn(field.getFieldOffset());

        // for setters, need en stack set value
        if (!isGetter) {
            BytecodeUtil.load(mv, field.getFieldType(), 1);
        }

        // visit method
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", buffMethodName, buffMethodDesc, false);

        if (isGetter) {
            BytecodeUtil.ret(mv, field.getFieldType());
        } else {
            BytecodeUtil.ret(mv, void.class);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private String generateClassName(Class clz) {
        return "com.ranttu.rapid.reffer.clone.VTFactory_"
            + clz.getSimpleName() + "$" + clazzCount.getAndIncrement();
    }
}