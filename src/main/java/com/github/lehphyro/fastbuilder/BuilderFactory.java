package com.github.lehphyro.fastbuilder;

import com.github.lehphyro.fastbuilder.internal.*;
import com.github.lehphyro.fastbuilder.internal.bytecode.*;
import com.github.lehphyro.fastbuilder.internal.spec.*;

/**
 * Creates {@link IBuilder} instances.
 */
public class BuilderFactory {

	private static final BuilderProvider BYTECODE_BUILDER_PROVIDER = new BytecodeBuilderProvider();

	/**
	 * Creates a new {@link IBuilder} implementing the provided interface following
	 * the {@link SimpleSetterBuilderSpecification} format.
	 *
	 * @param <T> Object being built type
	 * @param <V> Builder type
	 * @param spec Builder interface that the generated builder must implement.
	 * @return New builder instance.
	 */
	public static <T, V extends IBuilder<T>> V make(final Class<V> spec) {
		return make(spec, SimpleSetterBuilderSpecification.INSTANCE);
	}

	/**
	 * Creates a new {@link IBuilder} implementing the provided interface following
	 * the provided builder format specification.
	 *
	 * @param <T> Object being built type
	 * @param <V> Builder type
	 * @param spec Builder interface that the generated builder must implement.
	 * @param builderSpec Specification of how to create builders
	 * @return New builder instance.
	 */
	public static <T, V extends IBuilder<T>> V make(final Class<V> spec, final BuilderSpecification builderSpec) {
		return BYTECODE_BUILDER_PROVIDER.make(spec, builderSpec);
	}
}
