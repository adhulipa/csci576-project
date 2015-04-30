package edu.usc.csci576.mediaqueries.data;

import java.io.*;
import java.util.*;

import net.semanticmetadata.lire.utils.SerializationUtils;

import org.apache.commons.codec.binary.Base64;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.usc.csci576.mediaqueries.controller.CompareHistogram;
import edu.usc.csci576.mediaqueries.model.RGBHistogram;
import edu.usc.csci576.mediaqueries.model.SceneDetector;

public class DataLoader {
	
	public static void main(String[] args) {
		
		createRGBDataset();
		//System.exit(1);
		Map<String, List<List<String>>> map = null;
		try {
			FileInputStream fis = new FileInputStream("rgbMap.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (HashMap) ois.readObject();
			for(String key : map.keySet())
			{
				List<List<String>> jsonList = map.get(key);
				int i = 1;
				for(List<String> frameData : jsonList)
				{
					String filePathString1 = String.format("database/%s/%s%03d.rgb", key, key, i++);
					
					List<Mat> rgbmats = RGBHistogram.getRGBMat(filePathString1, 352, 288), rgbmats2 = new ArrayList<Mat>();
					for(String frameMat : frameData)
					{
						Mat m = matFromJson(frameMat);
						rgbmats2.add(m);
					}
					CompareHistogram.compareRGBHistogram(rgbmats, rgbmats2);
				}
			}
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
	
	public static void createRGBDataset()
	{
		String[] dataset = {"StarCraft"};
		//, "flowers", "interview", "movie", "sports", "musicvideo", "traffic"
		
//		Map<String, List<List<Mat>>> rgbMap = new HashMap<String, List<List<Mat>>>();
		Map<String, List<List<String>>> rgbjsonMap = new HashMap<String, List<List<String>>>();
		
		for(String item : dataset)
		{
//			List<List<Mat>> matList = new ArrayList<List<Mat>>();
			List<List<String>> matJsonList = new ArrayList<List<String>>();
			
			// Each frame
			for(int i = 1; i <= 600; i++)
			{
				String filePathString1 = String.format("database/%s/%s%03d.rgb", item, item, i);
				
				List<Mat> rgbmats = RGBHistogram.getRGBMat(filePathString1, 352, 288);
				List<Mat> rgbmats2 = new ArrayList<Mat>();
				
				// List of matrix jsons per frame
				List<String> jsonStrings = new ArrayList<String>();
				
				for(Mat mat : rgbmats)
				{
					String json = matToJson2(mat);					
					
					jsonStrings.add(json);
				}
				
//				for(String json : jsonStrings)
//				{
//					rgbmats2.add(matFromJson(json));
//				}
				//CompareHistogram.compareRGBHistogram(rgbmats, rgbmats2);
				
				// Add each frame to current image's matrix list
				matJsonList.add(jsonStrings);
//				matList.add(rgbmats);
				
				
			}
					
			
			//rgbMap.put(item, matList);
			rgbjsonMap.put(item, matJsonList);
		}
		
		
		
		writeToFile("rgbMap.ser", rgbjsonMap);
		
		
	}

	private static void writeToFile(String fileName, Object data)
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
		finally
		{
			System.out
			.printf("Serialized HashMap data is saved in rgbMap.ser");
		}
	}
	
	public static String matToJson(Mat mat) {
		JsonObject obj = new JsonObject();
 
		if (mat.isContinuous()) {
			StringBuilder builder = new StringBuilder();
 
			for (int i = 0; i < mat.rows(); i++) {
				double[] d = mat.get(i, 0);
				builder.append(d[0]);
				if (i != mat.rows() - 1) {
					builder.append(",");
				}
			}
 
			obj.addProperty("rows", mat.rows());
			obj.addProperty("cols", mat.cols());
			obj.addProperty("type", mat.type());
			obj.addProperty("data", builder.toString());
 
			Gson gson = new Gson();
			String json = gson.toJson(obj);
 
			//System.out.println("opencv_detector json: " + json);
			return json;
		} else {
			System.err.println("opencv_detector Mat not continuous.");
		}
		return "{}";
	}
 
	public static Mat matFromJson(String json) {
		JsonParser parser = new JsonParser();
		JsonObject JsonObject = parser.parse(json).getAsJsonObject();
 
		int rows = JsonObject.get("rows").getAsInt();
		int cols = JsonObject.get("cols").getAsInt();
		int type = JsonObject.get("type").getAsInt();
 
		String dataString = JsonObject.get("data").getAsString();
 
		Mat mat = new Mat(rows, cols, type);
 
		int rowIndex = 0;
 
		for (String s : dataString.split(",")) {
			mat.put(rowIndex++, 0, Double.parseDouble(s));
		}
 
		return mat;
 
	}
	
	public static String matToJson2(Mat mat){
	    JsonObject obj = new JsonObject();

	    if(mat.isContinuous()){
	        int cols = mat.cols();
	        int rows = mat.rows();
	        int elemSize = (int) mat.elemSize();
	        int type = mat.type();

	        obj.addProperty("rows", rows);
	        obj.addProperty("cols", cols);
	        obj.addProperty("type", type);

	        // We cannot set binary data to a json object, so:
	        // Encoding data byte array to Base64.
	        String dataString;

	        if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
	            int[] data = new int[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
	        }
	        else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
	            float[] data = new float[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
	        }
	        else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
	            double[] data = new double[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
	        }
	        else if( type == CvType.CV_8U ) {
	            byte[] data = new byte[cols * rows * elemSize];
	            mat.get(0, 0, data);
	            dataString = new String(Base64.encodeBase64(data));
	        }
	        else {

	            throw new UnsupportedOperationException("unknown type");
	        }
	        obj.addProperty("data", dataString);

	        Gson gson = new Gson();
	        String json = gson.toJson(obj);

	        return json;
	    } else {
	        System.out.println("Mat not continuous.");
	    }
	    return "{}";
	}

	public static Mat matFromJson2(String json){


	    JsonParser parser = new JsonParser();
	    JsonObject JsonObject = parser.parse(json).getAsJsonObject();

	    int rows = JsonObject.get("rows").getAsInt();
	    int cols = JsonObject.get("cols").getAsInt();
	    int type = JsonObject.get("type").getAsInt();

	    Mat mat = new Mat(rows, cols, type);

	    String dataString = JsonObject.get("data").getAsString();
	    if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
	        int[] data = SerializationUtils.toIntArray(Base64.decodeBase64(dataString.getBytes()));
	        mat.put(0, 0, data);
	    }
	    else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
	        float[] data = SerializationUtils.toFloatArray(Base64.decodeBase64(dataString.getBytes()));
	        mat.put(0, 0, data);
	    }
	    else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
	        double[] data = SerializationUtils.toDoubleArray(Base64.decodeBase64(dataString.getBytes()));
	        mat.put(0, 0, data);
	    }
	    else if( type == CvType.CV_8U ) {
	        byte[] data = Base64.decodeBase64(dataString.getBytes());
	        mat.put(0, 0, data);
	    }
	    else {

	        throw new UnsupportedOperationException("unknown type");
	    }
	    return mat;
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
		
		
		try {
			FileOutputStream fos = new FileOutputStream("scenesMap.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(scenesMap);
			oos.close();
			fos.close();
			System.out
					.printf("Serialized HashMap data is saved in scenesMap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
}
