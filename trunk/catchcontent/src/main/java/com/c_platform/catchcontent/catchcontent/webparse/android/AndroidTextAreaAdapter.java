package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.webparse.common.TextAreaAdapter;

public final class AndroidTextAreaAdapter implements TextAreaAdapter {
	/**
	 * 
	 * TODO 对textarea标签的特殊处理
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param childrenList
	 *            当前节点的孩子集合
	 * @param i
	 *            指针
	 * @return 新的指针
	 */
	public int doChangeTextArea(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) {
		// String tagName = eleChild.getName();
		// if (TEXTAREA.equalsIgnoreCase(tagName))
		// {
		// // 解决nekohtml对<textarea></textarea>补全成<textarea/>的BUG
		// if (!eleChild.hasContent())
		// {
		// DocumentFactory m_factory = DocumentFactory.getInstance();
		// Element ele = m_factory.createElement(QName.get(TEXTAREA,
		// parentElement.getNamespace()));
		// ele.add(m_factory.createText(" "));
		// int attributeCount = eleChild.attributeCount();
		// Attribute attr;
		// for (int j = 0; j < attributeCount; j++)
		// {
		// attr = eleChild.attribute(j);
		// ele.addAttribute(attr.getQName(), attr.getValue());
		// }
		// childrenList.add(i, ele);
		// childrenList.remove(i + 1);
		// parentElement.setContent(childrenList);
		// }else {
		// eleChild.addEntity("textArea", eleChild.getText());
		// }
		// }
		return i;
	}

	/**
	 * 
	 * TODO 改变TEXTAREA控件显示样式
	 * 
	 * @param eleChild
	 *            当前节点
	 */
	public void doChangeTextArea(Element eleChild) {
		String tagName = eleChild.getName();
		if (TEXTAREA.equalsIgnoreCase(tagName)
				&& (eleChild.attribute(TAG_TYPE) == null || !"custom"
						.equalsIgnoreCase(eleChild.attribute(TAG_TYPE)
								.getValue().trim()))) {
			eleChild.addAttribute("width", "99%");// 定义TEXTAREA控件宽度
			eleChild.addAttribute("height", "50px");// 定义TEXTAREA控件高度
			String style = eleChild.attributeValue(STYLE) == null ? "display:block;"
					: "display:block;" + eleChild.attributeValue(STYLE);
			eleChild.addAttribute(STYLE, style);
		}
	}
}
