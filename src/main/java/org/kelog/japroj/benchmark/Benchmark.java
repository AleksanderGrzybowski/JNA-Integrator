package org.kelog.japroj.benchmark;

import com.google.inject.Guice;
import com.google.inject.Inject;
import org.kelog.japroj.core.Integrator;
import org.kelog.japroj.di.MainModule;
import org.kelog.japroj.impl.AllIntegrators;

@SuppressWarnings("FieldCanBeLocal")
public class Benchmark {
	
	private String function = "sin(x)";
	private double left = 0;
	private double right = Math.PI * 2;
	private int points = 1_000_000;
	private int iters = 10;
	private int threads = 1;

	private final AllIntegrators all;

	@Inject
	public Benchmark(AllIntegrators all) {
		this.all = all;
	}

	public void start() throws Exception {
		for (Integrator instance : all.integrators) {
			System.out.print("*** Benchmark for " + instance + " -> ");

			long sumOfTimes = 0;
			for (int i = 0; i < iters; ++i) {
				sumOfTimes += instance.integrate(left, right, points, function, threads).timeNS;
			}

			double result = ((double) (sumOfTimes / iters)) / (1_000_000.0);
			System.out.println(result + " ms\n");
		}
	}
	
	public static void main(String[] args) throws Exception {
		Guice.createInjector(new MainModule()).getInstance(Benchmark.class).start();
	}
}
