import com.sun.jna.Pointer;
import exceptions.PlatformLibraryNotFoundException;

public class AsmFPUIntegrator extends Integrator {

	public AsmFPUIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callNativeAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
		return library.integrateASM_FPU(left, right, numberOfPoints, values);
	}
}
