package com.snoopinou.fun;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class URLViewer extends JFrame{
	
	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	
	public URLViewer() {
		
		this.setTitle("ZTSearch");
		this.setSize(800,800);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		editorPane = new JEditorPane("text/html", null);
		scrollPane = new JScrollPane(editorPane);
		
		this.add(scrollPane);
		
		this.setVisible(true);
	}
	
	
	public void setPaneType(String type) {
		editorPane.setContentType(type);
	}
	
	
	public void setText(String text) {
		editorPane.setText(text);
	}

}
