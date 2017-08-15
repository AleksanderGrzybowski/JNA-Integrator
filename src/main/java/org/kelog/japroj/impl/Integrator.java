package org.kelog.japroj.impl;

import com.google.common.collect.Range;
import lombok.extern.java.Log;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.kelog.japroj.exceptions.IntegrationNumericError;
import org.kelog.japroj.exceptions.InvalidInputFunctionError;
import org.kelog.japroj.value.IntegrationResult;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;


@Log
public abstract class Integrator {
    
    public abstract double callAlgorithm(Range<Double> range, double[] values);
    
    public IntegrationResult integrate(
            Range<Double> range,
            int numberOfPoints,
            final String function,
            int threadCount
    ) {
        
        double slice = (range.upperEndpoint() - range.lowerEndpoint()) / (double) threadCount;
        final int numberOfPointsPerThread = numberOfPoints / threadCount;
        
        Set<IntegrationResult> results = new HashSet<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CompletionService<IntegrationResult> service = new ExecutorCompletionService<>(executor);
        
        for (int i = 0; i < threadCount; ++i) {
            Range<Double> threadRange = Range.closed(
                    range.lowerEndpoint() + i * slice,
                    range.lowerEndpoint() + (i + 1) * slice
            );
            
            service.submit(() -> {
                String threadInfo = "Thread range + " + threadRange + ", points " + numberOfPointsPerThread;
                log.info("Starting " + threadInfo);
                
                IntegrationResult result = integrateSingle(threadRange, numberOfPointsPerThread, function);
                
                log.info("Finishes " + threadInfo);
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
                    log.severe("There was an unknown exception in some thread: " + e);
                    throw new AssertionError();
                }
            }
        }
        
        executor.shutdown();
        return IntegrationResult.sumOf(results);
    }
    
    
    // We have points x0, x1, x2 ... x(n-1), xn - so in fact the number of points is numberOfPoints+1
    // it doesn't matter actually, when we have like a million points
    private IntegrationResult integrateSingle(
            Range<Double> range,
            int numberOfPoints,
            String functionString
    ) {
        
        // numberOfPoints must be even, so SSE can work (it can be fixed by checking that first, but yeah...)
        if (numberOfPoints % 2 == 0) {
            numberOfPoints++;
        }
        double width = (range.upperEndpoint() - range.lowerEndpoint()) / ((double) numberOfPoints);
        
        Expression expression;
        try {
            expression = new ExpressionBuilder(functionString).variable("x").build();
        } catch (IllegalArgumentException e) {
            log.severe("Failed to make ExpressionBuilder " + e);
            throw new InvalidInputFunctionError(e);
        }
        
        double[] points = new double[numberOfPoints + 1];
        
        try {
            // be careful, we use <= here, so one more iteration
            for (int i = 0; i <= numberOfPoints; ++i) {
                double y = expression.setVariable("x", range.lowerEndpoint() + width * i).evaluate();
                
                if (y == Double.NaN || y == Double.NEGATIVE_INFINITY || y == Double.POSITIVE_INFINITY) {
                    throw new IntegrationNumericError();
                }
                
                points[i] = y;
            }
        } catch (ArithmeticException e) { // div by 0
            log.severe("An exception happened while calculating function table values: " + e);
            throw new IntegrationNumericError(e);
        }
        
        long before = System.nanoTime();
        double result = callAlgorithm(range, points);
        long after = System.nanoTime();
        
        return new IntegrationResult(result, after - before);
    }
}