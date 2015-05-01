package edu.usc.csci576.mediaqueries.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
		
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool = Executors.newSingleThreadExecutor();

		Frame clipFrame = new Frame(clip.getVideoPath() 
				+ "/" + clip.getVideoName(), clip.getBeginIdx());
		Frame mainFrame;
		FrameChecker frameChecker;
		
		List<Future<Double>> frameCheckResults = new ArrayList<Future<Double>>();
		Future<Double> result;
		
		for (int frameIdx = mainScene.getBeginIdx(); 
				frameIdx <= mainScene.getEndIdx();
				frameIdx++) {
			mainFrame = new Frame(mainScene.getFullPath()
					+ "/" + mainScene.getVideoName(), frameIdx);
			
			frameChecker = new FrameChecker(mainFrame, clipFrame);
			
			result = threadPool.submit(frameChecker);
			frameCheckResults.add(result);
		}
		
		for (Future<Double> r : frameCheckResults) {
			if (r.get() > 95) {
				System.out.println(r.get() + " good match found");
			}
		}
		
		
		
				
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
