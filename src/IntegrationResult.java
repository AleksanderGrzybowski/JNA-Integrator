/**
 * Created by kelog on 24.10.14.
 */
public class IntegrationResult {
	public final double result;
	public final long timeNS;


	public IntegrationResult(double result, long timeNS) {
		this.result = result;
		this.timeNS = timeNS;
	}
}