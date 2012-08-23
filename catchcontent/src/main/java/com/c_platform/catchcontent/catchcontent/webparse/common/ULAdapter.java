package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.HashSet;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

public interface ULAdapter {
	final String SPAN = "SPAN";

	final String UL = "UL"; // ul标签

	final String LI = "LI"; // li标签

	final int BIG_UL_BOUND = 18; // ul内容大小界限

	final String DIV = "div"; // div标签

	final String STYLE = "style"; // style 属性名

	final String BR = "br"; // br标签

	DocumentFactory m_factory = DocumentFactory.getInstance();

	void doChangeLIElement(Element eleChild);

	int doChangeUlElement(Element eleChild, Element parentElement, int i,
			HashSet<String> hashSet, String enginId) throws Exception;

}
