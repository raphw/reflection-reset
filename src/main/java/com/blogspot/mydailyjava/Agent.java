package com.blogspot.mydailyjava;

import org.objectweb.asm.*;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;

public class Agent {

    public static void premain(String argument, Instrumentation instrumentation) {
        enableReflection(instrumentation);
    }

    public static void agentmain(String argument, Instrumentation instrumentation) {
        enableReflection(instrumentation);
    }

    public static void enableReflection(Instrumentation instrumentation) {
        try {
            ClassReader classReader = new ClassReader(AccessibleObject.class.getName());
            ClassWriter classWriter = new ClassWriter(classReader, 0);
            final String descriptor = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(Class.class), Type.getType(Class.class));
            classReader.accept(new ClassVisitor(Opcodes.ASM6, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                    if (name.equals("checkCanSetAccessible") && desc.equals(descriptor)) {
                        methodVisitor.visitCode();
                        methodVisitor.visitInsn(Opcodes.RETURN);
                        methodVisitor.visitEnd();
                        return null;
                    } else {
                        return methodVisitor;
                    }
                }
            }, 0);
            instrumentation.redefineClasses(new ClassDefinition(AccessibleObject.class, classWriter.toByteArray()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
