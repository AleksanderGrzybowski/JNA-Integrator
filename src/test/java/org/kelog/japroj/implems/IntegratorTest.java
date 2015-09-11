package org.kelog.japroj.implems;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class IntegratorTest {

	private static final int NUMBER_OF_POINTS = 10_000;
	private static final List<Integer> threadCombinations = Arrays.asList(1, 2, 4);
	private static final double COMPARISON_DELTA = 0.01;

	private enum TestCase {

		T01("sin(x)", 0, Math.PI, 2),
		T02("cos(x)", 0, 20 * Math.PI, 0),
		T03("1", -10, 10, 20),
		T04("x", 0, 1, 0.5),
		T05("x^2 + 1", 0, 2, 14.0 / 3.0),
		T06("2^x", 2, 4, 17.3123),
		T07("x^2 + x^(-2)", 1, 2, 17.0 / 6.0);

		TestCase(String function, double left, double right, double expected) {
			this.left = left;
			this.right = right;
			this.function = function;
			this.expected = expected;
		}

		private double left, right;
		private String function;
		private double expected;
	}

	@Test
	public void test_all() throws Exception {
		for (Integrator implem : Integrator.values()) {
			for (int numberOfThreads : threadCombinations) {
				for (TestCase testCase : TestCase.values()) {
					double actualResult = implem.integrate(testCase.left, testCase.right,
							NUMBER_OF_POINTS, testCase.function, numberOfThreads).result;

					assertEquals(testCase.expected, actualResult, COMPARISON_DELTA);
				}
			}
		}
	}
}