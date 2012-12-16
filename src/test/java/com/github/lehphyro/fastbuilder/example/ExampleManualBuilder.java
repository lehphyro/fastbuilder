package com.github.lehphyro.fastbuilder.example;

import java.util.*;

import com.github.lehphyro.fastbuilder.example.Example.*;

public class ExampleManualBuilder implements Example.Builder {

	private String value1;
	private int value2;
	private char value3;
	private List<Integer> numbers;

	public ExampleManualBuilder() {
		numbers = new ArrayList<Integer>();
	}

	@Override
	public Builder value1(String v) {
		value1 = v;
		return this;
	}

	@Override
	public Builder value2(int v) {
		value2 = v;
		return this;
	}

	@Override
	public Builder value3(char v) {
		value3 = v;
		return this;
	}

	@Override
	public Builder addNumber(int v) {
		numbers.add(v);
		return this;
	}

	@Override
	public Builder addNumbers(Collection<? extends Integer> v) {
		numbers.addAll(v);
		return this;
	}

	@Override
	public String getValue1() {
		return value1;
	}

	@Override
	public int getValue2() {
		return value2;
	}

	@Override
	public char getValue3() {
		return value3;
	}

	@Override
	public List<Integer> getNumbers() {
		return numbers;
	}

	@Override
	public Example build() {
		return new Example(this);
	}
}
