package org.kelog.japroj.platform;

import com.sun.jna.Native;
import lombok.extern.java.Log;
import org.kelog.japroj.impl.Integrator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringJoiner;
import java.util.logging.Level;

@Configuration
@Log
public class NativeConfiguration {
    
    private static final int ASM_TEST_MAGIC_NUMBER = 1337;
    private static final String LIBRARY_DIRECTORY = "native";
    private static final String LIBRARY_NAME = "native";
    
    @Bean
    NativeInterface nativeInterface() {
        log.info("Trying to load native library...");
        log.info(" * java.library.path -> " + System.getProperty("java.library.path"));
        
        String currentDir = getCurrentDir();
        log.info(" * current directory -> " + currentDir);
        
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
    
        System.setProperty("jna.library.path", newJNApath);
        log.info(" * Setting up JNA: jna.library.path is now -> " + newJNApath);
        
        log.info(" * Trying to load platform dependent library...");
        
        NativeInterface library;
        try {
            library = (NativeInterface) Native.loadLibrary(LIBRARY_NAME, NativeInterface.class);
        } catch (LinkageError e) {
            log.log(Level.SEVERE, " * Error loading native library!");
            throw new PlatformLibraryNotFoundException(e);
        }
        
        log.info(" * Platform library loaded, testing...");
        if (library.testASMLibrary() == ASM_TEST_MAGIC_NUMBER)
            log.info(" * Test passed");
        else {
            log.log(Level.SEVERE, " * Test FAILED, this should never happen!");
            throw new RuntimeException("Test FAILED!");
        }
        
        log.info("****** Finished loading ******");
        
        return library;
    }
    
    private static String getCurrentDir() {
        String path = Integrator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            return new File(decodedPath).getParent();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
