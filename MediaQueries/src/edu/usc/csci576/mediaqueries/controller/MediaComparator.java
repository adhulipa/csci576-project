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
		
		ExecutorService worker = Executors.newFixedThreadPool(1);
		Future<VideoCompareResult> result = worker.submit(videoTask);
		
		String queryAudioDir;
		String queryAudioName;
		
		//		List<AudioComparatorResult> audioResults = 
//				AudioComparator.getSimilarAudios(queryDir 
//						+ "/" + queryDir.substring(6) + ".wav");
//		
		List<AudioComparatorResult> audioResults = 
				AudioComparator.getSimilarAudios(
						"query/Q5/Q5.wav");
		
		
		
		VideoCompareResult comparisonResults = null;
		try {
		
			comparisonResults = result.get();
			
			
			// Update comparisonResults
			// with audio results
			for (AudioComparatorResult audioResult : audioResults) {
				String key = audioResult.getAudioFileName().split("/")[1];
				float audioFramePos = audioResult.getMostSimilarFramePosition();
				float audioTimePos = audioResult.getMostSimilarTimePosition();
				
				comparisonResults.bestMatchedSceneResults.get(key)
				.setAudioMostSimilarFramePosition(audioFramePos);
				comparisonResults.bestMatchedSceneResults.get(key)
				.setAudioMostSimilarFramePosition(audioTimePos);
				
				//ystem.out.println(key);
			}
			
			
		
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(comparisonResults.scoresMap);
		System.out.println(
				"bestFrame " + comparisonResults.getBestMatchedScene()
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
		
		
		
		VideoCompareResult comparisonResults = null;
		try {
		
			comparisonResults = result.get();
			
			
			// Update comparisonResults
			// with audio results
			for (AudioComparatorResult audioResult : audioResults) {
				String key = audioResult.getAudioFileName().split("/")[1];
				float audioFramePos = audioResult.getMostSimilarFramePosition();
				float audioTimePos = audioResult.getMostSimilarTimePosition();
				
				comparisonResults.bestMatchedSceneResults.get(key)
				.setAudioMostSimilarFramePosition(audioFramePos);
				comparisonResults.bestMatchedSceneResults.get(key)
				.setAudioMostSimilarFramePosition(audioTimePos);
				
				comparisonResults.bestMatchedSceneResults.get(key)
				.setAudioScore(audioResult.getScore());
				
				comparisonResults.bestMatchedSceneResults.get(key)
				.setAudioSimilarity(audioResult.getSimilarity());
				
			}
			
			
		
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(comparisonResults.scoresMap);
		System.out.println(
				"bestFrame " + comparisonResults.getBestMatchedScene()
				);
		worker.shutdown();
		
		return comparisonResults;
	}
}
