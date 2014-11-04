package implems;

import com.sun.jna.Pointer;
import exceptions.PlatformLibraryNotFoundException;

public class AsmSSEIntegrator extends Integrator {

	public AsmSSEIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
		return library.integrateASM_SSE(left, right, numberOfPoints, values);
	}
}
