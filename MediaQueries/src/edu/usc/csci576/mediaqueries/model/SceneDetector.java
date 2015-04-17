package edu.usc.csci576.mediaqueries.model;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class SceneDetector {

	public static void main(String[] args) throws Exception{
		System.out.println("Scene detector");
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		
		int TOTAL_FRAMES = 400;
		List<BufferedImage> frames = new ArrayList<BufferedImage>(TOTAL_FRAMES);
		
		// load some scenes
		BufferedImage frame = null;
		int frameNum = 100;
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
		
		System.out.println("DONE!");
		
		
		
		
		
		// Alg exp
		
		// Step1 get the frames
		BufferedImage im1, im2;
		im1 = frames.get(2);
		im2 = frames.get(99);
		Mat f1 = matify(im1);
		Mat f2 = matify(im2);
		
		// Step2 compute edges
		Mat f1e = new Mat();
		Mat f2e = new Mat();
		Imgproc.Canny(f1, f1e, 100, 1);
		Imgproc.Canny(f2, f2e, 100, 1);
		
		//Step3 dilate the edges -- improves algorithm
		//Imgproc.dilate(f1e, f1e, kernel);
		//Imgproc.dilate(f2e, f2e, kernel);
		
		//Step4 Edge change calcualtion
		// 4.1 Hausdorff distance for motion compensation
		// 4.2 Compute edge change fraction
		// 4.3 Compute entering and exiting edges
		// TODO
		
		
		// Step5 compute diffs
		Mat d1 = new Mat();
		Mat d2 = new Mat();
		Core.absdiff(f1e,f2e,d1);
		Core.subtract(f2e,f1e,d2);
		
		// If the diff is close to zero then
		// scene hasnt changed
		
		// Step6: Plot all of above to visualze progress
		frame = im1;
		view.addImage(frame);
		frame = im2;
		view.addImage(frame);

		frame = ImageHandler.toBufferedImage(f1e);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(f2e);
		view.addImage(frame);

		frame = ImageHandler.toBufferedImage(d1);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(d2);
		view.addImage(frame);
		
		
		// ADI: Matify works correctly
		// TODO: Use canny edge detector to get edges of image
		// Use edge-based scene detectiona lgorithm by ranier linehart
		
		System.out.println(f1);
		
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
