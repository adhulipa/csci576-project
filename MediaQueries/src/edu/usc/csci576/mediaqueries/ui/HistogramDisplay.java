package edu.usc.csci576.mediaqueries.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.data.ImageHandler;
import edu.usc.csci576.mediaqueries.descriptor.RGBHistogram;

public class HistogramDisplay
{	
	public static BufferedImage getDiffImage(String resultFilePath, String queryFilePath,
			int height, int width, int panelHeight, int panelWidth, double audioSimNumber, double audioSimMaxValue)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		MatOfInt histSize = new MatOfInt(256);
		boolean accumulate = false;

		final MatOfFloat histRange = new MatOfFloat(0f, 256f);

		Mat r_hist = new Mat();
		Mat g_hist = new Mat();
		Mat b_hist = new Mat();
		
		List<Mat> resultChannels = RGBHistogram.getRGBMat(resultFilePath, width, height);
		List<Mat> queryChannels = RGBHistogram.getRGBMat(queryFilePath, width, height);
		
		List<Mat> diffChannels = new ArrayList<Mat>();
		
		for(int i = 0; i < resultChannels.size(); i++)
		{
			Mat resultMat = resultChannels.get(i);
			Mat queryMat = queryChannels.get(i);
			Mat diff = new Mat(resultMat.rows(), resultMat.cols(), resultMat.type());
			Core.absdiff(resultMat, queryMat, diff);
			diffChannels.add(i, diff);
		}
		
		Imgproc.calcHist(diffChannels, new MatOfInt(0), new Mat(), r_hist, histSize,
				histRange, accumulate);
		Imgproc.calcHist(diffChannels, new MatOfInt(1), new Mat(), g_hist, histSize,
				histRange, accumulate);
		Imgproc.calcHist(diffChannels, new MatOfInt(2), new Mat(), b_hist, histSize,
				histRange, accumulate);

		int hist_w = panelWidth;
		int hist_h = panelHeight;
		long bin_w;
		bin_w = Math.round((double) (hist_w / panelWidth));

		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(255,255,255));

		Core.normalize(r_hist, r_hist, 3, histImage.rows(), Core.NORM_MINMAX);
		Core.normalize(g_hist, g_hist, 3, histImage.rows(), Core.NORM_MINMAX);
		Core.normalize(b_hist, b_hist, 3, histImage.rows(), Core.NORM_MINMAX);

		for (int i = 1; i < 256; i++)
		{

			Core.line(
					histImage,
					new Point(bin_w * (i - 1), hist_h
							- Math.round(r_hist.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h
							- Math.round(Math.round(r_hist.get(i, 0)[0]))),
					new Scalar(51, 51, 255), 2, 8, 0);

			Core.line(
					histImage,
					new Point(bin_w * (i - 1), hist_h
							- Math.round(g_hist.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h
							- Math.round(Math.round(g_hist.get(i, 0)[0]))),
					new Scalar(0, 204, 0), 2, 8, 0);

			Core.line(
					histImage,
					new Point(bin_w * (i - 1), hist_h
							- Math.round(b_hist.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h
							- Math.round(Math.round(b_hist.get(i, 0)[0]))),
					new Scalar(255, 153, 51), 2, 8, 0);

		}


		BufferedImage hist = ImageHandler.toBufferedImage(histImage);
		return hist;		
	}
	
	public static BufferedImage getImage(String filePath, int height, int width, int panelHeight,
			int panelWidth, double audioSimNumber, double audioSimMaxValue)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		MatOfInt histSize = new MatOfInt(256);
		boolean accumulate = false;

		final MatOfFloat histRange = new MatOfFloat(0f, 256f);

		Mat r_hist = new Mat();
		Mat g_hist = new Mat();
		Mat b_hist = new Mat();

		List<Mat> channels = RGBHistogram.getRGBMat(filePath, width, height);
		Imgproc.calcHist(channels, new MatOfInt(2), new Mat(), r_hist, histSize,
				histRange, accumulate);
		Imgproc.calcHist(channels, new MatOfInt(1), new Mat(), g_hist, histSize,
				histRange, accumulate);
		Imgproc.calcHist(channels, new MatOfInt(0), new Mat(), b_hist, histSize,
				histRange, accumulate);


		int hist_w = panelWidth;
		int hist_h = panelHeight;
		long bin_w;
		bin_w = Math.round((double) (hist_w / panelWidth));

		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(255,255,255));

		Core.normalize(r_hist, r_hist, 3, histImage.rows(), Core.NORM_MINMAX);
		Core.normalize(g_hist, g_hist, 3, histImage.rows(), Core.NORM_MINMAX);
		Core.normalize(b_hist, b_hist, 3, histImage.rows(), Core.NORM_MINMAX);

		
		// Scalar (b, g, r)
		for (int i = 1; i < 256; i++)
		{

			Core.line(
					histImage,
					new Point(bin_w * (i - 1), hist_h
							- Math.round(r_hist.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h
							- Math.round(Math.round(r_hist.get(i, 0)[0]))),
					new Scalar(51, 51, 255), 2, 8, 0);

			Core.line(
					histImage,
					new Point(bin_w * (i - 1), hist_h
							- Math.round(g_hist.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h
							- Math.round(Math.round(g_hist.get(i, 0)[0]))),
					new Scalar(0, 204, 0), 2, 8, 0);

			Core.line(
					histImage,
					new Point(bin_w * (i - 1), hist_h
							- Math.round(b_hist.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h
							- Math.round(Math.round(b_hist.get(i, 0)[0]))),
					new Scalar(255, 153, 51), 2, 8, 0);

		}

		double normal = audioSimNumber/audioSimMaxValue;
		long intensity = Math.round(normal * hist_h);
		Core.line(
				histImage,
				new Point(0, hist_h - intensity),
				new Point(hist_w, hist_h - intensity),
				new Scalar(0, 0, 0), 2, 8, 0);



		BufferedImage hist = ImageHandler.toBufferedImage(histImage);
		return hist;
	}
}
