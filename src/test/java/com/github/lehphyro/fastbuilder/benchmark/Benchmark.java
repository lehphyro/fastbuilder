package com.github.lehphyro.fastbuilder.benchmark;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.googlecode.charts4j.*;

public class Benchmark {

	private static final String VALUE_1 = "Mandatory Value";
	private static final int VALUE_2 = 20;
	private static final char VALUE_3 = 'C';

	private static final TestClass CORRECT_RESULT = TestClass.builder().value1(VALUE_1).value2(VALUE_2).value3(VALUE_3).build();

	private static final int FACTOR = 1000;

	@Test
	public void benchmark() throws Exception {
		List<Integer> executions = ImmutableList.of(3, 5, 10, 20, 40, 80, 150, 200, 400, 800, 1600);

		double[] manualX = new double[executions.size()];
		double[] generatedX = new double[executions.size()];
		double totalManual = 0.0, totalGenerated = 0.0;

		for (int i = 0; i < executions.size(); i++) {
			double manualTime = runManual(executions.get(i) * FACTOR);
			double generatedTime = runGenerated(executions.get(i) * FACTOR);

			manualX[i] = manualTime;
			generatedX[i] = generatedTime;

			totalManual += manualTime;
			totalGenerated += generatedTime;
		}

		System.out.printf("%10s%,13dms%8dms%n", "Total", (long) totalManual, (long) totalGenerated);

		Line generatedLine = Plots.newLine(DataUtil.scaleWithinRange(0, 1000, generatedX), Color.RED, "Generated Builder");
		Line manualLine = Plots.newLine(DataUtil.scaleWithinRange(0, 1000, manualX), Color.BLUE, "Manual Builder");

		LineChart chart = GCharts.newLineChart(generatedLine, manualLine);
		chart.setTitle("Builder Benchmark on JDK 6");
		chart.setSize(600, 450);

		AxisLabels xAxis = AxisLabelsFactory.newAxisLabels(Lists.newArrayList(Collections2.transform(executions, new Function<Integer, String>() {
			@Override
			public String apply(Integer from) {
				return from.toString();
			}
		})));
		chart.addXAxisLabels(xAxis);

		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000");
		chart.addYAxisLabels(yAxis);

		System.out.println(chart.toURLString());
	}

	protected long runManual(int count) {
		long t0 = System.nanoTime();

		int actual = 0;
		for (int i = 0; i < count; i++) {
			TestClass aux = new TestClassManualBuilder().value1(VALUE_1).value2(VALUE_2).value3(VALUE_3).build();
			if (CORRECT_RESULT.equals(aux)) {
				actual++;
			}
		}
		long t1 = System.nanoTime();
		assertEquals(count, actual);

		long time = MILLISECONDS.convert(t1 - t0, NANOSECONDS);

		System.out.printf("%10s%,13d%8d%1s%n", "Manual", count, time, "ms");

		return time;
	}

	protected long runGenerated(int count) {
		long t0 = System.nanoTime();

		int actual = 0;
		for (int i = 0; i < count; i++) {
			TestClass aux = TestClass.builder().value1(VALUE_1).value2(VALUE_2).value3(VALUE_3).build();
			if (CORRECT_RESULT.equals(aux)) {
				actual++;
			}
		}
		long t1 = System.nanoTime();
		assertEquals(count, actual);

		long time = MILLISECONDS.convert(t1 - t0, NANOSECONDS);

		System.out.printf("%10s%,13d%8d%1s%n", "Generated", count, time, "ms");

		return time;
	}

	public static void main(String[] args) throws Exception {
		new Benchmark().benchmark();
	}
}
