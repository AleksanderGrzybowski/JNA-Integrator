import com.sun.jna.Pointer;
import exceptions.PlatformLibraryNotFoundException;

public class JavaIntegrator extends Integrator {

	public JavaIntegrator() throws PlatformLibraryNotFoundException {
	}

	@Override
	double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
		double[] array = values.getDoubleArray(0, numberOfPoints+1);

		double result = array[0] + array[numberOfPoints];
		int i;
		for (i = 1; i <= (numberOfPoints-1); ++i)
			result += 2*array[i];

		result *= ( (right-left)/(2.0*numberOfPoints));
		return result;
	}
}
