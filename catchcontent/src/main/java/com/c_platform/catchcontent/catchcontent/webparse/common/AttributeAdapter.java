package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.HashSet;

import org.dom4j.Attribute;
import org.dom4j.Element;

public interface AttributeAdapter {
	/**
	 * 
	 * TODO 属性过滤
	 * 
	 * @param eleChild
	 *            当前节点
	 * @throws Exception
	 *             异常
	 */
	void doFilterAttribute(Element eleChild, HashSet<String> hashSet,
			String engineId) throws Exception;

	/**
	 * TODO 节点属性过滤
	 * 
	 * @param attribute
	 *            源属性
	 * @param element
	 *            当前节点
	 * @param index
	 *            指针
	 * @return 删除操作后的指针
	 * @throws Exception
	 *             异常
	 */
	int filterAttribute(Attribute attribute, Element element, int index,
			HashSet<String> hashSet, String engineId) throws Exception;
}
