package edu.usc.csci576.mediaqueries.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.spi.SyncResolver;

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
		
		ExecutorService sceneCheckExecutor = Executors.newFixedThreadPool(1);
		//sceneCheckExecutor = Executors.newSingleThreadExecutor();
		
		// query vide stuff
		String queryPath = "query/Q4";
		String queryName = "Q4_";
		
		//queryPath = "query/first";
		//queryFile = "first";
		
		List<Integer[]> queryScenes = SceneDetector.getScenes(queryPath, queryName, 150);
		
		// database stuff
		String dataPath = "database/";
		String dataName = "StarCraft";
		HashMap<String, List<Integer[]>> sceneMap = DataLoader.loadScenes();
		List<Integer[]> dataScenes = sceneMap.get(dataName);
		
		/* Idea: get first scene fo query
		 * Try to match with some scene in database
		 * Do this in parallel
		 * Stage 1:-
		 */
		Scene firstQueryScene = new Scene(queryPath, queryName, queryScenes.get(0), Scene.FIRST_SCENE);
		Scene targetScene;
		SceneChecker sceneChecker;
		List<Future<SCResultType>> resultList = new ArrayList<Future<SCResultType>>();
		Future<SCResultType> result;
		Integer[] sceneIndices;
		
		for (int i = 0; i < dataScenes.size(); i++) {
			sceneIndices = dataScenes.get(i); 
			targetScene = new Scene(dataPath, dataName, sceneIndices, i);
			sceneChecker = new SceneChecker(targetScene, firstQueryScene, SceneChecker.COMPARE_FIRST_TO_ONE);
			result = sceneCheckExecutor.submit(sceneChecker);
			resultList.add(result);
		}
		
		/*
		 * Intermediate stage
		 * Find best scene
		 */
		// TODO: Possible thread erros in this code
		// Think about thread safe data structs or practices
		Queue<SCResultType> resultsHeap= new PriorityBlockingQueue<SCResultType>();
		for (Future<SCResultType> each : resultList) {
			SCResultType scRes = each.get();	
			resultsHeap.offer(scRes);
		}
		
		Scene bestMatchedScene = resultsHeap.poll().getTargetScene();
		
		System.out.println(bestMatchedScene.getBeginIdx() + "-" + bestMatchedScene.getEndIdx() + " ---- best scene" );
		
		/* 
		 * Stage 2
		 * Compare all scenes from bestmatched scene
		 * to end of query video.
		 * Return overall match
		* Take each tgt scene
		 * compare frmae by frame with correpsonding query scence.
		 * Store result comp value
		 */
		int bestMatchedSceneIdx = bestMatchedScene.scenePosition;
		int numDataScenes = dataScenes.size();
		int numQuerScenes = queryScenes.size();
		int maxComps = Math.min(numDataScenes, numQuerScenes);
		int numComp = 0;
		
		int qidx = 0;
		int didx = bestMatchedSceneIdx;
		
		resultList = new ArrayList<Future<SCResultType>>();
		result = null;
		System.out.println("Running all scene comparisons");
		
		sceneCheckExecutor.shutdown();
		sceneCheckExecutor = Executors.newFixedThreadPool(1);
		
		while (numComp < maxComps && qidx < numQuerScenes && didx < numDataScenes) {
			Scene qs = new Scene(queryPath, queryName, queryScenes.get(qidx), qidx);
			Scene ds = new Scene(dataPath, dataName, dataScenes.get(didx), didx);
			
			sceneChecker = new SceneChecker(ds, qs, SceneChecker.COMPARE_IN_ORDER);
			
			result = sceneCheckExecutor.submit(sceneChecker);
			System.out.println("submitted comp for q[" + qs.getBeginIdx()+"-"+qs.getEndIdx()+"]"
					+ "ds["+ds.getBeginIdx()+"-"+ds.getEndIdx()+"]");
			resultList.add(result);
			
			qidx++;
			didx++;
			numComp++;
		}
		
		/* 
		 * Fetch the results now
		 * 
		 */
		
		Double totalMatchPercent = 0.0;
		//sceneCheckExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		for (int i = 0; i < resultList.size(); i++) {
			SCResultType scRes = resultList.get(i).get();
			
//			synchronized(sceneCheckExecutor) {
				totalMatchPercent += scRes.getMatchPercent();
//			}
		}
		totalMatchPercent /= resultList.size();
		
		
		System.out.println("total match of video to query " + totalMatchPercent);
		
		
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
