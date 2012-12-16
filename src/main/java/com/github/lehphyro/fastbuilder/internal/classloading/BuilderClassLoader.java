package com.github.lehphyro.fastbuilder.internal.classloading;

/**
 * Exposes defineClass publicly for use by bytecode generators.
 */
public class BuilderClassLoader extends ClassLoader {

	public BuilderClassLoader(final ClassLoader classLoader) {
		super(classLoader);
	}

	public Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}
}
