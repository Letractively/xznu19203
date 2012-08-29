package com.c_platform.parsebody.parsebody.vips;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public class AddDocAttrForElementUtils {

	private static final String DOC = "doc"; // 疑似分割概率

	private static final int INCREMENT = 60;

	private static final int FIRST_THRESHOLD = 20; // 一级门槛的大小

	private static final int SECOND_THRESHOLD = 45; // 二级门槛的大小
	// private static final int TEXT_THRESHOLD = 500;

	private final static int PER_TEXT_NUMBER = 15;

	// private static final int PDOC = 15; //kaixin,hao123
	private static final int PDOC = 30; // sina(38),sohu(44),baidu(2),google(2),readnovel(13)
	// private static final int PDOC = 35; //ifeng(44),hongxiu(24)
	// private static final int PDOC = 45;
	// private static final int PDOC = 50; //qq

	private static final String[] NOT_DIVIDE_ELEMENT = { "FORM" };

	// 为Body里面所有的节点赋上DOC值
	@SuppressWarnings("unchecked")
	public static boolean addDocForNode(Node node) {
		// if (CommonDivideUtils.isNullContentElement(node)) { // 去掉页面中的空元素节点
		// if (node.getParent() != null){
		// node.getParent().remove(node);
		// }
		// }
		System.out.println(node.asXML());
		// 为所有的原子节点赋上DOC值0
		if (CommonDivideUtils.isAtomNode(node)) {
			((Element) node).addAttribute(DOC, "0");
			return false;
		} else if (NodeUtils.isAllContainInlineNode(node)
				&& !"body".equalsIgnoreCase(node.getName())) { // 为非原子节点但其子节点全部是虚拟文本节点赋上DOC值0
			((Element) node).addAttribute(DOC, "0");
			return false;
		} else if (NodeUtils.isVirtualTextNode(node)) { // 为除原子节点外的虚拟文本节点赋上DOC值0
			((Element) node).addAttribute(DOC, "0");
			return false;
		} else {
			boolean flag = false;
			Element element = (Element) node;
			List<Element> elements = element.elements();
			for (Element temp : elements) {
				if (addDocForNode(temp))
					flag = true;
			}
			addDocForBody(node); // 为Body里非原子节点和虚拟文本节点赋上DOC值
			if (flag)
				return true;
		}
		return false;
	}

	// 为Body里非原子节点和虚拟文本节点赋上DOC值
	@SuppressWarnings("unchecked")
	private static void addDocForBody(Node node) {
		Element element = (Element) node;
		if (element.attribute("doc") == null) {
			List<Element> elements = element.elements();
			for (Element temp : elements) {
				if (temp.attribute("doc") == null) {
					if (isAllContainDoc(temp)) {
						// 如果当前节点只有一个有效的孩子结点，那么该结点的DOC等于他的孩子节点的DOC
						if (temp.elements() != null
								&& temp.elements().size() == 1) {
							temp.addAttribute("doc", ((Element) temp.elements()
									.get(0)).attribute("doc").getValue());
						} else {
							int docmax = getMaxDocValue(temp);
							int inlineCount = NodeUtils
									.getVirtualTextNodeNumber(temp);

							if (inlineCount <= FIRST_THRESHOLD) { // 小于或者等于一级门槛的处理
								if (docmax == 0) {
									temp.addAttribute("doc", INCREMENT / 12
											+ "");
								} else {
									temp.addAttribute("doc", docmax + INCREMENT
											/ 6 + "");
								}
							} else if (inlineCount <= SECOND_THRESHOLD) { // 小于或者等于二级门槛的处理
								if (docmax == 0) {
									temp.addAttribute("doc", INCREMENT / 4 + "");
								} else {
									temp.addAttribute("doc", docmax + INCREMENT
											/ 4 + "");
								}
							} else {
								if (docmax == 0
										&& NodeUtils.getTextLen(temp, 0)
												/ inlineCount <= PER_TEXT_NUMBER) {
									temp.addAttribute("doc", docmax + INCREMENT
											/ 6 + "");
								} else {
									temp.addAttribute("doc", docmax + INCREMENT
											/ 2 + "");
								}
							}
						}
					}
				} else {
					addDocForBody(temp);
				}
			}
		}

	}

	// 收集属性值小于或者等于疑似分割概率(PDOC)的节点元素
	@SuppressWarnings("unchecked")
	public static void treeWalkByDoc(Node node, ArrayList<Node[]> result) {
		Element element = (Element) node;
		List<Element> elements = element.elements();
		for (Element temp : elements) {
			if (temp.attribute("doc") != null
					&& Integer.parseInt(temp.attribute("doc").getValue()) <= PDOC) {
				result.add(new Node[] { temp });
			} else if (CommonDivideUtils.hasTag(temp.getName(),
					NOT_DIVIDE_ELEMENT)) { // FORM元素的节点不能分割
				result.add(new Node[] { temp });
			} else {
				treeWalkByDoc(temp, result);
			}

		}
	}

	// 判断一个元素的子元素是不是全部含有DOC属性值
	@SuppressWarnings("unchecked")
	private static boolean isAllContainDoc(Element element) {
		boolean flag = true;
		List<Element> elements = element.elements();
		for (Element temp : elements) {
			if (temp.attribute("doc") == null) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 得到一个元素下面所有子元素的DOC属性值的最大值
	@SuppressWarnings("unchecked")
	private static int getMaxDocValue(Element element) {
		int max = 0;
		int tempvalue = 0;
		List<Element> elements = element.elements();
		for (int i = 0, len = elements.size(); i < len; i++) {
			Element temp = elements.get(i);
			if (temp != null && temp.attribute("doc") != null) {
				tempvalue = Integer.parseInt(temp.attribute("doc").getValue());
			}
			if (max < tempvalue)
				max = tempvalue;
		}
		return max;
	}
}
