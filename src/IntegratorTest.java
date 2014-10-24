import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegratorTest {

	@Test
	public void test_all() throws Exception {
		doAllTests(new CIntegrator());
		doAllTests(new AsmFPUIntegrator());
	}

	private void doAllTests(Integrator integrator) throws Exception {
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
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);

		function = "cos(x)";
		left = 0;
		right = Math.PI * 2 * 10;
		expected = 0;
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);

		function = "1";
		left = -10;
		right = 10;
		expected = 20;
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);

		function = "x";
		left = 0;
		right = 1;
		expected = 0.5;
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);

		function = "x^2 + 1";
		left = 0;
		right = 2;
		expected = 14.0/3.0;
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);

		function = "2^x";
		left = 2;
		right = 4;
		expected = 17.3123;
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);

		function = "x^2 + x^(-2)";
		left = 1;
		right = 2;
		expected = 17.0/6.0;
		actual = integrator.integrate(left, right, POINTS, function).result;
		assertEquals(expected, actual, 0.01);
	}
}