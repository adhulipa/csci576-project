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
	/*
	 * 0 - STOPPED
	 * 1 - PLAY
	 * 2 - PAUSE
	 */
	private int state = 0;
	
	private FileInputStream inputStream;
	private AudioInputStream audioInputStream = null;
	private SourceDataLine dataLine = null;
	
	private final int EXTERNAL_BUFFER_SIZE = 32768; // 4Kb
	
	public AudioPlayer(String threadName, String filepath) {
		int lastsep = filepath.lastIndexOf("/");
		
		this.threadName = threadName;
		this.filepath = filepath;
		this.filename = filepath.substring(lastsep + 1);
	}
	public void setAudioPath(String filepath, String fileName) {
		stopAudio();
		this.filepath = filepath;
		this.filename = fileName;
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

		if (state == 1)
			return;
		
		if (state == 0)
			initAudio();

		/* Start the music */
		dataLine.start();

		if (audioPlayer == null) {
			audioPlayer = new Thread(this, threadName);
			audioPlayer.start();
		}

		state = 1;
	}

	/**
	 * stop playing audio
	 * 
	 */
	public void stopAudio() {
		if (state == 0)
			return;
		
		audioPlayer = null;
		dataLine.drain();
		dataLine.close();
		
		state = 0;
	}
	
	/**
	 * pause the audio playback
	 */
	public void pauseAudio() {
		if (state == 1) {
			audioPlayer = null;
			dataLine.stop();
			
			state = 2;
		}
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
			if (state == 1) {
				stopAudio();
			}
		}
	}
}
