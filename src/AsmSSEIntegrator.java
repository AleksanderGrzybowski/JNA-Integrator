import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;

public class AsmSSEIntegrator extends Integrator {

	public AsmSSEIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callNativeAlgorithm(double left, double right, int numberOfPoints, Pointer values) throws IntegrationNumericError, InvalidInputFunctionError {
		return library.integrateASM_SSE(left, right, numberOfPoints, values);
	}
}
