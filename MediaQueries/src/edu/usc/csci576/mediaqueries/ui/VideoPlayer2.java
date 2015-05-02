package edu.usc.csci576.mediaqueries.ui;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import edu.usc.csci576.mediaqueries.model.MyVideo;

public class VideoPlayer2 implements Runnable
{
	private MyVideo video;
	private Thread videoPlayer;
	private String threadName;
	private JLabel imageBox;
	private int currentFrame;
	
	public VideoPlayer2(String threadName, String filepath, String fileName, int totalFrames, JLabel imgBox)
	{		
		this.threadName = threadName;
		this.imageBox = imgBox;
		video = new MyVideo(filepath, fileName, totalFrames);
		this.setCurrentFrame(1);
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while(videoPlayer == thisThread) {
			setCurrentFrameToImageBox();	
			setCurrentFrame(getCurrentFrame() + 1);
			try
			{
				Thread.sleep(36);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void setCurrentFrameToImageBox()
	{		
		imageBox.setIcon(video.getImage(getCurrentFrame()));		
	}
	
	public void playVideo() {
		playFromFrame(getCurrentFrame());
	}
	
	public void pauseVideo() {
		videoPlayer = null;
	}
	
	public void stopVideo() {
		videoPlayer = null;
		setCurrentFrame(1);
		/* also set current frame in UI to 1 */
		setCurrentFrameToImageBox();
	}

	private void playFromFrame(int frameNum)
	{
		setCurrentFrame(frameNum);
		if (videoPlayer == null) {
	         videoPlayer = new Thread (this, threadName);
	         videoPlayer.start();
	      }
		
	}

	/**
	 * @return the currentFrame
	 */
	public int getCurrentFrame()
	{
		return currentFrame;
	}

	/**
	 * @param currentFrame the currentFrame to set
	 */
	public void setCurrentFrame(int currentFrame)
	{
		this.currentFrame = currentFrame;
	}
}
