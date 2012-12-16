package com.github.lehphyro.fastbuilder.internal.bytecode;

import static org.objectweb.asm.Opcodes.*;

import java.util.*;

import org.objectweb.asm.*;

import com.github.lehphyro.fastbuilder.internal.*;
import com.github.lehphyro.fastbuilder.util.*;
import com.google.common.collect.*;

public class BytecodeBuilderClassVisitor extends ClassVisitor {

	private static final String CONSTRUCTOR_NAME = "<init>";
	private static final String VOID_METHOD = "()V";

	private final BuilderSpecification builderSpecification;
	private final String builderInterfaceName;
	private final String builderImplementationName;

	private String targetInternalName;
	private String targetDescriptor;
	private Set<String> generatedListFields;

	public BytecodeBuilderClassVisitor(final ClassVisitor visitor, final Class<?> target, final BuilderSpecification builderSpecification, final String builderInterfaceName, final String builderImplementationName) {
		super(ASM4, visitor);
		this.builderSpecification = builderSpecification;
		this.builderInterfaceName = builderInterfaceName;
		this.builderImplementationName = builderImplementationName;
		this.targetInternalName = Type.getInternalName(target);
		this.targetDescriptor = Type.getDescriptor(target);
		this.generatedListFields = Sets.newHashSet();
	}

	@Override
	public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
		super.visit(version, ACC_PUBLIC + ACC_STATIC + ACC_FINAL + ACC_SUPER, builderImplementationName, null, superName, new String[] { builderInterfaceName });
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		int argCount = Type.getArgumentTypes(desc).length;
		Type returnType = Type.getReturnType(desc);
		if (builderSpecification.isReader(name, argCount)) {
			generateReader(returnType, name, desc, signature, exceptions);
		} else if (builderSpecification.isCollection(name)) {
			String fieldName = builderSpecification.getFieldName(name);
			Type fieldType = Type.getArgumentTypes(desc)[0];
			if (generatedListFields.add(fieldName)) {
				generateListField(fieldType, fieldName);
			}
			if (Types.isPrimitive(fieldType)) {
				validateReturnType(returnType, name, desc);
				generateAddElementWriter(fieldType, fieldName, name, desc, signature, exceptions);
			} else {
				validateReturnType(returnType, name, desc);
				generateAddAllElementsWriter(fieldType, fieldName, name, desc, signature, exceptions);
			}
		} else if (builderSpecification.isWriter(name, argCount)) {
			validateReturnType(returnType, name, desc);
			Type fieldType = Type.getArgumentTypes(desc)[0];
			generateField(fieldType.getDescriptor(), name);
			generateWriter(fieldType, name, desc, signature, exceptions);
		} else {
			throw new IllegalStateException(String.format("Unknown method [%s] with description [%s]", name, desc));
		}
		return null;
	}

	@Override
	public void visitEnd() {
		generateConstructor();
		generateBuild();
		super.visitEnd();
	}

	protected void generateConstructor() {
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, CONSTRUCTOR_NAME, VOID_METHOD, null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), CONSTRUCTOR_NAME, VOID_METHOD);

		int maxs = 1;
		for (String listField : generatedListFields) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/util/ArrayList");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
			mv.visitFieldInsn(PUTFIELD, builderImplementationName, listField, "Ljava/util/List;");
			maxs += 2;
		}

		mv.visitInsn(RETURN);
		mv.visitMaxs(maxs, 1);
		mv.visitEnd();
	}

	protected void generateField(final String type, final String name) {
		generateField(ACC_PRIVATE, type, name, null);
	}

	protected void generateListField(final Type type, final String name) {
		if (Types.isPrimitive(type)) {
			generateField(ACC_PRIVATE, "Ljava/util/List;", name, "Ljava/util/List<L" + Types.getWrapperClassName(type) + ";>;");
		} else {
			throw new UnsupportedOperationException(String.format("Field from collection not supported, put an addXxx method before this one for the field [%s]", name));
		}
	}

	protected void generateField(final int access, final String type, final String name, final String signature) {
		FieldVisitor fv = super.visitField(access, name, type, signature, null);
		fv.visitEnd();
	}

	protected void generateWriter(final Type type, final String name, final String desc, final String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(type.getOpcode(ILOAD), 1);
		mv.visitFieldInsn(PUTFIELD, builderImplementationName, name, type.getDescriptor());
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	protected void generateAddElementWriter(final Type type, final String fieldName, final String name, final String desc, final String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, builderImplementationName, fieldName, "Ljava/util/List;");
		mv.visitVarInsn(type.getOpcode(ILOAD), 1);
		mv.visitMethodInsn(INVOKESTATIC, Types.getWrapperClassName(type), "valueOf", "(" + type.getDescriptor() + ")L" + Types.getWrapperClassName(type) + ";");
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
		mv.visitInsn(POP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	protected void generateAddAllElementsWriter(final Type type, final String fieldName, final String name, final String desc, final String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, builderImplementationName, fieldName, "Ljava/util/List;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z");
		mv.visitInsn(POP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	protected void generateReader(final Type type, final String name, final String desc, final String signature, final String[] exceptions) {
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, builderImplementationName, builderSpecification.getFieldName(name), type.getDescriptor());
		mv.visitInsn(type.getOpcode(IRETURN));
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	protected void generateBuild() {
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "build", "()" + targetDescriptor, null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, targetInternalName);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, targetInternalName, CONSTRUCTOR_NAME, "(L" + builderInterfaceName + ";)V");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(3, 1);
		mv.visitEnd();

		mv = super.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "build", "()" + Type.getDescriptor(Object.class), null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, builderImplementationName, "build", "()L" + targetInternalName + ";");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	protected void validateReturnType(Type returnType, String name, String desc) {
		if (Types.isPrimitive(returnType) || !builderInterfaceName.equals(returnType.getInternalName())) {
			throw new IllegalStateException(String.format("Writer method must return the builder in method [%s] with description [%s]", name, desc));
		}
	}
}
