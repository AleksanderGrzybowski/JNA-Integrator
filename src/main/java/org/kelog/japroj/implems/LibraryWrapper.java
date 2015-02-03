package org.kelog.japroj.implems;

import com.sun.jna.Native;
import org.kelog.japroj.exceptions.PlatformLibraryNotFoundException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryWrapper {

	public static final int ASM_TEST_MAGIC_NUMBER = 1337;
	public static final String LIBRARY_DIRECTORY = "native";
	public static final String LIBRARY_NAME = "native";
	private static NativeInterface library;

	private static Logger logger = Logger.getLogger(LibraryWrapper.class.getName());

	public static NativeInterface getLibrary() throws PlatformLibraryNotFoundException {
		if (library == null)
			initLibrary();
		return library;
	}

	private static void initLibrary() throws PlatformLibraryNotFoundException {
		logger.log(Level.INFO, "org.kelog.japroj.implems.LibraryWrapper.getInstance() trying to load");
		logger.log(Level.INFO, " * java.library.path -> " + System.getProperty("java.library.path"));

		String currentDir = getCurrentDir();
		logger.log(Level.INFO, " * current directory -> " + currentDir);


		// There are lots of problems when dealing with paths to JNA libraries.
		// This should do the trick.
		String pathWhenRunningFromGradle = currentDir + "/" + LIBRARY_DIRECTORY;
		String pathWhenRunningFromJar = currentDir;
		String pathWhenTesting = currentDir + "/../../" + LIBRARY_DIRECTORY;
		String newJNApath = new StringJoiner(":")
				.add(pathWhenRunningFromGradle)
				.add(pathWhenRunningFromJar)
				.add(pathWhenTesting)
				.toString();

		logger.log(Level.INFO, " * Setting up JNA: jna.library.path is now -> " + newJNApath);
		System.setProperty("jna.library.path", newJNApath);

		logger.log(Level.INFO, " * Trying to load platform dependent library...");
		try {
			library = (NativeInterface) Native.loadLibrary(LIBRARY_NAME, NativeInterface.class);
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
