public class Playground {
	public static void main(String[] args) throws Exception {
//		String functionString = "asin(x)";
//		Expression e = new ExpressionBuilder(functionString).variable("x").build();
//
//		double result = e.setVariable("x", 0).evaluate();
//		System.out.println(result);

		double result;

		result = new Integrator().integrateASM(0, Math.PI, 100, "sin(x)");
		System.out.printf("" + result);

		result = new Integrator().integrateASM(0, Math.PI, 100, "sin(x)");
		System.out.printf("" + result);



	}
}
