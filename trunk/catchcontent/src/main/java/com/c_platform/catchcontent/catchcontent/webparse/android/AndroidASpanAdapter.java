package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.vips.CommonDivideUtils;
import com.c_platform.catchcontent.catchcontent.webparse.common.ASpanAdapter;
import com.c_platform.catchcontent.catchcontent.webparse.common.ParseHtmlUtil;

public class AndroidASpanAdapter implements ASpanAdapter {
	public int doChangeASPANElement(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) {
		String tagName = eleChild.getName();
		if (A.equalsIgnoreCase(tagName)) {
			if (i > 0) {
				Element preChild = ParseHtmlUtil.getPreElement(childrenList, i);
				if (preChild != null
						&& (A.equalsIgnoreCase(preChild.getName()) || SPAN
								.equalsIgnoreCase(preChild.getName()))
						&& ParseHtmlUtil.haveOnlyTextNode(eleChild)) {
					if ((childrenList.get(i - 1).getNodeType() == Node.TEXT_NODE && CommonDivideUtils
							.isNullContentElement(childrenList.get(i - 1)))
							|| childrenList.get(i - 1).getNodeType() == Node.ELEMENT_NODE) {
						// 如果当前A标签的上一个元素标签也是A，那么在中间插入空格
						String cv = eleChild.attributeValue("class");
						if (cv != null) {
							eleChild.addAttribute("class", cv + " bk_A");
						} else {
							eleChild.addAttribute("class", "bk_A");
						}
					}
				}
			}
		} else if (SPAN.equalsIgnoreCase(tagName) && i > 0) {
			Element preChild = ParseHtmlUtil.getPreElement(childrenList, i);
			if (preChild != null
					&& (SPAN.equalsIgnoreCase(preChild.getName()) || A
							.equalsIgnoreCase(preChild.getName()))
					&& ParseHtmlUtil.haveOnlyTextNode(eleChild)) {
				if ((childrenList.get(i - 1).getNodeType() == Node.TEXT_NODE && CommonDivideUtils
						.isNullContentElement(childrenList.get(i - 1)))
						|| childrenList.get(i - 1).getNodeType() == Node.ELEMENT_NODE) {
					// 如果当前A标签的上一个元素标签也是A，那么在中间插入空格
					String cv = eleChild.attributeValue("class");
					if (cv != null) {
						eleChild.addAttribute("class", cv + " bk_A");
					} else {
						eleChild.addAttribute("class", "bk_A");
					}
				}
			}
		}
		return i;
	}
}