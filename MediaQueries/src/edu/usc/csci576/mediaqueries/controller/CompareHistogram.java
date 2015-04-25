package edu.usc.csci576.mediaqueries.controller;

import java.awt.image.BufferedImage;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.model.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.ViewFrame;

public class CompareHistogram {
	/*
	 * @param hist1
	 * @param hist2
	 * @return boolean
	 * 
	 * Pass 2 RGB histograms from RGBHistogram.java and if comparison is successful
	 * based on our heuristic this function will return true.
	 * 
	 */
	public static boolean compareRGBHistogram(List<Mat> hist1, List<Mat> hist2) {
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
		double b_ret = Imgproc.compareHist(b1, b2, Imgproc.CV_COMP_BHATTACHARYYA);
		double g_ret = Imgproc.compareHist(g1, g2, Imgproc.CV_COMP_BHATTACHARYYA);
		double r_ret = Imgproc.compareHist(r1, r2, Imgproc.CV_COMP_BHATTACHARYYA);
		
		System.out.println(r_ret);
		System.out.println(g_ret);
		System.out.println(b_ret);
		
		return false;
	}
	
	public static void main(String[] args) {
		List<Mat> rgbmats1 = RGBHistogram.getRGBMat("database/starcraft/StarCraft020.rgb", 352, 288);
		List<Mat> rgbmats2 = RGBHistogram.getRGBMat("database/flowers/flowers100.rgb", 352, 288);
		
		//List<Mat> rgbmats1 = RGBHistogram.getRGBMat("database/flowers/flowers001.rgb", 352, 288);
		//List<Mat> rgbmats2 = RGBHistogram.getRGBMat("database/flowers/flowers001.rgb", 352, 288);
		
		compareRGBHistogram(rgbmats1, rgbmats2);
		
		byte[] bytes = ImageHandler.readImageFromFile("database/starcraft/StarCraft020.rgb");
		BufferedImage img1 = ImageHandler.toBufferedImage(bytes, 352,
				288, BufferedImage.TYPE_INT_RGB);
		
		byte[] bytes1 = ImageHandler.readImageFromFile("database/flowers/flowers100.rgb");
		BufferedImage img2 = ImageHandler.toBufferedImage(bytes1, 352,
				288, BufferedImage.TYPE_INT_RGB);
		
		ViewFrame vf = new ViewFrame("My");
		vf.addImage(img1);
		vf.addImage(img2);
		vf.setVisible(true);
	}

}
