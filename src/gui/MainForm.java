package gui;

import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;
import implems.*;
import misc.IntegrationResult;
import plotter.Plotter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainForm extends JFrame {

	public static final int PLOT_WIDTH = 300;
	public static final int PLOT_HEIGHT = 100;

	private Logger logger = Logger.getLogger(MainForm.class.getName());

	class IntegrateButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			logger.log(Level.INFO, "User have clicked INTEGRATE button");

			double left, right;
			int numberOfPoints, numberOfThreads;
			String functionString;

			try {
				left = Double.parseDouble(leftField.getText().replaceAll(",", "."));
				right = Double.parseDouble(rightField.getText().replaceAll(",", "."));
				numberOfPoints = Integer.parseInt(pointsField.getText());
				numberOfThreads = Integer.parseInt(threadsField.getText());

				if (right < left) {
					logger.log(Level.WARNING, "User have set noncreasing range");
					resultLabel.setText("range must be increasing");
					return;
				}

				functionString = functionField.getText();
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING, "User have input something wrong");
				resultLabel.setText("input error");
				return;
			}

			logger.log(Level.INFO, "User input ok");

			try {
				IntegrationResult result;
				Integrator integrator;

				// may replace with sth more elegant
				if (useCradioButton.isSelected()) {
					integrator = new CIntegrator();
				} else if (useASM_FPUradioButton.isSelected()) {
					integrator = new AsmFPUIntegrator();
				} else if (useASM_SSEradioButton.isSelected()) {
					integrator = new AsmSSEIntegrator();
				} else {
					integrator = new JavaIntegrator();
				}

				logger.log(Level.INFO, "Starting calculation using " + integrator.getClass()
						+ "from " + left + " to " + right
						+ " points " + numberOfPoints);

				result = integrator.integrate(left, right, numberOfPoints, functionString, numberOfThreads);

				timeLabel.setText("" + result.timeNS / 1_000_000.0 + " ms");
				resultLabel.setText("S = " + result.result);
				graphLabel.setIcon(new ImageIcon(new Plotter(PLOT_WIDTH, PLOT_HEIGHT).plot(left, right, functionString)));

				logger.log(Level.INFO, "Result = " + result.result);
				pack(); // ??

			} catch (InvalidInputFunctionError ee) {
				logger.log(Level.WARNING, ee.getClass().toString());
				resultLabel.setText("function input error");
			} catch (IntegrationNumericError ee) {
				logger.log(Level.WARNING, ee.getClass().toString());
				resultLabel.setText("calculation error");
			} catch (PlatformLibraryNotFoundException ignored) {
			} // we check before!
		}
	}

	public static final String WINDOW_TITLE = "Integrator";

	private JTextField functionField;
	private JTextField leftField;
	private JTextField rightField;
	private JTextField pointsField;
	private JTextField threadsField;

	private JLabel resultLabel;
	private JLabel timeLabel;
	private JLabel graphLabel;

	private JButton calculateButton;

	private JRadioButton useCradioButton;
	private JRadioButton useASM_FPUradioButton;
	private JRadioButton useASM_SSEradioButton;
	private JRadioButton useJAVAradioButton;

	private ButtonGroup useGroup;

	private JPanel rootPanel;

	public MainForm() {
		createUI();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle(WINDOW_TITLE);

		if (!LibraryWrapper.isPlatformLibraryPresent()) {
			logger.log(Level.SEVERE, "Platform library not present! Exiting now.");
			JOptionPane.showMessageDialog(rootPanel, "Could not find platform library!");
			System.exit(1); // ??
		}

		calculateButton.addActionListener(new IntegrateButtonListener());
	}

	private void createUI() {

		// from google 'nimbus look and feel'
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ignored) {
			// metal is default
		}

		GridBagConstraints gbc = new GridBagConstraints();
		rootPanel = new JPanel();
		rootPanel.setLayout(new GridBagLayout());
		setContentPane(rootPanel);

		// STATIC LABELS

		JLabel label1 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label1.setText("Function");
		rootPanel.add(label1, gbc);

		JLabel label2 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label2.setText("From");
		rootPanel.add(label2, gbc);

		JLabel label3 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label3.setText("To");
		rootPanel.add(label3, gbc);

		JLabel label4 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label4.setText("Points");
		rootPanel.add(label4, gbc);

		JLabel label5 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label5.setText("Threads");
		rootPanel.add(label5, gbc);

		// TEXT FIELDS

		functionField = new JTextField();
		functionField.setColumns(20);
		functionField.setText("sin(x)");
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(functionField, gbc);

		leftField = new JTextField();
		leftField.setColumns(20);
		leftField.setText("0");
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(leftField, gbc);

		rightField = new JTextField();
		rightField.setColumns(20);
		rightField.setText("3.1415");
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(rightField, gbc);

		pointsField = new JTextField();
		pointsField.setColumns(20);
		pointsField.setText("1000000");
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(pointsField, gbc);

		threadsField = new JTextField();
		threadsField.setColumns(20);
		threadsField.setText("2");
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(threadsField, gbc);

		// BUTTONS

		calculateButton = new JButton();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		calculateButton.setText("Run!");
		rootPanel.add(calculateButton, gbc);

		// DYNAMIC LABELS

		resultLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		resultLabel.setText("waiting");
		rootPanel.add(resultLabel, gbc);

		timeLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		timeLabel.setText("time = ?");
		rootPanel.add(timeLabel, gbc);

		// RADIO's

		useASM_FPUradioButton = new JRadioButton("asm_FPU");
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useASM_FPUradioButton, gbc);

		useASM_SSEradioButton = new JRadioButton("asm_SSE");
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useASM_SSEradioButton, gbc);

		useCradioButton = new JRadioButton("C");
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useCradioButton, gbc);

		useJAVAradioButton = new JRadioButton("Java");
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useJAVAradioButton, gbc);

		useGroup = new ButtonGroup();
		useGroup.add(useCradioButton);
		useGroup.add(useASM_FPUradioButton);
		useGroup.add(useASM_SSEradioButton);
		useGroup.add(useJAVAradioButton);

		graphLabel = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 3;
		gbc.gridheight = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		rootPanel.add(graphLabel, gbc);

		useCradioButton.setSelected(true);

		///
		setVisible(true);
		pack();
	}

	public static void main(String[] args) {
		new MainForm();
	}
}
