package edu.usc.csci576.mediaqueries.controller;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.data.ImageHandler;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.Scene;
import edu.usc.csci576.mediaqueries.ui.ViewFrame;

public class CompareFrames  {
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Scene q, d;
		
		sceneComparator();
		
	}
	
	
	private static void sceneComparator() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * @param hist1
	 * @param hist2
	 * @param CV_COMP_TYPE - use Imgproc.CV_COMP_CORREL
	 * @return boolean
	 * 
	 * 
	 * Pass 2 RGB histograms from RGBHistogram.java and if comparison is successful
	 * based on our heuristic this function will return true.
	 * 
	 */
	public static double[] compareRGBHistogram(List<Mat> hist1, 
			List<Mat> hist2) {
		
		
		Mat b1 = new Mat(288, 352, CvType.CV_32F);
		Mat g1 = new Mat(288, 352, CvType.CV_32F);
		Mat r1 = new Mat(288, 352, CvType.CV_32F);
		
		Mat b2 = new Mat(288, 352, CvType.CV_32F);
		Mat g2 = new Mat(288, 352, CvType.CV_32F);
		Mat r2 = new Mat(288, 352, CvType.CV_32F);
		
		hist1.get(0).convertTo(b1, CvType.CV_32F);
		hist1.get(1).convertTo(g1, CvType.CV_32F);
		hist1.get(2).convertTo(r1, CvType.CV_32F);
		
		hist2.get(0).convertTo(b2, CvType.CV_32F);
		hist2.get(1).convertTo(g2, CvType.CV_32F);
		hist2.get(2).convertTo(r2, CvType.CV_32F);
		
		double b_ret = Imgproc.compareHist(b1, b2, Imgproc.CV_COMP_CORREL);
		double g_ret = Imgproc.compareHist(g1, g2, Imgproc.CV_COMP_CORREL);
		double r_ret = Imgproc.compareHist(r1, r2, Imgproc.CV_COMP_CORREL);
		
//		
//		System.out.println(r_ret);
//		System.out.println(g_ret);
//		System.out.println(b_ret);
//		
		return new double[]{r_ret, g_ret, b_ret};
	}
	
	public static void TestHarness_main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String q = "database/starcraft/StarCraft035.rgb";
		String d = "database/flowers/flowers001.rgb";				
		//d = "database/starcraft/StarCraft039.rgb";
		
		List<Mat> rgbmats1 = RGBHistogram.getRGBMat(q, 352, 288);
		List<Mat> rgbmats2 = RGBHistogram.getRGBMat(d, 352, 288);
		
		//List<Mat> rgbmats1 = RGBHistogram.getRGBMat("database/flowers/flowers001.rgb", 352, 288);
		//List<Mat> rgbmats2 = RGBHistogram.getRGBMat("database/flowers/flowers001.rgb", 352, 288);
		
		
		double[] diffs = compareRGBHistogram(rgbmats1, rgbmats2);
		
		System.out.println(Arrays.toString(diffs));
		
		byte[] bytes = ImageHandler.readImageFromFile(q);
		BufferedImage img1 = ImageHandler.toBufferedImage(bytes, 352,
				288, BufferedImage.TYPE_INT_RGB);
		
		byte[] bytes1 = ImageHandler.readImageFromFile(d);
		BufferedImage img2 = ImageHandler.toBufferedImage(bytes1, 352,
				288, BufferedImage.TYPE_INT_RGB);
		
		ViewFrame vf = new ViewFrame("My");
		vf.addImage(img1);
		vf.addImage(img2);
		vf.setVisible(true);
	}

}
