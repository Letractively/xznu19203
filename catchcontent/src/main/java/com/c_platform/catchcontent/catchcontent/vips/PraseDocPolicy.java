/*
 * 文 件 名：PraseDocPolicy.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：江伟
 * 修改时间：2010-3-30
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.vips;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * TODO DOC值设定
 * 
 * @author 江伟
 * @version C02 2010-3-30
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class PraseDocPolicy {

	private static final String ISATOM = "isatom";

	private static final String DOC = "doc"; // 疑似分割概率

	private static final String ISVIRTUALTEXTNODE = "isvirtualtextnode"; // 是否是虚拟文本节点

	public int getTHRESHOLD() {

		return 20;
	}

	// 按照规则为node赋DOC值
	public void praseDoc(Element eleNode) {
		String tagName = eleNode.getName();
		int depth = Integer.valueOf(eleNode.attribute("depth").getValue());
		// 规则1：如果当前节点是原子节点则不再分割，DOC=0
		// 规则2：如果当前节点不是文本节点，并且没有任何有效的孩子节点，则删除该节点
		// 无效节点已经被删除
		if (eleNode.getNodeType() == eleNode.TEXT_NODE
				&& eleNode.nodeCount() == 0) {
			eleNode.getParent().remove(eleNode);
			return;
		}

		// 规则3：当前节点的孩子节点中含有原子节点，那么该节点DOC=10
		// if (hasAtom(eleNode) ){
		// eleNode.addAttribute(DOC, "10");
		// }

		// 规则4：如果当前节点只有一个有效的孩子结点，那么该结点的DOC等于他的孩子节点的DOC
		// 当此唯一的孩子节点为文本时，DOC=0
		// 注：无效节点已经被删除
		if (eleNode.elements().size() == 1) {
			Element e = (Element) eleNode.elements().get(0);
			if (e.attribute(DOC) != null) {
				eleNode.addAttribute(DOC, e.attribute(DOC).getValue());
				return;
			}
		} else if (eleNode.nodeCount() == 1) {
			eleNode.addAttribute(DOC, "0"); // 唯一节点是文本节点时
			return;
		}

		// 规则5：如果当前节点的所有孩子节点都是文本节点或虚拟文本节点或原子块，
		// 那么如果所有孩子节点的CSS样式相同，则DOC设置为0否则设置为10
		if (ChildrenIsAllTextOrAtomNode(eleNode)) {
			int maxDoc = getMaxDOC(eleNode);
			int num = getVirtualTextImgNum(eleNode, 0);
			int doc = maxDoc + num / depth;
			eleNode.addAttribute(DOC, String.valueOf(doc));
			// if ( isChildrenClassSame(eleNode) ){
			// eleNode.addAttribute(DOC, "0");
			// }else{
			// eleNode.addAttribute(DOC, "10");
			// }
			return;
		}
		// 规则6：如果当前结点的孩子结点中存在<HR>结点，DOC设置为50
		if (!"table".equalsIgnoreCase(tagName)
				&& !"tr".equalsIgnoreCase(tagName)
				&& !"td".equalsIgnoreCase(tagName) && isChildrenHR(eleNode)) {
			eleNode.addAttribute(DOC, "50");
			return;
		}
		// 规则7: 如果当前结点至少具有一个文本或者虚拟文本子结点或者原子子结点，
		// 并且该结点的相对大小小于门槛大小(定义为length)，
		// 则根据标签的不同，DOC的值设置为20-50（目前暂时全设为20）
		if (isChildOneText(eleNode)) {
			int num = getVirtualTextImgNum(eleNode, 0);
			if (num < getTHRESHOLD()) {
				if (!"table".equalsIgnoreCase(tagName)
						&& !"tr".equalsIgnoreCase(tagName)) {
					eleNode.addAttribute(DOC, "40");
					return;
				}
			}
		}

		// 规则8：如果当前结点的所有子结点中最大的大小也小于门槛大小，则DoC值根据HTML标签和结点大小设置
		if (getMaxChildNum(eleNode) <= getTHRESHOLD()) {
			getMaxDoc(eleNode);
			eleNode.addAttribute(DOC, String.valueOf(getMaxDoc(eleNode) + 10));
			return;
		}
		// 规则9：（对于td,th标签）如果前一个兄弟结点没有被分割，那么该结点也不会被继续分割
		// 判断该结点是否为td结点，然后判断其上一个或者下一个节点是否为td节点，然后将其DOC值同一
		if (unifyTdDoc(eleNode)) {
			return;
		}

		// 规则10 对于table,tr,td,th标签,不要分割此节点
		if ("table".equalsIgnoreCase(tagName)) {
			eleNode.addAttribute(DOC, String
					.valueOf(eleNode.elements().size() * 10 > 50 ? 50 : eleNode
							.elements().size() * 10));
			return;
		}
		if ("tr".equalsIgnoreCase(tagName)) {
			eleNode.addAttribute(DOC, String
					.valueOf(eleNode.elements().size() * 5 > 50 ? 40 : eleNode
							.elements().size() * 5));
			return;
		}
		if ("td".equalsIgnoreCase(tagName) || "th".equalsIgnoreCase(tagName)) {
			eleNode.addAttribute(DOC, "10");
			return;
		}
	}

	private int getMaxDoc(Element eleNode) {
		int maxDoc = 0;
		Element childEle;
		List<Node> children = eleNode.content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				childEle = (Element) child;
				maxDoc = Math.max(maxDoc, Integer.valueOf(childEle
						.attribute("doc") == null ? "0" : childEle.attribute(
						"doc").getValue()));
			}
		}
		return maxDoc;
	}

	private int getVirtualTextImgNum(Element eleNode, int result) {
		List<Node> children = eleNode.content();
		Element tmpChild;
		Attribute attribute;
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					if (!"".equals(child.getText().trim())) {
						result++;
					}
				} else {
					tmpChild = (Element) child;
					attribute = tmpChild.attribute("isvirtualtextnode");
					if (attribute != null
							&& "true".equalsIgnoreCase(attribute.getValue())) {
						result++;
					} else {
						result = getVirtualTextImgNum(tmpChild, result);
					}
				}
			}
		}
		return result;
	}

	// 统一TD标签的DOC值（规则9）
	// 将TD标签的DOC值改为前一个TD标签的DOC值
	// 判断该TD节点是其父节点的第几个子节点
	// 第一个不做处理
	// 第二个以后，查看其前一个兄弟结点是不是TD节点
	// 如果是，则将该节点的DOC修改为前一个兄弟结点的DOC
	// 如果不是，结束

	private boolean unifyTdDoc(Element e) {
		Element parentEle = e.getParent();
		Element preEle;
		int eIndex;
		if ("TD".equalsIgnoreCase(e.getQName().getName())
				|| "TH".equalsIgnoreCase(e.getQName().getName())) {
			eIndex = parentEle.indexOf(e);
			if (eIndex >= 1
					&& parentEle.node(eIndex - 1).getNodeType() == Node.ELEMENT_NODE) {
				preEle = (Element) parentEle.node(eIndex - 1);
				if (preEle.attribute(DOC) != null) {
					e.addAttribute(DOC, preEle.attribute(DOC).getValue());
					return true;
				} else {
					// 当前一个TD兄弟结点的DOC为null时，不做处理
				}
			}
		}
		return false;
	}

	// 取当前结点的所有子结点中最大的子节点的大小（规则8）

	private int getMaxChildNum(Node n) {
		int maxChildNum = 0;
		Element childEle;
		List<Node> children = ((Element) n).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					childEle = (Element) child;
					maxChildNum = Math.max(maxChildNum, getVirtualTextImgNum(
							childEle, 0));
				}
			}
		}
		return maxChildNum;
	}

	// 判断当前结点是否至少具有一个文本或者虚拟文本子结点或者原子子结点（规则7）

	private boolean isChildOneText(Node n) {
		List<Node> children = ((Element) n).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					return true;// 判断是不是文本节点
				}
				Element e = (Element) child;
				if (e.attribute(ISVIRTUALTEXTNODE) != null
						&& "true".equalsIgnoreCase(e.attribute(
								ISVIRTUALTEXTNODE).getValue())) {
					return true;// 判断是不是虚拟文本节点
				}
				if (e.attribute(ISATOM) != null
						&& !e.attribute(ISATOM).getValue().equals("false")) {
					return true;// 判断是不是原子子结点
				}
			}
		}
		return false;
	}

	// 判断当前结点的孩子结点中是否存在<HR>结点（规则6）

	private boolean isChildrenHR(Node n) {
		List<Node> children = ((Element) n).content();
		for (Node child : children) {
			if ("HR".equalsIgnoreCase(child.getName())) {
				return true;
			}
		}
		return false;
	}

	// 判断当前节点的所有孩子节点的CSS是不是相同（规则5）

	private boolean isChildrenClassSame(Node n) {
		List<Node> children = ((Element) n).content();
		if (children != null && children.size() > 0) {
			String firstCss = null;
			String secondCss = null;
			for (Node firstChild : children) {// 实际上此循环只执行第一个node
				for (Node secondChild : children) {
					if (firstChild.getNodeType() == Node.ELEMENT_NODE) {
						Element firstEleChild = (Element) firstChild;
						if (firstEleChild.attribute("class") != null) {
							firstCss = firstEleChild.attribute("class")
									.getValue();
						}
					}
					if (secondChild.getNodeType() == Node.ELEMENT_NODE) {
						Element secondEleChild = (Element) secondChild;
						if (secondEleChild.attribute("class") != null) {
							secondCss = secondEleChild.attribute("class")
									.getValue();
						}
					}
					if (!firstCss.equalsIgnoreCase(secondCss)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// 获取子节点中最大的DOC值

	private int getMaxDOC(Element element) {
		int maxDoc = -1;
		if (element == null) {
			return maxDoc;
		}
		for (Element ec : (List<Element>) element.elements()) {
			Attribute attr;
			if ((attr = ec.attribute(DOC)) != null) {
				maxDoc = Math.max(maxDoc, Integer.valueOf(attr.getValue()));
			}
		}
		return maxDoc;
	}

	// 判断当前节点的所有孩子节点都是文本节点或虚拟文本节点或者原子块（规则5）

	private boolean ChildrenIsAllTextOrAtomNode(Node n) {
		List<Node> children = ((Element) n).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element e = (Element) child;
				if ((e.attribute(ISVIRTUALTEXTNODE) == null || "false"
						.equalsIgnoreCase(e.attribute(ISVIRTUALTEXTNODE)
								.getValue()))
						&& (e.attribute(ISATOM) == null || "false"
								.equalsIgnoreCase(e.attribute(ISATOM)
										.getValue()))) {
					return false;
				}
			}
		}
		return true;
	}

	// 判断当前节点中是否含有原子节点
	// private boolean hasAtom(Element e){
	// if(e.elements().size() == 0) return false;
	// for(int p = 0; p < e.elements().size(); p++){
	// Attribute attr = ((Element)e.elements().get(p)).attribute(ISATOM);
	// if(attr!=null && "true".equalsIgnoreCase(attr.getValue())){
	// return true;
	// }
	// }
	// return false;
	// }
}
