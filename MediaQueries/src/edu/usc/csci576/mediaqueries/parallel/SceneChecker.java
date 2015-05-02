package edu.usc.csci576.mediaqueries.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opencv.core.Mat;

import edu.usc.csci576.mediaqueries.controller.CompareFrames;
import edu.usc.csci576.mediaqueries.model.Frame;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.Scene;

public class SceneChecker implements Callable<Double> {

	/*
	 * Helps search for clip inside a scene
	 */
	
	private Scene mainScene;
	private Scene clip;
	
	
	public SceneChecker(Scene mainScene, Scene clip) {
		this.mainScene = mainScene;
		this.clip = clip;
	}

	@Override
	public Double call() throws Exception {
		// TODO
		/*
		 * This should implement
		 * a bunch of callable that compare frames
		 * Pass buck to FrameChceker
		 */
		
		
		ExecutorService frameCheckExecutor = Executors.newCachedThreadPool();
		//frameCheckExecutor = Executors.newFixedThreadPool(5);
		//frameCheckExecutor = Executors.newSingleThreadExecutor();
		
		Frame clipFrame = new Frame(clip.getVideoPath(),
				clip.getVideoName(), clip.getBeginIdx());
		
		List<Mat> clipHist = RGBHistogram.getRGBMat(clipFrame.getPath(), Frame.WIDTH, Frame.HEIGHT);
		clipFrame.setRgbHist(clipHist);
		
		Frame mainFrame;
		FrameChecker frameChecker;
		
		List<Future<Double[]>> frameCheckResults = new ArrayList<Future<Double[]>>();
		Future<Double[]> result;
		
		for (int frameIdx = mainScene.getBeginIdx(); 
				frameIdx <= mainScene.getEndIdx();
				frameIdx++) {
			mainFrame = new Frame(mainScene.getFullPath(),
					mainScene.getVideoName(), frameIdx);
			
			frameChecker = new FrameChecker(mainFrame, clipFrame);
			
			result = frameCheckExecutor.submit(frameChecker);
			frameCheckResults.add(result);
		}
		
		Future<Double[]> r;
		
		// Format of r -- frameChekcResult is
		// Double[] 
		//-- Double[0] - databaseVideoFrameIdx
		//-- Double[1] - queryVideoFrameIdx
		//-- Double[2] - matchPercent
		// TODO: Use better data struture
		
		for (int i = 0; i < frameCheckResults.size(); i++) {
			r = frameCheckResults.get(i);
			
				System.out.println(
						r.get()[0] + " " +
						r.get()[1] + " " + 
						r.get()[2]
						);
			
		}
		
		frameCheckExecutor.shutdown();
		
				
//		System.out.println(
//				"Comparing Scene - "
//				+ mainScene.getVideoName() + "[" 
//		+ mainScene.getBeginIdx()
//		+ " to "
//		+ mainScene.getEndIdx() + "] "
//		+ " & ["
//		+ clip.getVideoName() + " - "
//				+ clip.getBeginIdx() + " - " + clip.getEndIdx()  + "]"
//		);
		
		
		
		
		return 0.0;
	}
}
