package org.kelog.japroj.impl;

import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import org.kelog.japroj.platform.NativeAlgorithmProxy;
import org.kelog.japroj.platform.NativeInterface;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsmFPUIntegrator extends NativeAlgorithmProxy {

    private final NativeInterface library;

    public double callAlgorithm(double left, double right, int numberOfPoints, Pointer values) {
        return library.integrateASM_FPU(left, right, numberOfPoints, values);
    }
}
