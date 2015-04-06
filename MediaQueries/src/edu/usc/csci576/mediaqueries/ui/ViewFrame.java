package edu.usc.csci576.mediaqueries.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewFrame extends JFrame{
	private JPanel panel;
	
	public ViewFrame(String title) {
		super(title);
		panel = new JPanel();
		this.getContentPane().add(panel);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setLocation(dim.width / 2 - this.getSize().width / 2,
				dim.height / 2 - this.getSize().height / 2);
				
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}