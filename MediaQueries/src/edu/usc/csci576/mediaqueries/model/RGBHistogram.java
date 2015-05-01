package edu.usc.csci576.mediaqueries.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.text.Highlighter.HighlightPainter;

import org.apache.commons.lang3.ArrayUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import edu.usc.csci576.mediaqueries.data.ImageHandler;
import edu.usc.csci576.mediaqueries.ui.MainFrameUI;

public class RGBHistogram {
	
	private int WIDTH = 352;
	private int HEIGHT = 288;
	
	private List<Mat> bgrMat;
	private List<byte[][]> bgrArrays;
	
	public RGBHistogram(String filename) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );		
		
		byte[] raw = ImageHandler.readImageFromFile(filename);
		BufferedImage img = ImageHandler.toBufferedImage(raw, WIDTH, HEIGHT, 
				BufferedImage.TYPE_3BYTE_BGR);
		Mat m = ImageHandler.matify(img);
		Core.split(m, bgrMat);
	}
	
	public void asArrays() {
		// TODO
	}
	
	public void asMat() {
		// TODO
	}
	
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
	public static List<Mat> getRGBMat(String frame, int width,
			int height) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );		
		
		byte[] raw = ImageHandler.readImageFromFile(frame);
		BufferedImage img = ImageHandler.toBufferedImage(raw, width, height, 
				BufferedImage.TYPE_3BYTE_BGR);
		Mat m = ImageHandler.matify(img);
		
		
		List<Mat> rgb = new ArrayList<Mat>();
		Core.split(m, rgb);
		
		
		return rgb;
		
	}
	
	/**
	 * 
	 * @param frame
	 * @param width
	 * @param height
	 * @return List<byte[][]> 
	 * get(0) returns blue hist
	 * get(1) returns green hist
	 * get(2) returns red hist
	 * 
	 * Each byte[][] is represented as
	 * byte[height][width]
	 */
	
	public static List<byte[][]> getRGBArrays(String frame, int width,
			int height) {
		byte[][] r,g,b;
		
		List<byte[][]> hists;
		
		r = new byte[height][width];
		g = new byte[height][width];
		b = new byte[height][width];
		
		byte[] bytes = ImageHandler.readImageFromFile(frame);
		
		int ind = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				r[y][x] = bytes[ind];
				g[y][x] = bytes[ind + height * width];
				b[y][x] = bytes[ind + height * width * 2];

				ind++;
			}
		}
		
		hists = new ArrayList<byte[][]>();
		hists.add(b);
		hists.add(g);
		hists.add(r);
		
		return hists;
	}
	
	
	public static void main(String[] args) {
		List<Mat> rgbmats = getRGBMat("database/flowers/flowers001.rgb", 352, 288);
		List<byte[][]> rgbarrs = getRGBArrays("database/flowers/flowers001.rgb", 352, 288);
		
		Mat bm = rgbmats.get(0);
		byte[][] ba = rgbarrs.get(0);
		
		System.out.println(
				ba[101][10] + " " +
				Arrays.toString(bm.get(101, 10))
				);
		
	}
	
	public static void main__OLD_TESTS(String[] args) throws IOException {
		
		
		// 3 line - read image
		byte[] px = ImageHandler.readImageFromFile("database/movie/movie001.rgb");
		Mat img = new Mat(352, 288, CvType.CV_8UC3);
		img.put(0,0,px);
				
		List<Mat> channels = new ArrayList<Mat>(img.channels());
		Core.split(img, channels);

		MatOfInt histSize = new MatOfInt(256);
		float range[] = {0, 256};
		MatOfFloat ranges = new MatOfFloat(range);
		Mat b_hist, g_hist, r_hist;
		b_hist = new Mat();
		boolean uniform = true;
		boolean accumulate = false;
		
		
		Imgproc.calcHist(
				channels, 
				new MatOfInt(1), 
				new Mat(), 
				b_hist, 
				histSize, 
				ranges);
		
		System.out.println(b_hist.dump());

		byte[] b = new byte[b_hist.rows()*b_hist.cols() ];
		//b_hist.get(0, 0, b);
		//System.out.println(b_hist.type());
		
		
	    Mat image = img;
	    
	    Mat src = new Mat(image.height(), image.width(), CvType.CV_8UC2);

	    Imgproc.cvtColor(image, src, Imgproc.COLOR_RGB2GRAY);



	    Vector<Mat> bgr_planes = new Vector<>();
	    Core.split(src, bgr_planes);

	    //MatOfInt histSize = new MatOfInt(256);


	    final MatOfFloat histRange = new MatOfFloat(0f, 256f);

	    //boolean accumulate = false;

	    b_hist = new  Mat();

	    Imgproc.calcHist(bgr_planes, new MatOfInt(0),new Mat(), b_hist, histSize, histRange, accumulate);

	    int hist_w = 512;
	    int hist_h = 600;
	    long bin_w;
	    bin_w = Math.round((double) (hist_w / 256));

	    Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC1);

	    Core.normalize(b_hist, b_hist, 3, histImage.rows(), Core.NORM_MINMAX);



	    for (int i = 1; i < 256; i++) {         


	        Core.line(histImage, new Point(bin_w * (i - 1),hist_h- Math.round(b_hist.get( i-1,0)[0])), 
	                new Point(bin_w * (i), hist_h-Math.round(Math.round(b_hist.get(i, 0)[0]))),
	                new  Scalar(255, 0, 0), 2, 8, 0);

	    }


	    System.out.println(histImage);

		BufferedImage hist = ImageHandler.toBufferedImage(histImage);
		
		try {
		    // retrieve image
		    BufferedImage bi = hist;
		    File outputfile = new File("saved.png");
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		

	}
}
