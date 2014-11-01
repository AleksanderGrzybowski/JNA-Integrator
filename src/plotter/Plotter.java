package plotter;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Plotter extends JFrame {
	private int height;
	private int width;

	private static final double CEIL_FACTOR = 0.7;
	public static final Color FUNCTION_COLOR = Color.GREEN;
	public static final Color AXIS_COLOR = Color.red;

	public Plotter(int width, int height) {
		this.height = height;
		this.width = width;
	}

	public BufferedImage plot(double left, double right, String functionString) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = bi.createGraphics();

		// draw x axis
		g2d.setColor(AXIS_COLOR);
		g2d.drawLine(0, height/2, width, height/2);


		double t = right - left;
		double[] points = new double[width];
		Expression expression = new ExpressionBuilder(functionString).variable("x").build();

		// generate points
		for (int i = 0; i < width; ++i) {
			expression.setVariable("x", (t / (width / 3.0)) * i + left - t);
			points[i] = expression.evaluate();
		}

		// scale points, so the biggest value is at the CEIL_FACTOR of screen
		double maxMag = maxMag(points);
		for (int i = 0; i < width; ++i) {
			points[i] *= (0.5 * height * CEIL_FACTOR) / maxMag;
		}

		// debug print
		for (int i = 0; i < width; ++i) {
			System.out.println("" + i + " " + points[i]);
		}


		for (int i = 0; i < width; ++i) {
			points[i] *= -1; // BufferedImage's x axis points down
			points[i] += height / 2; // center the points relative to x axis
		}


		// draw function
		g2d.setColor(FUNCTION_COLOR);
		for (int i = 0; i < (width-1); ++i) {
			g2d.drawLine(i, (int) points[i], i + 1, (int) points[i + 1]);
		}

		// draw area from left to right
		for (int i = width/3; i < 2*width/3; ++i) {
			g2d.drawLine(i, height/2, i, (int)points[i]);
		}

		return bi;
	}

	private double maxMag(double[] arr) {
		if (arr == null || arr.length == 0)
			throw new IllegalArgumentException();

		double current = Math.abs(arr[0]);
		for (double element : arr)
			if (Math.abs(element) > current)
				current = Math.abs(element);
		return current;
	}
}
