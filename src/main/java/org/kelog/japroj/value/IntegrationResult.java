package org.kelog.japroj.value;

import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class IntegrationResult {
    
    public final double result;
    public final long timeNS;
    
    public static IntegrationResult sumOf(Collection<IntegrationResult> results) {
        double result = 0.0;
        long time = 0;
        
        for (IntegrationResult ir : results) {
            result += ir.result;
            time += ir.timeNS;
        }
        
        return new IntegrationResult(result, time);
    }
}
