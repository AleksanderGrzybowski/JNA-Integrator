package benchmark;

import implems.*;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {

	static String functionString = "sin(x)";
	static double left = 0;
	static double right = Math.PI * 2;
	static int points = 1000 * 1000;
	static int iters = 10;

	static List<Class<? extends Integrator>> implems = new ArrayList<Class<? extends Integrator>>();
	static {
		implems.add(CIntegrator.class);
		implems.add(AsmFPUIntegrator.class);
		implems.add(AsmSSEIntegrator.class);
		implems.add(JavaIntegrator.class);
	}

	public static void main(String[] args) throws Exception {
		for (Class<? extends Integrator> clazz : implems) {
			Integrator instance = clazz.newInstance();
			System.out.print("*** Benchmark for " + instance.getClass() + " -> ");

			long sumOfTimes = 0;
			for (int i = 0; i < iters; ++i) {
				sumOfTimes += instance.integrate(left, right, points, functionString, 1).timeNS;
			}

			System.out.println("" + ((double) (sumOfTimes / iters)) / (1000000.0) + " ms\n");
		}
	}

}
