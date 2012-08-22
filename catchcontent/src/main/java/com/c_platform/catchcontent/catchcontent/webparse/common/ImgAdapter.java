package com.c_platform.catchcontent.catchcontent.webparse.common;

import org.dom4j.Element;

public interface ImgAdapter {
	final String STYLE = "style"; // style 属性名
	final String IMG = "IMG"; // style 属性名
	final String CLASS = "CLASS"; // style 属性名

	void doAddBlockAttr(Element eleChild, Element parentElement,
			boolean needAddBlockAttr, double sreenWidth, double sreenHeight);
}
