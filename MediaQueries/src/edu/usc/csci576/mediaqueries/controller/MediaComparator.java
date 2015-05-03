package edu.usc.csci576.mediaqueries.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MediaComparator {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		
		String databaseDirString = "database/";
		String[] databaseVideoNames = new String[]{"StarCraft", "flowers", "traffic", "musicvideo", "movie", "interview", "sports" };
		String queryDir = "query/Q4";
		String queryVideoString = "Q4_";
		
		VideoComparator videoTask = new VideoComparator(databaseDirString,
				databaseVideoNames, queryDir, queryVideoString);
		
		ExecutorService worker = Executors.newSingleThreadExecutor();
		Future<VideoCompareResult> result = worker.submit(videoTask);
		
		VideoCompareResult compare = result.get();
		
		System.out.println(compare.scoresMap);
	}
}
