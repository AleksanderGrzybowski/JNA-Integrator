import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;

public class CIntegrator extends Integrator {

	public CIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) throws IntegrationNumericError, InvalidInputFunctionError {
		return library.integrateC(left, right, numberOfPoints, values);
	}
}
