package edu.usc.csci576.mediaqueries.parallel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
		
		
		// This is only checking for the fist frame
		Frame clipFrame = new Frame(clip.getVideoPath(),
				clip.getVideoName(), clip.getBeginIdx());
		
		List<Mat> clipHist = 
				RGBHistogram.getRGBMat(clipFrame.getPath(), Frame.WIDTH, Frame.HEIGHT);
		clipFrame.setRgbHist(clipHist);
		
		Frame mainFrame;
		FrameChecker frameChecker;
		
		List<Future<FCResultType>> frameCheckResults = 
				new ArrayList<Future<FCResultType>>();
		Future<FCResultType> result;
		
		for (int frameIdx = mainScene.getBeginIdx(); 
				frameIdx <= mainScene.getEndIdx();
				frameIdx++) {
			mainFrame = new Frame(mainScene.getFullPath(),
					mainScene.getVideoName(), frameIdx);
			
			frameChecker = new FrameChecker(mainFrame, clipFrame);
			
			result = frameCheckExecutor.submit(frameChecker);
			frameCheckResults.add(result);
		}

		Future<FCResultType> r;
		
		FCResultType comprator = new FCResultType();
		PriorityQueue<FCResultType> resultsHeap= new PriorityQueue<>(comprator);
		
		for (int i = 0; i < frameCheckResults.size(); i++) {
			r = frameCheckResults.get(i);
			resultsHeap.offer(r.get());			
		}
		
		String id = "Sc[" + this.mainScene.getBeginIdx() + "-" + this.mainScene.getEndIdx() + "]";
		for (int i = 0; i < 4; i++)
			System.out.println(id + " "  + resultsHeap.poll());
		
		
		// Now, get the best matched frames
		// Find scene to which these frames belong
		// Call this the candidata scene
		// Compare each frame of clip with each of the candidates
		// return top matching score 
		
		
		
		
		
		
		
		// shutdown
		frameCheckExecutor.shutdown();
		frameCheckExecutor.awaitTermination(Long.MAX_VALUE , TimeUnit.NANOSECONDS);

		return 0.0;
	}
}
