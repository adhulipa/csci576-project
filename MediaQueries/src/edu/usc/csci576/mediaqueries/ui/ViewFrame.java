package edu.usc.csci576.mediaqueries.ui;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ViewFrame extends JFrame{
	private JPanel panel;
	
	public ViewFrame(String title) {
		super(title);
		panel = new JPanel();
		this.getContentPane().add(panel);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setLocation(100, 100);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	public void addImage(Component img) {
		panel.add(img);
		this.pack();
		this.repaint();
	}

	public void addImage(Image img) {

		Component c = new JLabel(new ImageIcon(img));
		addImage(c);
	}

}
