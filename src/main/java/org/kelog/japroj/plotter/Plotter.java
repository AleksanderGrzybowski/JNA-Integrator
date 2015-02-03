package org.kelog.japroj.plotter;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Hand-crafted shit, doesn't like much refactoring. Leaving for now.
 */
public class Plotter {

	private static final double MAXIMUM_GRAPH_HEIGHT = 0.7;

	private static final Color FUNCTION_COLOR = Color.RED;
	private static final Color AXIS_COLOR = Color.BLACK;
	private static final Color BACKGROUND_COLOR = Color.WHITE;

	public static BufferedImage plot(int width, int height, double left, double right, String functionString) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = image.createGraphics();

		// fill background
		g2d.setColor(BACKGROUND_COLOR);
		g2d.fillRect(0, 0, width, height);

		// draw x axis at the center of the image
		g2d.setColor(AXIS_COLOR);
		g2d.drawLine(0, height / 2, width, height / 2);

		// initialize
		double[] points = new double[width];
		Expression expression = new ExpressionBuilder(functionString).variable("x").build();

		// generate points
		double t = right - left;
		for (int i = 0; i < width; ++i) {
			expression.setVariable("x", (t / (width / 3.0)) * i + left - t);
			points[i] = expression.evaluate();
		}

		// scale points, so the biggest value is at the MAXIMUM_GRAPH_HEIGHT of screen
		double maxMag = maxMagnitude(points);
		for (int i = 0; i < width; ++i) {
			points[i] *= (0.5 * height * MAXIMUM_GRAPH_HEIGHT) / maxMag;
		}

		for (int i = 0; i < width; ++i) {
			points[i] *= -1; // BufferedImage's x axis points down
			points[i] += height / 2; // center the points relative to x axis
		}

		// draw function
		// we need drawLine even for pixels because otherwise we run
		// into issues with broken lines
		g2d.setColor(FUNCTION_COLOR);
		for (int i = 0; i < (width - 1); ++i) {
			g2d.drawLine(i, (int) points[i], i + 1, (int) points[i + 1]);
		}

		// draw area from 'left' to 'right'
		for (int i = width / 3; i < 2 * width / 3; ++i) {
			g2d.drawLine(i, height / 2 + ((points[i] - height / 2) * (-1) > 0 ? -1 : 1), i, (int) points[i]);
		}

		return image;
	}

	private static double maxMagnitude(double[] arr) {
		double currentMax = Math.abs(arr[0]);

		for (double element : arr)
			if (Math.abs(element) > currentMax)
				currentMax = Math.abs(element);

		return currentMax;
	}
}
