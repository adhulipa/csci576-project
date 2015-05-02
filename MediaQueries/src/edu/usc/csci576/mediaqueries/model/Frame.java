package edu.usc.csci576.mediaqueries.model;

import java.util.List;

import org.opencv.core.Mat;

public class Frame {

	public static final int WIDTH = 352;
	public static final int HEIGHT = 288;
	
	private String videoName;
	private String path;
	private int frameIdx;
	
	
	private List<Mat> rgbHist;
	
	
	public Frame(String prefix, String videoName, int frameIdx) {
		
		String path = prefix + "/" + videoName;
		this.videoName = videoName;
		this.path = String.format("%s%03d.rgb", path, frameIdx);
		this.setFrameIdx(frameIdx);
		// String filePathString = String.
		// format("%s/%s%03d.rgb", filepath, filename, frame);

	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the frameIdx
	 */
	public int getFrameIdx() {
		return frameIdx;
	}

	/**
	 * @param frameIdx the frameIdx to set
	 */
	public void setFrameIdx(int frameIdx) {
		this.frameIdx = frameIdx;
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
	 * @return the rgbHist
	 */
	public List<Mat> getRgbHist() {
		return rgbHist;
	}

	/**
	 * @param rgbHist the rgbHist to set
	 */
	public void setRgbHist(List<Mat> rgbHist) {
		this.rgbHist = rgbHist;
	}

}
