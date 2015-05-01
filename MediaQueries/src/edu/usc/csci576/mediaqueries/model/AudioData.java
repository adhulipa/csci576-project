package edu.usc.csci576.mediaqueries.model;

import com.musicg.fingerprint.FingerprintSimilarity;

public class AudioData
{
	private String wavFilePath;
	private FingerprintSimilarity fs;
	
	public AudioData(String wavFilePath, FingerprintSimilarity fs)
	{
		this.setFs(fs);
		this.setWavFilePath(wavFilePath);
	}

	/**
	 * @return the fs
	 */
	public FingerprintSimilarity getFs()
	{
		return fs;
	}

	/**
	 * @param fs the fs to set
	 */
	public void setFs(FingerprintSimilarity fs)
	{
		this.fs = fs;
	}

	/**
	 * @return the wavFilePath
	 */
	public String getWavFilePath()
	{
		return wavFilePath;
	}

	/**
	 * @param wavFilePath the wavFilePath to set
	 */
	public void setWavFilePath(String wavFilePath)
	{
		this.wavFilePath = wavFilePath;
	}
}
