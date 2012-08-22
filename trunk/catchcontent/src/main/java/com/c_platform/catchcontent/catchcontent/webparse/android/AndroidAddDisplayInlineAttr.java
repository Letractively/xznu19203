package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.webparse.common.AddDisplayInlineAttr;
import com.c_platform.catchcontent.catchcontent.webparse.common.ParseHtmlUtil;

public class AndroidAddDisplayInlineAttr implements AddDisplayInlineAttr {
	/**
	 * TODO 添加display:inline属性
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param childrenList
	 *            当前节点的孩子集合
	 * @param i
	 *            指针
	 */
	public void doAddDisplayInlineAttr(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) {
		String tagName = eleChild.getName();
		if (ParseHtmlUtil.hasTag(tagName, HEAD_LINES) && i > 0) {
			// 如果有连续的h1或h2或h3或h4或h5或h6标签出现，则让他们显示在一行上
			Element preChild = ParseHtmlUtil.getPreElement(childrenList, i);
			if (preChild != null
					&& ParseHtmlUtil.hasTag(preChild.getName(), HEAD_LINES)
					&& preChild.attributeValue("class") != null
					&& !"rss_title".equalsIgnoreCase(preChild
							.attributeValue("class"))) {
				if (preChild.attributeValue("style") == null
						|| (preChild.attributeValue("style") != null && preChild
								.attributeValue("style").indexOf(
										"display:inline") < 0)) {
					preChild.addAttribute("style", "display:inline;");
				}
				if (eleChild.attributeValue("style") == null
						|| (eleChild.attributeValue("style") != null && eleChild
								.attributeValue("style").indexOf(
										"display:inline") < 0)) {
					eleChild.addAttribute("style", "display:inline;");
				}
				if (!"&nbsp;".equalsIgnoreCase(childrenList.get(i - 1)
						.getText())
						&& ParseHtmlUtil.haveOnlyTextNode(eleChild)) {
					// 如果当前A标签的上一个元素标签也是A，那么在中间插入空格
					DocumentFactory m_factory = DocumentFactory.getInstance();
					Entity entity = m_factory.createEntity("blank", "&nbsp;");
					childrenList.add(i, entity);
					parentElement.setContent(childrenList);
				}
			}
		}
	}
}