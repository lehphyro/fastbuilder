package com.github.lehphyro.fastbuilder.internal.classloading;

import java.security.*;
import java.util.concurrent.*;

import com.google.common.cache.*;

public class ClassLoaders {

	private static final LoadingCache<ClassLoader, BuilderClassLoader> CLASS_LOADER_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<ClassLoader, BuilderClassLoader>() {
		public BuilderClassLoader load(final ClassLoader typeClassLoader) {
			return AccessController.doPrivileged(new PrivilegedAction<BuilderClassLoader>() {
				public BuilderClassLoader run() {
					return new BuilderClassLoader(typeClassLoader);
				}
			});
		}
	});

	private ClassLoaders() {
	}

	/**
	 * In order to make defineClass() public, we create our own class loader.
	 * 
	 * @param type Type from which we get the class loader.
	 * @return Our own class loader making defineClass() public.
	 */
	public static BuilderClassLoader getClassLoader(Class<?> type) {
		return getClassLoader(type.getClassLoader());
	}

	private static BuilderClassLoader getClassLoader(ClassLoader delegate) {
		ClassLoader aux = canonicalize(delegate);

		if (aux instanceof BuilderClassLoader) {
			return (BuilderClassLoader) aux;
		}

		try {
			return CLASS_LOADER_CACHE.get(aux);
		} catch (ExecutionException e) {
			throw new RuntimeException(e.getCause());
		}
	}

	private static ClassLoader canonicalize(ClassLoader classLoader) {
		ClassLoader result;
		if (classLoader == null) {
			result = getSystemClassLoaderOrNull();
			if (result == null) {
				throw new IllegalStateException("Could not find a class loader");
			}
		} else {
			result = classLoader;
		}

		return result;
	}

	private static ClassLoader getSystemClassLoaderOrNull() {
		return ClassLoader.getSystemClassLoader();
	}
}
