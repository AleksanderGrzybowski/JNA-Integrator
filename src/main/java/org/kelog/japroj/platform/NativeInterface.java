package org.kelog.japroj.platform;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface NativeInterface extends Library {
    double integrateC(double a, double b, int n, Pointer values);
    
    double integrateASM_FPU(double a, double b, int n, Pointer values);
    
    double integrateASM_SSE(double a, double b, int n, Pointer values);
    
    int testASMLibrary();
}