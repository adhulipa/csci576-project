package edu.usc.csci576.mediaqueries.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.spi.SyncResolver;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import edu.usc.csci576.mediaqueries.data.DataLoader;
import edu.usc.csci576.mediaqueries.model.Scene;
import edu.usc.csci576.mediaqueries.model.SceneDetector;
import edu.usc.csci576.mediaqueries.parallel.SCResultType;
import edu.usc.csci576.mediaqueries.parallel.SceneChecker;

public class VideoComparator implements Callable<VideoCompareResult> {

	private static final int NUM_QUERY_FRAMES = 150;
	private HashMap<String, List<Integer[]>> sceneMap = DataLoader.loadScenes();

	private static String queryVideo;
	private static String queryVideoDir;
	
	private static String[] databaseVideos;
	private static String databaseDir;

	/**
	 * @param queryVideo
	 * queryVideo should be of the form "query/first/first"
	 * 
	 * @param databaseDirString
	 * databaseDirString should be of form "database/"
	 * 
	 * @param databaseVideoNames
	 * expected form is String[]{"StarCraft","traffic","flowers"}
	 */
	public VideoComparator(String databaseDirString, String[] databaseVideoNames, String queryVideoString) {
		databaseDir = databaseDirString;
		queryVideo = queryVideoString;
		databaseVideos = databaseVideoNames;
	}

	@Override
	public VideoCompareResult call() throws Exception {
		
		// Setup
		VideoCompareResult videoCompareResult = null;
		ExecutorService executor = Executors
				.newFixedThreadPool(databaseVideos.length);
		CompletionService<Pair<String, Double>> videoComparatorService = 
				new ExecutorCompletionService<Pair<String, Double>>(
				executor);
		int remainingFutures = databaseVideos.length;
		Map<String, Double> result = new HashMap<String, Double>();
		List<Future<Pair<String, Double>>> matchResults = new ArrayList<>();
		Future<Pair<String, Double>> future; 
		
		// For a thread for each video comparator
		for (String video : databaseVideos) {
			SingleVideoComparator task = new SingleVideoComparator(video);
			future = videoComparatorService.submit(task);
			matchResults.add(future);
		}
		
		// fetch results
		while (remainingFutures > 0) {
			future = videoComparatorService.take();
			remainingFutures--;
			
			// get the result -- Double representing matchPercent
			Pair<String, Double> retval = future.get();
			result.put(retval.getLeft(), retval.getRight());
		}
		
		// finish and return
		videoCompareResult = new VideoCompareResult();
		videoCompareResult.scoresMap = result;
		executor.shutdown();
		
		return videoCompareResult;
	}

	private class SingleVideoComparator implements Callable<Pair<String,Double>> {

		private static final int SCENE_CHECKER_THREADS = 3;
		private String databaseVideoName;

		public SingleVideoComparator(String databaseVideo) {
			this.databaseVideoName = databaseVideo;
			
		}

		@Override
		public Pair<String, Double> call() throws Exception {
			
			if (databaseVideos == null) {
				databaseVideos = new String[]{"StarCraft","traffic","flowers"};
			}
			if (queryVideoDir == null) {
				queryVideoDir = "query/Q4";
			}
			if (queryVideo == null) {
				queryVideo = "Q4_";
			}
			
			System.out.println("Started comparator with " + databaseVideoName);

			ExecutorService sceneCheckExecutor = Executors
					.newFixedThreadPool(SCENE_CHECKER_THREADS);

			List<Integer[]> queryScenes = SceneDetector.getScenes(queryVideoDir,
					queryVideo, NUM_QUERY_FRAMES);

			List<Integer[]> dataScenes = sceneMap.get(databaseVideoName);

			/*
			 * Idea: get first scene fo query Try to match with some scene in
			 * database Do this in parallel Stage 1:-
			 */
			Scene firstQueryScene = new Scene(queryVideoDir, queryVideo,
					queryScenes.get(0), Scene.FIRST_SCENE);
			Scene targetScene;
			SceneChecker sceneChecker;
			List<Future<SCResultType>> resultList = new ArrayList<Future<SCResultType>>();
			Future<SCResultType> result;
			Integer[] sceneIndices;

			for (int i = 0; i < dataScenes.size(); i++) {
				sceneIndices = dataScenes.get(i);
				targetScene = new Scene(databaseDir, databaseVideoName, sceneIndices, i);
				sceneChecker = new SceneChecker(targetScene, firstQueryScene,
						SceneChecker.COMPARE_FIRST_TO_ONE);
				result = sceneCheckExecutor.submit(sceneChecker);
				resultList.add(result);
			}

			/*
			 * Intermediate stage Find best scene
			 */
			// TODO: Possible thread erros in this code
			// Think about thread safe data structs or practices
			Queue<SCResultType> resultsHeap = new PriorityBlockingQueue<SCResultType>();
			for (Future<SCResultType> each : resultList) {
				SCResultType scRes = each.get();
				resultsHeap.offer(scRes);
			}
			SCResultType bestSceneResult = resultsHeap.poll();
			Scene bestMatchedScene = bestSceneResult.getTargetScene();
			int bestMacthedFrameIdxInScene = bestSceneResult
					.getBestMatchedFrameIdx();

			System.out.println(bestMatchedScene.getBeginIdx() + "-"
					+ bestMatchedScene.getEndIdx() + " ---- best scene in " + databaseVideoName);

			/*
			 * Stage 2 Compare all scenes from bestmatched scene to end of query
			 * video. Return overall match Take each tgt scene compare frmae by
			 * frame with correpsonding query scence. Store result comp value
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

			while (numComp < maxComps && qidx < numQuerScenes
					&& didx < numDataScenes) {
				Scene qs = new Scene(queryVideoDir, queryVideo,
						queryScenes.get(qidx), qidx);
				Scene ds = new Scene(databaseDir, databaseVideoName, dataScenes.get(didx),
						didx);

				sceneChecker = new SceneChecker(ds, qs,
						SceneChecker.COMPARE_IN_ORDER,
						bestMacthedFrameIdxInScene);

				result = sceneCheckExecutor.submit(sceneChecker);
				System.out.println("submitted comp for q[" + qs.getBeginIdx()
						+ "-" + qs.getEndIdx() + "]" + "ds[" + ds.getBeginIdx()
						+ "-" + ds.getEndIdx() + "]");
				resultList.add(result);

				qidx++;
				didx++;
				numComp++;
			}

			/*
			 * Fetch the results now
			 */

			Double totalMatchPercent = 0.0;
			// sceneCheckExecutor.awaitTermination(Long.MAX_VALUE,
			// TimeUnit.NANOSECONDS);

			for (int i = 0; i < resultList.size(); i++) {
				SCResultType scRes = resultList.get(i).get();

				// synchronized(sceneCheckExecutor) {
				totalMatchPercent += scRes.getMatchPercent();
				// }
			}
			totalMatchPercent /= resultList.size();

			System.out.println("total match of video " + databaseVideoName + " to query "
					+ totalMatchPercent);

			sceneCheckExecutor.shutdown();
			
			
			return Pair.of(databaseVideoName, totalMatchPercent);
		}

	}


	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		System.out.println("Started comparator...");

		
		
		
		
		System.out.println("DONE!!");
		
		System.exit(1);
		
		ExecutorService sceneCheckExecutor = Executors.newFixedThreadPool(5);
		// sceneCheckExecutor = Executors.newSingleThreadExecutor();

		// query vide stuff
		String queryPath = "query/Q3";
		String queryName = "Q3_";

		// queryPath = "query/first";
		// queryFile = "first";

		List<Integer[]> queryScenes = SceneDetector.getScenes(queryPath,
				queryName, 150);

		// database stuff
		String dataPath = "database/";
		String dataName = "traffic";
		HashMap<String, List<Integer[]>> sceneMap = DataLoader.loadScenes();
		List<Integer[]> dataScenes = sceneMap.get(dataName);

		/*
		 * Idea: get first scene fo query Try to match with some scene in
		 * database Do this in parallel Stage 1:-
		 */
		Scene firstQueryScene = new Scene(queryPath, queryName,
				queryScenes.get(0), Scene.FIRST_SCENE);
		Scene targetScene;
		SceneChecker sceneChecker;
		List<Future<SCResultType>> resultList = new ArrayList<Future<SCResultType>>();
		Future<SCResultType> result;
		Integer[] sceneIndices;

		for (int i = 0; i < dataScenes.size(); i++) {
			sceneIndices = dataScenes.get(i);
			targetScene = new Scene(dataPath, dataName, sceneIndices, i);
			sceneChecker = new SceneChecker(targetScene, firstQueryScene,
					SceneChecker.COMPARE_FIRST_TO_ONE);
			result = sceneCheckExecutor.submit(sceneChecker);
			resultList.add(result);
		}

		/*
		 * Intermediate stage Find best scene
		 */
		// TODO: Possible thread erros in this code
		// Think about thread safe data structs or practices
		Queue<SCResultType> resultsHeap = new PriorityBlockingQueue<SCResultType>();
		for (Future<SCResultType> each : resultList) {
			SCResultType scRes = each.get();
			resultsHeap.offer(scRes);
		}
		SCResultType bestSceneResult = resultsHeap.poll();
		Scene bestMatchedScene = bestSceneResult.getTargetScene();
		int bestMacthedFrameIdxInScene = bestSceneResult
				.getBestMatchedFrameIdx();

		System.out.println(bestMatchedScene.getBeginIdx() + "-"
				+ bestMatchedScene.getEndIdx() + " ---- best scene");

		/*
		 * Stage 2 Compare all scenes from bestmatched scene to end of query
		 * video. Return overall match Take each tgt scene compare frmae by
		 * frame with correpsonding query scence. Store result comp value
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

		while (numComp < maxComps && qidx < numQuerScenes
				&& didx < numDataScenes) {
			Scene qs = new Scene(queryPath, queryName, queryScenes.get(qidx),
					qidx);
			Scene ds = new Scene(dataPath, dataName, dataScenes.get(didx), didx);

			sceneChecker = new SceneChecker(ds, qs,
					SceneChecker.COMPARE_IN_ORDER, bestMacthedFrameIdxInScene);

			result = sceneCheckExecutor.submit(sceneChecker);
			System.out.println("submitted comp for q[" + qs.getBeginIdx() + "-"
					+ qs.getEndIdx() + "]" + "ds[" + ds.getBeginIdx() + "-"
					+ ds.getEndIdx() + "]");
			resultList.add(result);

			qidx++;
			didx++;
			numComp++;
		}

		/*
		 * Fetch the results now
		 */

		Double totalMatchPercent = 0.0;
		// sceneCheckExecutor.awaitTermination(Long.MAX_VALUE,
		// TimeUnit.NANOSECONDS);

		for (int i = 0; i < resultList.size(); i++) {
			SCResultType scRes = resultList.get(i).get();

			// synchronized(sceneCheckExecutor) {
			totalMatchPercent += scRes.getMatchPercent();
			// }
		}
		totalMatchPercent /= resultList.size();

		System.out
				.println("total match of video to query " + totalMatchPercent);

		sceneCheckExecutor.shutdown();

	}

	/**
	 * @return the queryVideoFile
	 */
	public String getQueryVideoFile() {
		return queryVideo;
	}

	/**
	 * @param queryVideoFile
	 *            the queryVideoFile to set
	 */
	public void setQueryVideoFile(String queryVideoFile) {
		this.queryVideo = queryVideoFile;
	}

}
