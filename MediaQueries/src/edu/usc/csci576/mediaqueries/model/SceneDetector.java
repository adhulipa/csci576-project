package edu.usc.csci576.mediaqueries.model;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Algorithm.*;


import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class SceneDetector {

	public static void main(String[] args) throws Exception{
		System.out.println("Scene detector");
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		
		int TOTAL_FRAMES = 400;
		List<BufferedImage> frames = new ArrayList<BufferedImage>(TOTAL_FRAMES);
		
		// load some scenes
		BufferedImage frame = null;
		int frameNum = 100;
		while (frameNum <= TOTAL_FRAMES) {
			String filePathString = String.format("%s/%s%03d.rgb", "database/starcraft", "StarCraft", frameNum);
			frame = ImageHandler.toBufferedImage(
					(ImageHandler.readImageFromFile(filePathString)),
					352, 288, BufferedImage.TYPE_3BYTE_BGR);
			frames.add(frame);
			
			frameNum++;
		}
		

		// test GUI
		ViewFrame view = new ViewFrame("Display Images");
		
		System.out.println("DONE!");
		
		
		
		
		
		// Alg exp
		
		// Step1 get the frames
		BufferedImage im1, im2;
		im1 = frames.get(2);
		im2 = frames.get(99);
		Mat f1 = ImageHandler.matify(im1);
		Mat f2 = ImageHandler.matify(im2);
		
		// Step2 compute edges
		Mat f1e = new Mat();
		Mat f2e = new Mat();
		Imgproc.Canny(f1, f1e, 100, 1);
		Imgproc.Canny(f2, f2e, 100, 1);
		
		//Step3 dilate the edges -- improves algorithm
		//Imgproc.dilate(f1e, f1e, );
		//Imgproc.dilate(f2e, f2e, kernel);
		
		//Step4 Edge change calcualtion
		// 4.1 Hausdorff distance for motion compensation
		// 4.2 Compute edge change fraction
		// 4.3 Compute entering and exiting edges
		// TODO
		
		
		// Step5 compute diffs
		Mat d1 = new Mat();
		Mat d2 = new Mat();
		Core.absdiff(f1e,f2e,d1);
		Core.subtract(f2e,f1e,d2);
		
		// If the diff is close to zero then
		// scene hasnt changed
		
		// Step6: Plot all of above to visualze progress
		frame = im1;
		view.addImage(frame);
		frame = im2;
		view.addImage(frame);

		frame = ImageHandler.toBufferedImage(f1e);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(f2e);
		view.addImage(frame);

		frame = ImageHandler.toBufferedImage(d1);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(d2);
		view.addImage(frame);
		
		
		// ADI: Matify works correctly
		// TODO: Use canny edge detector to get edges of image
		// Use edge-based scene detectiona lgorithm by ranier linehart
		
		System.out.println(f1);
		
		view.setVisible(true);
		
		
	}
	
	
	
	public static void hausdorff(Mat set1, Mat set2, int distType, double propRank) {
		Mat distMat = new Mat(set1.cols(), set2.cols(), CvType.CV_32F);
		int K = (int) (propRank * (distMat.rows() - 1));
		
		for (int r=0; r<distMat.rows()-1; r++) {
			for (int c=0; c<distMat.cols()-1; c++) {
				
				double[] p1 = set1.get(0, r);
				double[] p2 = set2.get(0, c);
				double[] d = subtract(p1, p2);
				
				
				Point p = new Point(d);
				
				List<Point> pl = new ArrayList<Point>();
				pl.add(p);
				
				Mat dmat = org.opencv.utils.Converters.vector_Point_to_Mat(pl);
				
				distMat.put(r, c, Core.norm(dmat, distType));
				
				System.out.println(distMat);
			}
		}
		
		/*
		 * 
		static float _apply(const Mat &set1, const Mat &set2, int distType, double propRank)
		{
		    // Building distance matrix //
		    Mat disMat(set1.cols, set2.cols, CV_32F);
		    int K = int(propRank*(disMat.rows-1));

		    for (int r=0; r<disMat.rows; r++)
		    {
		        for (int c=0; c<disMat.cols; c++)
		        {
		            Point2f diff = set1.at<Point2f>(0,r)-set2.at<Point2f>(0,c);
		            disMat.at<float>(r,c) = (float)norm(Mat(diff), distType);
		        }
		    }

		    Mat shortest(disMat.rows,1,CV_32F);
		    for (int ii=0; ii<disMat.rows; ii++)
		    {
		        Mat therow = disMat.row(ii);
		        double mindis;
		        minMaxIdx(therow, &mindis);
		        shortest.at<float>(ii,0) = float(mindis);
		    }
		    Mat sorted;
		    cv::sort(shortest, sorted, SORT_EVERY_ROW | SORT_DESCENDING);
		    return sorted.at<float>(K,0);
		}

		
		*/
	}

	static double[] subtract(double[] p1, double[] p2) {
		
		double[] d = new double[p1.length];
		
		for (int i=0; i<p1.length; i++) {
			double d1 = p1[i];
			double d2 = p2[i];
			d[i] = d1-d2;
		}
		
		return d;
		// TODO Auto-generated method stub
		
	}
}
