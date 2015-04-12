package edu.usc.csci576.mediaqueries.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.usc.csci576.mediaqueries.controller.*;
import javax.swing.JSplitPane;

public class MainFrameUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3055287536615125669L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrameUI frame = new MainFrameUI();
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
	public MainFrameUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		byte[] bytes = ImageHandler.readImageFromFile("Image1.rgb");
		BufferedImage originalImg = ImageHandler.toBufferedImage(bytes, 352,
				288, BufferedImage.TYPE_INT_RGB);
		JLabel imageBox = new JLabel(new ImageIcon(originalImg));
		imageBox.setBounds(200, 200, 352, 288);
		contentPane.add(imageBox);
	}

}
