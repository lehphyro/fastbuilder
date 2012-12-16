package com.github.lehphyro.fastbuilder.internal;

import com.github.lehphyro.fastbuilder.*;

public interface BuilderProvider {

	<T, V extends IBuilder<T>> V make(Class<V> spec, BuilderSpecification builderSpecification);

}
