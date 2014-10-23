public class Playground {
	public static void main(String[] args) throws Exception {
//		String functionString = "asin(x)";
//		Expression e = new ExpressionBuilder(functionString).variable("x").build();
//
//		double result = e.setVariable("x", 0).evaluate();
//		System.out.println(result);

		double result = new Integrator().integrateASM(-10, 10, 1000, "1");
		System.out.printf("%.3f", result);



	}
}
