package edu.usc.csci576.mediaqueries.model;

public class Frame {

	private String path;
	private int frameIdx;
	
	public Frame(String path, int frameIdx) {

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

}
