/*
 * 文 件 名：AddFloatAttr.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：wei.jiang
 * 修改时间：2010-6-18
 * 修改内容：新增
 */
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
