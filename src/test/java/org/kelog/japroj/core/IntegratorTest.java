package org.kelog.japroj.core;

import com.google.common.collect.Range;
import com.google.inject.Guice;
import org.junit.Test;
import org.kelog.japroj.di.MainModule;
import org.kelog.japroj.impl.AllIntegrators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class IntegratorTest {

	private static final int NUMBER_OF_POINTS = 10_000;
	private static final List<Integer> threadCombinations = Arrays.asList(1, 2, 4);
	private static final double COMPARISON_DELTA = 0.01;

	private enum TestCase {

		T01("sin(x)", Range.closed(0.0, Math.PI), 2),
		T02("cos(x)", Range.closed(0.0, 20 * Math.PI), 0),
		T03("1", Range.closed(-10.0, 10.0), 20),
		T04("x", Range.closed(0.0, 1.0), 0.5),
		T05("x^2 + 1", Range.closed(0.0, 2.0), 14.0 / 3.0),
		T06("2^x", Range.closed(2.0, 4.0), 17.3123),
		T07("x^2 + x^(-2)", Range.closed(1.0, 2.0), 17.0 / 6.0);

		TestCase(String function, Range<Double> range, double expected) {
			this.range = range;
			this.function = function;
			this.expected = expected;
		}

		private Range<Double> range;
		private String function;
		private double expected;
	}

	@Test
	public void test_all() throws Exception {
		Collection<Integrator> integrators = Guice.createInjector(new MainModule()).getInstance(AllIntegrators.class).integrators;
		
		for (Integrator implem : integrators) {
			for (int numberOfThreads : threadCombinations) {
				for (TestCase testCase : TestCase.values()) {
					double actualResult = implem.integrate(testCase.range,
							NUMBER_OF_POINTS, testCase.function, numberOfThreads).result;

					assertEquals(testCase.expected, actualResult, COMPARISON_DELTA);
				}
			}
		}
	}
}