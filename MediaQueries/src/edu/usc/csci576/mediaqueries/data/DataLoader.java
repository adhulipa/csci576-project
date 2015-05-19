package edu.usc.csci576.mediaqueries.data;

import java.io.*;
import java.util.*;

import org.opencv.core.Mat;

import edu.usc.csci576.mediaqueries.descriptor.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.SceneDetector;

public class DataLoader {

	public static void main(String[] args) {

		//loadScenes();
		// createOfflineDataset();
		//serializeRGBArrays();

		// desrialize(histogram/traffic/traffic600.histogram)
		/*
		 * HashMap<String, List<byte[]>> map = null; try { FileInputStream fis =
		 * new FileInputStream("bytesMap.ser"); ObjectInputStream ois = new
		 * ObjectInputStream(fis); map = (HashMap) ois.readObject();
		 * List<byte[]> p = map.get("starcraft"); byte[] frame10 = p.get(10);
		 * ois.close(); fis.close(); } catch (IOException ioe) {
		 * ioe.printStackTrace(); return; } catch (ClassNotFoundException c) {
		 * System.out.println("Class not found"); c.printStackTrace(); return; }
		 * System.out.println("Deserialized HashMap..");
		 * 
		 * // Display content using Iterator Set set = map.entrySet(); Iterator
		 * iterator = set.iterator(); while (iterator.hasNext()) { Map.Entry
		 * mentry = (Map.Entry) iterator.next(); System.out.print("key: " +
		 * mentry.getKey() + " & Value: ");
		 * System.out.println(mentry.getValue()); }
		 */
	}

	public static HashMap<String, List<Integer[]>> loadScenes() {
		HashMap<String, List<Integer[]>> map = null;
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
		System.out.println("LoadScenes Deserialized scenes map..");

		// // Display content using Iterator
		// Set set = map.entrySet();
		// Iterator iterator = set.iterator();
		// while (iterator.hasNext()) {
		// Map.Entry mentry = (Map.Entry) iterator.next();
		// System.out.print("key: " + mentry.getKey() + " & Value: ");
		// System.out.println(mentry.getValue());
		// }

		return map;
	}

	public static void serializeRGBArrays() {
		System.out.println("Serializing rgb .... ");
		
		String[] dataset = { "StarCraft", "flowers", "interview", "movie",
				"sports", "musicvideo", "traffic" };

		int width = 352;
		int height = 288;

		for (String item : dataset) {
			System.out.println("serialising item - " + item);
			List<byte[][]> bgrArrayList = new ArrayList<byte[][]>();
			for (int i = 1; i <= 600; i++) {

				System.out.println("serialising - " + item + i);

				String fileName = String.format("%s/%s%03d.rgb", "database/"
						+ item, item, i);
				bgrArrayList = RGBHistogram.getRGBArrays(fileName, width,
						height);

				String rgbFilePath = String.format("%s/%s%03d.histogram",
						"histogram/" + item, item, i);
				writeFile(rgbFilePath, bgrArrayList);

			}

		}

	}

	public static List<byte[][]> deserializeRGBArrays(String file) {

		List<byte[][]> bgrHist = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			bgrHist = (List<byte[][]>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}
		System.out.println("Deserialized RGB Arrays..");
		return bgrHist;
	}
	public static List<Mat> deserializeRGBMats(String file) {

		List<byte[][]> bgrHist = deserializeRGBArrays(file);
		return matify(bgrHist);
	}

	private static List<Mat> matify(List<byte[][]> bgrHists) {
		List<Mat> bgrHistsMat = new ArrayList<Mat>(3);
		
		for (byte[][] channel : bgrHists) {
			Mat m = new Mat();
			for (int row = 0; row < channel.length; row++) {
				m.put(row, 0, channel[row]);
			}
			bgrHistsMat.add(m);
		}
		return bgrHistsMat;
	}

	public static void createOfflineDataset() {

		// Scene Detector for all videos
		String[] dataset = { "StarCraft", "flowers", "interview", "movie",
				"sports", "musicvideo", "traffic" };

		Map<String, List<Integer[]>> scenesMap = new HashMap<String, List<Integer[]>>();

		for (String item : dataset) {
			scenesMap.put(item,
					SceneDetector.getScenes("database/" + item, item, 600));
			System.out.println("done with " + item + " scenes");
		}
		writeFile("scenesMap.ser", scenesMap);

		// Create RGB hist data
		serializeRGBArrays();

	}

	private static void writeFile(String fileName, Object data) {

		File file = new File(fileName);

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			System.out.printf("Serialized HashMap data is saved in " + fileName
					+ "\n");
		}

	}
}
