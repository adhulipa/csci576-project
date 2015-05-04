package edu.usc.csci576.mediaqueries.controller;

import java.util.Map;

import edu.usc.csci576.mediaqueries.model.Scene;
import edu.usc.csci576.mediaqueries.parallel.SCResultType;

public class VideoCompareResult {

	Map<String, Double> scoresMap;
	Map<String, SCResultType> bestMatchedSceneResults;
	
	
	public Map<String, SCResultType> getBestMatchedScene() {
		return bestMatchedSceneResults;
	}
	
	public Map<String, Double> getScoresMap() {
		return scoresMap;
	}
}
