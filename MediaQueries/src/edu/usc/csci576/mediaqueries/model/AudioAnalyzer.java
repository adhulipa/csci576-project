package edu.usc.csci576.mediaqueries.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
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

	public static void main(String[] args) throws Exception {
	    
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

	    Wave wave = new Wave("database/musicvideo/musicvideo.wav");
		Wave q = new Wave("query/first/first.wav");
		

		Spectrogram sg = new Spectrogram(wave);
		
		System.out.println(sg.getNumFrames() + " " + sg.getFramesPerSecond());
		
		
		
		WaveHeader h = wave.getWaveHeader();
		
		//System.out.println(h);
		AudioFormat f = new AudioFormat(h.getSampleRate(),
				h.getBitsPerSample(), h.getChannels(), true, false);

		// playBytes(wave.getBytes(), f);

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


		Map<Integer, Point> dataConstMap = createConstellationmap(sg);
		Map<Integer, Point> queryConstMap = createConstellationmap(qsg);
		
		
		System.out.println(dataConstMap);
		System.out.println(queryConstMap);
		
		//compareSpectroData(qsg, sg);

		System.exit(1);
		
		
//		for (int i =0; i < sgNormData.length; i++)
//		System.out.println(
//				(sgNormData[i].length)
//				);		
//		
		System.out.println("DONE!");
		
		
	}
	
	
	
 static	private Map<Integer, Point> createConstellationmap(Spectrogram spec) {
		
		/* Idea:
		 * use opencv fn minMaxLoc() to find 
		 * global min or max in array;
		 * 
		 * iterate over each col of spectrodata,
		 * find max in each col
		 * keep track of Tuple (col_id, max_val)
		 * the List<Tuple> above is the constMap
		 */
		
		//double[][] ddat = dataSpec.getNormalizedSpectrogramData();
		double[][] dat = spec.getAbsoluteSpectrogramData();
		
		
		Mat m = new Mat();
		Double[] temp;

		MinMaxLocResult res;
		Map<Integer, Point> constMap = new HashMap<>();
		
		
		
		for (int i = 0; i < dat.length; i++) {

			m = Converters.vector_double_to_Mat(
							Arrays.asList(
									ArrayUtils.toObject(dat[i])
							)
						);
			
			res = Core.minMaxLoc(m);
			constMap.put(i, res.maxLoc);
		}
		
		return constMap;
		
	}

	private static void compareSpectroData(Spectrogram qsg, Spectrogram dsg) throws FileNotFoundException, UnsupportedEncodingException {
		
		double[][] qnd = qsg.getNormalizedSpectrogramData();
		Mat qmat = new Mat(qsg.getFramesPerSecond(), qsg.getNumFrames(), CvType.CV_32F);
		for (int row = 0; row < qsg.getFramesPerSecond(); row++)
			qmat.put(row, 0, qnd[row]);
		
		double[][] dnd = dsg.getNormalizedSpectrogramData();
		Mat dmat = new Mat(dsg.getFramesPerSecond(), dsg.getNumFrames(), CvType.CV_32F);
		for (int row = 0; row < dsg.getFramesPerSecond(); row++)
			dmat.put(row, 0, dnd[row]);
		
		
		
		PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
		writer.println(dmat.dump());
		writer.close();		
		
		int windowWidth = qmat.cols();
		
		Mat sub = new Mat();
//		for (int i = 0; i+windowWidth<dmat.cols(); i++){
//			sub = dmat.submat(0, dmat.rows(), i, i+windowWidth);
//			Mat diff = new Mat();
//			Core.compare(qmat, sub, diff, Core.CMP_EQ);
//			
//			
//			double d = Core.sumElems(diff).val[0];
//
//			System.out.println(
//					d					
//					);
//						
//		}
		
		
//		
//		// TODO: perform template matching on
//		// dmat & qmat
//		// http://docs.opencv.org/doc/tutorials/imgproc/histograms/template_matching/template_matching.html
//		
//		Mat result = new Mat();
//		Imgproc.matchTemplate(dmat, qmat, result, Imgproc.TM_SQDIFF_NORMED);
//		
//		//result = Core.sumElems(result).val;
//		
//		
//		
////		int windowSize = new Double(dsg.getNumFrames() / (double) qsg.getNumFrames()).intValue();
////		
////		for (int window = 0; window < dsg.getNumFrames(); window += windowSize) {
////			
////		}
////		

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
