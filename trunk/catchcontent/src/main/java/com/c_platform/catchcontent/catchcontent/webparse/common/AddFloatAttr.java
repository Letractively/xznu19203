package com.c_platform.catchcontent.catchcontent.webparse.common;

import org.dom4j.Element;

public interface AddFloatAttr {
	final String IMG = "img"; // img标签
	final String A = "a"; // a标签
	final String STYLE = "style"; // style标签

	/**
	 * TODO 添加float:left属性
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点父节点
	 */
	void doAddFloatLeftAttr(Element eleChild, Element parentElement);
}
