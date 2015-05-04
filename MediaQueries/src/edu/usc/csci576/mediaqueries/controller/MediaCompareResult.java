package edu.usc.csci576.mediaqueries.controller;

import java.util.Map;

import edu.usc.csci576.mediaqueries.model.Scene;

public class MediaCompareResult {

	Map<String, Double> scoresMap;
	
	/*
	 * Can extend these to arays
	 * to get data for each video
	 */
	
	String bestMacthedVideoName;
	Scene bestMatchedScene;
	int bestMacthedFrame;
	
	public Map<String, Double> getScoreMap() {
		return scoresMap;
	}
	
}
