/**
 * Created by kelog on 23.10.14.
 */
public class TestAdapter {
	public enum AlgorithmType {C, ASM};

	private Integrator integrator;
	private AlgorithmType algorithmType;
	public TestAdapter(Integrator in, AlgorithmType al) {
		integrator = in;
		algorithmType = al;
	}

	public double integrate(double left, double right, int numberOfPoints, String functionString) throws
			IntegrationNumericError, InvalidInputFunctionError {

		if (algorithmType == AlgorithmType.ASM) {
			return integrator.integrateASM(left, right, numberOfPoints, functionString);
		} else if (algorithmType == AlgorithmType.C) {
			return integrator.integrateC(left, right, numberOfPoints, functionString);
		}
		throw new RuntimeException("Should never happen");
	}


}
