package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public interface TextAreaAdapter {
	final String TEXTAREA = "textarea"; // textarea标签
	final String TAG_TYPE = "tagtype"; // 自定义标签属性
	final String STYLE = "style"; // style 属性名

	int doChangeTextArea(Element eleChild, Element parentElement,
			List<Node> childrenList, int i);

	void doChangeTextArea(Element eleChild);
}
