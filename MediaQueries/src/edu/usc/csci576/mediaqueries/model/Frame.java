package edu.usc.csci576.mediaqueries.model;

public class Frame {
	
	private String path;

	public Frame(String path, int frameIdx) {
				
		this.path = String.format("%s%03d.rgb", path, frameIdx);
		
//		String filePathString = String.
//				format("%s/%s%03d.rgb", filepath, filename, frame);

	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	


}
