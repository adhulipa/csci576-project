package edu.usc.csci576.mediaqueries.model;

import java.io.File;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

import org.opencv.core.*;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.musicg.wave.*;
import com.musicg.wave.extension.*;
import com.musicg.graphic.*;



public class AudioAnalyzer {

	public static void main(String[] args) throws Exception {
	    
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

	    Wave wave = new Wave("database/musicvideo/musicvideo.wav");
		Wave q = new Wave("query/first/first.wav");
		
		WaveHeader h = wave.getWaveHeader();
		
		//System.out.println(h);
		AudioFormat f = new AudioFormat(h.getSampleRate(),
				h.getBitsPerSample(), h.getChannels(), true, false);

		// playBytes(wave.getBytes(), f);

		Spectrogram sg = new Spectrogram(wave);

		// Graphic render
		GraphicRender render = new GraphicRender();
		// render.setHorizontalMarker(1);
		// render.setVerticalMarker(1);
		render.renderWaveform(wave, "offlineData/waveform.jpg");
		
		double[][] sgNormData = sg.getNormalizedSpectrogramData();
		double[][] sgAbsData = sg.getAbsoluteSpectrogramData();
		render.renderSpectrogramData(sgAbsData, "offlineData/musicvideo/absSpectogram.jpg");
		render.renderSpectrogramData(sgNormData, "offlineData/musicvideo/normSpectogram.jpg");
		
		
		Spectrogram qsg = q.getSpectrogram();
		double[][] qnd = qsg.getNormalizedSpectrogramData();
		double[][] and = qsg.getAbsoluteSpectrogramData();
		render.renderSpectrogramData(qnd, "offlineData/first/normSpectogram.jpg");
		render.renderSpectrogramData(and, "offlineData/first/absSpectogram.jpg");

		
		compareSpectroData(qsg, sg);
		
//		for (int i =0; i < sgNormData.length; i++)
//		System.out.println(
//				(sgNormData[i].length)
//				);		
//		
		System.out.println("DONE!");
		
		
	}

	private static void compareSpectroData(Spectrogram qsg, Spectrogram dsg) {
		
		double[][] qnd = qsg.getNormalizedSpectrogramData();
		Mat qmat = new Mat(qsg.getFramesPerSecond(), qsg.getNumFrames(), CvType.CV_32F);
		for (int row = 0; row < qsg.getFramesPerSecond(); row++)
			qmat.put(row, 0, qnd[row]);
		
		double[][] dnd = dsg.getNormalizedSpectrogramData();
		Mat dmat = new Mat(dsg.getFramesPerSecond(), dsg.getNumFrames(), CvType.CV_32F);
		for (int row = 0; row < dsg.getFramesPerSecond(); row++)
			dmat.put(row, 0, dnd[row]);
			
		// TODO: perform template matching on
		// dmat & qmat
		// http://docs.opencv.org/doc/tutorials/imgproc/histograms/template_matching/template_matching.html
		
		Mat result = new Mat();
		Imgproc.matchTemplate(dmat, qmat, result, Imgproc.TM_SQDIFF_NORMED);
		
		//result = Core.sumElems(result).val;
		
		
		
//		int windowSize = new Double(dsg.getNumFrames() / (double) qsg.getNumFrames()).intValue();
//		
//		for (int window = 0; window < dsg.getNumFrames(); window += windowSize) {
//			
//		}
//		
		System.out.println(
				result.dump() + "\n" +
				(Core.sumElems(result).val[0])
				//windowSize
				//qsg.getNumFrames() + " " + dsg.getNumFrames()
				
				
				
				);
		
	}

	private static void playBytes(byte[] bytes, AudioFormat audioFormat) throws Exception{

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
			buf = Arrays.copyOfRange(bytes, i, i+EXTERNAL_BUFFER_SIZE);
			dataLine.write(buf, 0, readBytes);
			i+= EXTERNAL_BUFFER_SIZE;

				
		}
		
//		readBytes = audioInputStream.read(audioBuffer, 0,
//				audioBuffer.length);
//			if (readBytes >= 0){
//			    dataLine.write(audioBuffer, 0, readBytes);

	}

}
