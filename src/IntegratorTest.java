import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegratorTest {

	@Test
	public void test_C() throws Exception {
		TestAdapter testAdapter;

		testAdapter = new TestAdapter(new Integrator(), TestAdapter.AlgorithmType.C);
		doAllTests(testAdapter);
		testAdapter = new TestAdapter(new Integrator(), TestAdapter.AlgorithmType.ASM);
		doAllTests(testAdapter);
	}


	private void doAllTests(TestAdapter testAdapter) throws Exception {
		double expected, actual;
		double left, right;
		String function;
		final int POINTS = 1000;

		/*
		function = "";
		left = ;
		right = ;
		expected = ;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);
		*/


		function = "sin(x)";
		left = 0;
		right = Math.PI * 2;
		expected = 0;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);

		function = "1";
		left = -10;
		right = 10;
		expected = 20;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);


		function = "x";
		left = 0;
		right = 1;
		expected = 0.5;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);

		function = "x^2 + 1";
		left = 0;
		right = 2;
		expected = 14.0/3.0;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);

		function = "2^x";
		left = 2;
		right = 4;
		expected = 17.3123;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);

		function = "x^2 + x^(-2)";
		left = 1;
		right = 2;
		expected = 17.0/6.0;
		actual = testAdapter.integrate(left, right, POINTS, function);
		assertEquals(expected, actual, 0.01);



	}

	public static class TestAdapter {
		public enum AlgorithmType {C, ASM}

		private Integrator integrator;
		private AlgorithmType algorithmType;
		public TestAdapter(Integrator in, AlgorithmType al) {
			integrator = in;
			algorithmType = al;
		}

		public double integrate(double left, double right, int numberOfPoints, String functionString) throws
				IntegrationNumericError, InvalidInputFunctionError {

			if (algorithmType == AlgorithmType.ASM) {
				return integrator.integrateASM(left, right, numberOfPoints, functionString);
			} else if (algorithmType == AlgorithmType.C) {
				return integrator.integrateC(left, right, numberOfPoints, functionString);
			}
			throw new RuntimeException("Should never happen");
		}


	}
}