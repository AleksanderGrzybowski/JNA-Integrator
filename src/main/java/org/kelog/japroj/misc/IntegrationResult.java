package org.kelog.japroj.misc;

import java.util.Collection;

public class IntegrationResult {
	public final double result;
	public final long timeNS;

	public IntegrationResult(double result, long timeNS) {
		this.result = result;
		this.timeNS = timeNS;
	}

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
