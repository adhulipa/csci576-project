package edu.usc.csci576.mediaqueries.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.text.Highlighter.HighlightPainter;

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

import edu.usc.csci576.mediaqueries.controller.ImageHandler;
import edu.usc.csci576.mediaqueries.ui.MainFrameUI;

public class RGBHistogram {

	public static void main(String[] args) throws IOException {
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );		
		
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
		
		System.out.println(b_hist.cols());

		byte[] b = new byte[b_hist.rows()*b_hist.cols() ];
		//b_hist.get(0, 0, b);
		//System.out.println(b_hist.type());
		System.out.println("ssss");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
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
