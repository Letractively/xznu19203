package com.c_platform.catchcontent.catchcontent.vips;

import org.dom4j.Element;

public class AttributeUtils {

	private static final String[] NOT_VISIBLE_ATTR = {
			"display[\\s]*:[\\s]*none;?", "visibility[\\s]*:[\\s]*hidden;?" };

	// 判断一个元素是否含有不可见属性: style="display:none"或者style="display:none;"
	// style="visibility:hidden"或者style="visibility:hidden;"
	public static boolean isExistNotVisibleAttribute(Element element) {
		if (element.attribute("style") != null) {
			String attr = element.attributeValue("style");
			for (int i = 0, len = NOT_VISIBLE_ATTR.length; i < len; i++) {
				if (attr.matches(NOT_VISIBLE_ATTR[i])) {
					return true;
				}
			}
		}
		return false;
	}

}
