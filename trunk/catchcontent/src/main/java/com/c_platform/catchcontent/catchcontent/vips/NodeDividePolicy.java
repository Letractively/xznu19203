package com.c_platform.catchcontent.catchcontent.vips;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.dom4j.Node;

/**
 * TODO
 * 
 * @author 江伟
 * @version C02 2010-3-30
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class NodeDividePolicy {
	VirtualTextDividePolicy vtdp = new VirtualTextDividePolicy();

	// inline之外的所有的结点我们统统称之为linebreak node

	public boolean isLineBreakNode(Node node) {
		return !this.vtdp.isInlineNode(node);
	}

	// 判断当前节点下是否存在HR元素孩子

	private boolean hasHRNodeInChildrens(Node node) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			return false;
		}
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if ("HR".equalsIgnoreCase(child.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	// 判断当前结点是不是可视结点，所谓可视结点，就是可以通过绘制边框显示出来的

	public boolean isNonVisualNode(Node node) {
		String tagName = node.getName();
		if (this.isVirtualTextNode(node)) {
			return false;
		}

		if (tagName.equalsIgnoreCase("CENTER")
				|| tagName.equalsIgnoreCase("DIV")
				|| tagName.equalsIgnoreCase("TR")
				|| tagName.equalsIgnoreCase("P")) {
			return true;
		}

		return false;
	}

	// 判断时候是虚拟文本节点

	public boolean isVirtualTextNode(Node node) {
		return this.vtdp.isVirtual(node);
		// return this.vtdp.isVirtualTextNode(node);
	}

	// 判断当前结点是否是有效的结点
	// 所谓有效的结点就是能够从浏览器中观察到的结点

	public boolean isValidNode(Node node) {
		Element eleNode = (Element) node;
		// 如果结点为隐藏则视为无效节点
		Pattern pattern;
		Matcher matcher;
		if (eleNode.attribute("style") != null) {
			pattern = Pattern
					.compile("(visibility[ \t]*:[ \t]*hidden[;]*)|(display[ \t]*:[ \t]*none[;]*)"); // display:
			// none;
			matcher = pattern.matcher(eleNode.attribute("style").getValue()
					.trim());
			while (matcher.find()) {
				return false;
			}
		}

		if (eleNode.attribute("class") != null) {
			pattern = Pattern.compile("hidden");
			matcher = pattern.matcher(eleNode.attribute("class").getValue()
					.trim());
			while (matcher.find()) {
				return false;
			}
		}

		if ("IMG".equalsIgnoreCase(eleNode.getName())) {
			return true;
		}

		if ("".equals(eleNode.getTextTrim())) {
			// 检查当前结点内部是否有IMG标签
			if (hasImgInChilds(node)) {
				return true;
			}
		}
		return true;
	}

	// 如果当前结点内部包含IMG标签，则肯定不是InValid结点

	public boolean hasImgInChilds(Node node) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return false;
		}
		if ("IMG".equalsIgnoreCase(node.getName())) {
			return true;
		}
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (hasImgInChilds(child)) {
					return true;
				} else {
					continue;
				}
			}
		}
		return false;
	}

	// 判断当前结点的CSS样式是否与所有的子结点都相同
	// 如果不相同,则继续分割

	public boolean isSameClass(Node node) {
		Element eleNode = (Element) node;
		String thisClass = null;
		if (eleNode.attribute("class") != null) {
			thisClass = eleNode.attribute("class").getValue();
		}
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				String childClass = null;
				Element eleChild = null;
				// 首先判断是不是文本节点，文本节点默认css为null
				if (child.getNodeType() != Node.TEXT_NODE) {
					eleChild = (Element) child;
					if (eleChild.attribute("class") != null) {
						childClass = eleChild.attribute("class").getValue();
					}
				}
				if (thisClass == null && childClass == null) {
					continue;
				}
				if (thisClass == null) {
					return false;
				}
				if (childClass == null) {
					return false;
				}
				if (eleChild.attribute("class") != null
						&& !thisClass.equalsIgnoreCase(eleChild.attribute(
								"class").getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	// 在子结点中是否存在Line Break结点

	public boolean hasLineBreakNodeInChildrens(Node node) {
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (isLineBreakNode(child))// 修改
				{
					return true;
				}
			}
		}
		return false;
	}
}
