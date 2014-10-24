import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public abstract class Integrator {

	protected interface IntInterface extends Library {
		public double integrateC(double a, double b, int n, Pointer values);

		public double integrateASM_FPU(double a, double b, int n, Pointer values);

		public int testASMLibrary();
	}

	protected IntInterface library;

	public Integrator() throws PlatformLibraryNotFoundException {
		System.out.println("****** Integrator() id=" + System.identityHashCode(this) + " start, info:  ******");
		System.out.println(" * java.library.path -> " + System.getProperty("java.library.path"));
		System.out.println(" * user.dir -> " + System.getProperty("user.dir"));
		System.out.println(" * getCurrentDir() -> " + getCurrentDir());
		System.out.println(" * Setting home/kelog library path, remove in release");
		System.setProperty("jna.library.path", getCurrentDir() + ":/home/kelog/Kodzenie/JNA-Integrator/native");

		System.out.println(" * Trying to load platform dependent library");
		try {
			library = (IntInterface) Native.loadLibrary("native", IntInterface.class);
		} catch (LinkageError e) {
			System.out.println(" * LinkageError, translate+propagate");
			throw new PlatformLibraryNotFoundException();
		}

		System.out.println(" * Platform library loaded, testing...");
		if (library.testASMLibrary() == 1337) // magic number
			System.out.println(" * Test passed");
		else {
			System.out.println(" * Test FAILED!");
			throw new RuntimeException("Test FAILED!");
		}

		System.out.println("****** Finished loading ******");

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
	private String getCurrentDir() {
		String path = Integrator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			return new File(decodedPath).getParent();
		} catch (UnsupportedEncodingException e) {
		}
		throw new RuntimeException();
	}

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
