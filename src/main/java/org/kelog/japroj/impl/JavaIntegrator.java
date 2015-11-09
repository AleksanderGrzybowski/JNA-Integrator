package org.kelog.japroj.impl;

import org.kelog.japroj.core.Integrator;

public class JavaIntegrator extends Integrator {
	
	public double callAlgorithm(double left, double right, double[] array) {
		int numberOfPoints = array.length - 1;
		
		double result = array[0] + array[numberOfPoints];
		for (int i = 1; i <= (numberOfPoints - 1); ++i) {
			result += 2 * array[i];
		}

		result *= ((right - left) / (2.0 * numberOfPoints));
		return result;
	}
}
