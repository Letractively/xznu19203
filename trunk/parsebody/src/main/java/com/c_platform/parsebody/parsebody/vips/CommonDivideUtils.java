package com.c_platform.parsebody.parsebody.vips;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public class CommonDivideUtils {

	private final static String[] ATOM_ELEMENTS = { "DIV", "TABLE" };// 原子块划分的标签

	private static final String[] NOT_NULL_ELEMENT = { "IMG", "BR", "HR",
			"INPUT", "SCRIPT", "STYLE", "LINK", "META" };

	private final static String TAG_TYPE = "tagtype"; // 自定义标签属性

	// 判断一个元素是不是空元素
	@SuppressWarnings("unchecked")
	public static boolean isNullContentElement(Node node) {
		// 当前节点是文本节点（非元素节点）,则判断该节点是否是一个空节点,有三种情况考虑
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			int length = node.getText().trim().length();
			if (length == 0) { // 情况1：文本是否是空字符串
				return true;
			} else if (length >= 1) { // 情况2：文本是否包含有特殊意义的空格字符
				boolean flag = true;
				for (int i = 0; i < length; i++) {
					char ch = node.getText().trim().charAt(i);
					if ((int) ch != 160 && !Character.isWhitespace(ch)) {
						flag = false;
						break;
					}
				}
				return flag;
			} else { // 情况3：其他
				return false;
			}
		}
		// 当前节点是元素节点
		Element element = (Element) node;
		// 去掉一些不包含文本内容但是有特殊意义的标签
		if (hasTag(element.getName(), NOT_NULL_ELEMENT)) {
			return false;
		}
		if (element.attribute(TAG_TYPE) != null
				&& "custom".equalsIgnoreCase(element.attribute(TAG_TYPE)
						.getValue().trim())) {
			return false;
		}
		boolean flag = true;
		List<Node> children = element.content();
		if (children != null) {
			if (children.size() == 0) { // 当前节点不含有子节点
				flag = true;
			} else {
				for (Node child : children) { // 遍历当前节点的每一个子节点
					if (!isNullContentElement(child))
						return false;
				}
			}
		}
		return flag;
	}

	// 判断当前节点是否是原子块
	@SuppressWarnings("unchecked")
	public static boolean isAtomNode(Node node) {
		boolean flag = false;
		// 如果当前节点的标签是ATOM_ELEMENTS,并且只含有文本节点则属于一个原子块
		if (node != null && hasTag(node.getName(), ATOM_ELEMENTS)) {
			if (((Element) node).elements() != null
					&& (((Element) node).elements().size() == 0)) {
				flag = true;
			}
			// 如果当前节点只含有一个元素节点
			if (((Element) node).elements() != null
					&& ((Element) node).elements().size() == 1) {
				// 获取当前节点的惟一子节点
				Element element = (Element) ((Element) node).elements().get(0);
				if (!hasTag(element.getName(), ATOM_ELEMENTS)) {
					return true;
				} else {
					if (!isAtomNode(element)) {
						return false;
					} else {
						flag = true;
					}
				}
			} else {
				// 如果当前节点含有多个元素节点,并且元素节点不是ATOM_ELEMENTS,则该节点是一个原子块
				List<Element> elements = ((Element) node).elements();
				for (Element element : elements) {
					if (hasTag(element.getName(), ATOM_ELEMENTS)) {
						flag = false;
						break;
					} else {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	// 判断数组strArray中是否包含有元素tagName
	public static boolean hasTag(String tagName, String[] strArray) {
		for (String item : strArray) {
			if (item.equalsIgnoreCase(tagName))
				return true;
		}
		return false;
	}
}
