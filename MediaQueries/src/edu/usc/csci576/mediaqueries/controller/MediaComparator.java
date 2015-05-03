package edu.usc.csci576.mediaqueries.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
		
		worker.shutdown();
		
		return;
	}

public VideoCompareResult run(String queryDir, String queryVideoString, String databaseDir, 
		String[] databaseVideoNames, String databaseDirString) {
		
		
//		String databaseDirString = "database/";
//		String[] databaseVideoNames = new String[]{"StarCraft", "flowers", "traffic", "musicvideo", "movie", "interview", "sports" };
//		String queryDir = "query/Q5";
//		String queryVideoString = "Q5_";
//		
		VideoComparator videoTask = new VideoComparator(databaseDirString,
				databaseVideoNames, queryDir, queryVideoString);
		
		ExecutorService worker = Executors.newSingleThreadExecutor();
		Future<VideoCompareResult> result = worker.submit(videoTask);
		
		VideoCompareResult compare = null;
		try {
		
			compare = result.get();
		
		
		
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(compare.scoresMap);
		
		worker.shutdown();
		
		return compare;
	}
}
