package edu.usc.csci576.mediaqueries.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Core;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import edu.usc.csci576.mediaqueries.model.AudioData;
import edu.usc.csci576.mediaqueries.parallel.AudioChecker;

public class AudioComparator
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ExecutorService audioCheckExecutor = Executors.newFixedThreadPool(1);
		
		String[] dataset = { "starcraft", "flowers", "interview", "movie",
				"sports", "musicvideo", "traffic" };
		
		String queryWavPath = "queries/Not From Searching Content/HQ4/HQ4.wav"; 

		ArrayList<Future<AudioData>> scores = new ArrayList<Future<AudioData>>();
		for(String item : dataset)
		{
			String itemPath = String.format("database/%s/%s.wav", item, item);
			AudioChecker audioChecker = new AudioChecker(itemPath, queryWavPath);
			Future<AudioData> score = audioCheckExecutor.submit(audioChecker);
			scores.add(score);
		}
		
		for (int i = 0; i < scores.size(); i++)
		{
			try
			{
				Future<AudioData> audioData = scores.get(i);
				FingerprintSimilarity fs = audioData.get().getFs();
				String dataWav = audioData.get().getWavFilePath();
				
				System.out.println(fs.getScore() + " " + dataWav);
			} catch (InterruptedException | ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		audioCheckExecutor.shutdown();
	}
}
