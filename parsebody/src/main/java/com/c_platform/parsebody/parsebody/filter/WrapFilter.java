package com.c_platform.parsebody.parsebody.filter;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;

import com.c_platform.parsebody.parsebody.WebContext;

public class WrapFilter {
	final static String HEAD = "HEAD";
	final static String BODY = "BODY";
	final static String HTML = "HTML";

	public static WebContext wrap(WebContext ctx) {
		ArrayList<Node[]> result = (ArrayList<Node[]>) ctx.getContent();
		ArrayList<Document> blocks = new ArrayList<Document>();
		Document comDoc = null;
		Element els = null;
		QName qName = null;
		Element tmpEle = null;
		comDoc = DocumentHelper.createDocument();
		Element comxhtml = comDoc.addElement(HTML);
		comxhtml.addElement(HEAD).appendContent(ctx.getHtmlHead());
		comxhtml.addElement(BODY);
		removeScript(comxhtml);
		/**
		 * 余下正文合并
		 */
		for (Node[] ns : result) {
			Element xhtml = comxhtml.createCopy();
			Element body = xhtml.element("BODY");
			Element div = null;
			if (body != null) {
				div = DocumentHelper.createElement("DIV").addAttribute("class",
						"main");
				body.add(div);
			}
			Document doc_tmp = DocumentHelper.createDocument();
			doc_tmp.add(xhtml);
			for (Node n : ns) {
				if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
					els = (Element) n;
					qName = els.getQName();
					if (div != null
							&& !"FRAMESET".equalsIgnoreCase(qName.getName())) {
						tmpEle = div.addElement(qName);
					} else {
						tmpEle = xhtml.addElement(qName);
					}
					removeScript(els);
					tmpEle.appendAttributes(els);
					tmpEle.appendContent(els);
				} else if (n != null && n.getNodeType() == Node.TEXT_NODE) {
					if (div != null) {
						div.addText(n.getText());
					} else {
						xhtml.addText(n.getText());
					}
				}
			}
			blocks.add(doc_tmp);
		}
		ctx.setContent(blocks);
		return ctx;
	}

	/**
	 * 
	 * TODO 删除head中的script标签
	 * 
	 * @param element
	 */
	private static void removeScript(Element element) {
		List<Node> childrenList = element.content(); // 获取当前节点子内容
		for (int i = 0; i < childrenList.size(); i++) {
			Node child = childrenList.get(i); // element的孩子节点
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element eleChild = (Element) child; // element的孩子节点
				String tagName = eleChild.getName();
				if ("SCRIPT".equals(tagName)) {
					eleChild.detach();
					continue;
				}
				removeScript((Element) child);
			}
		}
	}

}
