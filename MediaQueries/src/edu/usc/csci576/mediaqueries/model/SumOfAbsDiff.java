package edu.usc.csci576.mediaqueries.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;
import edu.usc.csci576.mediaqueries.ui.ViewFrame;

public class SumOfAbsDiff {
	public static ViewFrame view;

	public static void main(String[] args) throws InterruptedException {
		

		System.out.println("Scene detector");
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		
		int TOTAL_FRAMES = 150;
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
		

		
		// Alg
		BufferedImage f1 = frames.get(10);
		BufferedImage f2 = frames.get(11);
		
		for (int i = 0; i < frames.size(); i++) {
			for (int j = i+1; j<frames.size() -1; j++ ) {

				view = new ViewFrame("Display Images");
				f1 = frames.get(i);
				f2 = frames.get(j);
				
				Mat m1 = ImageHandler.matify(f1);
				Mat m2 = ImageHandler.matify(f2);
				Mat diff = new Mat();
				
				Core.absdiff(m1, m2, diff);
				
				
				Scalar d =  computeSAD(f1, f2);
				
				System.out.println("sum of diffs = " + d);
				
				Thread.currentThread().sleep(200);
			}
		}
				
	}
	
	static Scalar computeSAD(BufferedImage f1, BufferedImage f2){
		Mat m1 = ImageHandler.matify(f1);
		Mat m2 = ImageHandler.matify(f2);
		
		//Imgproc.Canny(m1, m1, 0, 0);
		//Imgproc.Canny(m2, m2, 0, 0);
		
		
		Mat diff = new Mat();
		
		Core.absdiff(m1, m2, diff);
		Scalar d = Core.sumElems(diff);
		
		
		// for testing purpose
		// test GUI
		view.setVisible(true);
		view.addImage(ImageHandler.toBufferedImage(m1));
		view.addImage(ImageHandler.toBufferedImage(m2));
		view.addImage(ImageHandler.toBufferedImage(diff));
				
		
		
		return d;
		
	}

}
