package edu.usc.csci576.mediaqueries.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import edu.usc.csci576.mediaqueries.data.DataLoader;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.Scene;
import edu.usc.csci576.mediaqueries.model.SceneDetector;
import edu.usc.csci576.mediaqueries.parallel.FCResultType;
import edu.usc.csci576.mediaqueries.parallel.SCResultType;
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
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("Started comparator...");
		
		ExecutorService sceneCheckExecutor = Executors.newFixedThreadPool(150);
		//sceneCheckExecutor = Executors.newSingleThreadExecutor();
		
		// query vide stuff
		String queryPath = "query/Q4";
		String queryFile = "Q4_";
		
		//queryPath = "query/first";
		//queryFile = "first";
		
		List<Integer[]> queryScenes = SceneDetector.getScenes(queryPath, queryFile, 150);
		
		// database stuff
		String dataPath = "database/";
		String dataFile = "StarCraft";
		HashMap<String, List<Integer[]>> sceneMap = DataLoader.loadScenes();
		List<Integer[]> dataScenes = sceneMap.get(dataFile);
		
		/* Idea: get first scene fo query
		 * Try to match with some scene in database
		 * Do this in parallel
		 * Stage 1:-
		 */
		Scene firstQueryScene = new Scene(queryPath, queryFile, queryScenes.get(0), Scene.FIRST_SCENE);
		Scene targetScene;
		SceneChecker sceneChecker;
		List<Future<SCResultType>> resultList = new ArrayList<Future<SCResultType>>();
		Future<SCResultType> result;
		Integer[] sceneIndices;
		
		for (int i = 0; i < dataScenes.size(); i++) {
			sceneIndices = dataScenes.get(i); 
			targetScene = new Scene(dataPath, dataFile, sceneIndices, i);
			sceneChecker = new SceneChecker(targetScene, firstQueryScene, SceneChecker.COMPARE_FIRST_TO_ONE);
			result = sceneCheckExecutor.submit(sceneChecker);
			resultList.add(result);
		}
		
		/*
		 * Intermediate stage
		 * Find best scene
		 */
		
//		SCResultType comprator = new SCResultType();
//		PriorityQueue<SCResultType> resultsHeap= new PriorityQueue<>(comprator);
//		for (Future<SCResultType> each : resultList) {
//			resultsHeap.offer(each.get());
//		}
//		
//		resultsHeap.poll().getTargetScene();
		
		/* 
		 * Stage 2
		 */

		sceneCheckExecutor.shutdown();

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
