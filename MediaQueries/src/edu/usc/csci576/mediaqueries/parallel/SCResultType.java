package edu.usc.csci576.mediaqueries.parallel;

import java.util.Comparator;

import edu.usc.csci576.mediaqueries.model.Scene;

public class SCResultType  implements Comparable<SCResultType>, Comparator<SCResultType> {
	
	private Scene targetScene;
	private Scene clip;
	private int numFramesMatched;
	private double matchPercent;
	private int bestMatchedFrameIdx;
	
	private float audioMostSimilarTimePosition;
	private float audioMostSimilarFramePosition;
	
	
	
	/**
	 * @return the targetScene
	 */
	public Scene getTargetScene() {
		return targetScene;
	}
	/**
	 * @param targetScene the targetScene to set
	 */
	public void setTargetScene(Scene targetScene) {
		this.targetScene = targetScene;
	}
	/**
	 * @return the clip
	 */
	public Scene getClip() {
		return clip;
	}
	/**
	 * @param clip the clip to set
	 */
	public void setClip(Scene clip) {
		this.clip = clip;
	}
	/**
	 * @return the numFramesMatched
	 */
	public int getNumFramesMatched() {
		return numFramesMatched;
	}
	/**
	 * @param numFramesMatched the numFramesMatched to set
	 */
	public void setNumFramesMatched(int numFramesMatched) {
		this.numFramesMatched = numFramesMatched;
	}
	/**
	 * @return the matchPercent
	 */
	public double getMatchPercent() {
		return matchPercent;
	}
	/**
	 * @param matchPercent the matchPercent to set
	 */
	public void setMatchPercent(double matchPercent) {
		this.matchPercent = matchPercent;
	}
	
	
	
	
	

	/** 
	 * Compares in reverse order
	 * i.e. higher matchpercent is ranked lower in queue
	 */
	@Override
	public int compareTo(SCResultType o) {
		
		if (this.matchPercent < o.matchPercent)
			return 1;
		if (this.matchPercent > o. matchPercent)
			return -1;
		return 0;
	}

	@Override
	public int compare(SCResultType o1, SCResultType o2) {
		return o1.compareTo(o2);
	}
	
	public void setBestMatchedFrameIdx(int bestMatchedFrameIx) {
		
		this.bestMatchedFrameIdx = bestMatchedFrameIx;
	}	
	
	public int getBestMatchedFrameIdx() {
		return this.bestMatchedFrameIdx;
	}
	/**
	 * @return the audioMostSimilarTimePosition
	 */
	public float getAudioMostSimilarTimePosition() {
		return audioMostSimilarTimePosition;
	}
	/**
	 * @param audioMostSimilarTimePosition the audioMostSimilarTimePosition to set
	 */
	public void setAudioMostSimilarTimePosition(float audioMostSimilarTimePosition) {
		this.audioMostSimilarTimePosition = audioMostSimilarTimePosition;
	}
	/**
	 * @return the audioMostSimilarFramePosition
	 */
	public float getAudioMostSimilarFramePosition() {
		return audioMostSimilarFramePosition;
	}
	/**
	 * @param audioMostSimilarFramePosition the audioMostSimilarFramePosition to set
	 */
	public void setAudioMostSimilarFramePosition(
			float audioMostSimilarFramePosition) {
		this.audioMostSimilarFramePosition = audioMostSimilarFramePosition;
	}
	
	
}
