package org.kelog.japroj.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sun.jna.Native;
import org.kelog.japroj.core.Integrator;
import org.kelog.japroj.core.NativeInterface;
import org.kelog.japroj.exceptions.PlatformLibraryNotFoundException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        // Every type is concrete, so no specific config
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static final int ASM_TEST_MAGIC_NUMBER = 1337;
    public static final String LIBRARY_DIRECTORY = "native";
    public static final String LIBRARY_NAME = "native";

    private static Logger logger = Logger.getLogger(MainModule.class.getName());

    @Provides
    @Singleton
    private NativeInterface loadNativeLibrary() {
        logger.log(Level.INFO, "org.kelog.japroj.core.LibraryWrapper.getInstance() trying to load");
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

        NativeInterface library;
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

        return library;
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
}
