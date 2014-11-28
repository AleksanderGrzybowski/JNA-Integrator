package implems;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;
import misc.IntegrationResult;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.concurrent.CountDownLatch;


public abstract class Integrator {

	protected NativeInterface library;

	public Integrator() throws PlatformLibraryNotFoundException {
		library = LibraryWrapper.getLibrary();
	}

	public IntegrationResult integrate(double left, double right,
	                                   int numberOfPoints, final String functionString, int numberOfThreads) throws
			IntegrationNumericError, InvalidInputFunctionError {


		double slice = (right - left) / (double) numberOfThreads;
		final int threadPoints = numberOfPoints / numberOfThreads;
		List<Runnable> tasks = new ArrayList<Runnable>();
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);

		final Set<IntegrationResult> resultSet = Collections.synchronizedSet(new HashSet<IntegrationResult>());
		final Set<Exception> exceptions = Collections.synchronizedSet(new HashSet<Exception>());

		for (int i = 0; i < numberOfThreads; ++i) {
			final double threadLeft = left + i * slice;
			final double threadRight = left + (i + 1) * slice;

			tasks.add(new Runnable() {
				@Override
				public void run() {
					try {
						IntegrationResult ir = integrateSingle(threadLeft, threadRight, threadPoints, functionString);
						resultSet.add(ir);
					} catch (Exception e) {
						exceptions.add(e);
					}
					latch.countDown();
				}
			});

		}

		for (Runnable r : tasks) {
			new Thread(r).start();
		}
		try {
			latch.await();
		} catch (InterruptedException ignored) {
			ignored.printStackTrace();
		}

		if (exceptions.isEmpty()) {
			System.out.println("There were no exceptions in threads");
		} else {
			System.out.println("There were " + exceptions.size() + " EXCEPTIONS in threads");
			Exception e = exceptions.iterator().next();
			if (e instanceof IntegrationNumericError) {
				throw new IntegrationNumericError();
			} else if (e instanceof InvalidInputFunctionError) {
				throw new InvalidInputFunctionError();
			} else {
				System.out.println("There was an unknown exception in some thread: " + e);
			}
		}

		return IntegrationResult.combine(resultSet);
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
		} catch (IllegalArgumentException eaea) {
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
		} catch (ArithmeticException ee) { // div by 0 i think
			throw new IntegrationNumericError();
		}

		long time = System.nanoTime();
		///////////////////////////
		double result = callAlgorithm(left, right, numberOfPoints, memory);
		///////////////////////////
		time = System.nanoTime() - time;
		return new IntegrationResult(result, time);
	}

	abstract double callAlgorithm(double left, double right, int numberOfPoints, Pointer values);
}