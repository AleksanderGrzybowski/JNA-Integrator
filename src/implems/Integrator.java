package implems;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;
import misc.IntegrationResult;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public abstract class Integrator {

	protected NativeInterface library;

	public Integrator() throws PlatformLibraryNotFoundException {
		library = LibraryWrapper.getLibrary();
	}

	public static boolean isPlatformLibraryPresent() {
		// may change, however, if there fails it will fail everywhere else
		try {
			new AsmFPUIntegrator();
			return true;
		} catch (PlatformLibraryNotFoundException e) {
			return false;
		}
	}


	// We have points x0, x1, x2 ... x(n-1) xn, so in fact the number of points is numberOfPoints+1
	// it doesn't matter actually, when we have like a million points
	public IntegrationResult integrate(double left, double right, int numberOfPoints, String functionString) throws
			IntegrationNumericError, InvalidInputFunctionError {

		// numberOfPoints must be even, so SSE can work (it can be fixed by checking that first, but yeah...)
		if (numberOfPoints % 2 == 0) numberOfPoints++;

		double width = (right - left) / ((double) numberOfPoints);

		int sizeofDouble = Native.getNativeSize(Double.class);
		Pointer memory = new Memory((numberOfPoints + 1) * sizeofDouble);

		Expression expression;
		try {
			expression = new ExpressionBuilder(functionString).variable("x").build();
		} catch (IllegalArgumentException eaea) {
			throw new InvalidInputFunctionError();
		}

		try {
			// be careful, we use <= here, so one more iteration
			for (int i = 0; i <= numberOfPoints; ++i) {
				double y = expression.setVariable("x", left + width * i).evaluate();
				if (y == Double.NaN || y == Double.NEGATIVE_INFINITY || y == Double.POSITIVE_INFINITY)
					throw new IntegrationNumericError();
				memory.setDouble(i * sizeofDouble, y);
			}
		} catch (ArithmeticException ee) { // div by 0 i think
			throw new IntegrationNumericError();
		}

		long time = System.nanoTime();
		///////////////////////////
		double result = callAlgorithm(left, right, numberOfPoints, memory);
		///////////////////////////
		time = System.nanoTime() - time;
		return new IntegrationResult(result, time);
	}

	abstract double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) throws
			IntegrationNumericError, InvalidInputFunctionError;
}


class LibraryWrapper {

	public static final int ASM_TEST_MAGIC_NUMBER = 1337;
	private static NativeInterface library;

	public static NativeInterface getLibrary() throws PlatformLibraryNotFoundException {
		if (library != null) {
			System.out.println("****** implems.LibraryWrapper.getInstance() giving already initialized one");
			return library;
		} else {
			System.out.println("****** implems.LibraryWrapper.getInstance() trying to load");
			System.out.println(" * java.library.path -> " + System.getProperty("java.library.path"));
			System.out.println(" * user.dir -> " + System.getProperty("user.dir"));
			System.out.println(" * getCurrentDir() -> " + getCurrentDir());
			System.out.println(" * Setting home/kelog library path and current dir, remove in release");
			System.setProperty("jna.library.path", getCurrentDir() + ":/home/kelog/Kodzenie/JNA-Integrator/native");
			System.out.println(" * jna.library.path -> " + System.getProperty("jna.library.path"));

			System.out.println(" * Trying to load platform dependent library...");
			try {
				library = (NativeInterface) Native.loadLibrary("native", NativeInterface.class);
			} catch (LinkageError e) {
				System.out.println(" * ERROR LinkageError, propagating translated one");
				throw new PlatformLibraryNotFoundException();
			}

			System.out.println(" * Platform library loaded, testing...");
			if (library.testASMLibrary() == ASM_TEST_MAGIC_NUMBER)
				System.out.println(" * Test passed");
			else {
				System.out.println(" * Test FAILED, this should never happen!");
				throw new RuntimeException("Test FAILED!");
			}

			System.out.println("****** Finished loading ******");
			return library;
		}
	}

	// from stack, I don't really know if it works, but doesn't break anything
	private static String getCurrentDir() {
		String path = Integrator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			return new File(decodedPath).getParent();
		} catch (UnsupportedEncodingException e) {
		}
		throw new RuntimeException();
	}
}