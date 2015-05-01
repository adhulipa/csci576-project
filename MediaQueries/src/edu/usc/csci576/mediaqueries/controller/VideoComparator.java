package edu.usc.csci576.mediaqueries.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edu.usc.csci576.mediaqueries.data.DataLoader;
import edu.usc.csci576.mediaqueries.model.SceneDetector;


public class VideoComparator implements Runnable {

	private String queryVideoFile;
	
	public void run() {
		// TODO: Complete comparator logic
		
		
		// AD: Idea
		/*
		 * Step1 : iterate over dataset
		 * Step2 : for each item get scenes & get scens for query
		 * Step3 : Spawn thread for each frame in scene
		 * Step4 : Compare query frame with item frame
		 * 			using compareHist with CV_CORRELL
		 *  We can use compareHist() to get  percent match of frame.
		 *  This can be intelligently used to compute macth
		 */
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
	
	
	
	public static void main(String[] args) {
		
		String query = "query/first";
		HashMap<String, List<int[]>> map = DataLoader.loadScenes();
		int[] arr = new int[]{map.get("StarCraft").size()};
		List<int[]> scenes = map.get("StarCraft");
		
		List<byte[][]> bgrHist = DataLoader.deserializeRGBArrays("StarCraftRGB.hist");
		System.out.println(Arrays.deepToString(bgrHist.get(0)));
		
		
		for (int[] e : scenes) {
			System.out.println(
					
					Arrays.toString(e)
					
					);
			
		}
		
	}
	
	
	
	
	
	
}
