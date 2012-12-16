package com.github.lehphyro.fastbuilder.example;

import static org.junit.Assert.*;

import org.junit.*;

public class ExampleTest {

	@Test
	public void test() throws Exception {
		Example.Builder builder = Example.builder();
		builder.value1("Mandatory Value");
		builder.value2(10);
		builder.value3('A');
		builder.addNumber(999);

		Example example = builder.build();
		assertEquals("Mandatory Value", example.getValue1());
		assertEquals(10, example.getValue2());
		assertEquals('A', example.getValue3());
		assertEquals(1, example.getNumbers().size());
		assertEquals(999L, (long)example.getNumbers().get(0));
	}

}
