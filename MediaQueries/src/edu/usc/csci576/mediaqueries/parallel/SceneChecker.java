package edu.usc.csci576.mediaqueries.parallel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.activity.InvalidActivityException;
import javax.naming.ConfigurationException;

import org.opencv.core.Mat;

import edu.usc.csci576.mediaqueries.controller.CompareFrames;
import edu.usc.csci576.mediaqueries.descriptor.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.Frame;
import edu.usc.csci576.mediaqueries.model.Scene;

public class SceneChecker implements Callable<SCResultType> {

	/*
	 * Helps search for clip inside a scene
	 */
	
	private Scene mainScene;
	private Scene clip;
	private int comparisonType;
	private ExecutorService frameCheckExecutor;
	private int bestMatchedFrameIdx = -999;
	
	public static final int COMPARE_IN_ORDER = 0;
	public static final int COMPARE_FIRST_TO_ONE = 1;
	
	public SceneChecker(Scene mainScene, Scene clip, int comparisonType) {
		this.mainScene = mainScene;
		this.clip = clip;
		this.comparisonType = comparisonType;
		this.frameCheckExecutor = Executors.newCachedThreadPool();

	}

	public SceneChecker(Scene ds, Scene qs, int compareInOrder,
			int bestMatchedFrameIdxInScene) {
		
		this.mainScene = ds;
		this.clip = qs;
		this.comparisonType = compareInOrder;
		this.frameCheckExecutor = Executors.newFixedThreadPool(2);
		this.bestMatchedFrameIdx = bestMatchedFrameIdxInScene;
	}

	@Override
	public SCResultType call() throws Exception {
		// TODO
		/*
		 * This should implement
		 * a bunch of callable that compare frames
		 * Pass buck to FrameChceker
		 */
		
		SCResultType finalResult = null;
		switch(comparisonType) {
		
		case SceneChecker.COMPARE_FIRST_TO_ONE:
			finalResult = doFirstFrameComparison();
			break;
		case SceneChecker.COMPARE_IN_ORDER:
			if (bestMatchedFrameIdx < 0) {
				bestMatchedFrameIdx = mainScene.getBeginIdx();
			}
			finalResult = doFrameForFrameComparison(bestMatchedFrameIdx );
			break;
			
		}
		return finalResult;
	}

	
	private SCResultType doFirstFrameComparison()  {
		frameCheckExecutor = Executors.newFixedThreadPool(1);
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
		Queue<FCResultType> resultsHeap= new PriorityBlockingQueue<FCResultType>();
		
		for (int i = 0; i < frameCheckResults.size(); i++) {
			r = frameCheckResults.get(i);
			try {
				resultsHeap.offer(r.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				frameCheckExecutor.shutdownNow();
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				frameCheckExecutor.shutdownNow();
				e.printStackTrace();
			}			
		}
		
		// Now, get the best matched frames
		// Find scene to which these frames belong
		// Call this the candidata scene
		// Compare each frame of clip with each of the candidates
		// return top matching score 
		
		
		FCResultType bestResult = resultsHeap.poll();
		bestResult.getMatchpercent();
		
		/* If match percent is ver low. Return
		 * as is. Dont computa full scnene match
		 * TODO
		 */
		
		
		int bestMatchedFrameIx = bestResult.getTargetFrameIdx();
		int numMainFrames = mainScene.getEndIdx() - mainScene.getBeginIdx() + 1;
		int numClipFrames = clip.getEndIdx() - clip.getBeginIdx() + 1;
		int numCompStarted = 0;
		
		int maxComp = Math.min(numClipFrames, numMainFrames);
		
		frameCheckResults = 
				new ArrayList<Future<FCResultType>>();
		
		
		
		int mfIdx = bestMatchedFrameIx;
		int cfIdx = clip.getBeginIdx();
		
		while (numCompStarted <= maxComp 
				&& mfIdx >= mainScene.getBeginIdx() && mfIdx <= mainScene.getEndIdx()
				&& cfIdx >= clip.getBeginIdx() && cfIdx <= clip.getEndIdx()) {
			mainFrame = new Frame(mainScene.getFullPath(),
					mainScene.getVideoName(), mfIdx);
			clipFrame = new Frame(clip.getVideoPath(),
					clip.getVideoName(), cfIdx);
			
			frameChecker = new FrameChecker(mainFrame, clipFrame);
			
			result = frameCheckExecutor.submit(frameChecker);
			frameCheckResults.add(result);
			
			numCompStarted++;
			mfIdx++;
			cfIdx++;
		}
		
		// Check results
		FCResultType item;
		double matchPercent = 0;
		for (int i = 0; i < frameCheckResults.size(); i++) {
			
			try {
				item = frameCheckResults.get(i).get();
				matchPercent += item.getMatchpercent();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				frameCheckExecutor.shutdownNow();
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				frameCheckExecutor.shutdownNow();
				e.printStackTrace();
			}
			
			/*System.out.println(
					
					item.getTargetVideoName() + "" + item.getTargetFrameIdx() +
					" matched " + item.getClipFrameIdx() + " by "  + item.getMatchpercent()
					
					);*/
		}
		
		
		matchPercent = matchPercent / maxComp;
//		System.out.println("matched percentage of clip sncee in mainscene -" + 
//		mainScene.getVideoName() + " [" + mainScene.getBeginIdx() + " - " + mainScene.getEndIdx() + "]" + clip.getFullPath() + " by " + matchPercent);
//		
//		System.out.println("best matched frameIdx " + bestMatchedFrameIx);
		
		SCResultType sceneCheckResult = new SCResultType();
		sceneCheckResult.setClip(clip);
		sceneCheckResult.setTargetScene(mainScene);
		sceneCheckResult.setNumFramesMatched(maxComp);
		sceneCheckResult.setMatchPercent(matchPercent);
		sceneCheckResult.setBestMatchedFrameIdx(bestMatchedFrameIx);
		
		frameCheckExecutor.shutdown();
		
		return sceneCheckResult;
	}

	private SCResultType doFrameForFrameComparison(int bestMatchedFrameIx) throws InterruptedException, ExecutionException {
		// Now, get the best matched frames
		// Find scene to which these frames belong
		// Call this the candidata scene
		// Compare each frame of clip with each of the candidates
		// return top matching score 
	
//		System.out.println("scene fbf comparison");
		
		frameCheckExecutor = Executors.newFixedThreadPool(2);
		
		int numMainFrames = mainScene.getEndIdx() - mainScene.getBeginIdx() + 1;
		int numClipFrames = clip.getEndIdx() - clip.getBeginIdx() + 1;
		int numCompStarted = 0;
		
		int maxComp = Math.min(numClipFrames, numMainFrames);
		
		ArrayList<Future<FCResultType>> frameCheckResults 
		= new ArrayList<Future<FCResultType>>();
		
		
		// if bestMacthedFrame is not in this scene,
		// then compare from first frame of this scene
		
		if (bestMatchedFrameIx < mainScene.getBeginIdx() 
				|| bestMatchedFrameIx > mainScene.getEndIdx()) {
			bestMatchedFrameIx = mainScene.getBeginIdx();
		}
		
		
		int mfIdx = bestMatchedFrameIx;
		int cfIdx = clip.getBeginIdx();
		
		while (numCompStarted <= maxComp 
				&& mfIdx >= mainScene.getBeginIdx() && mfIdx <= mainScene.getEndIdx()
				&& cfIdx >= clip.getBeginIdx() && cfIdx <= clip.getEndIdx()) {
			Frame mainFrame = new Frame(mainScene.getFullPath(),
					mainScene.getVideoName(), mfIdx);
			Frame clipFrame = new Frame(clip.getVideoPath(),
					clip.getVideoName(), cfIdx);
			
			FrameChecker frameChecker = new FrameChecker(mainFrame, clipFrame);
			
			Future<FCResultType> result = frameCheckExecutor.submit(frameChecker);
			frameCheckResults.add(result);
			
			numCompStarted++;
			mfIdx++;
			cfIdx++;
		}
		
		// Check results
		FCResultType item;
		double matchPercent = 0;
		for (int i = 0; i < frameCheckResults.size(); i++) {
			
			item = frameCheckResults.get(i).get();
			
			
			matchPercent += item.getMatchpercent();

		}
		
		
		matchPercent = matchPercent / numCompStarted;
		
		
//		System.out.println(" FbF comp matched percentage of clip sncee in mainscene -" + 
//		mainScene.getVideoName() + " [" + mainScene.getBeginIdx() + 
//		" - " + mainScene.getEndIdx() + "]" + clip.getFullPath() + " by " + matchPercent);
		
		SCResultType sceneCheckResult = new SCResultType();
		sceneCheckResult.setClip(clip);
		sceneCheckResult.setTargetScene(mainScene);
		sceneCheckResult.setNumFramesMatched(maxComp);
		sceneCheckResult.setMatchPercent(matchPercent);
		
		frameCheckExecutor.shutdown();
		
		return sceneCheckResult;

	}
}
