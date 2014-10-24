import exceptions.IntegrationNumericError;
import exceptions.InvalidInputFunctionError;
import exceptions.PlatformLibraryNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyForm extends JFrame {
	private JTextField functionField;
	private JTextField leftField;
	private JTextField rightField;
	private JTextField pointsField;

	private JLabel resultLabel;
	private JLabel timeLabel;

	private JButton calculateButton;

	private JRadioButton useCradioButton;
	private JRadioButton useASMradioButton;
	private ButtonGroup useGroup;

	private JPanel rootPanel;

	public MyForm() {
		createUI();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("JNA-Integrator");

		if (!Integrator.isPlatformLibraryPresent()) {
			JOptionPane.showMessageDialog(rootPanel, "Could not find platform library!");
			System.exit(1); // ??
		}

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double left, right;
				int points;
				String func;
				try {
					leftField.setText(leftField.getText().replaceAll(",", ".")); // fix later TODO
					rightField.setText(rightField.getText().replaceAll(",", "."));

					left = Double.parseDouble(leftField.getText());
					right = Double.parseDouble(rightField.getText());
					points = Integer.parseInt(pointsField.getText());

					if (right < left) {
						resultLabel.setText("przedział nierosnący");
						return;
					}

					func = functionField.getText();
				} catch (NumberFormatException ee) {
					resultLabel.setText("błąd w wejściu");
					return;
				}

				try {
					IntegrationResult result;
					Integrator integrator;

					if (useCradioButton.isSelected()) {
						integrator = new CIntegrator();
					} else {
						integrator = new AsmFPUIntegrator();
					}

					result = integrator.integrate(left, right, points, func);
					timeLabel.setText("" + result.timeNS/10000000.0 + " ms");
					System.out.println("Using " + integrator.getClass() + ", result = " + result.result);
					resultLabel.setText("S = " + result.result);

				} catch (InvalidInputFunctionError ee) {
					resultLabel.setText("błąd w funkcji");
				} catch (IntegrationNumericError ee) {
					resultLabel.setText("błąd w liczeniu");
				} catch (PlatformLibraryNotFoundException ee) {} // we check before!
			}
		};
		calculateButton.addActionListener(listener);
	}

	private void createUI() {

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
		label1.setText("Funkcja");
		rootPanel.add(label1, gbc);

		JLabel label2 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label2.setText("Od");
		rootPanel.add(label2, gbc);

		JLabel label3 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label3.setText("Do");
		rootPanel.add(label3, gbc);

		JLabel label4 = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		label4.setText("Punkty");
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
		calculateButton.setText("Licz!");
		rootPanel.add(calculateButton, gbc);

		// DYNAMIC LABELS

		resultLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		resultLabel.setText("czekam");
		rootPanel.add(resultLabel, gbc);

		timeLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		timeLabel.setText("czas = ?");
		rootPanel.add(timeLabel, gbc);

		// RADIO's

		useASMradioButton = new JRadioButton("asm");
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useASMradioButton, gbc);

		useCradioButton = new JRadioButton("C");
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		rootPanel.add(useCradioButton, gbc);

		useGroup = new ButtonGroup();
		useGroup.add(useCradioButton);
		useGroup.add(useASMradioButton);


		useCradioButton.setSelected(true);

		///
		setVisible(true);
		pack();

	}

	public static void main(String[] args) {
		new MyForm();
	}
}
