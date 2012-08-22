package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.HashSet;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.c_platform.catchcontent.catchcontent.webparse.common.AttributeAdapter;

public class AndroidAttributeAdapter implements AttributeAdapter {
	private static final String TAG_TYPE = "tagtype"; // 自定义标签属性
	private static final String IMG = "img"; // img标签

	public void doFilterAttribute(Element eleChild, HashSet<String> hashSet,
			String engineId) throws Exception {
		// 过滤标签属性
		if (eleChild.attribute(TAG_TYPE) == null
				|| !"custom".equalsIgnoreCase(eleChild.attributeValue(TAG_TYPE)
						.trim())) {
			for (int j = 0; j < eleChild.attributeCount(); j++) {
				// 执行属性过滤
				int index = filterAttribute(eleChild.attribute(j), eleChild, j,
						hashSet, engineId);
				if (index < j) {
					j = index;
				}
			}
		}

		// // 删除自定义TAG_TYPE属性
		// if (eleChild.attribute(TAG_TYPE) != null)
		// {
		// eleChild.remove(eleChild.attribute(TAG_TYPE));
		// }
	}

	public int filterAttribute(Attribute attribute, Element element, int index,
			HashSet<String> hashSet, String engineId) throws Exception {
		String tagName = element.getName();
		String attrName = attribute.getName().toLowerCase();
		String attrValue = attribute.getValue();
		// 对于style属性使用cssparse过滤统一处理
		// if ("style".equalsIgnoreCase(attrName))
		// {
		// String tmpStyleString = new
		// ParseCssInTag().doFilterStyleInTag(attrValue,engineId,tagName);
		// attribute.setValue(tmpStyleString);
		// return index;
		// }
		// 删除所有对img定义100%宽度的属性
		if (!IMG.equalsIgnoreCase(tagName)
				&& "width".equalsIgnoreCase(attrName)
				&& "100%".equals(attrValue)) {
			return index;
		}
		// // 用户对图片容器的缩放 由于很多图片无法显示而保留空白框，因此注释此段代码
		// if(IMG.equalsIgnoreCase(tagName)
		// && ("width".equalsIgnoreCase(attrName) ||
		// "height".equalsIgnoreCase(attrName)))
		// {
		// return index;
		// }

		if (hashSet.contains(attrName)) {
			if ("checked".equalsIgnoreCase(attrName)
					&& "".equals(attrValue.trim())) {
				element.addAttribute(attrName, "true");
			}
		} else {
			// 删除不支持的属性
			element.remove(attribute);
			--index;
		}
		return index;
	}
}
