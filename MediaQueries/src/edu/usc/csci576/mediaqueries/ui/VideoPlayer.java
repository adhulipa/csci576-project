package edu.usc.csci576.mediaqueries.ui;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class VideoPlayer implements Runnable {
	
	private String filepath;
	private String filename;
	private Thread videoPlayer;
	private String threadName;
	private int currentFrame;
	private BufferedImage[] scrubBuffer;
	private JLabel imageBox;
	
	public VideoPlayer(String threadName, String filepath, JLabel imgBox) {
		
		int lastsep = filepath.lastIndexOf("/");
		
		this.threadName = threadName;
		this.currentFrame = 1;
		this.filepath = filepath;
		this.filename = filepath.substring(lastsep + 1);
		this.scrubBuffer = populateScrubBuffer(filepath);
		this.imageBox = imgBox;
	}

	private BufferedImage[] populateScrubBuffer(String filepath) {
		
		int frame = 1;
		int i = 0;
		BufferedImage[] sb = new BufferedImage[20];
		while (frame <= 600) {
			
			String filePathString = String.format("%s/%s%03d.rgb", filepath, filename, frame);
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
		currentFrame = frameNum;
		if (videoPlayer == null) {
	         videoPlayer = new Thread (this, threadName);
	         videoPlayer.start();
	      }
	}
	
	/**
	 * play video
	 */
	public void playVideo() {
		playFromFrame(currentFrame);
	}
	
	/**
	 * stops playing the video.
	 * Should reset current frame to 1
	 * 
	 */
	public void stopVideo() {
		videoPlayer = null;
		currentFrame = 1;
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
		String filePathString = String.format("%s/%s%03d.rgb", filepath, filename, currentFrame);
		File f = new File(filePathString);
		if(f.exists() && !f.isDirectory()) {
			byte[] bytes = ImageHandler.readImageFromFile(filePathString);
			BufferedImage img = ImageHandler.toBufferedImage(bytes, 352,
					288, BufferedImage.TYPE_INT_RGB);
			imageBox.setIcon(new ImageIcon(img));
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
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentFrame++;
		}
	}

	public void setFrameAtIndex(int scrubIndex) {
		pauseVideo();
		imageBox.setIcon(new ImageIcon(scrubBuffer[scrubIndex - 1]));
	}
}

