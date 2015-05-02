package edu.usc.csci576.mediaqueries.ui;

import javax.swing.JLabel;

public class MediaPlayer {
	private VideoPlayer2 videoPlayer;
	private AudioPlayer audioPlayer;
	
	public MediaPlayer(String threadName, String filepath, String fileName, int totalFrames, JLabel imgBox) {
		setVideoPlayer(new VideoPlayer2(threadName, filepath, fileName, totalFrames, imgBox));
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
	
//	public void setFrameAtIndex(int scrubIndex) {
//		audioPlayer.pauseAudio();
//		getVideoPlayer().setFrameAtIndex(scrubIndex);
//		getVideoPlayer().playVideo();
//	}

	/**
	 * @return the videoPlayer
	 */
	public VideoPlayer2 getVideoPlayer()
	{
		return videoPlayer;
	}

	/**
	 * @param videoPlayer the videoPlayer to set
	 */
	public void setVideoPlayer(VideoPlayer2 videoPlayer)
	{
		this.videoPlayer = videoPlayer;
	}
}
