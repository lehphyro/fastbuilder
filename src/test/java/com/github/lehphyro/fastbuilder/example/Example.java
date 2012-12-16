package com.github.lehphyro.fastbuilder.example;

import java.util.*;

import com.github.lehphyro.fastbuilder.*;
import com.google.common.collect.*;

public class Example {

	private final String value1;
	private final int value2;
	private final char value3;
	private final List<Integer> numbers;

	public Example(Builder builder) {
		this.value1 = builder.getValue1();
		this.value2 = builder.getValue2();
		this.value3 = builder.getValue3();
		this.numbers = ImmutableList.copyOf(builder.getNumbers());
	}

	public interface Builder extends IBuilder<Example> {
		Builder value1(String v);
		Builder value2(int v);
		Builder value3(char v);
		Builder addNumber(int v);
		Builder addNumbers(Collection<? extends Integer> v);
		String getValue1();
		int getValue2();
		char getValue3();
		List<Integer> getNumbers();
	}

	public static Builder builder() {
		return BuilderFactory.make(Builder.class);
	}

	public String getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}

	public char getValue3() {
		return value3;
	}

	public List<Integer> getNumbers() {
		return numbers;
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
		if (!(obj instanceof Example)) {
			return false;
		}
		Example other = (Example) obj;
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
		return String.format("value1=%s, value2=%s, value3=%s, numbers=%s", value1, value2, value3, numbers);
	}
}
