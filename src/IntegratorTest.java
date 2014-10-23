import org.junit.Test;

import static org.junit.Assert.*;

public class IntegratorTest {

	@Test
	public void test_simple() throws Exception {
		double exp;
		double act;
		Integrator library = new Integrator();

		exp = 0;
		act = library.integrateC(0, Math.PI * 2, 1000, "sin(x)");
		assertEquals(exp, act, 0.01);

		exp = 20;
		act = library.integrateC(-10, 10, 1000, "1");
		assertEquals(exp, act, 0.01);

		exp = 0.5;
		act = library.integrateC(0, 1, 1000, "x");
		assertEquals(exp, act, 0.01);
	}

}