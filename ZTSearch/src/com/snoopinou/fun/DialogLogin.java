package com.snoopinou.fun;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogLogin extends JDialog{

	JTextField user = new JTextField();
	JTextField pass = new JTextField();
	
	JLabel lab1 = new JLabel("Username : ");
	JLabel lab2 = new JLabel("Password : ");
	
	JPanel panJTF = new JPanel();
	
	JButton button = new JButton("OK");
	
	
	public DialogLogin() {
		super();
		
		this.setTitle("Login");
		this.setSize(500, 125);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setResizable(false);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		lab1.setFont(new Font("Arial", Font.PLAIN, 20));
		lab2.setFont(new Font("Arial", Font.PLAIN, 20));
		lab1.setPreferredSize(new Dimension(150,25));
		lab2.setPreferredSize(new Dimension(150,25));
		
		user.setPreferredSize(new Dimension(200,25));
		pass.setPreferredSize(new Dimension(200,25));
		user.setFont(new Font("Arial", Font.PLAIN, 20));
		pass.setFont(new Font("Arial", Font.PLAIN, 20));
		
		
		this.getContentPane().setLayout(new BorderLayout());
//		panJTF.setLayout(new GridLayout(2, 2));
		panJTF.add(lab1);
		panJTF.add(user);
		panJTF.add(lab2);
		panJTF.add(pass);
		
		this.getContentPane().add(panJTF, BorderLayout.CENTER);
		this.getContentPane().add(button, BorderLayout.SOUTH);
		
	}
	
	
	public String[] showDialog() {
		
		this.setVisible(true);
		
		String[] credentials = new String[2];
		
		credentials[0] = user.getText();
		credentials[1] = pass.getText();
		
		return credentials;
	}
}
