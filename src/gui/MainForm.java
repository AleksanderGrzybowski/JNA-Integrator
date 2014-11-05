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


public class MainForm extends JFrame {

	class IntegrateButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			double left, right;
			int points;
			String func;

			try {
				left = Double.parseDouble(leftField.getText().replaceAll(",", "."));
				right = Double.parseDouble(rightField.getText().replaceAll(",", "."));
				points = Integer.parseInt(pointsField.getText());

				if (right < left) {
					resultLabel.setText("range must be increasing");
					return;
				}

				func = functionField.getText();
			} catch (NumberFormatException ee) {
				resultLabel.setText("input error");
				return;
			}

			try {
				IntegrationResult result;
				Integrator integrator;

				// may replace with sth more elegant, but not worth it
				if (useCradioButton.isSelected()) {
					integrator = new CIntegrator();
				} else if (useASM_FPUradioButton.isSelected()) {
					integrator = new AsmFPUIntegrator();
				} else if (useASM_SSEradioButton.isSelected()) {
					integrator = new AsmSSEIntegrator();
				} else {
					integrator = new JavaIntegrator();
				}

				result = integrator.integrate(left, right, points, func);
				timeLabel.setText("" + result.timeNS / 10000000.0 + " ms");
				System.out.println("Using " + integrator.getClass() + ", result = " + result.result);
				resultLabel.setText("S = " + result.result);
				graph.setIcon(new ImageIcon(new Plotter(300, 100).plot(left, right, func)));
				pack(); // ??

			} catch (InvalidInputFunctionError ee) {
				resultLabel.setText("function input error");
			} catch (IntegrationNumericError ee) {
				resultLabel.setText("calculation error");
			} catch (PlatformLibraryNotFoundException ee) {
			} // we check before!
		}
	}

	;

	public static final String WINDOW_TITLE = "JNA-implems.Integrator";

	private JTextField functionField;
	private JTextField leftField;
	private JTextField rightField;
	private JTextField pointsField;

	private JLabel resultLabel;
	private JLabel timeLabel;

	private JButton calculateButton;

	private JRadioButton useCradioButton;
	private JRadioButton useASM_FPUradioButton;
	private JRadioButton useASM_SSEradioButton;
	private JRadioButton useJAVAradioButton;

	private JLabel graph;

	private ButtonGroup useGroup;

	private JPanel rootPanel;


	public MainForm() {
		createUI();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle(WINDOW_TITLE);

		if (!Integrator.isPlatformLibraryPresent()) {
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

		// BUTTONS

		calculateButton = new JButton();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		calculateButton.setText("Run!");
		rootPanel.add(calculateButton, gbc);

		// DYNAMIC LABELS

		resultLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		resultLabel.setText("waiting");
		rootPanel.add(resultLabel, gbc);

		timeLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		timeLabel.setText("time = ?");
		rootPanel.add(timeLabel, gbc);

		// RADIO's

		useASM_FPUradioButton = new JRadioButton("asm_FPU");
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useASM_FPUradioButton, gbc);

		useASM_SSEradioButton = new JRadioButton("asm_SSE");
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useASM_SSEradioButton, gbc);

		useCradioButton = new JRadioButton("C");
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useCradioButton, gbc);

		useJAVAradioButton = new JRadioButton("Java");
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useJAVAradioButton, gbc);

		useGroup = new ButtonGroup();
		useGroup.add(useCradioButton);
		useGroup.add(useASM_FPUradioButton);
		useGroup.add(useASM_SSEradioButton);
		useGroup.add(useJAVAradioButton);

		graph = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 3;
		gbc.gridheight = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		rootPanel.add(graph, gbc);


		useCradioButton.setSelected(true);

		///
		setVisible(true);
		pack();

	}

	public static void main(String[] args) {
		new MainForm();
	}
}
