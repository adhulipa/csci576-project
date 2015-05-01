package edu.usc.csci576.mediaqueries.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

import org.apache.commons.lang3.ArrayUtils;
import org.opencv.core.*;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.*;

import com.musicg.wave.*;
import com.musicg.wave.extension.*;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.graphic.*;
import com.musicg.main.demo.FingerprintDemo;

public class AudioAnalyzer {

	public static List<Wave> loadOfflineData() {
		List<Wave> list = new ArrayList<Wave>();
		
		try {
			FileInputStream fis = new FileInputStream("AudioWaveList.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			list =  (List<Wave>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return null;
		}
		System.out.println("Deserialized..");
		return list;
		

	}
	
	public static void createOfflineDatabase() {
		String[] dataset = {"StarCraft", "flowers", "interview", 
				"movie", "sports", "musicvideo", "traffic"};
		
		List<Wave> audioDB = new ArrayList<Wave>();
		
		for (String file : dataset) {
			audioDB.add(new Wave("database/" + file + "/" + file + ".wav"));
		}
		
		try {
			FileOutputStream fos = new FileOutputStream("AudioWaveList.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(audioDB);
			oos.close();
			fos.close();
			System.out
					.printf("Serialized data is saved in AudioWaveList.ser") ;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		
		
	}
	
	public static void main_(String[] args) {
		String[] dataset = {"StarCraft", "flowers", "interview", 
				"movie", "sports", "musicvideo", "traffic"};
		
		List<Wave> audioDB = new ArrayList<Wave>();
		
		
		for (String file : dataset) {
			audioDB.add(new Wave("database/" + file + "/" + file + ".wav"));
		}
		
	}
	
	
	public static void main(String[] args) throws Exception {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Wave dwave = new Wave("database/musicvideo/musicvideo.wav");
		Wave qwave = new Wave("query/first/first.wav");
		Wave q2 = new Wave("database/StarCraft/StarCraft.wav");
		
		
		
		
//		FingerprintSimilarity fs = dwave.getFingerprintSimilarity(qwave);
//		System.out.println(fs.getScore());
//		System.out.println(fs.getMostSimilarFramePosition());
//		System.exit(1);
		

		System.out.println("wave.getSamplesAmp().len "
				+ dwave.getSampleAmplitudes().length);
		System.out.println(dwave.getSampleAmplitudes().length);

		Spectrogram dsg = dwave.getSpectrogram();
		Spectrogram qsg = qwave.getSpectrogram();
		
		
		System.out.println("dsg " + dsg.getAbsoluteSpectrogramData().length);
		System.out.println("qsg " + qsg.getAbsoluteSpectrogramData().length);
		
		
		List<List<Integer>> qpeaks = createConstellationmap(qsg);
		List<List<Integer>> dpeaks = createConstellationmap(dsg);
		
		PrintWriter dwriter = new PrintWriter("musicpeaks", "UTF-8");
		for (List<Integer> each : dpeaks) {
			Integer a[] = new Integer[each.size()];
			dwriter.println(Arrays.toString(each.toArray(a)));
		}
		
		PrintWriter qwriter = new PrintWriter("firstpeaks", "UTF-8");
		for (List<Integer> each : qpeaks) {
			Integer a[] = new Integer[each.size()];
			qwriter.println(Arrays.toString(each.toArray(a)));
		}
		
		qwriter.close();
		dwriter.close();
		
		
		WaveHeader h = dwave.getWaveHeader();

		System.out.println(h);
		AudioFormat f = new AudioFormat(h.getSampleRate(),
				h.getBitsPerSample(), h.getChannels(), true, false);

		// playBytes(wave.getBytes(), f);

		// Graphic render
		GraphicRender render = new GraphicRender();
		// render.setHorizontalMarker(1);
		// render.setVerticalMarker(1);
		render.renderWaveform(dwave, "offlineData/waveform.jpg");

		double[][] dsgNormData = dsg.getNormalizedSpectrogramData();
		double[][] dsgAbsData = dsg.getAbsoluteSpectrogramData();
		render.renderSpectrogramData(dsgAbsData,
				"offlineData/musicvideo/absSpectogram.jpg");
		render.renderSpectrogramData(dsgNormData,
				"offlineData/musicvideo/normSpectogram.jpg");

		double[][] qsgAbsData = qsg.getAbsoluteSpectrogramData();
		double[][] qsgNormData = qsg.getNormalizedSpectrogramData();
		render.renderSpectrogramData(qsgNormData,
				"offlineData/first/normSpectogram.jpg");
		render.renderSpectrogramData(qsgAbsData,
				"offlineData/first/absSpectogram.jpg");

		System.out.println("sg numFraex x FramePerSec = " + dsg.getNumFrames()
				+ " " + dsg.getFramesPerSecond());
		System.out.println(qsg.getUnitFrequency() + " "
				+ qsg.getNumFrequencyUnit());
		System.out.println(dsgAbsData.length + " " + dsgAbsData[0].length);
		System.out.println(qsgAbsData.length + " " + qsgAbsData[0].length);

		// compareSpectroData(qsg, sg);

		/*
		 * Spectrogram data = 2D-Double-array[time][frequency] = intensity
		 */

		System.exit(1);

		// for (int i =0; i < sgNormData.length; i++)
		// System.out.println(
		// (sgNormData[i].length)
		// );
		//
		System.out.println("DONE!");

	}

	private static void matchQueryAndData(Map<Integer, Point> queryConstMap,
			Map<Integer, Point> dataConstMap) {

		System.out.println(queryConstMap.size() + " " + dataConstMap.size());

		System.exit(1);
	}

	private static List<List<Integer>> createConstellationmap(Spectrogram spec)
			throws InterruptedException {
		
		double[][] data = spec.getNormalizedSpectrogramData();
		//double[][] data = spec.getAbsoluteSpectrogramData();
		
		List<List<Integer>> frameFreqPeaks = new ArrayList<List<Integer>>();
		
		double[] prevFrame;
		List<Integer> peaks = new ArrayList<Integer>();
		
		// do first one
		double[] nextFrame = data[1];
		
		for (int i = 0; i < data[0].length; i++) {
			if (data[0][i] > nextFrame[i]) {
				peaks.add(i);
			}
		}
		
		frameFreqPeaks.add(peaks);
		
		int ii = 1;
		double[] frame;
		while (ii <= data.length-2) {

			frame = data[ii];
			int prevIdx = ii - 1;
			int nextIdx = ii + 1;
			prevIdx = (prevIdx < 0) ? 0 : prevIdx;
			nextIdx = (prevIdx > data.length-1) ? data.length-1 : nextIdx;
			
			prevFrame = data[prevIdx];
			nextFrame = data[nextIdx];
			
			
			peaks = new ArrayList<Integer>();
			for (int i = 0; i < frame.length; i++) {
				if (frame[i] > prevFrame[i] && frame[i] > nextFrame[i] ) {
					
					// check lef and right neighbors within same array
					int leftIdx =i-1;
					int rightIdx = i+1;
					
					// check left
					if ((leftIdx >= 0)) {
						if ( frame[i] > frame[leftIdx]) {
							if (( rightIdx <= frame.length-1)) {
								if (frame[i] > frame[rightIdx]) {
									peaks.add(i);
								}
							}
						}
					}
					
				}
			}
			
			frameFreqPeaks.add(peaks);
			ii++;
		}
		
		// do the last one
		prevFrame = data[data.length-2];
		peaks = new ArrayList<>();
		for (int i = 0; i<data[data.length-1].length; i++) {
			if (data[data.length-1][i] > prevFrame[i]) {
				peaks.add(i);
			}
		}
		frameFreqPeaks.add(peaks);
		
		return frameFreqPeaks;

	}

	private static void compareSpectroData(Spectrogram qsg, Spectrogram dsg)
			throws FileNotFoundException, UnsupportedEncodingException {

		double[][] qnd = qsg.getNormalizedSpectrogramData();
		Mat qmat = new Mat(qsg.getFramesPerSecond(), qsg.getNumFrames(),
				CvType.CV_32F);
		for (int row = 0; row < qsg.getFramesPerSecond(); row++)
			qmat.put(row, 0, qnd[row]);

		double[][] dnd = dsg.getNormalizedSpectrogramData();
		Mat dmat = new Mat(dsg.getFramesPerSecond(), dsg.getNumFrames(),
				CvType.CV_32F);
		for (int row = 0; row < dsg.getFramesPerSecond(); row++)
			dmat.put(row, 0, dnd[row]);

		PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
		writer.println(dmat.dump());
		writer.close();

		int windowWidth = qmat.cols();

		Mat sub = new Mat();
		// for (int i = 0; i+windowWidth<dmat.cols(); i++){
		// sub = dmat.submat(0, dmat.rows(), i, i+windowWidth);
		// Mat diff = new Mat();
		// Core.compare(qmat, sub, diff, Core.CMP_EQ);
		//
		//
		// double d = Core.sumElems(diff).val[0];
		//
		// System.out.println(
		// d
		// );
		//
		// }

		//
		// // TODO: perform template matching on
		// // dmat & qmat
		// //
		// http://docs.opencv.org/doc/tutorials/imgproc/histograms/template_matching/template_matching.html
		//
		// Mat result = new Mat();
		// Imgproc.matchTemplate(dmat, qmat, result, Imgproc.TM_SQDIFF_NORMED);
		//
		// //result = Core.sumElems(result).val;
		//
		//
		//
		// // int windowSize = new Double(dsg.getNumFrames() / (double)
		// qsg.getNumFrames()).intValue();
		// //
		// // for (int window = 0; window < dsg.getNumFrames(); window +=
		// windowSize) {
		// //
		// // }
		// //

	}

	private static void playBytes(byte[] bytes, AudioFormat audioFormat)
			throws Exception {
		
		
		int readBytes = 0;
		int EXTERNAL_BUFFER_SIZE = 524288;

		Info info = new Info(SourceDataLine.class, audioFormat);
		SourceDataLine dataLine = null;
		dataLine = (SourceDataLine) AudioSystem.getLine(info);
		dataLine.open(audioFormat, EXTERNAL_BUFFER_SIZE);

		// Starts the music :P
		dataLine.start();
		int i = 0;
		byte[] buf = new byte[EXTERNAL_BUFFER_SIZE];
		while (i <= bytes.length) {
			System.out.println(i);
			buf = Arrays.copyOfRange(bytes, i, i + EXTERNAL_BUFFER_SIZE);
			dataLine.write(buf, 0, readBytes);
			i += EXTERNAL_BUFFER_SIZE;

		}

		// readBytes = audioInputStream.read(audioBuffer, 0,
		// audioBuffer.length);
		// if (readBytes >= 0){
		// dataLine.write(audioBuffer, 0, readBytes);

	}

}
