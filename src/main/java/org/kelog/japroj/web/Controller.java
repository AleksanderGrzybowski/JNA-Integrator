package org.kelog.japroj.web;

import com.google.common.collect.Range;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.kelog.japroj.core.Integrator;
import org.kelog.japroj.di.MainModule;
import org.kelog.japroj.impl.AsmSSEIntegrator;
import spark.Spark;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.halt;

public class Controller {

	private Integrator integrator;
	
	@Inject
	public Controller(AsmSSEIntegrator integrator) {
		this.integrator = integrator;
	}
	
	public void init() {

		Spark.externalStaticFileLocation(System.getProperty("user.dir") + File.separator + "web-app");

		get("/calculate", (req, res) -> {
			res.type("application/json"); // needed, otherwise string is returned as body
			
			Range<Double> range;
			String function;
			int numberOfPoints;
			
			try {
				range = Range.closed(
						Double.parseDouble(req.queryParams("left")),
						Double.parseDouble(req.queryParams("right"))
				);
				function = req.queryParams("function");
				numberOfPoints = Integer.parseInt(req.queryParams("numberOfPoints"));
			} catch (NumberFormatException e) {
				halt(400);
				return "";
			}
			
			try {
				return integrator.integrate(range, numberOfPoints, function, 3);
			} catch (Exception e) {
				halt(400);
				return "";
			}
			
		}, new JsonTransformer());
	}

	public static void main(String[] args) {
		Guice.createInjector(new MainModule()).getInstance(Controller.class).init();
	}
}
