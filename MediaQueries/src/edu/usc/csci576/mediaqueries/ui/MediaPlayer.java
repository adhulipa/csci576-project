package edu.usc.csci576.mediaqueries.ui;

import javax.swing.JLabel;

public class MediaPlayer {
	private VideoPlayer videoPlayer;
	private AudioPlayer audioPlayer;
	
	public MediaPlayer(String threadName, String filepath, JLabel imgBox) {
		setVideoPlayer(new VideoPlayer(threadName, filepath, imgBox));
		audioPlayer = new AudioPlayer(threadName, filepath);
	}
	
	public void playMedia() {
		getVideoPlayer().playVideo();
		audioPlayer.playAudio();
	}
	
	public void pauseMedia() {
		getVideoPlayer().pauseVideo();
		audioPlayer.pauseAudio();
	}
	
	public void stopMedia() {
		getVideoPlayer().stopVideo();
		audioPlayer.stopAudio();
	}
	
	public void setFrameAtIndex(int scrubIndex) {
		audioPlayer.pauseAudio();
		getVideoPlayer().setFrameAtIndex(scrubIndex);
		getVideoPlayer().playVideo();
	}

	/**
	 * @return the videoPlayer
	 */
	public VideoPlayer getVideoPlayer()
	{
		return videoPlayer;
	}

	/**
	 * @param videoPlayer the videoPlayer to set
	 */
	public void setVideoPlayer(VideoPlayer videoPlayer)
	{
		this.videoPlayer = videoPlayer;
	}
}
