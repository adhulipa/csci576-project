package edu.usc.csci576.mediaqueries.model;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Scene {
	
	/*
	 *  Video full filepath -- 
	 *  "database/flowers/flowers001.rgb"
	 *  <---------------><------------->
	 *      ^filepath^     ^filename^
	*/
	
	protected String videoPath;
	protected String videoName;
	public static int FIRST_SCENE;
	private int beginIdx;
	private int endIdx;
	
	private int scenePosition;
	
	protected Scene(){}
	
	public Scene(String videoPath, String videoName, Integer[] indices, int scenePositionInListOfScenes) {
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		this.setVideoPath(videoPath);
		this.setVideoName(videoName);
		
		this.scenePosition = scenePositionInListOfScenes;
		
		setBeginIdx(indices[0]);
		setEndIdx(indices[1]);
		
//		// Temp testing code
//		int frame = (beginIdx + endIdx) / 2;
//		String fullPath = String.format("%s/%s%03d.rgb", 
//				videoPath, videoName, frame);
//		rgbHistsMats = RGBHistogram.getRGBMat(fullPath, 352, 288);
	}
	
	private void getFrame(int index) {
		
	}

	/**
	 * @return the videoPath
	 */
	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * @param videoPath the videoPath to set
	 */
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	/**
	 * @return the videoName
	 */
	public String getVideoName() {
		return videoName;
	}

	/**
	 * @param videoName the videoName to set
	 */
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	/**
	 * @return the endIdx
	 */
	public int getEndIdx() {
		return endIdx;
	}

	/**
	 * @param endIdx the endIdx to set
	 */
	public void setEndIdx(int endIdx) {
		this.endIdx = endIdx;
	}

	/**
	 * @return the beginIdx
	 */
	public int getBeginIdx() {
		return beginIdx;
	}

	/**
	 * @param beginIdx the beginIdx to set
	 */
	public void setBeginIdx(int beginIdx) {
		this.beginIdx = beginIdx;
	}

	public String getFullPath() {
		
		return videoPath + videoName;
	}
}
