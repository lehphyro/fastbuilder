package com.github.lehphyro.fastbuilder.internal.spec;

import com.github.lehphyro.fastbuilder.internal.*;

/**
 * <p>Setters with same name as attributes.</p>
 * <p>Getters starting with "get".</p>
 * <p>Collections adding starting with "add".</p>
 * 
 * Example:
 * 
 * <pre>
 * {@code
 *   private int value;
 *   Builder value(int val) {
 *     ...
 *   }
 *   int getValue() {
 *     ...
 *   }
 *   Builder addItem(Object v) {
 *     ...
 *   }
 *   public List&lt;Object&gt; getItems() {
 *     ...
 *   }
 * }
 * </pre>
 */
public class SimpleSetterBuilderSpecification implements BuilderSpecification {

	public static final SimpleSetterBuilderSpecification INSTANCE = new SimpleSetterBuilderSpecification();

	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";
	private static final String ADD_PREFIX = "add";

	public boolean isReader(String name, int argCount) {
		return argCount == 0 && (name.startsWith(GET_PREFIX) || name.startsWith(IS_PREFIX));
	}

	public boolean isWriter(String name, int argCount) {
		return argCount == 1;
	}

	@Override
	public boolean isCollection(String name) {
		return name.startsWith(ADD_PREFIX) && Character.isUpperCase(name.charAt(ADD_PREFIX.length()));
	}

	@Override
	public String getFieldName(String name) {
		String aux;
		if (name.startsWith(GET_PREFIX)) {
			aux = name.substring(GET_PREFIX.length());
		} else if (name.startsWith(IS_PREFIX)) {
			aux = name.substring(IS_PREFIX.length());
		} else if (name.startsWith(ADD_PREFIX)) {
			aux = name.substring(ADD_PREFIX.length());
			if (!name.endsWith("s")) {
				aux += "s";
			}
		} else {
			throw new IllegalStateException(String.format("Unknown field name format [%s]", name));
		}
		if (aux.length() > 0) {
			aux = Character.toLowerCase(aux.charAt(0)) + aux.substring(1);
		}
		return aux;
	}
}
