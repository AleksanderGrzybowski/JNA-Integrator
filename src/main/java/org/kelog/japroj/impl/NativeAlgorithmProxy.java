package org.kelog.japroj.impl;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.kelog.japroj.core.Integrator;

public abstract class NativeAlgorithmProxy extends Integrator {
	
	@Override
	public double callAlgorithm(double left, double right, double[] values) {
		int numberOfPoints = values.length - 1;
		int sizeofDouble = Native.getNativeSize(Double.class);
		Pointer memory = new Memory((numberOfPoints + 1) * sizeofDouble);

		for (int i = 0; i <= numberOfPoints; ++i) {
			memory.setDouble(i * sizeofDouble, values[i]);
		}
		
		return callAlgorithm(left, right, numberOfPoints, memory);
	}

	public abstract double callAlgorithm(double left, double right, int numberOfPoints, Pointer values);
}
