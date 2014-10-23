import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyForm extends JFrame {
	private JTextField functionField;
	private JTextField leftField;
	private JTextField rightField;

	private JLabel resultLabel;
	private JButton calculateButton;
	private JPanel rootPanel;

	private Integrator integrator;

	public MyForm() {
		createUI();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		try {
			integrator = new Integrator();
		} catch (PlatformLibraryNotFoundException e) {
			JOptionPane.showMessageDialog(rootPanel, "Could not find platform library!");
			System.exit(1); // ??
		}

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double left, right;
				String func;
				try {
					// TODO przecinek nie działa
					left = Double.parseDouble(leftField.getText());
					right = Double.parseDouble(rightField.getText());
					if ((right < left)) {
						resultLabel.setText("przedział nierosnący");
						return;
					}
					func = functionField.getText();
				} catch (NumberFormatException ee) {
					resultLabel.setText("błąd w wejściu");
					return;
				}

				try {
					double result = integrator.integrateC(left, right, 1000, func);
					resultLabel.setText("" + result);
				} catch (InvalidInputFunctionError ee) {
					resultLabel.setText("błąd w funkcji");
				} catch (IntegrationNumericError ee) {
					resultLabel.setText("błąd w liczeniu");
				}
			}
		};
		calculateButton.addActionListener(listener);
	}

	private void createUI() {

		GridBagConstraints gbc = new GridBagConstraints();
		rootPanel = new JPanel();
		rootPanel.setLayout(new GridBagLayout());
		setContentPane(rootPanel);




		// pion, poziom <=> x, y

		// LABELS
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

		// TEXT FIELDS
		functionField = new JTextField();
		functionField.setColumns(20);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(functionField, gbc);

		leftField = new JTextField();
		leftField.setColumns(20);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(leftField, gbc);

		rightField = new JTextField();
		rightField.setColumns(20);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		rootPanel.add(rightField, gbc);


		calculateButton = new JButton();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		calculateButton.setText("Licz!");
		rootPanel.add(calculateButton, gbc);


		resultLabel = new JLabel();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		resultLabel.setText("czekam");
		rootPanel.add(resultLabel, gbc);



		///
		setVisible(true);
		pack();

	}


	public static void main(String[] args) {
		new MyForm();
	}


}
