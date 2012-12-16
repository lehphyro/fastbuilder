package com.github.lehphyro.fastbuilder.benchmark;

import com.github.lehphyro.fastbuilder.benchmark.TestClass.*;

public class TestClassManualBuilder implements TestClass.Builder {

	private String value1;
	private int value2;
	private char value3;

	@Override
	public Builder value1(String v) {
		this.value1 = v;
		return this;
	}

	@Override
	public TestClass.Builder value2(int v) {
		this.value2 = v;
		return this;
	}

	@Override
	public TestClass.Builder value3(char v) {
		this.value3 = v;
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
	public TestClass build() {
		return new TestClass(this);
	}
}
