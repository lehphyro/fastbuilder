package com.github.lehphyro.fastbuilder.benchmark;

import com.github.lehphyro.fastbuilder.*;

public class TestClass {
	
	private final String value1;
	private final int value2;
	private final char value3;

	public TestClass(Builder builder) {
		this.value1 = builder.getValue1();
		this.value2 = builder.getValue2();
		this.value3 = builder.getValue3();
	}

	public interface Builder extends IBuilder<TestClass> {
		Builder value1(String v);
		Builder value2(int v);
		Builder value3(char v);
		String getValue1();
		int getValue2();
		char getValue3();
	}

	public static Builder builder() {
		return BuilderFactory.make(Builder.class);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + value2;
		result = prime * result + value3;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TestClass)) {
			return false;
		}
		TestClass other = (TestClass) obj;
		if (value1 == null) {
			if (other.value1 != null) {
				return false;
			}
		} else if (!value1.equals(other.value1)) {
			return false;
		}
		if (value2 != other.value2) {
			return false;
		}
		if (value3 != other.value3) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("value1=%s, value2=%s, value3=%s", value1, value2, value3);
	}
}
