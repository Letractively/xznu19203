package com.c_platform.catchcontent.catchcontent.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.c_platform.catchcontent.catchcontent.WebContext;

public class ForumUtil {
	private static final String SCRIPT = "//SCRIPT";

	/*
	 * 将Document文档转换为字符串
	 */
	public static String formateXml(Document document, String charset,
			boolean istrans) throws IOException {
		OutputFormat format = OutputFormat.createCompactFormat(); // 以紧凑格式输出
		format.setTrimText(false); // 不需要去除空格或换行
		format.setEncoding(charset); // 设置字符集
		format.setSuppressDeclaration(false); // 去除<?xml version="1.0"
		// encoding="UTF-8"?>
		format.setXHTML(true); // 设置为XHTML格式
		format.setExpandEmptyElements(true); // <tagName/> to
		// <tagName></tagName>.
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans); // 反转义
		xw.write(document);
		xw.flush();
		xw.close();
		return sw
				.toString()
				.replaceAll(
						"</\\s*(IMG|INPUT|BR|LINK|META|HR|FRAME|EMBED|AREA|BASE|BASEFONT|BGSOUND|COL)>",
						"");
	}

	/*
	 * 移除元素中包含有宽度或者高度属性
	 */
	@SuppressWarnings("unchecked")
	public static void removeHeightWidth(Element element, WebContext ctx)
			throws UnsupportedEncodingException {
		if (element == null) {
			return;
		}
		String tagName = element.getName();
		for (int i = element.attributeCount() - 1; i >= 0; i--) {
			if (element.attribute(i).getName().trim().equalsIgnoreCase("width")
					|| element.attribute(i).getName().trim().equalsIgnoreCase(
							"height")
					|| element.attribute(i).getName().trim().equalsIgnoreCase(
							"cols")) {
				element.remove(element.attribute(i));
			} else if (element.attribute(i).getName().trim().equalsIgnoreCase(
					"style")) {
				String style = element.attribute(i).getValue();
				if (style.indexOf("base64") == -1) {
					style = style.toLowerCase();
				}
				style = style
						.replaceAll(
								"(width|height)[\\s]*:[\\s]*(\\d+)(\\.\\d+)?(px|em|%|en|ex|pt|in|cm|mm|pc)[;]?",
								" ");
				element.remove(element.attribute(i));
				element.addAttribute("style", style);
			} else if ("img".equalsIgnoreCase(tagName)) {
				String real_srcString = element.attributeValue("real_src");
				if (real_srcString != null && !"".equals(real_srcString.trim())) {
					element.addAttribute("src", real_srcString);
				} else if ((real_srcString = element.attributeValue("lsrc")) != null
						&& !"".equals(real_srcString.trim())) {
					element.addAttribute("src", real_srcString);
				} else if ((real_srcString = element.attributeValue("file")) != null
						&& !"".equals(real_srcString.trim())) {
					element.addAttribute("src", real_srcString);
				}
			}
		}
		List<Element> children = element.elements();
		if (children != null && children.size() > 0) {
			for (Element child : children) {
				removeHeightWidth(child, ctx);
			}
		}
	}

	/*
	 * 移除元素中Style样式
	 */
	@SuppressWarnings("unchecked")
	public static Element removeStyle(Element element) {

		if (element == null) {
			return null;
		}
		for (int i = element.attributeCount() - 1; i >= 0; i--) {
			String e = element.attribute(i).getName().trim();

			if (e != null && e.equalsIgnoreCase("style")) {
				element.remove(element.attribute(i));
			}
		}
		List<Element> children = element.elements();
		if (children != null && children.size() > 0) {
			for (Element child : children) {
				if ((child.getTextTrim().equalsIgnoreCase("&nbsp;")// 如果标签里只有nbsp,则删除
				&& child.elements() == null)
						|| child.getName().equalsIgnoreCase("br")
						|| (child.getName().equalsIgnoreCase("p")
								&& child.getTextTrim().equalsIgnoreCase("") && child
								.elements() == null)) {
					if (child.asXML().indexOf("引用") != -1) {
						System.out.println("rrrrr");
					}
					child.detach();
				} else {
					removeStyle(child);
				}
			}
		}
		return element;
	}

	/**
	 * TODO 收集所有的script标签
	 * 
	 * @param element
	 * @return scriptList
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getScriptList(Element element) {
		if (element == null) {
			return null;
		}
		List<Element> scriptList = (List<Element>) element.selectNodes(SCRIPT);
		return scriptList;
	}

	/**
	 * TODO 收集script标签
	 * 
	 * @param element
	 * @param withSrc收集带src的还是不带src的
	 * @return scriptList
	 */
	public static List<Element> getScriptList(Element element, boolean withSrc) {
		if (element == null) {
			return null;
		}
		List<Element> scriptList = (List<Element>) element.selectNodes(SCRIPT);
		List<Element> result = new ArrayList<Element>();
		for (Element e : scriptList) {
			if (e.attribute("src") == null && !withSrc) {
				result.add(e);
			} else if (e.attribute("src") != null
					&& !e.attributeValue("src").equals("") && withSrc) {
				result.add(e);
			}
		}
		return result;
	}

	/*
	 * 删除<head>元素里面的<link>元素
	 */
	@SuppressWarnings("unchecked")
	public static Element removeLinkInHead(Element element) {
		List<Element> links = element.elements("LINK");
		if (links != null && links.size() > 0) {
			for (Element link : links) {
				if (link.attributeValue("type").indexOf("rss") == -1) {
					element.remove(link);
				}

			}
		}
		return element;
	}

	/*
	 * 同时删除<head>元素里面的<link>元素和<script>元素
	 */
	@SuppressWarnings("unchecked")
	public static Element removeLinkAndScriptInHead(Element element) {
		element = ForumUtil.removeLinkInHead(element);
		List<Element> scripts = element.elements("SCRIPT");
		if (scripts != null && scripts.size() > 0) {
			for (Element script : scripts) {
				element.remove(script.detach());
			}
		}
		return element;
	}
	
	/**
	 * 删除内部指定Tag
	 * @param element 待删除element
	 * @return
	 */
	public static Element removeTag(Element element, String tag)
	{
	    List<Element> brs = element.selectNodes("descendant::" + tag);
	    
	    if (null != brs && brs.size() > 0)
        {
            for (Element br : brs)
            {
                element.remove(br.detach());
            }
        }
        
        return element;
	}
}
