package com.c_platform.catchcontent.catchcontent.scale;


public class ScaleByIm4java {
	/**
	 * 描述：图片压缩算法的实现。 根据原图宽高和设备宽高的比例关系确定压缩率。
	 * 
	 * @param w_h
	 *            ，设备宽和高
	 * @param imageWidth
	 *            ，原图宽度
	 * @param imageHeight
	 *            ，原图高度
	 * 
	 * @return scale，压缩率
	 */
	public double getScale(double[] w_h, double imageWidth, double imageHeight) {
		double SCALE_NUM_H = 0.9;
		double SCALE_NUM_L = 0.8;
		double width = w_h[0];
		double height = w_h[1];
		double scale;
		double wh = width / height;
		double image_wh = imageWidth / imageHeight;

		/*
		 * 如果原图片的宽高比例大于或等于设备的宽高比例 ， 则以宽度为准进行压缩。
		 */
		if (true) {
			if (imageWidth > width) // 原图片的宽度大于设备的宽度,则按照设备宽度压缩
			{
				scale = width / imageWidth;
			} else {
				if (imageWidth > width / 2) // 原图宽度大于设备宽度的一半，则按高压缩率0.8压缩
				{
					scale = SCALE_NUM_L;
				} else
				// 原图宽度小于设备宽度的一半，则按低压缩率0.9压缩
				{
					scale = SCALE_NUM_H;
				}
			}
			if (imageWidth * scale >= (width - 5))// 压缩后图宽大于设备宽，则按设备宽-15修正
			{
				scale = (width - 48) / imageWidth;
			}
//			if (imageHeight * scale > height) // 压缩后的图高大于设备高，则按设备高压缩
//			{
//				scale = scale * (height / (imageHeight * scale));
//			}
		}
		/*
		 * 如果原图的宽高比小于设备宽高比， 则以高度为准进行压缩。
		 */
//		else {
//			if (imageHeight > height) // 原图高大于设备高，则按设备高压缩
//			{
//				scale = height / imageHeight;
//			} else {
//				if (imageHeight > height / 2) // 原图高大于设备高的一半，则按高压缩率0.8压缩
//				{
//					scale = SCALE_NUM_L;
//				} else
//				// 原图高小于设备高的一半，则按低压缩率0.9压缩
//				{
//					scale = SCALE_NUM_H;
//				}
//			}
//			if (imageWidth * scale >= (width - 5))// 压缩后的图宽大于设备宽，则按设备宽-15修正
//			{
//				scale = (width - 48) / imageWidth;
//			}
//		}
		if (scale > 1.0) {
			scale = 1.0;
		}
		return scale;
	}
}
