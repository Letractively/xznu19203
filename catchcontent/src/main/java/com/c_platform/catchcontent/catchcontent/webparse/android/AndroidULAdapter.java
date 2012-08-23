package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.HashSet;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.webparse.common.AttributeAdapter;
import com.c_platform.catchcontent.catchcontent.webparse.common.ParseHtmlUtil;
import com.c_platform.catchcontent.catchcontent.webparse.common.ULAdapter;

public final class AndroidULAdapter implements ULAdapter {
	protected final String TAG_TYPE = "tagtype"; // 自定义标签属性
	protected final String TAG_TYPE_VALUE = "custom";

	AttributeAdapter attrAdapter = new AndroidAttributeAdapter();

	public AndroidULAdapter() {

	}

	/**
	 * TODO 将较小的UL的eleChild替换为单行显示
	 * 
	 * @param eleChild
	 */
	public void doChangeLIElement(Element eleChild) {
		String tagName = eleChild.getName();
		if (eleChild.attributeValue(TAG_TYPE) != null
				&& LI.equalsIgnoreCase(tagName)
				&& !"UL".equalsIgnoreCase(eleChild.getParent().getName())
				&& ParseHtmlUtil.getTextLen(eleChild, 0) < 15) {
			String classValueString = eleChild.attributeValue("class");
			if (classValueString != null) {
				eleChild.addAttribute("class", "inlLi " + classValueString);
			} else {
				eleChild.addAttribute("class", "inlLi");
			}
			eleChild.addAttribute(TAG_TYPE, TAG_TYPE_VALUE);
		}
	}

	/**
	 * 
	 * TODO 将较小的UL的eleChild替换为单行显示
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param i
	 *            指针
	 * @return 新的指针
	 * @throws Exception
	 */
	public int doChangeUlElement(Element eleChild, Element parentElement,
			int i, HashSet<String> hashSet, String enginId) throws Exception {
		String tagName = eleChild.getName();
		if (UL.equalsIgnoreCase(tagName)) {
			attrAdapter.doFilterAttribute(eleChild, hashSet, enginId);
			if (eleChild.attributeValue(TAG_TYPE) == null
					&& ParseHtmlUtil.needChangeUL(eleChild, BIG_UL_BOUND)) {
				// 将较小的UL的eleChild替换为单行显示
				modifyUL(eleChild, hashSet, enginId);// 针对较小的UL合并行时做的调整
				String classValueString = eleChild.attributeValue("class");
				if (classValueString != null) {
					eleChild.addAttribute("class", "inlUL " + classValueString);
				} else {
					eleChild.addAttribute("class", "inlUL");
				}
			}
			eleChild.addAttribute(TAG_TYPE, TAG_TYPE_VALUE);
		} else {
			// 将较小的无UL的LI以内联元素显示
			new AndroidULAdapter().doChangeLIElement(eleChild);
		}
		return i;
	}

	/**
	 * 
	 * TODO 针对较小的UL合并行时做的调整
	 * 
	 * @param element
	 *            当前节点
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void modifyUL(Element element, HashSet<String> hashSet,
			String enginId) throws Exception {
		List<Node> nodeList = element.content();
		Node nc;
		Element ec;
		String name;
		for (int m = 0; m < nodeList.size(); m++) {
			nc = nodeList.get(m);
			if (nc.getNodeType() == Node.ELEMENT_NODE) {
				name = nc.getName();
				ec = (Element) nc;
				// 如果该孩子节点标签不是BR,收集该孩子节点
				if (BR.equalsIgnoreCase(name)) {
					nodeList.remove(m--);
					element.setContent(nodeList);
				}
				modifyUL(ec, hashSet, enginId);
			}
		}
	}
}
