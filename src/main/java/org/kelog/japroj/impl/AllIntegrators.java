package org.kelog.japroj.impl;

import com.google.inject.Inject;
import org.kelog.japroj.core.Integrator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class AllIntegrators {

    public final Collection<Integrator> integrators;

    @Inject
    public AllIntegrators(AsmFPUIntegrator fpu, AsmSSEIntegrator sse, CIntegrator c, JavaIntegrator java) {
        integrators = Collections.unmodifiableList(Arrays.asList(fpu, sse, c, java));
    }
}
