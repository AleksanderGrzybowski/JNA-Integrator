package org.kelog.japroj.core;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.kelog.japroj.exceptions.IntegrationNumericError;
import org.kelog.japroj.exceptions.InvalidInputFunctionError;
import org.kelog.japroj.value.IntegrationResult;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Integrator {
	
	private Logger logger = Logger.getLogger(Integrator.class.getName());

	public abstract double callAlgorithm(double left, double right, double[] values);
	
	public IntegrationResult integrate(double left, double right, int numberOfPoints,
	                                   final String function, int threadCount) throws
			IntegrationNumericError, InvalidInputFunctionError {

		double slice = (right - left) / (double) threadCount;
		final int numberOfPointsPerThread = numberOfPoints / threadCount;

		Set<IntegrationResult> results = new HashSet<>();

		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CompletionService<IntegrationResult> service = new ExecutorCompletionService<>(executor);

		for (int i = 0; i < threadCount; ++i) {
			final double threadLeft = left + i * slice;
			final double threadRight = left + (i + 1) * slice;

			service.submit(() -> {
                String threadInfo = "thread from " + threadLeft + " to " + threadRight + ", points " + numberOfPointsPerThread;
                logger.log(Level.INFO, "Starting " + threadInfo);

                IntegrationResult result = integrateSingle(threadLeft, threadRight, numberOfPointsPerThread, function);

                logger.log(Level.INFO, "Finishes " + threadInfo);
                return result;
			});
		}

		for (int i = 0; i < threadCount; ++i) {
			try {
				results.add(service.take().get());
			} catch (InterruptedException ignored) {
			} catch (ExecutionException ee) {
				Throwable e = ee.getCause();
				if (e instanceof IntegrationNumericError) {
					throw (IntegrationNumericError) e;
				} else if (e instanceof InvalidInputFunctionError) {
					throw (InvalidInputFunctionError) e;
				} else {
					logger.log(Level.WARNING, "There was an unknown exception in some thread: " + e);
				}
			}
		}

		executor.shutdown();
		return IntegrationResult.sumOf(results);
	}


	// We have points x0, x1, x2 ... x(n-1), xn - so in fact the number of points is numberOfPoints+1
	// it doesn't matter actually, when we have like a million points
	private IntegrationResult integrateSingle(double left, double right, int numberOfPoints, String functionString) throws
			IntegrationNumericError, InvalidInputFunctionError {

		// numberOfPoints must be even, so SSE can work (it can be fixed by checking that first, but yeah...)
		if (numberOfPoints % 2 == 0) numberOfPoints++;
		double width = (right - left) / ((double) numberOfPoints);
		
		Expression expression;
		try {
			expression = new ExpressionBuilder(functionString).variable("x").build();
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Failed to make ExpressionBuilder " + e);
			throw new InvalidInputFunctionError();
		}

		double[] points = new double[numberOfPoints+1];
		
		try {
			// be careful, we use <= here, so one more iteration
			for (int i = 0; i <= numberOfPoints; ++i) {
				double y = expression.setVariable("x", left + width * i).evaluate();

				if (y == Double.NaN || y == Double.NEGATIVE_INFINITY || y == Double.POSITIVE_INFINITY) {
					throw new IntegrationNumericError();
				}

				points[i] = y;
			}
		} catch (ArithmeticException e) { // div by 0
			logger.log(Level.SEVERE, "An exception happened while calculating function table values: " + e);
			throw new IntegrationNumericError();
		}

		long before = System.nanoTime();
		double result = callAlgorithm(left, right, points);
		long after = System.nanoTime();

		return new IntegrationResult(result, after - before);
	}
}