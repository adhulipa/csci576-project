package edu.usc.csci576.mediaqueries.model;

import java.util.Comparator;

public class AudioComparatorResult implements Comparator<AudioComparatorResult>
{
	private String audioFileName;
	private float similarity, score, mostSimilarTimePosition, mostSimilarFramePosition;
	
	public AudioComparatorResult()
	{
		// TODO Auto-generated constructor stub
	}
	
	public AudioComparatorResult(String audioFileName, float similarity, float score, float mostSimilarTimePosition, float mostSimilarFramePosition)
	{
		this.setAudioFileName(audioFileName);
		this.setSimilarity(similarity);
		this.setScore(score);
		this.setMostSimilarFramePosition(mostSimilarFramePosition);
		this.setMostSimilarTimePosition(mostSimilarTimePosition);
	}

	/**
	 * @return the audioFileName
	 */
	public String getAudioFileName()
	{
		return audioFileName;
	}

	/**
	 * @param audioFileName the audioFileName to set
	 */
	public void setAudioFileName(String audioFileName)
	{
		this.audioFileName = audioFileName;
	}

	/**
	 * @return the similarity
	 */
	public float getSimilarity()
	{
		return similarity;
	}

	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(float similarity)
	{
		this.similarity = similarity;
	}

	/**
	 * @return the mostSimilarTimePosition
	 */
	public float getMostSimilarTimePosition()
	{
		return mostSimilarTimePosition;
	}

	/**
	 * @param mostSimilarTimePosition the mostSimilarTimePosition to set
	 */
	public void setMostSimilarTimePosition(float mostSimilarTimePosition)
	{
		this.mostSimilarTimePosition = mostSimilarTimePosition;
	}

	/**
	 * @return the score
	 */
	public float getScore()
	{
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(float score)
	{
		this.score = score;
	}

	/**
	 * @return the mostSimilarFramePosition
	 */
	public float getMostSimilarFramePosition()
	{
		return mostSimilarFramePosition;
	}

	/**
	 * @param mostSimilarFramePosition the mostSimilarFramePosition to set
	 */
	public void setMostSimilarFramePosition(float mostSimilarFramePosition)
	{
		this.mostSimilarFramePosition = mostSimilarFramePosition;
	}

	@Override
	public int compare(AudioComparatorResult o1, AudioComparatorResult o2)
	{
		if(o1.getSimilarity() == o2.getSimilarity())
			return 0;
		else if(o1.getSimilarity() > o2.getSimilarity())
			return -1;
		else
			return 1;
//		return (int) (o2.getSimilarity() - o1.getSimilarity());
	}
	
	
	
}
