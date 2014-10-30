import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class IntegratorTest {

	private enum TestCase {

		T01("sin(x)", 0, Math.PI, 2, 1000),
		T02("cos(x)", 0, 20 * Math.PI, 0, 1000),
		T03("1", -10, 10, 20, 1000),
		T04("x", 0, 1, 0.5, 1000),
		T05("x^2 + 1", 0, 2, 14.0 / 3.0, 1000),
		T06("2^x", 2, 4, 17.3123, 1000),
		T07("x^2 + x^(-2)", 1, 2, 17.0 / 6.0, 1000);

		//T01("", , , , 1000),

		TestCase(String function, double left, double right, double expected, int points) {
			this.left = left;
			this.right = right;
			this.points = points;
			this.function = function;
			this.expected = expected;
		}

		public double left, right;
		public int points;
		public String function;
		public double expected;
	}

	@Test
	public void test_all() throws Exception {
		List<Class<? extends Integrator>> implems = new ArrayList<Class<? extends Integrator>>();
		implems.add(CIntegrator.class);
		implems.add(AsmFPUIntegrator.class);
		implems.add(AsmSSEIntegrator.class);
		implems.add(JavaIntegrator.class);

		for (Class<? extends Integrator> clazz : implems) {
			Integrator instance = clazz.newInstance();
			System.out.println("****** Starting test routine for " + instance.getClass() + " ******");

			for (TestCase c : TestCase.values()) {
				double expected = c.expected;
				double actual = instance.integrate(c.left, c.right, c.points, c.function).result;
				System.out.println("Expected = " + expected + " actual = " + actual);
				assertEquals(expected, actual, 0.01);
			}

			System.out.println("****** Finished test routine for " + instance.getClass() + " ******");
		}
	}
}