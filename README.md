Generates implementations for builder interfaces (the design pattern) that are as fast as those implemented by hand.

Project inspired by this blog post: http://garbagecollected.org/2007/07/12/builder-pattern-deluxe/

Example usage:

import java.util.*;

import com.github.lehphyro.fastbuilder.*;
import com.google.common.collect.*;

public class Example {

	private final String value1;
	private final int value2;
	private final List<Integer> numbers;

	public Example(Builder builder) {
		this.value1 = builder.getValue1();
		this.value2 = builder.getValue2();
		this.numbers = ImmutableList.copyOf(builder.getNumbers());
	}

	public interface Builder extends IBuilder<Example> {
		Builder value1(String v);
		Builder value2(int v);
		Builder addNumber(int v);
		Builder addNumbers(Collection<? extends Integer> v);
		String getValue1();
		int getValue2();
		List<Integer> getNumbers();
	}

	public static Builder builder() {
		// ====> An implementation of Builder will be generated and returned here <====
		return BuilderFactory.make(Builder.class);
	}

	public String getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}

	public List<Integer> getNumbers() {
		return numbers;
	}
}
