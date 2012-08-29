package com.c_platform.parsebody.parsebody.vips;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public class NodeUtils {
	// 属于inline节点的元素类型
	// 所谓inline结点通常只影响文字的外观,因此对于布局本身影响不大
	private static final String[] INLINE_TEXT_NODES = { "A", "ABBR", "ACRONYM",
			"ADDRESS", "B", "BASE", "BDO", "BIG", "BLOCKQUOTE", "BR", "CITE",
			"CODE", "DFN", "EM", "FONT", "H1", "H2", "H3", "H4", "H5", "H6",
			"I", "IMG", "INPUT", "KBD", "LABLE", "LI", "Q", "S", "SAMP",
			"SELECT", "SMALL", "SPAN", "STRIKE", "STRONG", "SUB", "SUP",
			"TEXTAREA", "TT", "U", "VAR" };

	// 判断当前的结点是否是inline结点
	public static boolean isInlineNode(Node node) {
		// 判断当前结点的tag是否是下面的一些组合即可
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return true; // 如果一个结点是文本结点（非元素节点）,那么它自然就是inline结点
		}
		String tagName = ((Element) node).getName();
		if (CommonDivideUtils.hasTag(tagName, INLINE_TEXT_NODES)) {
			return true;
		}
		return false;
	}

	// 判断当前的结点是否是虚拟文本结点
	@SuppressWarnings("unchecked")
	public static boolean isVirtualTextNode(Node node) {
		// 如果节点是文本节点（非元素节点）,自然就是虚拟文本节点
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return true;
		boolean flag = false;
		// 如果节点是Inline节点,所有子节点或者是文本节点或者是虚拟文本节点,则该节点就是虚拟文本节点
		if (isInlineNode(node)) {
			List<Node> children = ((Element) node).content();
			for (Node child : children) {
				if (child.getNodeType() != Node.ELEMENT_NODE
						|| isVirtualTextNode(child)) {
					flag = true;
					continue;
				}
				flag = false;
			}
		}
		return flag;
	}

	// 判断一个节点所包含的子节点是否全部都是内联元素节点
	@SuppressWarnings("unchecked")
	public static boolean isAllContainInlineNode(Node node) {
		boolean flag = true;
		Element element = (Element) node;
		List<Element> children = element.elements();
		if (children != null) {
			for (Element child : children) {
				if (!isInlineNode(child)) {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	// 判断一个节点中含有的虚拟文本节点的个数
	@SuppressWarnings("unchecked")
	public static int getVirtualTextNodeNumber(Element element) {
		int count = 0;
		if (AttributeUtils.isExistNotVisibleAttribute(element)) {
			count += 0;
		} else {
			List<Element> elements = element.elements();
			for (Element temp : elements) {
				if (isVirtualTextNode(temp)) {
					count++;
				} else {
					count += getVirtualTextNodeNumber(temp);
				}
			}
		}
		return count;
	}

	// 获取一个节点中所包含的文本数量
	@SuppressWarnings("unchecked")
	public static int getTextLen(Element eleNode, int result) {
		if (eleNode == null)
			return result;
		if (eleNode.getNodeType() == Node.TEXT_NODE
				&& !"".equals(eleNode.getTextTrim())) {
			result += eleNode.getTextTrim().getBytes().length;
			return result;
		}
		List<Node> children = eleNode.content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child.getNodeType() == Node.TEXT_NODE
						&& !"".equals(child.getText().trim())) {
					result += child.getText().trim().getBytes().length;
				} else if (child.getNodeType() == Node.ELEMENT_NODE) {
					result = getTextLen((Element) child, result);
				}
			}
		}
		return result;
	}
}
