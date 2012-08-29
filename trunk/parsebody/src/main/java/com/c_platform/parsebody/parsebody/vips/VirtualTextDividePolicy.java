/*
 * 文 件 名：VirtualTextDividePolicy.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：江伟
 * 修改时间：2010-3-30
 * 修改内容：新增
 */
package com.c_platform.parsebody.parsebody.vips;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

/**
 * TODO 虚拟节点判定
 * 
 * @author 江伟
 * @version C02 2010-3-30
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class VirtualTextDividePolicy {
	private static final String[] INLINE_TEXT_NODES = { "A", "ABBR", "ACRONYM",
			"ADDRESS", "B", "BASE", "BDO", "BIG", "BLOCKQUOTE", "BR", "CITE",
			"CODE", "DFN", "EM", "FONT", "H1", "H2", "H3", "H4", "H5", "H6",
			"I", "IMG", "INPUT", "KBD", "LABLE", "LI", "Q", "S", "SAMP",
			"SELECT", "SMALL", "SPAN", "STRIKE", "STRONG", "SUB", "SUP",
			"TEXTAREA", "TT", "U", "VAR" };

	public boolean isVirtualTextNode(Node node) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			return true;
		}
		String tagName = node.getName();
		if (tagName.equalsIgnoreCase("LI")
				|| tagName.equalsIgnoreCase("SELECT")
				|| tagName.equalsIgnoreCase("MARQUEE")
				|| tagName.equalsIgnoreCase("FORM")) {
			return true;
		}

		// 如果当前结点中

		if (tagName.equalsIgnoreCase("UL") || tagName.equalsIgnoreCase("OL")) {
			return false;
		}
		// 如果当前结点是虚拟文本结点,
		if (isRealVirtualTextNode(node)) {
			return true;
		}

		// 对当前结点的所有孩子结点进行处理,如果当前的孩子结点中存在一个
		// LineBreak结点,则返回false

		// 如果当前结点并不是虚拟结点,但是它的第一个孩子结点数目不为1的结点是虚拟结点
		// 那么该结点也是虚拟结点
		Node tmpNode = getNonOneChildNode(node);
		if (isRealVirtualTextNode(tmpNode)) {
			return true;
		}

		return false;
	}

	// 获取该给定结点中第一个孩子结点不为1的结点
	// 如果当前结点是文本接点，直接返回

	private Node getNonOneChildNode(Node node) {
		Element childEleNode = (Element) node;
		while (hasOnlyOneNoneText(childEleNode)) {
			childEleNode = (Element) childEleNode.elements().get(0);
			if (isVirtualTextNode(childEleNode)) {
				break;
			}
		}
		return childEleNode;
	}

	private boolean hasOnlyOneNoneText(Element element) {
		boolean result = false;
		if (element.elements().size() > 1 || element.elements().size() < 1) {
			return result;
		}
		List<Node> children = element.content();
		for (int p = 0; p < children.size(); p++) {
			Node n = children.get(p);
			if (n.getNodeType() == Node.TEXT_NODE) {
				if ("".equals(n.getText().trim())) {
					continue;
				}
				return result;
			}
		}
		result = true;
		return result;
	}

	// 判断当前结点是否是虚拟文本结点
	// 如果当前结点是inlineNode,并且它的内部的所有结点都是inline node
	// 则该结点就是虚拟文本结点

	// 判断当前结点的孩子结点全部是虚拟文本结点
	// 一个结点是虚拟文本结点，必须满足下面的几个条件
	// 1.所有结点都是文本结点
	// 2.如果一个结点是虚拟文本结点，该结点的父结点不是inline结点，
	// 但是该结点的父结点所有孩子结点都是虚拟文本节点，那么它的父结点也是
	// 虚拟文本结点

	// 以当前结点为根结点,如果当前结点的孩子结点中出现一个linebreak结点,则该结点并不是
	// RealVirtualText结点

	private boolean isRealVirtualTextNode(Node node) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			return true;
		}
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (isVirtual(child)) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	public boolean isVirtual(Node node) {
		if (!isInlineNode(node)) {
			return false;
		} // 如果该节点标签不是内联元素，则不是虚拟文本节点
		if (node.getNodeType() == Node.TEXT_NODE) {
			return true;
		}
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (isVirtual(child)) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	// 判断当前的结点是否是inline结点
	// the DOM node with inline text HTML tags, which affect that appearence of
	// text and can be applied to a string
	// of characters without introcducing line break;,such as <B> <BIG> <EM>
	// inline结点通常只影响文字的外观,因此对于布局本身影响不大

	public boolean isInlineNode(Node node) {
		// 判断当前结点的tag是否是下面的一些组合即可
		if (node.getNodeType() == Node.TEXT_NODE) {
			return true; // 如果一个结点是文本结点，那么它自然就是虚拟文本结点
		}
		String tagName = node.getName();
		if (hasTag(tagName, INLINE_TEXT_NODES)) {
			return true;
		}
		return false;
	}

	private boolean hasTag(String tagName, String[] strArray) {
		for (String item : strArray) {
			if (item.equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return false;
	}
}
