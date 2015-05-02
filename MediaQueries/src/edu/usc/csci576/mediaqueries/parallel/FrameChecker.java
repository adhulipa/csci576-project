package edu.usc.csci576.mediaqueries.parallel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Mat;

import edu.usc.csci576.mediaqueries.controller.CompareFrames;
import edu.usc.csci576.mediaqueries.data.DataLoader;
import edu.usc.csci576.mediaqueries.model.Frame;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;

public class FrameChecker implements Callable<FCResultType> {

	private int WIDTH = 352;
	private int HEIGHT = 288;

	private Frame frame1;
	private Frame frame2;
	
	public FrameChecker(Frame mainFrame, Frame clipFrame) {

		frame2 = clipFrame;
		frame1 = mainFrame;
		
	}
	
	@Override
	public FCResultType call() throws Exception {
		
		String hist1Path = String.format("histogram/%s/%s%03d.histogram",
				frame1.getVideoName(), frame1.getVideoName(), frame1.getFrameIdx());
		
		// Compare using RGBHistograms		
		// List<Mat> hist1 = RGBHistogram.getRGBMat(frame1.getPath(), WIDTH, HEIGHT);
		
		// frame1.path = database/StarCraft/StarCraft001.rgb
		List<Mat> hist1 = frame1.getRgbHist();
		List<Mat> hist2 = frame2.getRgbHist();
		
		if (hist1 == null) {
			hist1 = RGBHistogram.getRGBMat(frame1.getPath(), Frame.WIDTH, Frame.HEIGHT);
		}
		
		if (hist2 == null) {
			hist2 = RGBHistogram.getRGBMat(frame2.getPath(), Frame.WIDTH, Frame.HEIGHT);
		}

		
		
//		
//		System.out.println(Arrays.toString(hist1.get(0).get(0, 0)));
//		
//		System.out.println(" ");
//		
//		System.out.println(Arrays.toString(h1.get(0).get(0, 0)));
		

//		List<byte[][]> bgrHist = DataLoader.deserializeRGBArrays(
//				"histogram/"
//				+ "StarCraft/StarCraft150"
//				+ ".histogram");
		
		
		double[] match = CompareFrames.compareRGBHistogram(hist1, hist2);
		
//		System.out.println("FrameComp -- " +
//				frame1.getPath() + " & " + 
//				frame2.getPath() + " = " + 
//				Arrays.toString(result));
		
		double matchPercent = 0;
		
		for (double d : match) 
			matchPercent += d;
		matchPercent = (matchPercent / 3.0) * 100.0;
		
		Double[] result = new Double[]{
				Double.valueOf(frame1.getFrameIdx()),
				Double.valueOf(frame2.getFrameIdx()),
				matchPercent};
		
		Pair<String, String> framePair = Pair.of(frame1.getPath(), frame2.getPath());
		Map<Pair<String, String>, Double> resultMap = 
				new LinkedHashMap<Pair<String,String>, Double>();
		resultMap.put(framePair,  matchPercent);
		
		FCResultType resultData = new FCResultType(frame1.getFrameIdx(), frame2.getFrameIdx(), 
				matchPercent, frame1.getVideoName());
		
//		return resultMap;
		return resultData;
		
	}
}
