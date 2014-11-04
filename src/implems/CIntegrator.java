package implems;

import com.sun.jna.Pointer;
import exceptions.PlatformLibraryNotFoundException;

public class CIntegrator extends Integrator {

	public CIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
		return library.integrateC(left, right, numberOfPoints, values);
	}
}
