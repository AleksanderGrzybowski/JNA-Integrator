package org.kelog.japroj.impl;

import com.google.common.collect.Range;
import org.kelog.japroj.core.Integrator;

public class JavaIntegrator extends Integrator {
	
	public double callAlgorithm(Range<Double> range, double[] array) {
		int numberOfPoints = array.length - 1;
		
		double result = array[0] + array[numberOfPoints];
		for (int i = 1; i <= (numberOfPoints - 1); ++i) {
			result += 2 * array[i];
		}

		result *= ((range.upperEndpoint() - range.lowerEndpoint()) / (2.0 * numberOfPoints));
		return result;
	}
}
