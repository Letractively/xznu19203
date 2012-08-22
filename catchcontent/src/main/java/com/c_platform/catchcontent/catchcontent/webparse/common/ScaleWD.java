package com.c_platform.catchcontent.catchcontent.webparse.common;

import org.dom4j.Element;

import com.c_platform.catchcontent.catchcontent.scale.ScaleByIm4java;
import com.c_platform.catchcontent.catchcontent.util.StringUtil;

public class ScaleWD {
	private static final String REGEX_UNIT = "px|pt|em|%";
	private static final String REGEX_NUMSIC = "(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))";

	public void doScaleWD(Element element, double sreenWidth, double sreenHeight) {
		// 用户对图片容器的缩放 由于很多图片无法显示而保留空白框，因此注释此段代码
		if (element.attribute("height") != null
				&& element.attribute("width") != null) {
			String tmpString = element.attributeValue("height").replaceAll(
					REGEX_UNIT, "");
			double imgHeight = 0;
			double imgwidth = 0;
			if (StringUtil.isNumeric(tmpString)) {
				imgHeight = Double.valueOf(tmpString);
			}
			tmpString = element.attributeValue("width").toLowerCase()
					.replaceAll(REGEX_UNIT, "");
			if (StringUtil.isNumeric(tmpString)) {
				imgwidth = Double.valueOf(tmpString);
			}
			if (imgHeight != 0 && imgwidth != 0) {
				ScaleByIm4java scaleByIm4java = new ScaleByIm4java();
				double scale = scaleByIm4java.getScale(new double[] {
						sreenWidth, sreenHeight }, imgwidth, imgHeight);
				String newWidth = element.attribute("width").getValue()
						.replaceAll(REGEX_NUMSIC, imgwidth * scale + "");
				String newHeight = element.attribute("height").getValue()
						.replaceAll(REGEX_NUMSIC, imgHeight * scale + "");
				element.attribute("width").setValue(newWidth);
				element.attribute("height").setValue(newHeight);
			}
		}
	}
}
