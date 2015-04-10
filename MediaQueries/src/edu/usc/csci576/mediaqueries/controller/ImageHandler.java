package edu.usc.csci576.mediaqueries.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageHandler {

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
				byte ch = bytes[ind];

				int pix = 0xff000000 | ((ch & 0xff) << 16) | ((ch & 0xff) << 8)
						| (ch & 0xff);
				img.setRGB(x, y, pix);
				ind++;
			}
		}

		return img;
	}
}
