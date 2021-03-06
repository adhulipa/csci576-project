package edu.usc.csci576.mediaqueries.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.tuple.Pair;

import edu.usc.csci576.mediaqueries.controller.MediaComparator;
import edu.usc.csci576.mediaqueries.controller.VideoCompareResult;
import edu.usc.csci576.mediaqueries.parallel.SCResultType;

public class MediaSearchWorker extends SwingWorker<Map<String, Pair<Double, SCResultType>>, Void> {

	
	private JList<String> list;
	private JLabel wheelImg;
	String databaseDirString = "database/";
	String[] databaseVideoNames = new String[]{"StarCraft", "flowers", "traffic", "musicvideo", "movie", "interview", "sports" };
	String queryVideoDir = "query/Q5";
	String queryVideoName = "Q5_";
	String queryAudioDir = "query/Q5";
	String queryAudioName = "Q5_";
	Map<String, SCResultType> resultData;
	
	public MediaSearchWorker(JList<String> resultList, 
			Map<String, SCResultType> resultData, JLabel wheelImg,
			String queryVideoDir, String queryVideoName, String queryAudioDir,
			String queryAudioName) {
		
		this.queryVideoDir = queryVideoDir;
		this.queryVideoName = queryVideoName;
		
		this.queryAudioDir = queryAudioDir;
		this.queryAudioName = queryAudioName;
		
		this.list = resultList;
		this.wheelImg = wheelImg;
		this.resultData = resultData;
		
		
	}
	
	@Override
	protected Map<String, Pair<Double, SCResultType>> doInBackground()  {
		
		wheelImg.setVisible(true);
		MediaComparator mediaComparator = new MediaComparator();
		VideoCompareResult result = 
				mediaComparator.run(queryVideoDir, 
						queryVideoName, 
						databaseDirString, 
						databaseVideoNames, 
						databaseDirString, 
						queryAudioDir,
						queryAudioName);
		
		mediaComparator = null;
		
		// Use the fllowing for scnes Indices etc
		
		Map<String, SCResultType> sceneScoreMap = result.getBestMatchedScene();
		Map<String, Double> scoreMap = result.getScoresMap();
		
		Map<String, Pair<Double, SCResultType>> finalResult = new TreeMap<>();
		for (String key : scoreMap .keySet()) {
			Double score = scoreMap.get(key);
			SCResultType sceneResult = sceneScoreMap.get(key);
			
			Pair value = Pair.of(score, sceneResult);
			finalResult.put(key, value);
		}
		
		
		
		return finalResult;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void done() {
		try {
			
			Map<String, Pair<Double, SCResultType>> result = get();
			Vector<String> listData = new Vector<String>();
			
			Queue<Pair> listDataSorted = new PriorityQueue<>();
			
			for (String key : result.keySet()) {
				//listData.add(key + ": " + String.format("%.2f",result.get(key).getLeft()) + "%");
				
				Pair<Double, String> val = Pair.of(result.get(key).getLeft(), key);
				
				listDataSorted.add(val);
				
				resultData.put(key, result.get(key).getRight());
				
			}
			
			List<Pair> list2 = new ArrayList<>();
			list2.addAll(listDataSorted);
			Collections.sort(list2);
			Collections.reverse(list2);
			
			for (Pair each : list2) {
				listData.add(each.getRight() + ": " + String.format("%.2f", each.getLeft()) + "%");
			}
			
			list.setListData(listData);
//			list.setSelectedIndex(0);
			
			
			
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
