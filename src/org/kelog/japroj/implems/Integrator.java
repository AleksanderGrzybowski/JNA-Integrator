package org.kelog.japroj.implems;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.kelog.japroj.exceptions.IntegrationNumericError;
import org.kelog.japroj.exceptions.InvalidInputFunctionError;
import org.kelog.japroj.misc.IntegrationResult;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;


public enum Integrator {
	C_INTEGRATOR {
		@Override
		double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
			return library.integrateC(left, right, numberOfPoints, values);
		}
	}, SSE_INTEGRATOR {
		@Override
		double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
			return library.integrateASM_SSE(left, right, numberOfPoints, values);
		}
	}, FPU_INTEGRATOR {
		@Override
		double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
			return library.integrateASM_FPU(left, right, numberOfPoints, values);
		}
	}, JAVA_INTEGRATOR {
		@Override
		double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
			return JavaIntegrator.callAlgorithm(left, right, numberOfPoints, values);
		}
	};

	protected NativeInterface library = LibraryWrapper.getLibrary();
	private Logger logger = Logger.getLogger(Integrator.class.getName());


	public IntegrationResult integrate(double left, double right,
	                                   int numberOfPoints, final String functionString, int numberOfThreads) throws
			IntegrationNumericError, InvalidInputFunctionError {


		double slice = (right - left) / (double) numberOfThreads;
		final int threadPoints = numberOfPoints / numberOfThreads;
		List<Runnable> tasks = new ArrayList<>();
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);

		final Set<IntegrationResult> resultSet = Collections.synchronizedSet(new HashSet<>());
		final Set<Exception> exceptions = Collections.synchronizedSet(new HashSet<>());

//		Executor executor = Executors.newFixedThreadPool(10)

		for (int i = 0; i < numberOfThreads; ++i) {
			final double threadLeft = left + i * slice;
			final double threadRight = left + (i + 1) * slice;

			tasks.add(() -> {
				try {
					logger.log(Level.INFO, "Starting thread from " + threadLeft + " to " + threadRight + ", points " + threadPoints);
					IntegrationResult ir = integrateSingle(threadLeft, threadRight, threadPoints, functionString);
					logger.log(Level.INFO, "Finishes thread from " + threadLeft + " to " + threadRight + ", points " + threadPoints);
					resultSet.add(ir);
				} catch (Exception e) {
					logger.log(Level.WARNING, "An exception happened inside some thread: " + e);
					exceptions.add(e);
				}
				latch.countDown();
			});
		}

		for (Runnable r : tasks) {
			new Thread(r).start();
		}

		try {
			latch.await();
		} catch (InterruptedException ignored) { // should never happen
			ignored.printStackTrace();
		}

		if (!exceptions.isEmpty()) {
			logger.log(Level.WARNING, "There were " + exceptions.size() + " EXCEPTIONS in threads");

			// we care only the first one
			Exception e = exceptions.iterator().next();
			if (e instanceof IntegrationNumericError) {
				throw new IntegrationNumericError();
			} else if (e instanceof InvalidInputFunctionError) {
				throw new InvalidInputFunctionError();
			} else {
				logger.log(Level.WARNING, "There was an unknown exception in some thread: " + e);
			}
		}

		return IntegrationResult.sumOf(resultSet);
	}


	// We have points x0, x1, x2 ... x(n-1) xn, so in fact the number of points is numberOfPoints+1
	// it doesn't matter actually, when we have like a million points
	public IntegrationResult integrateSingle(double left, double right, int numberOfPoints, String functionString) throws
			IntegrationNumericError, InvalidInputFunctionError {

		// numberOfPoints must be even, so SSE can work (it can be fixed by checking that first, but yeah...)
		if (numberOfPoints % 2 == 0) numberOfPoints++;

		double width = (right - left) / ((double) numberOfPoints);

		int sizeofDouble = Native.getNativeSize(Double.class);
		Pointer memory = new Memory((numberOfPoints + 1) * sizeofDouble);

		Expression expression;
		try {
			expression = new ExpressionBuilder(functionString).variable("x").build();
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Failed to make ExpressionBuilder " + e);
			throw new InvalidInputFunctionError();
		}

		try {
			// be careful, we use <= here, so one more iteration
			for (int i = 0; i <= numberOfPoints; ++i) {
				double y = expression.setVariable("x", left + width * i).evaluate();

				if (y == Double.NaN || y == Double.NEGATIVE_INFINITY || y == Double.POSITIVE_INFINITY)
					throw new IntegrationNumericError();

				memory.setDouble(i * sizeofDouble, y);
			}
		} catch (ArithmeticException e) { // div by 0
			logger.log(Level.SEVERE, "An exception happened while calculating function table values: " + e);
			throw new IntegrationNumericError();
		}

		long time = System.nanoTime();
		///////////////////////////
		double result = callAlgorithm(left, right, numberOfPoints, memory);
		///////////////////////////
		time = System.nanoTime() - time;

		memory = null; // force GC?
		System.gc();

		return new IntegrationResult(result, time);
	}

	abstract double callAlgorithm(double left, double right, int numberOfPoints, Pointer values);
}