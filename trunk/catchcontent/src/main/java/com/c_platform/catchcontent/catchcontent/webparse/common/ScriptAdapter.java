package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public interface ScriptAdapter {
	final String SCRIPT = "script";

	int doProcessScript(Element eleChild, Element parentElement,
			List<Node> childrenList, int i, String url);
}
