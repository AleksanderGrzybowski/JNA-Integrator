import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;

/**
 * Created by kelog on 24.10.14.
 */
public class AsmFPUIntegrator extends Integrator {

	public AsmFPUIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callNativeAlgorithm(double left, double right, int numberOfPoints, Pointer values) throws IntegrationNumericError, InvalidInputFunctionError {
		return library.integrateASM_FPU(left, right, numberOfPoints, values);
	}
}
