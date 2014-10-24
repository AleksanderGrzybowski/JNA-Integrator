import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public abstract class Integrator {



	protected NativeInterface library;


	public Integrator() throws PlatformLibraryNotFoundException {
		library = LibraryWrapper.getLibrary();

	}

	public static boolean isPlatformLibraryPresent() {
		try {
			new AsmFPUIntegrator(); // any
			return true;
		} catch (PlatformLibraryNotFoundException e) {
			return false;
		}
	}

	// TODO does it work???


	// numberOfPoints to liczba punktów, przypisanych do x0, x1, ..., xn - razem (n+1) wartości

	public IntegrationResult integrate(double left, double right, int numberOfPoints, String functionString) throws
			IntegrationNumericError, InvalidInputFunctionError {
		double width = ((double) right - (double) left) / ((double) numberOfPoints);

		int sizeofDouble = Native.getNativeSize(Double.class);
		Pointer memory = new Memory((numberOfPoints + 1) * sizeofDouble);

		Expression e;
		try {
			e = new ExpressionBuilder(functionString).variable("x").build();
		} catch (IllegalArgumentException eaea) {
			throw new InvalidInputFunctionError();
		}

		try {
			for (int i = 0; i <= numberOfPoints; ++i) {
				double y = e.setVariable("x", left + width * i).evaluate();
				if (y == Double.NaN || y == Double.NEGATIVE_INFINITY || y == Double.POSITIVE_INFINITY)
					throw new IntegrationNumericError();
				memory.setDouble(i * sizeofDouble, y);
			}
		} catch (ArithmeticException ee) { // div by 0??
			throw new IntegrationNumericError();
		}

		long time = System.nanoTime();
		double result;
		///////////////////////////
		result = callNativeAlgorithm(left, right, numberOfPoints, memory);
		///////////////////////////
		time = System.nanoTime() - time;
		return new IntegrationResult(result, time);
	}


	abstract double callNativeAlgorithm(double left, double right, int numberOfPoints, Pointer values) throws
			IntegrationNumericError, InvalidInputFunctionError;
}
