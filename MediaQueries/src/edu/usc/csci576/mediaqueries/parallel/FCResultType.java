package edu.usc.csci576.mediaqueries.parallel;

import java.util.Comparator;

public class FCResultType implements Comparable<FCResultType>, Comparator<FCResultType> {
	
	private Integer targetFrameIdx;
	private Integer clipFrameIdx;
	private Double matchpercent;
	private String targetVideoName;
	
	
	public FCResultType(Integer targetFrameIdx, Integer clipFrameIdx,
			Double matchpercent, String targetVideoName) {
		
		this.targetFrameIdx = targetFrameIdx;;
		this.clipFrameIdx = clipFrameIdx;
		this.matchpercent = matchpercent;
		this.targetVideoName = targetVideoName;
			
	}
	
	public FCResultType() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the targetFrameIdx
	 */
	public Integer getTargetFrameIdx() {
		return targetFrameIdx;
	}
	/**
	 * @param targetFrameIdx the targetFrameIdx to set
	 */
	public void setTargetFrameIdx(Integer targetFrameIdx) {
		this.targetFrameIdx = targetFrameIdx;
	}
	/**
	 * @return the clipFrameIdx
	 */
	public Integer getClipFrameIdx() {
		return clipFrameIdx;
	}
	/**
	 * @param clipFrameIdx the clipFrameIdx to set
	 */
	public void setClipFrameIdx(Integer clipFrameIdx) {
		this.clipFrameIdx = clipFrameIdx;
	}
	/**
	 * @return the matchpercent
	 */
	public Double getMatchpercent() {
		return matchpercent;
	}
	/**
	 * @param matchpercent the matchpercent to set
	 */
	public void setMatchpercent(Double matchpercent) {
		this.matchpercent = matchpercent;
	}
	/**
	 * @return the targetVideoName
	 */
	public String getTargetVideoName() {
		return targetVideoName;
	}
	/**
	 * @param targetVideoName the targetVideoName to set
	 */
	public void setTargetVideoName(String targetVideoName) {
		this.targetVideoName = targetVideoName;
	}

	/**
	 * 
	 */
	public String toString() {
		return  getTargetVideoName() + "" +
				getTargetFrameIdx() + "" +
				 " clip" +
				getClipFrameIdx() + " " +
				String.format("%.2f", getMatchpercent()) + "%";
	}
	
	/** 
	 * Compares in reverse order
	 * i.e. higher matchpercent is ranked lower in queue
	 */
	@Override
	public int compareTo(FCResultType o) {
		
		if (this.matchpercent < o.matchpercent)
			return 1;
		if (this.matchpercent > o. matchpercent)
			return -1;
		return 0;
	}

	@Override
	public int compare(FCResultType o1, FCResultType o2) {
		return o1.compareTo(o2);
	}	
	

}
