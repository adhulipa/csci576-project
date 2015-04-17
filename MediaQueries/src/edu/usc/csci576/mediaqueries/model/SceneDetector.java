package edu.usc.csci576.mediaqueries.model;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class SceneDetector {

	public static void main(String[] args) throws Exception{
		System.out.println("Scene detector");
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		
		int TOTAL_FRAMES = 30;
		List<BufferedImage> frames = new ArrayList<BufferedImage>(TOTAL_FRAMES);
		
		// load some scenes
		BufferedImage frame = null;
		int frameNum = 1;
		while (frameNum <= TOTAL_FRAMES) {
			String filePathString = String.format("%s/%s%03d.rgb", "database/starcraft", "StarCraft", frameNum);
			frame = ImageHandler.toBufferedImage(
					(ImageHandler.readImageFromFile(filePathString)),
					352, 288, BufferedImage.TYPE_3BYTE_BGR);
			frames.add(frame);
			
			frameNum++;
		}
		

		// test GUI
		ViewFrame view = new ViewFrame("Display Images");
		view.addImage(frame);
		
		System.out.println("DONE!");
		
		
		Mat im = matify(frames.get(29));
		frame = ImageHandler.toBufferedImage(im);
		view.addImage(frame);
		
		
		// ADI: Matify works correctly
		// TODO: Use canny edge detector to get edges of image
		// Use edge-based scene detectiona lgorithm by ranier linehart
		
		System.out.println(im);
		
		view.setVisible(true);
		
		
	}
	
	// Convert image to Mat
	public static Mat matify(BufferedImage im) {
	    // Convert INT to BYTE
	    //im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
	    // Convert bufferedimage to byte array
	    byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer())
	            .getData();

	    // Create a Matrix the same size of image
	    Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
	    // Fill Matrix with image values
	    image.put(0, 0, pixels);

	    return image;

	}
}
