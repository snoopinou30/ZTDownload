package com.snoopinou.fun;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class DialogDebug extends JDialog{
	
	JLabel label = new JLabel("Password :");
	JPasswordField jtf = new JPasswordField();
	JButton button = new JButton ("OK");
	
	JPanel pan = new JPanel();
	
	public DialogDebug() {
		this.setTitle("Debug Login");
		this.setSize(500,125);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setResizable(false);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		jtf.addActionListener(new JTFListener());
		
		label.setPreferredSize(new Dimension(100,50));
		jtf.setPreferredSize(new Dimension(350,50));
		
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		jtf.setFont(new Font("Arial", Font.PLAIN, 20));
		
		pan.add(label);
		pan.add(jtf);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		this.getContentPane().add(pan, BorderLayout.CENTER);
		this.getContentPane().add(button, BorderLayout.SOUTH);
		
	}
	
	public String showDialog() {
		this.setVisible(true);
		
		return jtf.getText();
	}
	
	
	public class JTFListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			button.doClick();
		}
	}
	
}
