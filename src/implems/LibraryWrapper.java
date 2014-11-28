package implems;

import com.sun.jna.Native;
import exceptions.PlatformLibraryNotFoundException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class LibraryWrapper {

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

	public static boolean isPlatformLibraryPresent() {
		// may change, however, if there fails it will fail everywhere else
		try {
			new AsmFPUIntegrator();
			return true;
		} catch (PlatformLibraryNotFoundException e) {
			return false;
		}
	}
}
