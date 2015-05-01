package edu.usc.csci576.mediaqueries.data;

import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageHandler {
	
	// Convert image to Mat
	public static Mat matify(BufferedImage im) {
		// Convert INT to BYTE
		// im = new BufferedImage(im.getWidth(),
		// im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		// Convert bufferedimage to byte array
		byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer())
				.getData();

		// Create a Matrix the same size of image
		Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		// Fill Matrix with image values
		image.put(0, 0, pixels);

		return image;

	}

	public static BufferedImage toBufferedImage(Mat m){
	      int type = BufferedImage.TYPE_BYTE_GRAY;
	      if ( m.channels() > 1 ) {
	          type = BufferedImage.TYPE_3BYTE_BGR;
	      }
	      int bufferSize = m.channels()*m.cols()*m.rows();
	      byte [] b = new byte[bufferSize];
	      m.get(0,0,b); // get all the pixels
	      BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	      final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      System.arraycopy(b, 0, targetPixels, 0, b.length);  
	      return image;

	  }

	public static byte[] readImageFromFile(String filename) {
		byte[] bytes = null;

		try {
			File file = new File(filename);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			bytes = new byte[(int) len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		return bytes;
	}

	public static BufferedImage toBufferedImage(byte[] bytes, int width,
			int height, int imgType) {
		BufferedImage img = new BufferedImage(width, height, imgType);
		int ind = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				byte r = bytes[ind];
				byte g = bytes[ind + height * width];
				byte b = bytes[ind + height * width * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8)
						| (b & 0xff);
				img.setRGB(x, y, pix);
				ind++;
			}
		}

		return img;
	}
}
