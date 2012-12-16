package com.github.lehphyro.fastbuilder;

/**
 * Defines a builder object whose responsibilty is to build instances of {@code T}.
 * 
 * @param <T> Type of objects built by this builder.
 */
public interface IBuilder<T> {
	/**
	 * @return New instance of {@code T} with the current state of the builder.
	 */
	T build();
}
