package misc;

import java.util.Collection;

public class IntegrationResult {
	public final double result;
	public final long timeNS;

	public IntegrationResult(double result, long timeNS) {
		this.result = result;
		this.timeNS = timeNS;
	}

	public static IntegrationResult combine(Collection<IntegrationResult> results) {
		double res = 0.0;
		long tim = 0;

		for (IntegrationResult ir : results) {
			res += ir.result;
			tim += ir.timeNS;
		}
		return new IntegrationResult(res, tim);
	}
}
