package com.github.lehphyro.fastbuilder.util;

import java.lang.reflect.*;

public class Types {

	private Types() {
	}

	public static Class<?> getTypeArgumentOfInterface(Class<?> targetClass, Class<?> interfaceClass) {
		Type[] types = targetClass.getGenericInterfaces();

		for (Type type : types) {
			if (interfaceClass == type || type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] typeArguments = parameterizedType.getActualTypeArguments();
				if (typeArguments.length != 1) {
					throw new IllegalArgumentException("ParameterizedType [" + parameterizedType + "] cannot have more than one type argument");
				}
				return (Class<?>) typeArguments[0];
			}
		}

		return null;
	}

	public static boolean isPrimitive(org.objectweb.asm.Type type) {
		return type == org.objectweb.asm.Type.BOOLEAN_TYPE ||
			type == org.objectweb.asm.Type.BYTE_TYPE ||
			type == org.objectweb.asm.Type.CHAR_TYPE ||
			type == org.objectweb.asm.Type.DOUBLE_TYPE ||
			type == org.objectweb.asm.Type.FLOAT_TYPE ||
			type == org.objectweb.asm.Type.INT_TYPE ||
			type == org.objectweb.asm.Type.LONG_TYPE ||
			type == org.objectweb.asm.Type.SHORT_TYPE;
	}

	public static String getWrapperClassName(org.objectweb.asm.Type type) {
		if (type == org.objectweb.asm.Type.BOOLEAN_TYPE) {
			return "java/lang/Boolean";
		} else if (type == org.objectweb.asm.Type.BYTE_TYPE) {
			return "java/lang/Byte";
		} else if (type == org.objectweb.asm.Type.CHAR_TYPE) {
			return "java/lang/Character";
		} else if (type == org.objectweb.asm.Type.DOUBLE_TYPE) {
			return "java/lang/Double";
		} else if (type == org.objectweb.asm.Type.FLOAT_TYPE) {
			return "java/lang/Float";
		} else if (type == org.objectweb.asm.Type.INT_TYPE) {
			return "java/lang/Integer";
		} else if (type == org.objectweb.asm.Type.LONG_TYPE) {
			return "java/lang/Long";
		} else if (type == org.objectweb.asm.Type.SHORT_TYPE) {
			return "java/lang/Short";
		}
		throw new IllegalStateException(String.format("Unknown primitive type [%s]", type));
	}
}
