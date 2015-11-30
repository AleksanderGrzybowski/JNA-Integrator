package org.kelog.japroj.impl;

import com.google.inject.Inject;
import com.sun.jna.Pointer;
import org.kelog.japroj.core.NativeInterface;

public class AsmSSEIntegrator extends NativeAlgorithmProxy {

    private final NativeInterface library;

    @Inject
    public AsmSSEIntegrator(NativeInterface library) {
        this.library = library;
    }

    public double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
        return library.integrateASM_SSE(left, right, numberOfPoints, values);
    }
}
