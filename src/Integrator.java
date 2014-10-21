import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class Integrator {

	private interface IntInterface extends Library {
		public double integrate(double a, double b, int n, Pointer values);
	}

	private static IntInterface library = null;

	public Integrator() throws PlatformLibraryNotFoundException {
		if (library == null) {
//			try {
			System.out.println("Hello");

			System.out.println(System.getProperty("java.library.path"));
			System.out.println(System.getProperty("user.dir"));
			System.out.println("CURRENT: " + getCurrentDir());
			System.setProperty("jna.library.path", getCurrentDir() + ":/home/kelog/Kodzenie/JNA-Test/native");
//				System.setProperty("jna.library.path", System.getProperty("user.dir"));

//				System.load("/home/kelog/Kodzenie/JNA-Test/native/libnative.so");


			library = (IntInterface) Native.loadLibrary("native", IntInterface.class);
//			} catch (LinkageError err) {
//				throw new PlatformLibraryNotFoundException();
//			}
		}
	}


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

	public double integrate(double left, double right, int numberOfPoints, String functionString) throws
			IntegrationNumericError, InvalidInputFunctionError {
		double width = ((double) right - (double) left) / ((double) numberOfPoints);

		int doubleSize = Native.getNativeSize(Double.class);
		Pointer memory = new Memory((numberOfPoints + 1) * doubleSize);

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
				memory.setDouble(i * doubleSize, y);
			}
		} catch (ArithmeticException ee) { // div by 0??
			throw new IntegrationNumericError();
		}
		return library.integrate(left, right, numberOfPoints, memory);
	}

	static public void main(String argv[]) throws Exception {
//		Expression e = new ExpressionBuilder("x^2").variable("x").build();
//		System.out.println(e.setVariable("x", 4.1).evaluate());
//		System.out.println(e.setVariable("x", 5).evaluate());


	}
}