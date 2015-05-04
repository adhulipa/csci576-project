package edu.usc.csci576.mediaqueries.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import edu.usc.csci576.mediaqueries.parallel.SCResultType;

import edu.usc.csci576.mediaqueries.model.AudioComparatorResult;

public class MediaComparator {
	
public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
		String databaseDirString = "database/";
		String[] databaseVideoNames = new String[]{"StarCraft", "flowers", "traffic", "musicvideo", "movie", "interview", "sports" };
		String queryDir = "query/Q5";
		String queryVideoString = "Q5_";
		
		VideoComparator videoTask = new VideoComparator(databaseDirString,
				databaseVideoNames, queryDir, queryVideoString);
		
		ExecutorService worker = Executors.newSingleThreadExecutor();
		Future<VideoCompareResult> result = worker.submit(videoTask);
		
		VideoCompareResult compare = result.get();
		
		System.out.println(compare.scoresMap);
		System.out.println(
				"bestFrame " + compare.bestMatchedSceneResults

				
				);
		worker.shutdown();
		
		return;
	}

public VideoCompareResult run(String queryDir, String queryVideoString, String databaseDir, 
		String[] databaseVideoNames, String databaseDirString, 
		String queryAudioDir,
		String queryAudioName) {
		
		
//		String databaseDirString = "database/";
//		String[] databaseVideoNames = new String[]{"StarCraft", "flowers", "traffic", "musicvideo", "movie", "interview", "sports" };
//		String queryDir = "query/Q5";
//		String queryVideoString = "Q5_";
//		
		VideoComparator videoTask = new VideoComparator(databaseDirString,
				databaseVideoNames, queryDir, queryVideoString);
		
		ExecutorService worker = Executors.newFixedThreadPool(1);
		Future<VideoCompareResult> result = worker.submit(videoTask);
		
//		List<AudioComparatorResult> audioResults = 
//				AudioComparator.getSimilarAudios(queryDir 
//						+ "/" + queryDir.substring(6) + ".wav");
//		
		List<AudioComparatorResult> audioResults = 
				AudioComparator.getSimilarAudios(
						queryAudioDir+"/"+queryAudioName);
		
		VideoCompareResult compare = null;
		try {
		
			compare = result.get();
		
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(compare.scoresMap);
		System.out.println(
				"bestFrame " + compare.getBestMatchedScene()
				);
		worker.shutdown();
		
		return compare;
	}
}
