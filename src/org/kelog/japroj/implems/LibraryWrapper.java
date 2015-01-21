package org.kelog.japroj.implems;

import com.sun.jna.Native;
import org.kelog.japroj.exceptions.PlatformLibraryNotFoundException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryWrapper {

	public static final int ASM_TEST_MAGIC_NUMBER = 1337;
	public static final String NATIVE_LIBRARY_DIRECTORY_WHEN_TESTING = "native";
	private static NativeInterface library;

	private static Logger logger = Logger.getLogger(LibraryWrapper.class.getName());

	public static NativeInterface getLibrary() throws PlatformLibraryNotFoundException {
		if (library != null) {
			logger.log(Level.INFO, "org.kelog.japroj.implems.LibraryWrapper.getInstance() giving already initialized one");
			return library;
		} else {
			logger.log(Level.INFO, "org.kelog.japroj.implems.LibraryWrapper.getInstance() trying to load");
			logger.log(Level.INFO, " * java.library.path -> " + System.getProperty("java.library.path"));

			String currentDir = getCurrentDir();
			logger.log(Level.INFO, " * current directory -> " + currentDir);

			// this multiple-dir search is included so when tests are begin run
			// they can find the library
			// when running from jar, it's simpler - library is in the same folder as jar
			String newJNApath = currentDir + ":" + (currentDir + File.separator + NATIVE_LIBRARY_DIRECTORY_WHEN_TESTING);
			logger.log(Level.INFO, " * Setting up JNA: jna.library.path is now -> " + newJNApath);
			System.setProperty("jna.library.path", newJNApath);

			logger.log(Level.INFO, " * Trying to load platform dependent library...");
			try {
				library = (NativeInterface) Native.loadLibrary("native", NativeInterface.class);
			} catch (LinkageError e) {
				logger.log(Level.SEVERE, " * ERROR LinkageError, propagating translated one");
				throw new PlatformLibraryNotFoundException();
			}

			logger.log(Level.INFO, " * Platform library loaded, testing...");
			if (library.testASMLibrary() == ASM_TEST_MAGIC_NUMBER)
				logger.log(Level.INFO, " * Test passed");
			else {
				logger.log(Level.SEVERE, " * Test FAILED, this should never happen!");
				throw new RuntimeException("Test FAILED!");
			}

			logger.log(Level.INFO, "****** Finished loading ******");
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
		throw new RuntimeException(); // should never happen
	}

	public static boolean isPlatformLibraryPresent() {
		// may change, however, if there fails it will fail everywhere else
		try {
			LibraryWrapper.getLibrary();
			return true;
		} catch (PlatformLibraryNotFoundException e) {
			return false;
		}
	}
}
