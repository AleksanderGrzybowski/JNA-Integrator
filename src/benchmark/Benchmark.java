package benchmark;

import implems.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Benchmark {

	static String functionString = "sin(x)";
	static double left = 0;
	static double right = Math.PI * 2;
	static int points = 1_000_000;
	static int iters = 10;
	static int threads = 1;

	static List<Class<? extends Integrator>> implems = new ArrayList<Class<? extends Integrator>>();
	static {
		implems.add(CIntegrator.class);
		implems.add(AsmFPUIntegrator.class);
		implems.add(AsmSSEIntegrator.class);
		implems.add(JavaIntegrator.class);
	}

	public static void main(String[] args) throws Exception {
		// turn off logging
		Logger l0 = Logger.getLogger("");
		l0.removeHandler(l0.getHandlers()[0]);

		for (Class<? extends Integrator> clazz : implems) {
			Integrator instance = clazz.newInstance();
			System.out.print("*** Benchmark for " + instance.getClass() + " -> ");

			long sumOfTimes = 0;
			for (int i = 0; i < iters; ++i) {
				sumOfTimes += instance.integrate(left, right, points, functionString, threads).timeNS;
			}

			System.out.println("" + ((double) (sumOfTimes / iters)) / (1_000_000.0) + " ms\n");
		}
	}

}
