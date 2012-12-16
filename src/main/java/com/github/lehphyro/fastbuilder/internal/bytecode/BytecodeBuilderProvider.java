package com.github.lehphyro.fastbuilder.internal.bytecode;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.objectweb.asm.*;
import org.slf4j.*;

import com.github.lehphyro.fastbuilder.*;
import com.github.lehphyro.fastbuilder.internal.*;
import com.github.lehphyro.fastbuilder.internal.classloading.*;
import com.github.lehphyro.fastbuilder.util.*;

public class BytecodeBuilderProvider implements BuilderProvider {

	private static final Logger logger = LoggerFactory.getLogger(BytecodeBuilderProvider.class);

	private static final String BUILDER_SUFFIX = "Generated";

	private final Map<Class<?>, Class<? extends IBuilder<?>>> builderClassCache;

	public BytecodeBuilderProvider() {
		builderClassCache = new ConcurrentHashMap<Class<?>, Class<? extends IBuilder<?>>>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, V extends IBuilder<T>> V make(final Class<V> spec, final BuilderSpecification builderSpec) {
		Class<? extends IBuilder<?>> builderClass = builderClassCache.get(spec);
		if (builderClass == null) { // It's ok to regenerate builders if concurrent access occurs
									// because they are exactly the same
			builderClass = (Class<? extends IBuilder<?>>) generateBuilderClass(spec, builderSpec);
			builderClassCache.put(spec, builderClass);
		}
		try {
			return (V) builderClass.newInstance();
		} catch (Exception e) {
			// Should not happen
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T, V extends IBuilder<T>> Class<V> generateBuilderClass(final Class<T> spec, final BuilderSpecification builderSpec) {
		Class<?> target = Types.getTypeArgumentOfInterface(spec, IBuilder.class);
		String builderInterfaceName = Type.getInternalName(spec);
		String builderImplementationName = builderInterfaceName + BUILDER_SUFFIX;
		BuilderClassLoader classLoader = ClassLoaders.getClassLoader(target);

		logger.debug("Generating builder implementation class [{}] for target [{}]", builderImplementationName, target);

		ClassReader reader;
		ClassWriter writer;
		try {
			reader = new ClassReader(builderInterfaceName);
			writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			ClassVisitor chainVisitor = writer;
			if (logger.isDebugEnabled()) {
				try {
					Class<?> traceVisitorClass = Class.forName("org.objectweb.asm.util.TraceClassVisitor");
					java.lang.reflect.Constructor<?> constructor = traceVisitorClass.getConstructor(ClassVisitor.class, PrintWriter.class);
					chainVisitor = (ClassVisitor) constructor.newInstance(writer, new PrintWriter(System.out));
				} catch (ClassNotFoundException e) {
					// asm-util isnt available
				} catch (Exception e) {
					// couldnt create trace visitor
				}
			}

			BytecodeBuilderClassVisitor generator = new BytecodeBuilderClassVisitor(chainVisitor, target, builderSpec, builderInterfaceName, builderImplementationName);
			reader.accept(generator, ClassReader.SKIP_DEBUG);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to generate builder bytecode", e);
		}
		return (Class<V>) classLoader.defineClass(builderImplementationName.replace('/', '.'), writer.toByteArray());
	}
}
