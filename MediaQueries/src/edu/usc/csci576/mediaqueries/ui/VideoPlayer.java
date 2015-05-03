package edu.usc.csci576.mediaqueries.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.Core;

import edu.usc.csci576.mediaqueries.data.ImageHandler;

public class VideoPlayer implements Runnable {
	
	private String filepath;
	private String filename;
	private Thread videoPlayer;
	private String threadName;
	private int currentFrame;
	private int endFrame;
	private BufferedImage[] scrubBuffer;
	private JLabel imageBox;
	
	public VideoPlayer(String threadName, String filepath, String fileName, JLabel imgBox) {
		
//		int lastsep = filepath.lastIndexOf("/");
		
		this.threadName = threadName;
		this.setCurrentFrame(1);
		this.filepath = filepath;
		this.filename = fileName;
		this.scrubBuffer = populateScrubBuffer();
		this.imageBox = imgBox;
	}

	private BufferedImage[] populateScrubBuffer() {
		
		int frame = 1;
		int i = 0;
		BufferedImage[] sb = new BufferedImage[20];
		while (frame <= 600) {
			
			String filePathString = String.format("%s/%s%03d.rgb", this.filepath, this.filename, frame);
			File f = new File(filePathString);
			BufferedImage img;
			
			if(f.exists() && !f.isDirectory()) {
				byte[] bytes = ImageHandler.readImageFromFile(filePathString);
				 img = ImageHandler.toBufferedImage(bytes, 352,
						288, BufferedImage.TYPE_INT_RGB);
				 sb[i++] = img;
			}
			
			frame += 30;
		}
		return sb;
	}

	/**
	 * 
	 * @param frameNum - start playing from "frameNum" of the video
	 * Plays from arbitrary location in video rather than from the beginning
	 */
	public void playFromFrame(int frameNum) {
		setCurrentFrame(frameNum);
		if (videoPlayer == null) {
	         videoPlayer = new Thread (this, threadName);
	         videoPlayer.start();
	      }
	}
	
	/**
	 * play video
	 */
	public void playVideo() {
		playFromFrame(getCurrentFrame());
	}
	
	/**
	 * stops playing the video.
	 * Should reset current frame to 1
	 * 
	 */
	public void stopVideo() {
		videoPlayer = null;
		setCurrentFrame(1);
		/* also set current frame in UI to 1 */
		setCurrentFrameToImageBox();
	}
	
	/**
	 * pause execution of play at current frame
	 */
	public void pauseVideo() {
		videoPlayer = null;
	}
	
	private void setCurrentFrameToImageBox() {
		String filePathString = String.format("%s/%s%03d.rgb", filepath, filename, getCurrentFrame());
		File f = new File(filePathString);
		if(f.exists() && !f.isDirectory()) {
			byte[] bytes = ImageHandler.readImageFromFile(filePathString);
						
			BufferedImage img = ImageHandler.toBufferedImage(bytes, 352,
					288, BufferedImage.TYPE_3BYTE_BGR);
			
			
			imageBox.setIcon(new ImageIcon(img));
			//System.out.println("current frame " + getCurrentFrame());
		} else {
			this.stopVideo();
		}
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while(videoPlayer == thisThread) {
			setCurrentFrameToImageBox();
			try {
				//Thread.sleep(21);
				TimeUnit.MICROSECONDS.sleep(21319);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setCurrentFrame(getCurrentFrame() + 1);
		}
	}
	
	public void setVideoPath(String filepath, String fileName) {
		/* If the video is running stop it */
		stopVideo();
		this.filepath = filepath;
		this.filename = fileName;
		setCurrentFrameToImageBox();
	}
	
	public void setFrameAtIndex(int scrubIndex) {
		pauseVideo();
		if(scrubIndex > 0 && scrubIndex <= scrubBuffer.length)
			imageBox.setIcon(new ImageIcon(scrubBuffer[scrubIndex - 1]));
		setCurrentFrame(scrubIndex * 30);
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

