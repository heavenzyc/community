/**
 * @(#)QRCodeUtils.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.io;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * @author xu.jianguo
 * @date 2013-5-16 二维码工具类
 */
public class QRCodeUtils {
	private static final int MIN_SIZE = 50;
	private static final int MAX_ZIEE = 500;
	public static final Color DEFAULT_ON_COLOR = Color.BLACK;
	public static final Color DEFAULT_OFF_COLOR = Color.WHITE;
	  
	/**
	 * 创建二维码,并保存到文件中
	 * 
	 * @param content
	 * @param widthAndHeight
	 * @param descImage
	 * @author xu.jianguo
	 * @throws java.io.IOException
	 * @throws com.google.zxing.WriterException
	 */
	public static final void createQRCodeImage(String content,
			int widthAndHeight, String descImageName) throws IOException,
			WriterException {
		createQRCodeImage(content, widthAndHeight,DEFAULT_ON_COLOR, descImageName);
	}
	
	/**
	 * 创建二维码,并保存到文件中
	 * 
	 * @param content
	 * @param widthAndHeight
	 * @param descImage
	 * @author xu.jianguo
	 * @throws java.io.IOException
	 * @throws com.google.zxing.WriterException
	 */
	public static final void createQRCodeImage(String content,
			int widthAndHeight, Color qrColor,String descImageName) throws IOException,
			WriterException {
		BufferedImage bufferedImage = createQRCodeImage(content, widthAndHeight,qrColor);
		ImageIO.write(bufferedImage, "JPG", new File(descImageName)); 
	}
	
	/**
	 * 创建二维编返回二维码图像
	 * @param content
	 * @param widthAndHeight
	 * @return
	 * @throws java.io.IOException
	 * @throws com.google.zxing.WriterException
	 * @author  xu.jianguo
	 */
	public static final BufferedImage createQRCodeImage(String content,
			int widthAndHeight) throws IOException,
			WriterException {
		return createQRCodeImage(content, widthAndHeight, DEFAULT_ON_COLOR);
	}
	
	public static final BufferedImage createQRCodeImage(String content,
			int widthAndHeight,Color qrColor) throws IOException,
			WriterException {
		if (widthAndHeight < MIN_SIZE || widthAndHeight > MAX_ZIEE) {
			throw new IOException("二维码的长度必须介于50和500之间");
		}
		int tempWidthAndHeight = (int) (widthAndHeight * 1.5);
		content = new String(content.getBytes("GBK"), "iso-8859-1");
		MultiFormatWriter barcodeWriter = new MultiFormatWriter();
		BitMatrix matrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE,
				tempWidthAndHeight, tempWidthAndHeight);
		int[] enclosingRectangle = matrix.getEnclosingRectangle();
		BufferedImage bufferedImage = MatrixToImageWriter
				.toBufferedImage(matrix,new MatrixToImageConfig(qrColor.getRGB(), DEFAULT_OFF_COLOR.getRGB()));
	 
		//剪掉多余的空白边框
		bufferedImage = ImageUtils.cut(bufferedImage, new ImageUtils.Point(
				enclosingRectangle[0], enclosingRectangle[1]),
				enclosingRectangle[2], enclosingRectangle[3]);
		//适当的缩小到可以添加白边框的image
		bufferedImage = ImageUtils.scale(bufferedImage, widthAndHeight - 10,
				widthAndHeight - 10);
		//再次填充白边框
		bufferedImage = ImageUtils.fillBackGroup(bufferedImage, widthAndHeight,
				widthAndHeight, DEFAULT_OFF_COLOR, ImageUtils.Position.CENTER);
		return bufferedImage;
	}
	
	/**
	 * 创建二维码,
	 * 填充log,
	 * 并保存到文件中
	 * @param content
	 * @param widthAndHeight
	 * @param descImageName
	 * @param topLogImage
	 * @throws java.io.IOException
	 * @throws com.google.zxing.WriterException
	 * @author  xu.jianguo
	 */
	public static final void createQRCodeImage(String content,
			int widthAndHeight, String descImageName,BufferedImage topLogImage) throws IOException,
			WriterException {
		createQRCodeImage(content, widthAndHeight, descImageName,DEFAULT_ON_COLOR,topLogImage);
	}
	
	public static final void createQRCodeImage(String content,
			int widthAndHeight, String descImageName,Color qrColor,BufferedImage topLogImage) throws IOException,
			WriterException {
		BufferedImage bufferedImage = createQRCodeImage(content, widthAndHeight,qrColor,topLogImage);
		ImageIO.write(bufferedImage, "JPG", new File(descImageName)); 
	}
	
	
	/**
	 * 创建二维码,填充log
	 * 返回二维码图像
	 * @param content
	 * @param widthAndHeight
	 * @param topLogImage
	 * @return
	 * @throws java.io.IOException
	 * @throws com.google.zxing.WriterException
	 * @author  xu.jianguo
	 */
	public static final BufferedImage createQRCodeImage(String content,
			int widthAndHeight,BufferedImage topLogImage) throws IOException,
			WriterException {
		return createQRCodeImage(content, widthAndHeight, DEFAULT_ON_COLOR, topLogImage);
	}
	
	/**
	 * 创建二维码,填充log
	 * 返回二维码图像
	 * @param content
	 * @param widthAndHeight
	 * @param topLogImage
	 * @return
	 * @throws java.io.IOException
	 * @throws com.google.zxing.WriterException
	 * @author  xu.jianguo
	 */
	public static final BufferedImage createQRCodeImage(String content,
			int widthAndHeight,Color qrColor,BufferedImage topLogImage) throws IOException,
			WriterException {
		BufferedImage image  = createQRCodeImage(content, widthAndHeight,qrColor);
		fillBackGroup(topLogImage, image);
		return image; 
	}
	/**
	 * 填充背景色
	 * @param topImage
	 * @param backWidth
	 * @param backHeight
	 * @param backGroundColor
	 * @param topImgPosition
	 * @return
	 * @author  xu.jianguo
	 */
	private static void fillBackGroup(BufferedImage topImage,BufferedImage backgroundImage){
		int backWidth = backgroundImage.getWidth();
		int backHeight = backgroundImage.getHeight();
		topImage  = ImageUtils.scale(topImage, (int)(backWidth*0.2), (int)(backHeight*0.2));
		int topWidth = topImage.getWidth();
		int topHeight = topImage.getHeight();
		int[] topImageArray = new int[topWidth * topHeight];
		topImageArray = topImage.getRGB(0, 0, topWidth, topHeight,
				topImageArray, 0, topWidth);
		int topX = getXPosition(topWidth, backWidth);
		int topY = getYPosition(topHeight, backHeight);
		backgroundImage.setRGB(topX, topY, topWidth,
				topHeight, topImageArray, 0, topWidth);
	}
	
 
	private static int getXPosition(int topWidth,int backWidth) {
		return (int) ( (float) backWidth / 2 - (float) topWidth / 2);
	}
	
	private static int getYPosition(int topHeight,int backHeight) {
		 
		return (int) ((float)backHeight / 2 - (float)topHeight / 2);
	}
 
}
