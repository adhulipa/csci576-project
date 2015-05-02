package edu.usc.csci576.mediaqueries.model;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import edu.usc.csci576.mediaqueries.data.ImageHandler;

public class MyVideo
{
	private BufferedImage []videoBuffer;
	public MyVideo(String filePath, String fileName, int totalFrames)
	{
		videoBuffer = new BufferedImage[totalFrames];
		for(int i = 1; i <= 600; i++)
		{
			String filePathString = String.format("%s/%s%03d.rgb", filePath, fileName, i);
			
			byte[] bytes = ImageHandler.readImageFromFile(filePathString);
			
			videoBuffer[i-1] = ImageHandler.toBufferedImage(bytes, 352, 288, BufferedImage.TYPE_3BYTE_BGR);
		}		
	}
	
	public ImageIcon getImage(int frame)
	{
		return (frame > 0 && frame <= 600) ? new ImageIcon(videoBuffer[frame - 1]) : null;
	}
	
}
