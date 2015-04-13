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
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.UIManager;
import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.Color;

public class MainFrameUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3055287536615125669L;
	private JPanel contentPane;
	private JTextField queryTextField;
	private JList<String> resultList;
	private JLabel matchedVideosLabel;
	private JPanel resultVideoWrapper;
	private JPanel visualDescriptorGraphPanel;
	private JPanel queryVideoWrapper;
	private JPanel queryButtonsPanel;
	private JButton queryBtnPlay;
	private JButton queryBtnPause;
	private JButton queryBtnStop;
	private JPanel resultButtonPanel;
	private JButton resultBtnPlay;
	private JButton resultBtnPause;
	private JButton resultBtnStop;
	private JPanel queryVideoPanel;
	private JPanel resultVideoPanel;
	private JLabel queryImageBox;
	private JLabel resultImageBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					MainFrameUI frame = new MainFrameUI();
					frame.displayImages();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void displayImages() {
		byte[] bytes = ImageHandler.readImageFromFile("Image1.rgb");
		BufferedImage originalImg = ImageHandler.toBufferedImage(bytes, 352,
				288, BufferedImage.TYPE_INT_RGB);
		
		
		queryImageBox.setIcon(new ImageIcon(originalImg));
		resultImageBox.setIcon(new ImageIcon(originalImg));
		
		/* Right now for testing */
		VideoPlayer player = new VideoPlayer("Query", this, "database/flowers", 0);
		player.start();
	}
	
	/**
	 * External Interface to set the frame for queryImageBox
	 */
	public void setQueryImageBoxFrame(BufferedImage frame) {
		queryImageBox.setIcon(new ImageIcon(frame));
	}
	
	/**
	 * External Interface to set the frame for resultImageBox
	 */
	public void setResultImageBoxFrame(BufferedImage frame) {
		resultImageBox.setIcon(new ImageIcon(frame));
	}

	/**
	 * Create the frame.
	 */
	public MainFrameUI() {
		super("Media Queries");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 794, 814);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);
		
		queryTextField = new JTextField();
		queryTextField.setBounds(111, 191, 174, 32);
		mainPanel.add(queryTextField);
		queryTextField.setColumns(10);
		
		JLabel queryTextLabel = new JLabel("Query:");
		queryTextLabel.setBounds(111, 159, 62, 22);
		mainPanel.add(queryTextLabel);
		
		resultList = new JList<String>();
		resultList.setBounds(470, 75, 235, 110);
		String[] ResultListData = {"Mov 1 - 90%","Mov 31 - 87%","Mov 11 - 61%","Mov 12 - 14%"};
		resultList.setListData(ResultListData);
		mainPanel.add(resultList);
		
		matchedVideosLabel = new JLabel("Matched Videos");
		matchedVideosLabel.setBounds(470, 42, 96, 22);
		mainPanel.add(matchedVideosLabel);
		
		JSlider seekBar = new JSlider();
		seekBar.setBounds(401, 353, 352, 32);
		mainPanel.add(seekBar);
		
		resultVideoWrapper = new JPanel();
		resultVideoWrapper.setBounds(389, 396, 369, 358);
		mainPanel.add(resultVideoWrapper);
		resultVideoWrapper.setLayout(null);
		
		resultButtonPanel = new JPanel();
		resultButtonPanel.setBounds(10, 314, 352, 33);
		resultVideoWrapper.add(resultButtonPanel);
		resultButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		resultBtnPlay = new JButton("PLAY");
		resultButtonPanel.add(resultBtnPlay);
		
		resultBtnPause = new JButton("PAUSE");
		resultButtonPanel.add(resultBtnPause);
		
		resultBtnStop = new JButton("STOP");
		resultButtonPanel.add(resultBtnStop);
		
		resultVideoPanel = new JPanel();
		resultVideoPanel.setBounds(10, 11, 352, 292);
		resultVideoWrapper.add(resultVideoPanel);
		
		resultVideoPanel.setLayout(new BorderLayout(0, 0));
		resultImageBox = new JLabel();
		resultVideoPanel.add(resultImageBox, BorderLayout.NORTH);

		visualDescriptorGraphPanel = new JPanel();
		visualDescriptorGraphPanel.setBackground(Color.WHITE);
		visualDescriptorGraphPanel.setBounds(470, 219, 235, 100);
		mainPanel.add(visualDescriptorGraphPanel);
		
		queryVideoWrapper = new JPanel();
		queryVideoWrapper.setBounds(10, 396, 369, 358);
		mainPanel.add(queryVideoWrapper);
		queryVideoWrapper.setLayout(null);
		
		queryButtonsPanel = new JPanel();
		queryButtonsPanel.setBounds(10, 314, 352, 33);
		queryVideoWrapper.add(queryButtonsPanel);
		queryButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		queryBtnPlay = new JButton("PLAY");
		queryButtonsPanel.add(queryBtnPlay);
		
		queryBtnPause = new JButton("PAUSE");
		queryButtonsPanel.add(queryBtnPause);
		
		queryBtnStop = new JButton("STOP");
		queryButtonsPanel.add(queryBtnStop);
		
		queryVideoPanel = new JPanel();
		queryVideoPanel.setBounds(10, 11, 352, 292);
		queryVideoWrapper.add(queryVideoPanel);
		
		queryVideoPanel.setLayout(new BorderLayout(0, 0));
		queryImageBox = new JLabel();
		queryVideoPanel.add(queryImageBox, BorderLayout.CENTER);
	}
}
