package edu.usc.csci576.mediaqueries.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Core;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import edu.usc.csci576.mediaqueries.model.AudioComparatorResult;
import edu.usc.csci576.mediaqueries.model.AudioData;
import edu.usc.csci576.mediaqueries.parallel.AudioChecker;

public class AudioComparator
{

	public static ArrayList<AudioComparatorResult> getSimilarAudios(
			String queryWavPath)
	{
		ExecutorService audioCheckExecutor = Executors.newFixedThreadPool(1);
		
		String[] dataset = { "StarCraft", "flowers", "interview", "movie",
				"sports", "musicvideo", "traffic" };

		ArrayList<Future<AudioData>> scores = new ArrayList<Future<AudioData>>();
		for (String item : dataset)
		{
			String itemPath = String.format("database/%s/%s.wav", item, item);
			AudioChecker audioChecker = new AudioChecker(itemPath, queryWavPath);
			Future<AudioData> score = audioCheckExecutor.submit(audioChecker);
			scores.add(score);
		}

		ArrayList<AudioComparatorResult> results = new ArrayList<AudioComparatorResult>();

		for (int i = 0; i < scores.size(); i++)
		{
			try
			{
				Future<AudioData> audioData = scores.get(i);
				FingerprintSimilarity fs = audioData.get().getFs();
				String dataWav = audioData.get().getWavFilePath();

//				System.out.println(fs.getSimilarity() + " " + dataWav);

				results.add(new AudioComparatorResult(dataWav, fs.getSimilarity(),
						fs.getScore(), fs.getsetMostSimilarTimePosition(), fs
								.getMostSimilarFramePosition()));
			} catch (InterruptedException | ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		audioCheckExecutor.shutdown();

		return results;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ArrayList<AudioComparatorResult> audioResult = getSimilarAudios("query/HQ4/HQ4.wav");
		Collections.sort(audioResult, new AudioComparatorResult());
		for(AudioComparatorResult res : audioResult)
		{
			System.out.println(res.getSimilarity() + " " + res.getAudioFileName());
		}
	}
}
