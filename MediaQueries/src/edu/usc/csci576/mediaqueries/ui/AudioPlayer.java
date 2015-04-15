package edu.usc.csci576.mediaqueries.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;

public class AudioPlayer implements Runnable {
	
	private Thread audioPlayer;
	private String threadName;
	private String filepath;
	private String filename;
	
	private FileInputStream inputStream;
	private AudioInputStream audioInputStream = null;
	private SourceDataLine dataLine = null;
	
	private boolean needInit = true;
	
	private final int EXTERNAL_BUFFER_SIZE = 32768; // 4Kb
	
	public AudioPlayer(String threadName, String filepath) {
		int lastsep = filepath.lastIndexOf("/");
		
		this.threadName = threadName;
		this.filepath = filepath;
		this.filename = filepath.substring(lastsep + 1);
	}
	
	private void initAudio() {
		String filePathString = String.format("%s/%s.wav", filepath, filename);
		
		/* Check if file exists */
		File f = new File(filePathString);
		if(f.exists() && !f.isDirectory()) {
			try {
			    inputStream = new FileInputStream(filePathString);
			} catch (FileNotFoundException e) {
			    e.printStackTrace();
			}
			
			try {
				InputStream bufferedIn = new BufferedInputStream(inputStream);
				audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
			} catch (UnsupportedAudioFileException e) {
				 e.printStackTrace();
			} catch (IOException e) {
				 e.printStackTrace();
			}
			
			AudioFormat audioFormat = audioInputStream.getFormat();
			Info info = new Info(SourceDataLine.class, audioFormat);
			
			try {
				dataLine = (SourceDataLine) AudioSystem.getLine(info);
				dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * play audio
	 */
	public void playAudio() {
		if (needInit == true) {
			initAudio();
		}
		
		/* Start the music */
		dataLine.start();
		
		if (audioPlayer == null) {
			audioPlayer = new Thread(this, threadName);
			audioPlayer.start();
		}
	}

	/**
	 * stop playing audio
	 * 
	 */
	public void stopAudio() {
		needInit = true;
		audioPlayer = null;
		dataLine.drain();
		dataLine.close();
	}
	
	/**
	 * pause the audio playback
	 */
	public void pauseAudio() {
		needInit = false;
		audioPlayer = null;
		dataLine.drain();
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
			while (readBytes != -1 && audioPlayer == thisThread) {
				readBytes = audioInputStream.read(audioBuffer, 0,
						audioBuffer.length);
				if (readBytes >= 0) {
					dataLine.write(audioBuffer, 0, readBytes);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dataLine.drain();
			dataLine.close();
		}
	}
}
