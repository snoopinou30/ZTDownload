package com.snoopinou.fun;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DebugViewer extends JFrame{
	
	public final static int LIGHT = 1;
	public final static int HEAVY = 2;
	
	
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	
	JMenuBar menubar = new JMenuBar();
	
	JRadioButton rad1 = new JRadioButton("Light mode");
	JRadioButton rad2 = new JRadioButton("Heavy mode");
	
	ButtonGroup bg = new ButtonGroup();

	
	
	
	public DebugViewer() {
		this.setTitle("Debug");
		this.setSize(600, 600);
		this.setLocationRelativeTo(null);
		
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setAutoscrolls(true);
		
		bg.add(rad1);
		bg.add(rad2);
		
		menubar.add(rad1);
		menubar.add(rad2);
		
		rad1.setSelected(true);
		
		this.setJMenuBar(menubar);
		
		this.getContentPane().add(scrollPane);
	}
	
	public void println(Object obj, int type) {
		
		// Si message HEAVY et en mode leger est faux
		// Autrement dit on affiche tout le temps sauf si le mode est leger et message lourd
		if(!(rad1.isSelected() && type == HEAVY)) {
			textArea.append(obj+"\n"+"\n");
			textArea.setCaretPosition(textArea.getText().length()-1);
		}
	}
}
