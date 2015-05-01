package edu.usc.csci576.mediaqueries.parallel;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.Pair;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import edu.usc.csci576.mediaqueries.model.AudioData;

public class AudioChecker implements Callable<AudioData>
{
	private String data, query;
	
	public AudioChecker(String data, String query)
	{
		this.data = data;
		this.query = query;
	}

	@Override
	public AudioData call() throws Exception
	{
		Wave dataWave = new Wave(this.data);
		Wave queryWave = new Wave(this.query);
		FingerprintSimilarity fs = dataWave.getFingerprintSimilarity(queryWave);
		//getMostSimilarFramePosition
//		System.out.println(fs.getScore());
//		System.out.println(fs.getMostSimilarFramePosition());
		return new AudioData(this.data, fs);
		
	}
}
