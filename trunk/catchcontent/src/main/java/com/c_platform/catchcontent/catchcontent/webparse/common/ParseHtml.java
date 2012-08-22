/*
 * 文 件 名：ParseHtml.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：wei.jiang
 * 修改时间：2010-6-7
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.WebContext;
import com.c_platform.catchcontent.catchcontent.vips.CommonDivideUtils;
import com.c_platform.catchcontent.catchcontent.vips.NodeUtils;
import com.c_platform.catchcontent.catchcontent.vips.TableUtils;
import com.c_platform.catchcontent.catchcontent.vips.XslConverterUtils;
import com.c_platform.catchcontent.catchcontent.webparse.android.AndroidAttributeAdapter;
import com.c_platform.catchcontent.catchcontent.webparse.android.AndroidImgAdapter;
import com.c_platform.catchcontent.catchcontent.webparse.android.AndroidTextAreaAdapter;
import com.c_platform.catchcontent.catchcontent.webparse.android.AndroidULAdapter;

public abstract class ParseHtml implements ParseHtmlInf {
	private final String[] TAB_LIB = new String[] { "table", "tr", "th", "td" };

	static int no_acc = 0; // 累加标记

	protected final String IMG = "img"; // img标签

	protected final String A = "a";

	protected final String LI = "li"; // li标签

	protected final String H6 = "H6"; // H6标签

	protected final String BR = "br"; // br标签

	protected final String TABLE = "table"; // input标签

	protected final String INPUT = "input"; // input标签

	protected final String TYPE = "type"; // type标签

	protected final String NO = "NO"; // NO

	protected final String DIV = "div"; // div标签

	protected final String SPAN = "SPAN"; // span标签

	protected final String TEXT = "text"; // text标签

	protected final String TEXTAREA = "textarea"; // textarea标签

	protected final String PASSWORD = "password"; // password标签

	protected final String STYLE = "style"; // style 属性名

	protected final String[] UN_CLOSE_TAGS = { "img", "input", "br", "link",
			"meta" }; // 连接属性

	protected final String NAVIGATOR_COLUM = "navigator-colum"; // 是否是导航栏属性
	// true：是，false：不是

	protected final String[] BLOCK_TAG = { "div", "br", "table", "p", "ul",
			"td" }; // 会导致换行的元素

	protected ConcurrentHashMap<String, HashSet<String>> acceptElementTag; // 接受的标签

	protected HashSet<String> removeElementTag; // 删除的标签

	protected ConcurrentHashMap<String, String> replaceElementTag; // 需要替换的标签

	protected DocumentFactory m_factory;

	protected String url; // 当前请求URL

	protected WebContext ctx;

	protected double sreenWidth = 0; // 屏幕宽度

	protected double sreenHeight = 0; // 屏幕高度

	protected final String TAG_TYPE = "tagtype"; // 自定义标签属性

	protected final String TAG_TYPE_VALUE = "custom";
	protected final String[] UN_FORMAT_ATTRIBUTE = new String[] { "color",
			"background", "background-color", "font", "layer-background-color",
			"position", "background-position" };

	/** 替换单位的正则 */
	protected static final String REGEX_UNIT = "px|pt|em|%";

	protected static final String TABLE_STYLESHEET = "com/huawei/nwa/networkagent/xslt/table.xsl";

	protected static final String FORUM_STYLESHEET = "com/huawei/nwa/networkagent/xslt/ul.xsl";

	protected ULAdapter ulStyleAdapt = new AndroidULAdapter();

	protected AndroidImgAdapter androidImgAdapter = new AndroidImgAdapter();

	protected AndroidTextAreaAdapter androidTextAreaAdapter = new AndroidTextAreaAdapter();

	protected AttributeAdapter attrAdapter = new AndroidAttributeAdapter();
	protected String enginId;
	protected boolean alarmFlag = false;
	private static String[] NAVS = { "资讯", "新闻", "军事", "财经", "体育", "娱乐", "图片",
			"科技" };

	/**
	 * 
	 * TODO HTML过滤器主方法
	 * 
	 * @param resourceDom
	 *            源DOM
	 * @param elementConfigEntity
	 *            标签过滤规则
	 * @param url
	 *            请求URL
	 * @param sreenWH
	 *            手机屏幕分辨率
	 * @param ctx
	 *            上下文
	 * @return 过滤后的DOM
	 * @throws Exception
	 *             异常
	 */
	public Document doParse(Document resourceDom,
			ElementConfigEntity elementConfigEntity, String url,
			double[] sreenWH, WebContext ctx) throws Exception {
		// 初始化过滤引擎
		m_factory = DocumentFactory.getInstance();
		this.url = url;
		this.ctx = ctx;
		// this.enginId = this.ctx.getEngine_id();
		if (elementConfigEntity != null) {
			this.acceptElementTag = elementConfigEntity.getAcceptElementTag();
			this.replaceElementTag = elementConfigEntity.getReplaceElementTag();
			this.removeElementTag = elementConfigEntity.getRemoveElementTag();

			this.sreenWidth = sreenWH[1];
			this.sreenHeight = sreenWH[0];
			modifyElement(resourceDom.getRootElement()); // 执行过滤操作
		}
		return resourceDom;
	}

	/**
	 * 
	 * TODO 标签过滤执行器
	 * 
	 * @param element
	 *            当前节点
	 * @throws Exception
	 *             异常
	 */
	@SuppressWarnings("unchecked")
	public void modifyElement(Element element) throws Exception {
		List<Node> childrenList = element.content(); // 获取当前节点子内容
		for (int i = 0; i < childrenList.size(); i++) {
			Node child = childrenList.get(i); // element的孩子节点
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element eleChild = (Element) child; // element的孩子节点
				String tagName = eleChild.getName().toUpperCase();
				int tmpIndex;
				if (this.acceptElementTag != null
						&& this.acceptElementTag.containsKey(tagName)) {
					doAcceptProcess(tagName, eleChild, i, childrenList, element);
				} else if (this.replaceElementTag != null
						&& this.replaceElementTag.containsKey(tagName)) {
					tmpIndex = doReplaceProcess(tagName, eleChild, i,
							childrenList, element);
					if (tmpIndex != i) {
						i = tmpIndex;
						continue;
					}
				} else if (this.removeElementTag != null
						&& this.removeElementTag.contains(tagName)) {
					tmpIndex = doRemoveProcess(i, childrenList, element);
					if (tmpIndex != i) {
						i = tmpIndex;
						continue;
					}
				} else {
					tmpIndex = doOtherProcess(eleChild, i, childrenList,
							element);
					if (tmpIndex != i) {
						i = tmpIndex;
						continue;
					}
				}
				modifyElement((Element) child);
			}
		}
	}

	public int doReplaceProcess(String tagName, Element eleChild, int i,
			List<Node> childrenList, Element element) throws Exception {
		// 替换标签处理
		String newTag = this.replaceElementTag.get(eleChild.getName());
		eleChild.setName(newTag);
		String href = "#";
		if ("button".equalsIgnoreCase(tagName)) {
			eleChild.addAttribute("value",
					ParseHtmlUtil.getNodeTitle(eleChild, new StringBuffer())
							.toString()); // 针对<input
			// tyle="submit"
			// 的特殊处理
			eleChild.addAttribute("type", "submit");
			eleChild.clearContent();
		}
		// 过滤标签属性
		int attributeCount = eleChild.attributeCount();
		for (int j = 0; j < attributeCount; j++) {
			Attribute attr = eleChild.attribute(j);
			int index = attrAdapter.filterAttribute(attr, eleChild, j,
					this.acceptElementTag.get(newTag), enginId);
			if (index < j) {
				j = index;
				attributeCount--;
			}
		}
		return i;
	}

	public int doRemoveProcess(int i, List<Node> childrenList, Element element) {
		boolean tag = false;
		// 删除标签处理
		Element child = (Element) childrenList.get(i);
		if ((this.url.indexOf("app.wumii.com") > 0 && "script"
				.equalsIgnoreCase(child.getName()))
				|| "script".equalsIgnoreCase(child.getName())
				&& child.attributeValue("src") != null
				&& child.attributeValue("src").lastIndexOf(".txt") > 0) {
			// <script
			// src="http://files.qidian.com/Author6/1777445/30137717.txt"></script>
			return i;
		} else if ("form".equalsIgnoreCase(child.getName())
				&& "body".equalsIgnoreCase(element.getName())) {
			// 云加速模式不支持该网站，请使用互联网模式
			tag = true;
		}
		childrenList.remove(i--);
		if (tag & !alarmFlag) {
			Element eleAlarm;
			try {
				eleAlarm = DocumentHelper
						.parseText(
								"<DIV class='' style='clear:both;'><H2>您现在所用的云加速模式暂不支持该网页，请点击左上角模式切换按钮，切换至互联网模式继续浏览该网站。</H2></DIV>")
						.getRootElement();
				childrenList.add(0, eleAlarm);
				i++;
				alarmFlag = true;
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		element.setContent(childrenList);
		return i;
	}

	@SuppressWarnings("unchecked")
	protected int doOtherProcess(Element eleChild, int i,
			List<Node> childrenList, Element element) {
		// 只删除当前节点，但保留子项处理(包含文本内容)
		List<Node> nodeList = eleChild.content();
		if (nodeList != null) {
			childrenList.remove(i--);
			childrenList.addAll(i + 1, nodeList);
			element.setContent(childrenList);
		}
		return i;
	}

	/**
	 * 
	 * TODO 删除多余重复的BR
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param childrenList
	 *            当前节点的孩子集合
	 * @param i
	 *            指针
	 * @return 新的指针
	 */
	protected int doDeleteBR(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) {
		String tagName = eleChild.getName();
		if (BR.equalsIgnoreCase(tagName)) {
			// 删除重复的换行
			if (i > 0) {
				Element prevChildElement = ParseHtmlUtil.getPreElement(
						childrenList, i);
				if (prevChildElement != null
						&& ParseHtmlUtil.hasTag(prevChildElement.getName(),
								BLOCK_TAG)) {
					int preChildEleIndex = childrenList
							.indexOf(prevChildElement);
					boolean t = true;
					for (int p = preChildEleIndex + 1; p < i; p++) {
						if (!CommonDivideUtils
								.isNullContentElement(childrenList.get(p))) {
							t = false;
							break;
						}
					}
					if (t) {
						// 删除重复的换行
						if (childrenList.get(i - 1).getNodeType() == Node.TEXT_NODE) {
							childrenList.remove(i - 1);
							i--;
						}
						childrenList.remove(i);
						i--;
						parentElement.setContent(childrenList);
					}
				}
			}
			/*
			 * if ((i == 0 || i ==
			 * ParseHtmlUtil.getLastElementIndex(childrenList)) &&
			 * ParseHtmlUtil.hasTag(parentElement.getName(), BLOCK_TAG)) { //
			 * 删除div下第一个和最后一个<BR/> childrenList.remove(i--);
			 * parentElement.setContent(childrenList); }
			 */
		}
		return i;
	}

	/**
	 * 
	 * TODO table标签转换
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点的父节点
	 * @param childrenList
	 *            当前节点的孩子集合
	 * @param i
	 *            指针
	 * @return 新的指针
	 * @throws Exception
	 *             异常
	 */
	protected int doChangeTable(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) throws Exception {
		String tagName = eleChild.getName();
		if (TABLE.equalsIgnoreCase(tagName)) {

			if (TableUtils.isExistConverterElement(eleChild,
					new String[] { "TABLE" })) {
				Element resultEle = XslConverterUtils.useXslConverter(eleChild,
						TABLE_STYLESHEET);
				childrenList.add(i, resultEle);
				childrenList.remove(eleChild);
				parentElement.setContent(childrenList);
				i--;
			} else if (TableUtils.isExistConverterElement(eleChild,
					new String[] { "INPUT", "IMG", "UL" })) {
				Element resultEle = XslConverterUtils.useXslConverter(eleChild,
						FORUM_STYLESHEET);
				childrenList.add(i, resultEle);
				childrenList.remove(eleChild);
				parentElement.setContent(childrenList);
				i--;
			} else {
				int count = TableUtils.getTdOrThInTableNumber(eleChild);
				int textCount = NodeUtils.getTextLen(eleChild, 0);
				// int tdMaxCount = TableUtils.getTDMaxNumber(eleChild);
				// if ((tdMaxCount > 8) || (count > 0 && textCount / count >=
				// 7))
				if (count > 0 && textCount / count >= 7) {
					Element resultEle = XslConverterUtils.useXslConverter(
							eleChild, FORUM_STYLESHEET);
					childrenList.add(i, resultEle);
					childrenList.remove(eleChild);
					parentElement.setContent(childrenList);
					i--;
				}
			}
		}
		return i;
	}

	/**
	 * 
	 * TODO 标签折叠
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param i
	 *            指针
	 * @return 新的指针
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected int doFoldElement(Element eleChild, int i, HashSet<String> hashSet)
			throws Exception {
		/* 该标签为接受的标签时的逻辑处理 */
		String navigatorValue = eleChild.attribute(NAVIGATOR_COLUM) == null ? null
				: eleChild.attribute(NAVIGATOR_COLUM).getValue();
		if (navigatorValue != null && "true".equalsIgnoreCase(navigatorValue)) {
			// 折叠标签处理
			/*
			 * <h1 class="replicate" onClick="ShowMenu(this,'NO0')"> +
			 * 导航条标题</h1> <span id="NO0" style="display:none;"> ... </span>
			 */
			String strSpanId = NO + no_acc; // 定义折叠id
			String strH1Id = "repTitle" + no_acc; // 定义折叠id
			String imgId = "agentFoldIcon" + no_acc; // 定义折叠id
			String moreId = "moreNav" + no_acc;
			// 防止重复
			synchronized (this) {
				no_acc++;
			}

			// 定义折叠标签Title
			StringBuffer stringBuffer = new StringBuffer();
			StringBuffer navigatorTitle = new StringBuffer();
			StringBuffer titleBuffer = new StringBuffer(20);
			List<Node> tmpList = new ArrayList<Node>();
			modifyNavigator(eleChild, tmpList);
			if (tmpList != null && tmpList.size() > 0) {
				for (int k = tmpList.size() - 1; k > 0; k--) {
					ParseHtmlUtil.getNodeTitle(tmpList.get(0),
							stringBuffer.append(""));
					if (stringBuffer.length() >= 19) {
						break;
					}
					titleBuffer.append(tmpList.get(0).asXML());
					tmpList.remove(0);
				}
			}

			navigatorTitle.append("<DIV id='" + moreId + "'>");
			navigatorTitle.append(createMoreButton(imgId, strH1Id, strSpanId,
					"更多"));
			navigatorTitle.append(titleBuffer.toString());
			navigatorTitle.append("</DIV>");
			eleChild.clearContent();
			Element eleH6 = eleChild.addElement(H6);
			eleH6.addAttribute("class", "replicate");
			eleH6.addAttribute("id", strH1Id);
			eleH6.addEntity("SPAN", navigatorTitle.toString());

			// 定义折叠内容容器
			Element eleSpan = eleH6.addElement("NAV");
			eleSpan.addAttribute("id", strSpanId);
			eleSpan.addAttribute("class", "floatSpan");
			eleSpan.addAttribute("style", "display:none;");
			eleSpan.addAttribute(TAG_TYPE, TAG_TYPE_VALUE);
			eleChild.remove(eleChild.attribute(NAVIGATOR_COLUM));
			eleSpan.setContent(tmpList);
			i--;
		}
		return i;
	}

	private static String createMoreButton(String imgId, String strH1Id,
			String strSpanId, String more) {
		StringBuffer navitle = new StringBuffer();
		navitle.append("<A tagtype=\"custom\" ");
		navitle.append("id=\"");
		navitle.append(imgId);
		navitle.append("\" class=\"fold_title\" href=\"javascript:{var I='");
		navitle.append(strH1Id);
		navitle.append("';var L='");
		navitle.append(strSpanId);
		navitle.append("';var K='");
		navitle.append(imgId);
		navitle.append("';var P = document.getElementById(I);var N = document.getElementById(L);var J = document.getElementById(K);if (N.style.display == 'none') {N.style.display = 'block';J.innerHTML='返回<SPAN  class=&quot;icon3&quot;><SPAN  class=&quot;icon4&quot;></SPAN></SPAN>'} else {	N.style.display = 'none';	J.innerHTML='更多<SPAN  class=&quot;icon1&quot;><SPAN  class=&quot;icon2&quot;></SPAN></SPAN>';}} \" ");
		navitle.append(">");
		navitle.append(more);
		navitle.append("<SPAN  class='icon1'><SPAN class='icon2'></SPAN></SPAN>");
		navitle.append("</A>");
		return navitle.toString();
	}

	/**
	 * 
	 * TODO 添加方法注释
	 * 
	 * @param element
	 *            当前节点
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	void modifyNavigator(Element element, List<Node> newList) throws Exception {
		List<Node> nodeList = element.content();
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			boolean flag = false;
			if (nodeList.get(i).getNodeType() == Node.ELEMENT_NODE) {
				if (A.equalsIgnoreCase(nodeList.get(i).getName())) {
					for (String nav : NAVS) {
						if (nodeList.get(i).asXML().indexOf(nav) >= 0) {
							flag = true;
							newList.add(0, nodeList.get(i));
							break;
						}
					}
					if (!flag) {
						newList.add(nodeList.get(i));
					}
					continue;
				}
				modifyNavigator((Element) nodeList.get(i), newList);
			}
		}
	}

	/**
	 * 
	 * TODO 修复table转化后的属性
	 * 
	 * @param eleChild
	 *            当前节点
	 */
	protected void doRepairTableAttribute(Element eleChild) {
		if (ParseHtmlUtil.hasTag(eleChild.getName(), TAB_LIB)
				&& ParseHtmlUtil.isLegalTable(eleChild, TAB_LIB)) {
			eleChild.setName(SPAN);
			if (eleChild.attribute("colspan") != null) {
				eleChild.remove(eleChild.attribute("colspan"));
			}
			if (eleChild.attribute("rowspan") != null) {
				eleChild.remove(eleChild.attribute("rowspan"));
			}
			if (eleChild.attribute("cols") != null) {
				eleChild.remove(eleChild.attribute("cols"));
			}
			if (eleChild.attribute("rows") != null) {
				eleChild.remove(eleChild.attribute("rows"));
			}
		}
	}

	/**
	 * 
	 * TODO 正对连续的h3标签，处理为合并一行
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
	protected void doChangeH3Element(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) {
		String tagName = eleChild.getName();
		if ("h3".equalsIgnoreCase(tagName) && i > 0) {
			Element preChild = ParseHtmlUtil.getPreElement(childrenList, i);
			if (preChild != null
					&& "h3".equalsIgnoreCase(preChild.getName())
					&& !"&nbsp;".equalsIgnoreCase(childrenList.get(i - 1)
							.getText())) {
				String style = eleChild.attributeValue(STYLE) == null ? "float:left;padding:0 4px;"
						: "float:left;padding:0 4px;"
								+ eleChild.attributeValue(STYLE);
				eleChild.addAttribute(STYLE, style);
				eleChild.addAttribute(TAG_TYPE, TAG_TYPE_VALUE);
			}
		}
	}

	private List<Node> changeNavigatorOrder(List<Node> nodes) {
		List<Node> lists = new ArrayList<Node>();
		for (int i = 0; i < lists.size(); i++) {
			for (String nav : NAVS) {
			}
		}

		return null;
	}
}
