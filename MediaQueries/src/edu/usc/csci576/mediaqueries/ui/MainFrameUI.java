package edu.usc.csci576.mediaqueries.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import edu.usc.csci576.mediaqueries.controller.*;
import edu.usc.csci576.mediaqueries.data.ImageHandler;
import edu.usc.csci576.mediaqueries.parallel.SCResultType;

import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.UIManager;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.ListSelectionModel;

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
	private JSlider seekBar;
	private MediaPlayer queryMediaPlayer;
	private MediaPlayer resultMediaPlayer;
	private JPanel queryRGBPanel;
	private JLabel queryRGBBox;
	private JLabel queryRGBTextLabel;
	private JLabel resultRGBBox;
	private Container resultRGBPanel;
	private JLabel resultRGBTextLabel;
	
	
	private Map<String, SCResultType> resultData = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
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
		byte[] bytes = ImageHandler.readImageFromFile("database/flowers/flowers061.rgb");
		byte[] qbytes = ImageHandler.readImageFromFile("query/Q5/Q5_001.rgb");
		
		BufferedImage originalImg = ImageHandler.toBufferedImage(bytes, 352,
				288, BufferedImage.TYPE_INT_RGB);
		BufferedImage qoriginalImg = ImageHandler.toBufferedImage(qbytes, 352,
				288, BufferedImage.TYPE_INT_RGB);
		
		queryImageBox.setIcon(new ImageIcon(qoriginalImg));
		resultImageBox.setIcon(new ImageIcon(originalImg));
		
		queryMediaPlayer = new MediaPlayer("Query", "query/Q4", "Q4_", queryImageBox);
		resultMediaPlayer = new MediaPlayer("Result", "database/interview", "interview", resultImageBox);
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrameUI() {
		super("Media Queries");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 00, 805, 755);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);
		
		JLabel queryTextLabel = new JLabel("Query:");
		queryTextLabel.setBounds(43, 11, 62, 22);
		mainPanel.add(queryTextLabel);
		
		queryTextField = new JTextField();
		queryTextField.setBounds(43, 45, 235, 32);
		mainPanel.add(queryTextField);
		queryTextField.setColumns(1);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(43, 100, 89, 23);
		mainPanel.add(btnSearch);
		
		final JLabel wheelImg = new JLabel();
		wheelImg.setBounds(150, 100, 40, 40);
		wheelImg.setIcon(new ImageIcon("ajax-loader.gif"));
		mainPanel.add(wheelImg);
		wheelImg.setVisible(false);
		
		final JLabel msgLabel = new JLabel();
		msgLabel.setBounds(44, 130, 400, 40);
		mainPanel.add(msgLabel);
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				msgLabel.setText("");
				String text = "<html>Usage (Enter ABCDVideo for video file name ABCDVideo001.rgb)<br>C:\\Videos\\ABCDVideo C:\\Videos\\ABCDAudio.wav</html>";
				String query = queryTextField.getText();
				
				String []queryElements = new String[2];
				if(query == null || query.length() == 0 || (queryElements = query.split(" ")).length != 2)
				{
					msgLabel.setText(text);
					return;
				}	
				
				Path queryVideoPath = Paths.get(queryElements[0]);
				Path queryAudioPath = Paths.get(queryElements[1]);
				
				String queryVideoPathStr = queryVideoPath.getParent() == null ? Paths.get("").toAbsolutePath().toString() : queryVideoPath.getParent().toString();
				String queryAudioPathStr = queryAudioPath.getParent() == null ? Paths.get("").toAbsolutePath().toString() : queryAudioPath.getParent().toString();
				String queryVideoNameStr = queryVideoPath.getFileName() == null ? null : queryVideoPath.getFileName().toString();
				String queryAudioNameStr = queryAudioPath.getFileName() == null ? null : queryAudioPath.getFileName().toString();
					
				if(queryVideoNameStr == null || queryVideoNameStr == null)
				{
					msgLabel.setText(text);
					return;
				}
				
				File vidFile = new File(queryElements[0] + "001.rgb");
				File audFile = new File(queryElements[1]);
				
				if(vidFile.exists() && !vidFile.isDirectory() && audFile.exists() && !audFile.isDirectory())
				{
					SwingWorker searchWorker = 
							new MediaSearchWorker(
							resultList, resultData, wheelImg, queryVideoPathStr,
							queryVideoNameStr, queryAudioPathStr,
							queryAudioNameStr);
					searchWorker.execute();
				}
				else
				{					
					msgLabel.setText(text);
					return;
				}

			}
		});
		
		
		
		resultList = new JList<String>();
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultList.setBounds(469, 44, 235, 110);
		//String[] ResultListData = {"Mov 1 - 90%","Mov 31 - 87%","Mov 11 - 61%","Mov 12 - 14%"};
		//resultList.setListData(ResultListData);
		mainPanel.add(resultList);
		
		matchedVideosLabel = new JLabel("Matched Videos");
		matchedVideosLabel.setBounds(469, 11, 96, 22);
		mainPanel.add(matchedVideosLabel);
		
		seekBar = new JSlider(0, 20, 0);
		final Timer increaseValue = new Timer(50, new ActionListener() {// 50 ms interval in each increase.
	        public void actionPerformed(ActionEvent e) {
	            if (seekBar.getMaximum() != seekBar.getValue()) {
	            	if(resultMediaPlayer.getVideoPlayer().getCurrentFrame() % 30 == 0)
	            	{
	            		seekBar.setValue(resultMediaPlayer.getVideoPlayer().getCurrentFrame()/30);
	            	}
	            	
	            } else {
	                ((Timer) e.getSource()).stop();
	                resultBtnPlay.setEnabled(true);
	          		 resultBtnPause.setEnabled(false);
	          		 resultBtnStop.setEnabled(false);
	          		 seekBar.setValue(0);
	            }
	        }
	    });
		seekBar.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				increaseValue.stop();
				int scrubIndex = seekBar.getValue();
				resultMediaPlayer.setFrameAtIndex(scrubIndex);
				increaseValue.start();
				
			}

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}			
		});
		
		seekBar.setBounds(401, 299, 352, 32);
		
		mainPanel.add(seekBar);
		
		resultVideoWrapper = new JPanel();
		resultVideoWrapper.setBounds(389, 342, 369, 358);
		mainPanel.add(resultVideoWrapper);
		resultVideoWrapper.setLayout(null);
		
		resultButtonPanel = new JPanel();
		resultButtonPanel.setBounds(10, 314, 352, 33);
		resultVideoWrapper.add(resultButtonPanel);
		resultButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		resultBtnPlay = new JButton("PLAY");
		resultBtnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultMediaPlayer.playMedia();
				increaseValue.start();
				resultBtnPlay.setEnabled(false);
				resultBtnPause.setEnabled(true);
				resultBtnStop.setEnabled(true);
			}
		});
		resultButtonPanel.add(resultBtnPlay);
		
		resultBtnPause = new JButton("PAUSE");
		resultBtnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultMediaPlayer.pauseMedia();
				increaseValue.stop();
				resultBtnPlay.setEnabled(true);
				resultBtnPause.setEnabled(false);
			}
		});
		resultButtonPanel.add(resultBtnPause);
		
		resultBtnStop = new JButton("STOP");
		resultBtnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultMediaPlayer.stopMedia();
				seekBar.setValue(0);
				resultBtnPlay.setEnabled(true);
				resultBtnPause.setEnabled(false);
				resultBtnStop.setEnabled(false);
			}
		});
		resultButtonPanel.add(resultBtnStop);
		
		resultBtnPlay.setEnabled(true);
		resultBtnPause.setEnabled(false);
		resultBtnStop.setEnabled(false);
		
		resultVideoPanel = new JPanel();
		resultVideoPanel.setBounds(10, 11, 352, 292);
		resultVideoWrapper.add(resultVideoPanel);
		
		resultVideoPanel.setLayout(new BorderLayout(0, 0));
		resultImageBox = new JLabel();
		resultVideoPanel.add(resultImageBox, BorderLayout.NORTH);
		
		queryVideoWrapper = new JPanel();
		queryVideoWrapper.setBounds(10, 342, 369, 358);
		mainPanel.add(queryVideoWrapper);
		queryVideoWrapper.setLayout(null);
		
		queryButtonsPanel = new JPanel();
		queryButtonsPanel.setBounds(10, 314, 352, 33);
		queryVideoWrapper.add(queryButtonsPanel);
		queryButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		queryBtnPlay = new JButton("PLAY");
		queryBtnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryMediaPlayer.playMedia();
			}
			
		});
		queryButtonsPanel.add(queryBtnPlay);
		
		queryBtnPause = new JButton("PAUSE");
		queryButtonsPanel.add(queryBtnPause);
		queryBtnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryMediaPlayer.pauseMedia();
			}
		});
		
		queryBtnStop = new JButton("STOP");
		queryButtonsPanel.add(queryBtnStop);
		queryBtnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryMediaPlayer.stopMedia();
			}
		});

		queryVideoPanel = new JPanel();
		queryVideoPanel.setBounds(10, 11, 352, 292);
		queryVideoWrapper.add(queryVideoPanel);
		
		queryVideoPanel.setLayout(new BorderLayout(0, 0));
		queryImageBox = new JLabel();
		queryVideoPanel.add(queryImageBox, BorderLayout.CENTER);
		
		queryRGBPanel = new JPanel();
		queryRGBPanel.setBounds(43, 181, 300, 100);
		
		queryRGBBox = new JLabel();
		queryRGBPanel.add(queryRGBBox, BorderLayout.CENTER);		
		queryRGBBox.setIcon(new ImageIcon(HistogramDisplay.getImage("database/musicvideo/musicvideo200.rgb", 352, 288)));
		queryRGBTextLabel = new JLabel("Query RGB Histogram");
		queryRGBTextLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		queryRGBPanel.add(queryRGBTextLabel);
		mainPanel.add(queryRGBPanel);

		resultRGBPanel = new JPanel();
		resultRGBPanel.setBounds(453, 181, 300, 100);		
		resultRGBBox = new JLabel();
		resultRGBPanel.add(resultRGBBox, BorderLayout.CENTER);		
		resultRGBBox.setIcon(new ImageIcon(HistogramDisplay.getImage("database/musicvideo/musicvideo201.rgb", 352, 288)));
		resultRGBTextLabel = new JLabel("Result RGB Histogram");
		resultRGBTextLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		resultRGBPanel.add(resultRGBTextLabel);
		mainPanel.add(resultRGBPanel);
		
//		JList queryList = new JList();
//		queryList.setBounds(43, 44, 235, 100);
//		mainPanel.add(queryList);
		

		
		
		
	}
}
