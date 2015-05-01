package edu.usc.csci576.mediaqueries.parallel;

import java.util.concurrent.Callable;

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
		
				
		System.out.println(
				"Comparing Scene - "
				+ mainScene.getVideoName() + "[" 
		+ mainScene.getBeginIdx()
		+ " to "
		+ mainScene.getEndIdx() + "] "
		+ " & ["
		+ clip.getVideoName() + " - "
				+ clip.getBeginIdx() + " - " + clip.getEndIdx()  + "]"
		);
		
		return 0.0;
	}
}
