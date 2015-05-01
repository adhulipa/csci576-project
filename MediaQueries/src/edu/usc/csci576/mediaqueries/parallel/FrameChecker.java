package edu.usc.csci576.mediaqueries.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.opencv.core.Mat;

import edu.usc.csci576.mediaqueries.controller.CompareFrames;
import edu.usc.csci576.mediaqueries.data.DataLoader;
import edu.usc.csci576.mediaqueries.model.Frame;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;

public class FrameChecker implements Callable<Double> {

	private int WIDTH = 352;
	private int HEIGHT = 288;

	private Frame frame1;
	private Frame frame2;

	public FrameChecker(Frame mainFrame, Frame clipFrame) {

		frame2 = clipFrame;
		frame1 = mainFrame;
	}

	@Override
	public Double call() throws Exception {
		
		//System.out.println(frame1.getPath());
		
		// Compare using RGBHistograms
		List<Mat> hist1 = RGBHistogram.getRGBMat(frame1.getPath(), WIDTH, HEIGHT);
		List<Mat> hist2 = RGBHistogram.getRGBMat(frame2.getPath(), WIDTH, HEIGHT);
		
//		List<byte[][]> bgrHist = DataLoader.deserializeRGBArrays(
//				"histogram/"
//				+ "StarCraft/StarCraft150"
//				+ ".histogram");
		
		
		double[] result = CompareFrames.compareRGBHistogram(hist1, hist2);
		
//		System.out.println("FrameComp -- " +
//				frame1.getPath() + " & " + 
//				frame2.getPath() + " = " + 
//				Arrays.toString(result));
		
		double matchPercent = 0;
		
		for (double d : result) 
			matchPercent += d;
		matchPercent = (matchPercent / 3.0) * 100;
		
		return matchPercent;
		
	}
}
