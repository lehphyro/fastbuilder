package com.github.lehphyro.fastbuilder.internal;

public interface BuilderSpecification {

	boolean isReader(String name, int argCount);
	boolean isWriter(String name, int argCount);
	boolean isCollection(String name);

	String getFieldName(String name);

}
