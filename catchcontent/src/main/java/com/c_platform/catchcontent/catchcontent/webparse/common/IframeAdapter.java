package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public interface IframeAdapter {
	static final String IFRAME = "IFRAME"; // input标签
	static final String REGEX_UNIT = "px|pt|em|%";

	int doProcessIframe(Element eleChild, Element parentElement,
			List<Node> childrenList, int i);
}
