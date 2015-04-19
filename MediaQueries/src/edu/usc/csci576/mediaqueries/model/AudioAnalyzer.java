package edu.usc.csci576.mediaqueries.model;

import java.io.File;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

import com.musicg.wave.*;
import com.musicg.wave.extension.*;



public class AudioAnalyzer {

	public static void main(String[] args) throws Exception {
		
		Wave wave = new Wave("database/StarCraft/StarCraft.wav");
		
		WaveHeader h = wave.getWaveHeader();
		
		System.out.println(h);
		AudioFormat f = new AudioFormat(h.getSampleRate(), h.getBitsPerSample(), h.getChannels(), true, false);

		playBytes(wave.getBytes(), f);
		
		Spectrogram sg = new Spectrogram(wave);
		
		
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
