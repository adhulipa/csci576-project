package edu.usc.csci576.mediaqueries.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingWorker;

import edu.usc.csci576.mediaqueries.controller.MediaComparator;
import edu.usc.csci576.mediaqueries.controller.VideoCompareResult;

public class MediaSearchWorker extends SwingWorker<Map<String, Double>, Void> {

	
	private JList<String> list;
	private JLabel wheelImg;
	String databaseDirString = "database/";
	String[] databaseVideoNames = new String[]{"StarCraft", "flowers", "traffic", "musicvideo", "movie", "interview", "sports" };
	String queryVideoDir = "query/Q5";
	String queryVideoName = "Q5_";
	String queryAudioDir = "query/Q5";
	String queryAudioName = "Q5_";
	
	public MediaSearchWorker(JList<String> resultList, JLabel wheelImg, String queryVideoDir, String queryVideoName, String queryAudioDir, String queryAudioName) {
		this.queryVideoDir = queryVideoDir;
		this.queryVideoName = queryVideoName;
		this.queryAudioDir = queryAudioDir;
		this.queryAudioName = queryAudioName;
		this.list = resultList;
		this.wheelImg = wheelImg;
		
		
	}
	
	@Override
	protected Map<String, Double> doInBackground()  {
		
		wheelImg.setVisible(true);
		MediaComparator mediaComparator = new MediaComparator();
		VideoCompareResult result = mediaComparator.run(queryVideoDir, queryVideoName, databaseDirString, databaseVideoNames, databaseDirString);
		
		mediaComparator = null;
		
		// Use the fllowing for scnes Indices etc
		// result.getBestMatchedScene();
		
		return result.getScoresMap();
		
	}
	
	@Override
	protected void done() {
		try {
			
			Map<String, Double> result = get();
			Vector<String> listData = new Vector<String>();
			
			for (String key : result.keySet()) {
				listData.add(key + ": " + String.format("%.2f", result.get(key)) + "%");
			}
			
			list.setListData(listData);
			list.setSelectedIndex(2);
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			wheelImg.setVisible(false);
		}
	}

}
