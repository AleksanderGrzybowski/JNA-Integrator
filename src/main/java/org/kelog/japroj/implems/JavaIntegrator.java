package org.kelog.japroj.implems;

import com.sun.jna.Pointer;

public class JavaIntegrator {
	public static double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
		double[] array = values.getDoubleArray(0, numberOfPoints + 1);

		double result = array[0] + array[numberOfPoints];
		for (int i = 1; i <= (numberOfPoints - 1); ++i)
			result += 2 * array[i];

		result *= ((right - left) / (2.0 * numberOfPoints));
		return result;
	}
}
