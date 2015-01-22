package org.kelog.japroj.benchmark;

import org.kelog.japroj.implems.Integrator;

import java.util.logging.Logger;

public class Benchmark {

	static String functionString = "sin(x)";
	static double left = 0;
	static double right = Math.PI * 2;
	static int points = 1_000_000;
	static int iters = 10;
	static int threads = 1;

	public static void main(String[] args) throws Exception {
		// turn off logging
		Logger l0 = Logger.getLogger("");
		l0.removeHandler(l0.getHandlers()[0]);

		for (Integrator instance : Integrator.values()) {

			System.out.print("*** Benchmark for " + instance + " -> ");

			long sumOfTimes = 0;
			for (int i = 0; i < iters; ++i) {
				sumOfTimes += instance.integrate(left, right, points, functionString, threads).timeNS;
			}

			System.out.println("" + ((double) (sumOfTimes / iters)) / (1_000_000.0) + " ms\n");
		}
	}

}
