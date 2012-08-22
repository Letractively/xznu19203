package com.c_platform.catchcontent.catchcontent.webparse.common;

import org.dom4j.Element;

public interface InputAdapter {
	final String INPUT = "input"; // input标签
	final String TYPE = "type"; // input标签
	final String IMAGE = "image"; // input标签
	final String IMG = "img"; // input标签

	int doChangeInputElement(Element eleChild, int i);
}
