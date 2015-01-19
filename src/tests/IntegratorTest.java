package tests;

import implems.Integrator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;


public class IntegratorTest {

	private static final int POINTS = 10_000;
	private static final List<Integer> threadCombinations = Arrays.asList(1, 2, 4);

	private enum TestCase {

		T01("sin(x)", 0, Math.PI, 2, POINTS),
		T02("cos(x)", 0, 20 * Math.PI, 0, POINTS),
		T03("1", -10, 10, 20, POINTS),
		T04("x", 0, 1, 0.5, POINTS),
		T05("x^2 + 1", 0, 2, 14.0 / 3.0, POINTS),
		T06("2^x", 2, 4, 17.3123, POINTS),
		T07("x^2 + x^(-2)", 1, 2, 17.0 / 6.0, POINTS);

		TestCase(String function, double left, double right, double expected, int points) {
			this.left = left;
			this.right = right;
			this.points = points;
			this.function = function;
			this.expected = expected;
		}

		private double left, right;
		private int points;
		private String function;
		private double expected;
	}

	@Test
	public void test_all() throws Exception {
		// turn off logging
		Logger l0 = Logger.getLogger("");
		l0.removeHandler(l0.getHandlers()[0]);


		for (Integrator implem:  Integrator.values()) {
			for (int numberOfThreads : threadCombinations) {
				System.out.println("****** Starting test routine for " + implem.getClass() + " (threads: " + numberOfThreads + ") ******");

				for (TestCase testCase : TestCase.values()) {
					double expected = testCase.expected;
					double actual = implem.integrate(testCase.left, testCase.right,
							testCase.points, testCase.function, numberOfThreads).result;

					System.out.println("Expected = " + expected + " actual = " + actual);
					assertEquals(expected, actual, 0.01);
				}

				System.out.println("****** Finished test routine for " + implem.getClass() + " ******\n");
			}
		}
	}
}