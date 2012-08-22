package com.c_platform.catchcontent.catchcontent.vips;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

public class NavigatorIdentityUtils {

	private final static int LIORLINK_NUMBER1 = 12;

	private final static int LIORLINK_NUMBER2 = 10;

	private final static int PER_TEXT_NUMBER1 = 15;

	private final static int PER_TEXT_NUMBER2 = 30;

	private final static int TOTAL_TEXT_NUMBER = 30;

	private final static String[] LI_A_ELEMENT = { "LI", "A" };

	private final static String[] IMG_ELEMENT = { "IMG" };

	private final static String[] BR_ELEMENT = { "BR" };

	private final static String[] FILTER_ELEMENT1 = { "FORM", "INPUT", "H1",
			"H2", "H3", "H4", "H5", "H6" };

	private final static String[] FILTER_ELEMENT2 = { "FORM", "INPUT", "H1",
			"H2", "H3", "H4", "H5", "H6", "IMG", "TABLE" };

	private final static String[] HN = { "H2", "H3", "H4", "H5", "H6" };

	private static final String NAVIGATOR_COLUM = "navigator-colum";

	// 获取一个元素中包含有LI或者A的子元素个数
	@SuppressWarnings("unchecked")
	private static int getElementNumber(Element element, String[] tags) {
		int count = 0;
		if (AttributeUtils.isExistNotVisibleAttribute(element)) {
			count += 0;
		} else {
			List<Element> elements = element.elements();
			for (Element temp : elements) {
				if (CommonDivideUtils.hasTag(temp.getName(), tags)) {
					count++;
				} else {
					count += getElementNumber(temp, tags);
				}
			}
		}
		return count;
	}

	// 判断元素中是否包含有不属于导航条元素包含的子元素
	@SuppressWarnings("unchecked")
	private static boolean isContainFilterElement(Element element,
			String[] filters) {
		boolean flag = false;
		if (CommonDivideUtils.hasTag(element.getName(), filters)) {
			return true;
		}
		List<Element> elements = element.elements();
		if (elements != null) {
			for (Element temp : elements) {
				if (CommonDivideUtils.hasTag(temp.getName(), filters)) {
					flag = true;
					break;
				} else {
					if (isContainFilterElement(temp, filters)) {
						flag = true;
						break;
					} else {
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	// 判断一个标签元素是否是导航条 情况1
	private static boolean isNavigator1(Element element) {
		boolean flag = false;
		int linkNum = getElementNumber(element, LI_A_ELEMENT);
		int textNum = NodeUtils.getTextLen(element, 0);
		int imageNum = getElementNumber(element, IMG_ELEMENT);

		if (linkNum >= LIORLINK_NUMBER1 && textNum / linkNum < PER_TEXT_NUMBER1
				&& !isContainFilterElement(element, FILTER_ELEMENT1)
				&& (imageNum <= 2 || imageNum >= 10)
				&& NodeUtils.getTextLen(element, 0) > TOTAL_TEXT_NUMBER) {
			flag = true;
		}
		return flag;
	}

	// 判断一个标签元素是否是导航条 情况2
	private static boolean isNavigator2(Element element) {
		boolean flag = false;
		int linkNum = getElementNumber(element, LI_A_ELEMENT);
		int textNum = NodeUtils.getTextLen(element, 0);
		int brNum = getElementNumber(element, BR_ELEMENT);
		if (linkNum >= LIORLINK_NUMBER2
				&& NodeUtils.getTextLen(element, 0) > TOTAL_TEXT_NUMBER
				&& textNum / linkNum < PER_TEXT_NUMBER2
				&& !isContainFilterElement(element, FILTER_ELEMENT2)
				&& brNum < 3) {
			flag = true;
		}
		return flag;
	}

	// 判断一个标签元素是否是导航条 情况3
	private static boolean isNavigator3(Element element) {
		boolean flag = false;
		int linkNum = getElementNumber(element, LI_A_ELEMENT);
		int textNum = NodeUtils.getTextLen(element, 0);
		int imgNum = getElementNumber(element, IMG_ELEMENT);
		if (linkNum >= LIORLINK_NUMBER2 && textNum == 0 && imgNum >= 5) {
			flag = true;
		}
		return flag;
	}

	// 判断一个标签元素的直接子元素是否含有不属于导航条元素包含的子元素
	@SuppressWarnings("unchecked")
	private static boolean isContainFilterElementInChild(Element element,
			String[] filters) {
		boolean flag = false;
		if (element.elements() != null) {
			List<Element> children = element.elements();
			if (children != null) {
				for (Element child : children) {
					if (CommonDivideUtils.hasTag(child.getName(), filters)) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}

	// 判断一个节点的前一个兄弟节点中是否含有HN
	private static boolean isSiblingHNElement(Element element) {
		boolean flag = false;
		Element parent = element.getParent();
		Element temp = null;
		if (parent != null) {
			int current = parent.indexOf(element);
			while (--current > 0) {
				if (parent.node(current) != null
						&& parent.node(current).getNodeType() == Node.ELEMENT_NODE) {
					temp = (Element) parent.node(current);
					break;
				}
			}
			if (temp != null) {
				if (CommonDivideUtils.hasTag(temp.getName(), HN)
						|| isContainFilterElementInChild(temp, HN)) {
					flag = true;
				}
			}
		}

		return flag;
	}

	// 为导航条添加标识属性：navigator-colum = true
	@SuppressWarnings("unchecked")
	public static void addAttrForElement(ArrayList<Node[]> result) {
		int size = result.size();
		for (int i = 0; i < size; i++) {
			Node[] nodes = result.get(i);
			if (nodes.length > 0) {
				if (isNavigator1((Element) nodes[0])) {
					if (!isSiblingHNElement((Element) nodes[0])) {
						((Element) nodes[0]).addAttribute(NAVIGATOR_COLUM,
								"true");
					}
				} else {
					Element element = (Element) nodes[0];
					List<Element> elements = element.elements();
					if (elements != null) {
						for (Element temp : elements) {
							if (isNavigator2(temp) || isNavigator3(temp)) {
								if (!isSiblingHNElement(temp)) {
									temp.addAttribute(NAVIGATOR_COLUM, "true");
								}
							} else {
								if (!isContainFilterElementInChild(temp,
										FILTER_ELEMENT1)) {
									Node[] tempNode = new Node[] { temp };
									ArrayList<Node[]> list = new ArrayList<Node[]>();
									list.add(tempNode);
									addAttrForElement(list);
								}
							}
						}
					}
				}
			}

		}
	}
}
