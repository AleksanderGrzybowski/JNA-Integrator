import com.sun.jna.Native;
import exceptions.PlatformLibraryNotFoundException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class LibraryWrapper {

	private static NativeInterface library;

	public static NativeInterface getLibrary() throws PlatformLibraryNotFoundException {
		if (library != null)
			return library;
		else {
			System.out.println("****** LibraryWrapper.getInstance()");
			System.out.println(" * java.library.path -> " + System.getProperty("java.library.path"));
			System.out.println(" * user.dir -> " + System.getProperty("user.dir"));
			System.out.println(" * getCurrentDir() -> " + getCurrentDir());
			System.out.println(" * Setting home/kelog library path, remove in release");
			System.setProperty("jna.library.path", getCurrentDir() + ":/home/kelog/Kodzenie/JNA-Integrator/native");

			System.out.println(" * Trying to load platform dependent library");
			try {
				library = (NativeInterface) Native.loadLibrary("native", NativeInterface.class);
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

			return library;
		}
	}

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
