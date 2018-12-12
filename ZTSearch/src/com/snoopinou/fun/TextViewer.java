package com.snoopinou.fun;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextViewer extends JFrame{
	
	String text = "";
	
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	
	public TextViewer() {
		this.setTitle("TextViewer");
		this.setSize(750, 600);
		this.setLocationRelativeTo(null);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		this.getContentPane().add(scrollPane);
	}
	
	public TextViewer(String str) {
		this.setTitle("TextViewer");
		this.setSize(600, 600);
		this.setLocationRelativeTo(null);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		this.setText(str);
		
		this.getContentPane().add(scrollPane);
	}
	
	private void refresh() {
		textArea.setText(text);
		textArea.setCaretPosition(textArea.getText().length()-1);
	}
	
	public void removeText() {
		text = "";
		
		refresh();
	}
	
	public void setText(String str) {
		text = str;
		
		refresh();
	}
	
	public void append(String str) {
		text += "\n"+str;
		
		refresh();
	}
}
