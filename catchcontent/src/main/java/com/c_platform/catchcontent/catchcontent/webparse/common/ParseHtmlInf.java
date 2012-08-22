package com.c_platform.catchcontent.catchcontent.webparse.common;

/*
 * 文 件 名：ParseHtmlInf.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：wei.jiang
 * 修改时间：2010-6-7
 * 修改内容：新增
 */
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.WebContext;

public interface ParseHtmlInf {
	// 主方法，执行过滤操作
	public Document doParse(Document resourceDom,
			ElementConfigEntity elementConfigEntity, String url,
			double[] sreenWH, WebContext ctx) throws Exception;

	// 修改html元素
	void modifyElement(Element element) throws Exception;

	// 执行接受元素操作
	int doAcceptProcess(String tagName, Element eleChild, int i,
			List<Node> childrenList, Element element) throws Exception;

	// 执行替换元素操作
	int doReplaceProcess(String tagName, Element eleChild, int i,
			List<Node> childrenList, Element element) throws Exception;

	// 执行删除元素操作
	int doRemoveProcess(int i, List<Node> childrenList, Element element);
}
