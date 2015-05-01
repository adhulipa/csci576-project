package edu.usc.csci576.mediaqueries.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class FrameDiff
{
	
	/**
	 * 
	 * @param filename
	 * @param width
	 * @param height
	 * @return List<Mat>
	 * where Mat is a 3 channel matrix of BGR bytes
	 * get(0) is blue mat
	 * get(1) is green mat
	 * get(2) is red mat
	 * 
	 * Each Mat is 288 x 352
	 * rows = height of img
	 * cols = width of img
	 * 
	 */
	public static Mat getMat(String filename, int width,
			int height) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );		
		
		byte[] raw = ImageHandler.readImageFromFile(filename);
		BufferedImage img = ImageHandler.toBufferedImage(raw, width, height, 
				BufferedImage.TYPE_3BYTE_BGR);
		Mat m = ImageHandler.matify(img);
		
		
		return m;
		
	}
	
	public static void main(String []args)
	{
		
		
		ViewFrame vf = new ViewFrame("My");

		
		Mat rgbmats = getMat("database/flowers/flowers100.rgb", 352, 288);
		Mat rMat = RGBHistogram.getRGBMat("database/flowers/flowers100.rgb", 352, 288).get(0);
		
		Mat accumulator = Mat.zeros(288, 352, CvType.CV_32FC1);
		Mat diff = null, diff2 = null;
		for(int i = 100; i < 105; i++)
		{
			Mat r1 =  getMat("database/flowers/flowers" + i + ".rgb", 352, 288);	
			Mat rMat1 = RGBHistogram.getRGBMat("database/flowers/flowers" + i + ".rgb", 352, 288).get(2);
			
			Mat r2 =  getMat("database/flowers/flowers" + (i+1) + ".rgb", 352, 288);
			Mat rMat2 = RGBHistogram.getRGBMat("database/flowers/flowers" + (i+1) + ".rgb", 352, 288).get(2);
			
			diff = new Mat(352, 288, CvType.CV_8UC1);
			Core.absdiff(rMat1, rMat2, diff);
			
			Imgproc.accumulateWeighted(diff, accumulator, 1);
		}
		
		Mat disp1 = new Mat(352, 288, CvType.CV_8UC1);
		accumulator.convertTo(disp1, CvType.CV_8UC1);
		vf.addImage(ImageHandler.toBufferedImage(disp1));
		
		Mat accumulator2 = Mat.zeros(288, 352, CvType.CV_32FC1);		
		for(int i = 100; i < 106; i++)
		{
			Mat r1 =  getMat("database/flowers/flowers" + i + ".rgb", 352, 288);		
			Mat rMat1 = RGBHistogram.getRGBMat("database/flowers/flowers" + i + ".rgb", 352, 288).get(2);
			
			Mat r2 =  getMat("database/flowers/flowers" + (i+1) + ".rgb", 352, 288);
			Mat rMat2 = RGBHistogram.getRGBMat("database/flowers/flowers" + (i+1) + ".rgb", 352, 288).get(2);
			
			diff2 = new Mat(352, 288, CvType.CV_8UC1);
			Core.absdiff(rMat1, rMat2, diff2);
			
			Imgproc.accumulateWeighted(diff2, accumulator2, 1);
		}	
		Mat disp2 = new Mat(352, 288, CvType.CV_8UC1);
		accumulator2.convertTo(disp2, CvType.CV_8UC1);
		vf.addImage(ImageHandler.toBufferedImage(disp2));
		
		vf.setVisible(true);

		Mat res = Mat.zeros(288, 352, CvType.CV_32FC1);
		Core.compare(accumulator, accumulator2, res, Core.CMP_EQ );

		System.out.println( "res = " + res.dump() );
	}
}
