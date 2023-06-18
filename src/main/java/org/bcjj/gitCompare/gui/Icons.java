package org.bcjj.gitCompare.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JScrollBar;
import java.awt.Dimension;
import javax.swing.JSlider;

public class Icons extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Icons frame = new Icons();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Icons() {
		setTitle("show icons");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("dir:");
		panel.add(lblNewLabel, BorderLayout.WEST);
		

		textField = new JTextField();
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.EAST);
		
		JButton btnNewButton = new JButton("list");
		panel_1.add(btnNewButton, BorderLayout.SOUTH);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("subdir");
		panel_1.add(chckbxNewCheckBox);
		
		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(200, 20));
		panel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(null);
		
		JSlider slider = new JSlider();
		slider.setBounds(0, 0, 200, 20);
		panel_2.add(slider);
		
	}
}
