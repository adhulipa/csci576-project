package edu.usc.csci576.mediaqueries.model;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Algorithm.*;

import edu.usc.csci576.mediaqueries.controller.ImageHandler;

public class SceneDetector {
	public static int SCENE_BEGIN_INDEX = 0;
	public static int SCENE_END_INDEX = 1;
	
	public static BufferedImage readFrame(String filepath, String filename, int frameNum) {
		String filePathString = String.format("%s/%s%03d.rgb", filepath, filename, frameNum);
		BufferedImage frame = ImageHandler.toBufferedImage(
				(ImageHandler.readImageFromFile(filePathString)),
				352, 288, BufferedImage.TYPE_3BYTE_BGR);
		
		return frame;
	}
	
	public static List<int[]> getScenes(String filepath, String filename, int numFrames) {
		
		List<int[]> scenes = new ArrayList<int[]>();
		BufferedImage currentFrame, prevFrame;
		double ecr;
		
		int sceneBeginIdx = 1;
		int sceneFinIdx = 2;
		
		for (int currentFrameIdx = 2; currentFrameIdx <= numFrames; currentFrameIdx++ ) {
			currentFrame = readFrame(filepath, filename, currentFrameIdx);
			prevFrame = readFrame(filepath, filename, currentFrameIdx-1);
			ecr = computerECR(currentFrame, prevFrame);
			if (ecr > 0.6) {
				int[] scene = new int[2];
				scene[SceneDetector.SCENE_BEGIN_INDEX] = sceneBeginIdx;
				scene[SceneDetector.SCENE_END_INDEX] = currentFrameIdx-1;
				scenes.add(scene);
				sceneBeginIdx = currentFrameIdx;
			}
		}
		
		// Add the final scene
		scenes.add(new int[]{sceneBeginIdx, numFrames});
		
		return scenes;
	}
	
	

	public static void main(String[] args) throws Exception{
		System.out.println("Scene detector");
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		
		List<int[]> scenes = getScenes("database/starcraft", "StarCraft", 600);
		
		for (int[] each : scenes) {
			System.out.print(Arrays.toString(each) + " ");
		}
		
		System.exit(1);;
		
		
		
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
		// Get two frames f1,f2
		// Get edges e1,e2
		// Dilate the edgeframe d1,d2
		// Invert the invertframe i1,i2
		// Exting pixels = e1 & di2
		// Entering pixels = e2 & di1
		// ECR = max(Xin_nâ�„Ïƒ_n,Xout_n-1 â�„Ïƒ_n-1 ) (X-in, X-out are entering and exiting px count
		// where Ïƒ_n is num of edge pixels in frame n (i.e. sum(edgeMat))
		
		
		// Step1 get the frames
		BufferedImage im1, im2;
		im1 = frames.get(2);
		im2 = frames.get(3);
		im1 = frames.get(48);
		im2 = frames.get(49);
		Mat imageFrame1 = ImageHandler.matify(im1);
		Mat imageFrame2 = ImageHandler.matify(im2);
		
		// Step2 compute edges
		Mat edgeFrame1 = new Mat();
		Mat edgeFrame2 = new Mat();
		Imgproc.Canny(imageFrame1, edgeFrame1, 100, 1);
		Imgproc.Canny(imageFrame2, edgeFrame2, 100, 1);

		// Step3 dilate the inverted edges
		// Imgproc.dilate(invertFrame1, invertFrame1, new Mat());
		// Imgproc.dilate(invertFrame2, invertFrame2, new Mat());
		Mat dilateFrame1 = new Mat();
		Mat dilateFrame2 = new Mat();
		Imgproc.dilate(edgeFrame1, dilateFrame1, Imgproc.getStructuringElement(
				Imgproc.MORPH_RECT, new Size(2, 2)));
		Imgproc.dilate(edgeFrame2, dilateFrame2, Imgproc.getStructuringElement(
				Imgproc.MORPH_RECT, new Size(2, 2)));

		// Step4 
		Mat invertFrame1 = new Mat();
		Mat invertFrame2 = new Mat();
		Core.bitwise_not(dilateFrame1, invertFrame1);
		Core.bitwise_not(dilateFrame2, invertFrame2);
		
		// Step5
		Mat exitPxls = new Mat();
		Mat entrPxls = new Mat();
		Core.bitwise_and(edgeFrame1, invertFrame2, exitPxls);
		Core.bitwise_and(edgeFrame2, invertFrame1, entrPxls);

		double X_out = Core.sumElems(exitPxls).val[0];
		double X_in = Core.sumElems(entrPxls).val[0];
		double rho_1 = Core.sumElems(edgeFrame2).val[0];
		double rho_2 = Core.sumElems(edgeFrame1).val[0];
		
		double ecr = Double.max(X_in/rho_2, X_out/rho_1);
		
		System.out.println(ecr);
		
		
		// OLD
		/*//Step4 Edge change calcualtion
		// 4.1 Hausdorff distance for motion compensation
		// 4.2 Compute edge change fraction
		// 4.3 Compute entering and exiting edges
		// TODO
		
		
		// Step5 compute diffs
		Mat d1 = new Mat();
		Mat d2 = new Mat();
		Core.absdiff(edgeFrame1,edgeFrame2,d1);
		Core.subtract(edgeFrame2,edgeFrame1,d2);
		*/
		// If the diff is close to zero then
		// scene hasnt changed
		
		// Step6: Plot all of above to visualze progress
		frame = im1;
		view.addImage(frame);
		frame = im2;
		view.addImage(frame);

		frame = ImageHandler.toBufferedImage(edgeFrame1);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(edgeFrame2);
		view.addImage(frame);

		frame = ImageHandler.toBufferedImage(invertFrame1);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(invertFrame2);
		view.addImage(frame);
		
		frame = ImageHandler.toBufferedImage(entrPxls);
		view.addImage(frame);
		frame = ImageHandler.toBufferedImage(exitPxls);
		view.addImage(frame);
		
		Mat diff = new Mat();
		Core.subtract(edgeFrame2, edgeFrame1, diff);
		frame = ImageHandler.toBufferedImage(diff);
		view.addImage(frame);
		Core.absdiff(edgeFrame2, edgeFrame1, diff);
		frame = ImageHandler.toBufferedImage(diff);
		view.addImage(frame);
		
//		frame = ImageHandler.toBufferedImage(edgeFrame1);
//		view.addImage(frame);
//		frame = ImageHandler.toBufferedImage(edgeFrame2);
//		view.addImage(frame);
//
//		frame = ImageHandler.toBufferedImage(invertFrame1);
//		view.addImage(frame);
//		frame = ImageHandler.toBufferedImage(invertFrame2);
//		view.addImage(frame);
//		
//		frame = ImageHandler.toBufferedImage(entrPxls);
//		view.addImage(frame);
//		frame = ImageHandler.toBufferedImage(exitPxls);
//		view.addImage(frame);
//		
//		Mat diff = new Mat();
//		Core.subtract(edgeFrame2, edgeFrame1, diff);
//		frame = ImageHandler.toBufferedImage(diff);
//		view.addImage(frame);
//		Core.absdiff(edgeFrame2, edgeFrame1, diff);
//		frame = ImageHandler.toBufferedImage(diff);
//		view.addImage(frame);
//		
		
		// ADI: Matify works correctly
		// TODO: Use canny edge detector to get edges of image
		// Use edge-based scene detectiona lgorithm by ranier linehart
		
		System.out.println(imageFrame1);
		
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
				
				Mat shortest = new Mat(distMat.rows(), 1, CvType.CV_32F);
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
	
	private static double computerECR(BufferedImage frame2,
			BufferedImage frame1) {
		// Alg exp
		// Get two frames f1,f2
		// Get edges e1,e2
		// Dilate the edgeframe d1,d2
		// Invert the invertframe i1,i2
		// Exting pixels = e1 & di2
		// Entering pixels = e2 & di1
		// ECR = max(Xin_nâ�„Ïƒ_n,Xout_n-1 â�„Ïƒ_n-1 ) (X-in, X-out are entering and
		// exiting px count
		// where Ïƒ_n is num of edge pixels in frame n (i.e. sum(edgeMat))

		Mat imageFrame1 = ImageHandler.matify(frame1);
		Mat imageFrame2 = ImageHandler.matify(frame2);

		// Step2 compute edges
		Mat edgeFrame1 = new Mat();
		Mat edgeFrame2 = new Mat();
		Imgproc.Canny(imageFrame1, edgeFrame1, 100, 1);
		Imgproc.Canny(imageFrame2, edgeFrame2, 100, 1);

		// Step3 dilate the inverted edges
		// Imgproc.dilate(invertFrame1, invertFrame1, new Mat());
		// Imgproc.dilate(invertFrame2, invertFrame2, new Mat());
		Mat dilateFrame1 = new Mat();
		Mat dilateFrame2 = new Mat();
		Imgproc.dilate(edgeFrame1, dilateFrame1, Imgproc.getStructuringElement(
				Imgproc.MORPH_RECT, new Size(2, 2)));
		Imgproc.dilate(edgeFrame2, dilateFrame2, Imgproc.getStructuringElement(
				Imgproc.MORPH_RECT, new Size(2, 2)));

		// Step4
		Mat invertFrame1 = new Mat();
		Mat invertFrame2 = new Mat();
		Core.bitwise_not(dilateFrame1, invertFrame1);
		Core.bitwise_not(dilateFrame2, invertFrame2);

		// Step5
		Mat exitPxls = new Mat();
		Mat entrPxls = new Mat();
		Core.bitwise_and(edgeFrame1, invertFrame2, exitPxls);
		Core.bitwise_and(edgeFrame2, invertFrame1, entrPxls);

		double X_out = Core.sumElems(exitPxls).val[0];
		double X_in = Core.sumElems(entrPxls).val[0];
		double rho_1 = Core.sumElems(edgeFrame2).val[0];
		double rho_2 = Core.sumElems(edgeFrame1).val[0];

		double ecr = Double.max(X_in / rho_2, X_out / rho_1);

		return ecr;

	}
}
