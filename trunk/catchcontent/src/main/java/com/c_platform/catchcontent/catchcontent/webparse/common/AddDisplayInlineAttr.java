package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public interface AddDisplayInlineAttr {
	final String[] HEAD_LINES = { "H2", "H3", "H4", "H5", "H6" }; // 会导致换行的元素

	/**
	 * TODO 添加display:inline属性
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param childrenList
	 *            当前节点的孩子集合
	 * @param i
	 *            指针
	 */
	void doAddDisplayInlineAttr(Element eleChild, Element parentElement,
			List<Node> childrenList, int i);
}
