package edu.usc.csci576.mediaqueries.ui;

import java.awt.image.BufferedImage;
import java.io.File;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class VideoPlayer implements Runnable {
	
	/**
	 * 
	 */
	private String filepath;
	private String filename;
	private int type;
	MainFrameUI uiObject;
	private Thread t;
	private String threadName;
	
	/**
	 * 
	 */
	public void run() {
		int i = 1;
		while(true) {
			String filePathString = String.format("%s/%s%03d.rgb", filepath, filename, i);
			File f = new File(filePathString);
			if(f.exists() && !f.isDirectory()) {
				byte[] bytes = ImageHandler.readImageFromFile(filePathString);
				BufferedImage img = ImageHandler.toBufferedImage(bytes, 352,
						288, BufferedImage.TYPE_INT_RGB);
				if (type == 0) {
					uiObject.setQueryImageBoxFrame(img);
				} else {
					uiObject.setResultImageBoxFrame(img);
				}
				
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
			i++;
		}
	}
	
	/**
	 * 
	 */
	public void start () {
		if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	}
	
	/**
	 * type 0 = query
	 * type 1 = result
	 */
	public VideoPlayer(String name, MainFrameUI ui, String filepath_s, int t) {
		threadName = name;
		filepath = filepath_s;
		int lastsep = filepath.lastIndexOf("/");
		filename = filepath.substring(lastsep + 1);
		type = t;
		uiObject = ui;
	}
}
