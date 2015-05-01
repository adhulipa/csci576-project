package edu.usc.csci576.mediaqueries.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;

import edu.usc.csci576.mediaqueries.data.DataLoader;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.Scene;
import edu.usc.csci576.mediaqueries.model.SceneDetector;
import edu.usc.csci576.mediaqueries.parallel.SceneChecker;


public class VideoComparator  {

	private String queryVideoFile;
	
	public static void run() {
		// TODO: Complete comparator logic
		
		
		// AD: Idea
		/*
		 * Step1 : iterate over dataset
		 * 
		 * Step2 : for each item get scenes & get scenes for query
		 * 
		 * Step3 : Spawn thread for each frame in scene
		 * 
		 * Step4 : Compare query frame with item frame
		 * 			using compareHist with CV_CORRELL
		 *  We can use compareHist() to get  percent match of frame.
		 *  This can be intelligently used to compute match
		 */
		
		ExecutorService threadpool = Executors.newCachedThreadPool();
		
		
	}
	
	public static void main(String[] args) {
		System.out.println("Started comparator...");
		
		ExecutorService sceneCheckExecutor = Executors.newCachedThreadPool();
		sceneCheckExecutor = Executors.newSingleThreadExecutor();
		
		// query vide stuff
		String queryPath = "query/Q4";
		String queryFile = "Q4_";
		List<Integer[]> queryScenes = SceneDetector.getScenes(queryPath, queryFile, 150);
		
		// database stuff
		String dataPath = "database/";
		String dataFile = "StarCraft";
		HashMap<String, List<Integer[]>> sceneMap = DataLoader.loadScenes();
		List<Integer[]> dataScenes = sceneMap.get(dataFile);
		
		/* List<byte[][]> bgrHist = DataLoader.deserializeRGBArrays(
				"histogram/"
				+ "StarCraft/StarCraft150"
				+ ".histogram");
		*/
		
		// Idea: get first scene fo query
		// Try to match with some scene in database
		// Do this in parallel
		Scene firstQueryScene = new Scene(queryPath, queryFile, queryScenes.get(0));
		Scene targetScene;
		SceneChecker sceneChecker;
		List<Future<Double>> resultList = new ArrayList<Future<Double>>();
		Future<Double> result;
		
		for (Integer[] sceneIndices : dataScenes) {
			targetScene = new Scene(dataPath, dataFile, sceneIndices);
			sceneChecker = new SceneChecker(targetScene, firstQueryScene);
			result = sceneCheckExecutor.submit(sceneChecker);
			resultList.add(result);
		}

		sceneCheckExecutor.shutdown();
		
//		for (int[] e : dataScenes) {
//			System.out.println(
//					
//					Arrays.toString(e)
//					
//					);
//			
//		}
		
	}
	
	
	/**
	 * @return the queryVideoFile
	 */
	public String getQueryVideoFile() {
		return queryVideoFile;
	}

	/**
	 * @param queryVideoFile the queryVideoFile to set
	 */
	public void setQueryVideoFile(String queryVideoFile) {
		this.queryVideoFile = queryVideoFile;
	}
	
	public VideoComparator(String queryFile) {
		this.queryVideoFile = queryFile;
	}

	
	
	
	
	
	
}
