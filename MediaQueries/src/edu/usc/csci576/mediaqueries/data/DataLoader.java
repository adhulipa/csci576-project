package edu.usc.csci576.mediaqueries.data;

import java.io.*;
import java.util.*;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;
import edu.usc.csci576.mediaqueries.model.SceneDetector;

public class DataLoader {
	
	public static void main(String[] args) {
		
		serializeBytes();
		System.exit(1);
		HashMap<String, List<byte[]>> map = null;
		try {
			FileInputStream fis = new FileInputStream("bytesMap.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (HashMap) ois.readObject();
			List<byte[]> p = map.get("starcraft");
			byte[] frame10 = p.get(10);
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
		System.out.println("Deserialized HashMap..");
		
		
		
		// Display content using Iterator
		Set set = map.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
			System.out.print("key: " + mentry.getKey() + " & Value: ");
			System.out.println(mentry.getValue());
		}

	}
	public static HashMap<String, List<int[]>> loadScenes() {
		HashMap<String, List<int[]>> map = null;
		try {
			FileInputStream fis = new FileInputStream("scenesMap.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (HashMap) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}
		System.out.println("Deserialized HashMap..");
		
		
//		// Display content using Iterator
//		Set set = map.entrySet();
//		Iterator iterator = set.iterator();
//		while (iterator.hasNext()) {
//			Map.Entry mentry = (Map.Entry) iterator.next();
//			System.out.print("key: " + mentry.getKey() + " & Value: ");
//			System.out.println(mentry.getValue());
//		}
		
		return map;
	}
	
	public static void serializeBytes()
	{
		String[] dataset = {"starcraft", "flowers", "interview", "movie", "sports", "musicvideo", "traffic"};
		
		Map<String, List<byte[]>> bytesMap = new HashMap<String, List<byte[]>>();
		
		for (String item : dataset) {
			
			List<byte[]> imageBytes = new ArrayList<byte[]>();
			for(int i = 1; i <= 600; i++)
			{
				String fileName = String.format("%s/%s%03d.rgb", "database/" + item, item, i);
				imageBytes.add(ImageHandler.readImageFromFile(fileName));
			}
			
			bytesMap.put(item, imageBytes);
			System.out.println("done with " + item);
		}
		
		writeFile("bytesMap.ser", bytesMap);
	}
	
	public static void createOfflineDataset() {
		
		// Scene Detector for all videos
		String[] dataset = {"StarCraft", "flowers", "interview", 
				"movie", "sports", "musicvideo", "traffic"};
		
		
		Map<String, List<int[]>> scenesMap = new HashMap<String, List<int[]>>();
		
		for (String item : dataset) {
			scenesMap.put(item, SceneDetector.getScenes("database/" + item, item, 600));
			System.out.println("done with " + item);
		}
				
		writeFile("scenesMap.ser", scenesMap);		
	}

	private static void writeFile(String fileName, Object data)
	{
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			System.out
			.printf("Serialized HashMap data is saved in " + fileName + "\n");
		}
	
	}
}
