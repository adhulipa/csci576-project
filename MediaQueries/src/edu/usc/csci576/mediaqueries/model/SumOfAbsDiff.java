package edu.usc.csci576.mediaqueries.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.core.Core.*;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class SumOfAbsDiff {

	public static void main(String[] args) {
		

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
		view.setVisible(true);
		
		// Alg
		BufferedImage f1 = frames.get(10);
		BufferedImage f2 = frames.get(11);
		
		Mat m1 = ImageHandler.matify(f1);
		Mat m2 = ImageHandler.matify(f2);
		Mat diff = new Mat();
		
		Core.absdiff(m1, m2, diff);
		Scalar d = Core.sumElems(diff);
		
		System.out.println("sum of diffs = " + d);
		
		view.addImage(ImageHandler.toBufferedImage(m1));
		view.addImage(ImageHandler.toBufferedImage(m2));
		view.addImage(ImageHandler.toBufferedImage(diff));
		
	}

}
