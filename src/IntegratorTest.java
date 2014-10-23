import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegratorTest {

	// FIX THAT! TODO

	@Test
	public void test_C() throws Exception {
		TestAdapter testAdapter;


		testAdapter = new TestAdapter(new Integrator(), TestAdapter.AlgorithmType.C);
		doAllTests(testAdapter);
		testAdapter = new TestAdapter(new Integrator(), TestAdapter.AlgorithmType.ASM);
		doAllTests(testAdapter);
	}


	private void doAllTests(TestAdapter testAdapter) throws Exception {
		double exp;
		double act;
		exp = 0;
		act = testAdapter.integrate(0, Math.PI * 2, 1000, "sin(x)");
		assertEquals(exp, act, 0.01);

		exp = 20;
		act = testAdapter.integrate(-10, 10, 1000, "1");
		assertEquals(exp, act, 0.01);

		exp = 0.5;
		act = testAdapter.integrate(0, 1, 1000, "x");
		assertEquals(exp, act, 0.01);
	}

	/**
	 * Created by kelog on 23.10.14.
	 */
	public static class TestAdapter {
		public enum AlgorithmType {C, ASM};

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