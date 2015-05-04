package edu.usc.csci576.mediaqueries.ui;

import javax.swing.JLabel;

public class MediaPlayer {
	private VideoPlayer videoPlayer;
	private AudioPlayer audioPlayer;
	
	public MediaPlayer(String threadName, String filepath, String fileName, JLabel imgBox) {
		setVideoPlayer(new VideoPlayer(threadName, filepath, fileName, imgBox));
		setAudioPlayer(new AudioPlayer(threadName, filepath));
	}
	
	public void playMedia() {
		getVideoPlayer().playVideo();
		getAudioPlayer().playAudio();
	}
	
	public void pauseMedia() {
		getVideoPlayer().pauseVideo();
		getAudioPlayer().pauseAudio();
	}
	
	public void stopMedia() {
		getVideoPlayer().stopVideo();
		getAudioPlayer().stopAudio();
	}
	
	public void setFrameAtIndex(int scrubIndex) {
		getAudioPlayer().pauseAudio();
		getAudioPlayer().setFrameAtIndex(scrubIndex);
		getVideoPlayer().setFrameAtIndex(scrubIndex);
		getAudioPlayer().playAudio();
		getVideoPlayer().playVideo();		
	}
	
	public void setFrameAfterQuery(int frameIndex, long microsec)
	{
//		getAudioPlayer().pauseAudio();
		getVideoPlayer().setCurrentFrame(frameIndex);
		getAudioPlayer().setAudioAtTime(microsec);
//		getVideoPlayer().playVideo();		
	}
	
	public void setMediaPath(String filepath, String fileName) {
		getVideoPlayer().setVideoPath(filepath, fileName);
		getAudioPlayer().setAudioPath(filepath, fileName);
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

	/**
	 * @return the audioPlayer
	 */
	public AudioPlayer getAudioPlayer()
	{
		return audioPlayer;
	}

	/**
	 * @param audioPlayer the audioPlayer to set
	 */
	public void setAudioPlayer(AudioPlayer audioPlayer)
	{
		this.audioPlayer = audioPlayer;
	}
}
