package plotter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kelog on 01.11.14.
 */
public class PlotterDemo extends JFrame {

	JPanel rootPanel = new JPanel();

	public PlotterDemo(double left, double right, String functionString) {
		rootPanel.setLayout(new BorderLayout());
		setContentPane(rootPanel);


		Plotter plotter = new Plotter(600, 600);
		rootPanel.add(new JLabel(new ImageIcon(plotter.plot(left, right, functionString))));

		setVisible(true);
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		double left = 10;
		double right = 11;
		String functionString = "2*(1 - 2^x)";
		new PlotterDemo(left, right, functionString);
	}
}
