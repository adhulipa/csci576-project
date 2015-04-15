package edu.usc.csci576.mediaqueries.ui;

import javax.swing.JLabel;

public class MediaPlayer {
	private VideoPlayer videoPlayer;
	private AudioPlayer audioPlayer;
	
	public MediaPlayer(String threadName, String filepath, JLabel imgBox) {
		videoPlayer = new VideoPlayer(threadName, filepath, imgBox);
		audioPlayer = new AudioPlayer(threadName, filepath);
	}
	
	public void playMedia() {
		videoPlayer.playVideo();
		audioPlayer.playAudio();
	}
	
	public void pauseMedia() {
		videoPlayer.pauseVideo();
		audioPlayer.pauseAudio();
	}
	
	public void stopMedia() {
		videoPlayer.stopVideo();
		audioPlayer.stopAudio();
	}
	
	public void setFrameAtIndex(int scrubIndex) {
		videoPlayer.setFrameAtIndex(scrubIndex);
	}
}
