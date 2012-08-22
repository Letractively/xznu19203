package com.c_platform.catchcontent.catchcontent.webparse.android;

import org.dom4j.Element;

import com.c_platform.catchcontent.catchcontent.webparse.common.InputAdapter;

public class AndroidInputAdapter implements InputAdapter {
	public AndroidInputAdapter() {
	}

	/**
	 * TODO 将 input type="img" 替换为 <img 标签
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param i
	 *            指针
	 * @return 新的指针
	 */
	public int doChangeInputElement(Element eleChild, int i) {
		// String tagName = eleChild.getName();
		// if (INPUT.equalsIgnoreCase(tagName))
		// {
		// String typeValue;
		// if ((typeValue = eleChild.attributeValue(TYPE)) != null)
		// {
		// if (IMAGE.equalsIgnoreCase(typeValue))
		// {
		// // <input type="img" 替换为 <img 标签
		// eleChild.setName(IMG);
		// eleChild.remove(eleChild.attribute(TYPE));
		// }
		// if ("submit".equalsIgnoreCase(typeValue)
		// || "button".equalsIgnoreCase(typeValue))
		// {
		// String tv = eleChild.attributeValue("value");
		// if (tv == null || "".equals(tv))
		// {
		// // 针对某些input标签没有value值只有title值的情况，则将title值赋给value属性
		// String titleString;
		// if ((titleString = eleChild.attributeValue("title")) != null)
		// {
		// eleChild.addAttribute("value", titleString);
		// }
		// }
		// }
		// }
		// }
		return i;
	}
}