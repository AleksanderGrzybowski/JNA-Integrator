package org.kelog.japroj.benchmark;

import org.kelog.japroj.implems.Integrator;
import org.kelog.japroj.misc.Utils;

public class Benchmark {

	static String functionString = "sin(x)";
	static double left = 0;
	static double right = Math.PI * 2;
	static int points = 1_000_000;
	static int iters = 10;
	static int threads = 1;

	public static void main(String[] args) throws Exception {
		Utils.disableLogging();

		for (Integrator instance : Integrator.values()) {
			System.out.print("*** Benchmark for " + instance + " -> ");

			long sumOfTimes = 0;
			for (int i = 0; i < iters; ++i) {
				sumOfTimes += instance.integrate(left, right, points, functionString, threads).timeNS;
			}

			double result = ((double) (sumOfTimes / iters)) / (1_000_000.0);
			System.out.println("" + result + " ms\n");
		}
	}
}
