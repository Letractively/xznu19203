/*
 * 文 件 名：ASpanAdapter.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：wei.jiang
 * 修改时间：2010-6-18
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

public interface ASpanAdapter {
	final String A = "a";
	final String SPAN = "SPAN"; // span标签
	DocumentFactory m_factory = DocumentFactory.getInstance();

	/**
	 * TODO 改变a,span标签显示样式
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param childrenList
	 *            当前节点的孩子结合
	 * @param i
	 *            指针
	 * @return 新的指针
	 */
	int doChangeASPANElement(Element eleChild, Element parentElement,
			List<Node> childrenList, int i);
}
