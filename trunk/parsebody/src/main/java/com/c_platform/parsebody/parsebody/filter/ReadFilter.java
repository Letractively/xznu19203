package com.c_platform.parsebody.parsebody.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.parsebody.parsebody.WebContext;
import com.c_platform.parsebody.parsebody.denoise.LevenshteinDistance;
import com.c_platform.parsebody.parsebody.sequence.SequenceUtils;
import com.c_platform.parsebody.parsebody.util.Base64;

public class ReadFilter {
	private static final double TEXTLENGTHWEIGHT = 0.1; // 文本长度的权重 50/100/200

	private static final double SIMWITHTITLEWEIGHT = 0; // 与标题相似度的权重 0.01

	private static final double TEXTPCOUNTWEIGHT = 5;

	private static final double PIMAGECOUNTWEIGHT = 5; // 正文中图片的权重
	
	public static HashMap<String, Element> mapLink = new HashMap<String, Element>();
	
	private static final String[] keepTags = { "p", "b", "small", "big", "i",
			"tt", "br", "center", "em", "strong", "dfn", "code", "samp", "div",
			"span", "h1", "h2", "img", "font", "label", "td", "tr", "table",
			"tbody", "a", "mutilpagenav" };

	private static final String[] keepTagsSecond = { "p", "b", "i", "br", "em",
			"strong", "font", "table", "td", "tt" };

	public static WebContext read(WebContext ctx) {
		String url = ctx.getUrl();

		if (ctx.getContent() == null) {
			return null;
		}
		ArrayList<Node[]> blocks = (ArrayList<Node[]>) ctx.getContent();
		if (blocks == null) {
			return null;
		}
		System.out.println("=============阅读模式=============");
		ArrayList<Node[]> readNodeList = new ArrayList<Node[]>();
		double readWeightNode = 0;
		Node readNode = null;
		HashMap<String, Element> elementHashMap = new HashMap<String, Element>();
		double weightNode = 0;
//		Element div = DocumentHelper.createElement("div").addAttribute("class", "rssmain");
		for (Node[] node : blocks) {
			for (int i = 0; i < node.length; i++) {
				 if (node[i] != null) {
					// pImageCount[0] = 0;
					// pImageCount[1] = 0;
					// pImageCount[2] = 0;
					// 20111121huchangan将pImageCount改为局部变量，将此对象传入getElementListPTextLength方法
					int pImageCount[] = new int[3];
					
					pImageCount = getElementListPTextLength((Element) node[i],
							ctx, pImageCount);
					weightNode = weightNode((Element) node[i], ctx, pImageCount);

					if (readWeightNode <= weightNode) {
						readNode = node[i];
						readWeightNode = weightNode;
					}

					elementHashMap = getElementNextLink((Element) node[i], ctx);
				}
			}
		}
		if (readNode != null) {
			
			// 删除标题
			readNode = addTitleHNode(ctx, readNode);
			// 二次取最大节点
			readNode = getSencondMaxReadNode(readNode, ctx);
			// 对下一页的特殊处理
			elementHashMap = dealWidthNextPage(url, elementHashMap,
					(Element) readNode);
			removeTag((Element) readNode, keepTags, ctx);
			if (elementHashMap != null && elementHashMap.size() != 0) {
				if (readNode != null) {
					readNode = addHrefToReadNode(ctx, elementHashMap, readNode);
					elementHashMap.clear();
					mapLink.clear();
				}
			}
			// 增加退出阅读模式
			// readNode = addQuitReadNode(ctx, readNode);

			// 增加横线
//			readNode = addLineNode(readNode);

			// 纵横书城 有乱码没有处理需要处理 纵横小说 正文部分的span Css 样式是隐藏

			if (isCompareTwoText("zongheng", url)) {
				readNode = removeSpanForZongheng(readNode);
			}

			Element elementHTitle = getTitleElement(ctx, readNode);
			Node[] nodeAdd = new Node[1];

//			nodeAdd[0] = getElementTitle(elementHTitle, ctx, readNode);
//			nodeAdd[1] = DocumentHelper.createElement("DIV").addAttribute("class", "line");
//			nodeAdd[2] = DocumentHelper.createElement("DIV").addAttribute("class", "line1");
			nodeAdd[0] = readNode;
			readNodeList.add(nodeAdd);
			ctx.setContent(readNodeList);
		}
		return ctx;
	}
	
	
	@SuppressWarnings("unchecked")
	private static Node getSencondMaxReadNode(Node readNode, WebContext ctx) {
		Element element = null;
		if (readNode.getNodeType() == Element.ELEMENT_NODE) {
			element = (Element) readNode;

			List<Element> childrenList = element.elements();
			double weightNode = 0;
			double readWeightNode = 0;
			for (int i = 0; i < childrenList.size(); i++) {
				Element childElement = childrenList.get(i);
				if (isKeepTags(keepTagsSecond, childElement.getName())) {
					return readNode;
				}
			}

			for (int i = 0; i < childrenList.size(); i++) {
				Element childElement = childrenList.get(i);
				// pImageCount[0] = 0;
				// pImageCount[1] = 0;
				// pImageCount[2] = 0;
				// 20111121huchangan将pImageCount改为局部变量，将此对象传入getElementListPTextLength方法
				int[] pImageCount = new int[3];

				pImageCount = getElementListPTextLength(childElement, ctx,
						pImageCount);
				weightNode = weightNode(childElement, ctx, pImageCount);

				if (readWeightNode <= weightNode) {
					readNode = childElement;
					readWeightNode = weightNode;
				}
			}
		}
		return readNode;
	}

	private static Node getElementTitle(Element elementHTitle, WebContext ctx,
			Node readNode) {
		String url = ctx.getUrl();
		if (url.indexOf("xs8.cn/book") != -1) {
			return null;
		} else if (url.indexOf("hszw.com/book") != -1) {
			return null;
		} else if (url.indexOf("book.zhulang.com") != -1) {
			Element titleElement = ctx.getHtmlHead().element("TITLE");
			if (titleElement != null) {
				String title = titleElement.getTextTrim();
				String titleName = title.substring(0, title.lastIndexOf("，"));
				elementHTitle = DocumentHelper.createElement("H1").addText(
						titleName);
			}
		}

		return elementHTitle;
	}

	// 获取标题
	private static Element getTitleElement(WebContext ctx, Node readNode) {
		// System.out.println("--------->" + readNode.asXML());
		String url = ctx.getUrl();
		if (isCompareTwoText("hongxiu", url)) {
			List<Element> h1Nodes = readNode.selectNodes("//H1");
			for (Element ele : h1Nodes) {
				String idName = ele.attributeValue("id");
				if (idName != null && idName.length() > 0) {
					if (idName.equalsIgnoreCase("htmltimu")) {
						return ele;
					}
				}
			}
		}

		return ctx.getElementHTitle();
	}

	private static HashMap<String, Element> dealWidthNextPage(String url,
			HashMap<String, Element> elementHashMap, Element readElement) {
		// 西陆网，最后一页，还有下一页链接。这里去掉
		if (elementHashMap.get("nextPageLink") != null) {
			Element nextPageLinkUrl = elementHashMap.get("nextPageLink");
			String nextUrl = nextPageLinkUrl.attributeValue("href");
			if (nextUrl != null) {
				if (nextUrl.equalsIgnoreCase(url)) {
					elementHashMap.remove("nextPageLink");
				}
			}
		}
		// 榕树下的下一章是图片
		if (url.indexOf("rongshuxia") != -1) {
			List<Element> nextChapters = readElement
					.selectNodes("//DIV[@class='j a_prevnext_hook ac_boot']/A");
			for (Element ele : nextChapters) {
				Element element = ele.element("IMG");
				if (element != null) {
					Element elementA = DocumentHelper.createElement("A")
							.addAttribute("href", ele.attributeValue("href"))
							.addText(element.attributeValue("alt"));
					elementHashMap.put("nextChpterLink", elementA);
				}
			}

		}

		// 米尔军情 最后一页的链接
		if (elementHashMap.get("nextPageLink") != null) {
			Element nextPageLinkUrl = elementHashMap.get("nextPageLink");
			String nextUrl = nextPageLinkUrl.attributeValue("href");
			if (nextUrl != null) {

				if (nextUrl.equalsIgnoreCase(url + "#")) {
					elementHashMap.remove("nextPageLink");
				} else if (nextUrl.equalsIgnoreCase("javascript:")) {
					elementHashMap.remove("nextPageLink");
				}
			}
		}

		return elementHashMap;
	}

	private Node addLineNode(Node readNode) {
		Element readElement = (Element) readNode;

		Element divElement = readElement.addElement("DIV");
		Element hrElement = divElement.addElement("HR");

		hrElement.addAttribute("class", "pgDiv1");
		return readElement;
	}

	// 纵横网站span标签的东西不需要，是乱码
	private static Node removeSpanForZongheng(Node readNode) {
		Element readElement = (Element) readNode;
		if (readElement != null) {
			removeTagsForZongheng(readElement);
		}
		return readElement;
	}

	// 纵横网页的span CSS 样式是 不显示
	@SuppressWarnings("unchecked")
	static
	void removeTagsForZongheng(Element element) {
		List<Node> childrenList = element.content(); // 获取当前节点子内容
		for (int i = 0; i < childrenList.size(); i++) {
			Node child = childrenList.get(i); // element的孩子节点
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element eleChild = (Element) child; // element的孩子节点
				String tagName = eleChild.getName();
				if ("SPAN".equalsIgnoreCase(tagName)) {
					String spanAttribute = eleChild.attributeValue("class");
					if (spanAttribute != null && spanAttribute.length() != 0) {
						if (spanAttribute.equalsIgnoreCase("watermark")) {
							eleChild.detach();
							continue;
						}
					}

				}
				removeTagsForZongheng((Element) child);
			}
		}
	}

	/**
	 * 小说或者新闻的标题加在正文上。
	 * 
	 * **/
	private static Node addTitleHNode(WebContext ctx, Node readNode) {
		Element readElement = (Element) readNode;
		Element titleElement = ctx.getElementHTitle();

		if (titleElement != null) {
			Element titleElementC = (Element) readElement
					.selectSingleNode(titleElement.getName());

			if (titleElementC != null) {
				Element titleElementP = titleElementC.getParent();

				if (titleElementP != null) {
					titleElementP.remove(titleElementC);
					readElement.remove(titleElementC);
				} else {
					readElement.remove(titleElementC);
				}
			} else {
				titleElementC = titleElement;

				Element titleElementP = titleElementC.getParent();

				if (titleElementP != null) {
					titleElementP.remove(titleElementC);
					readElement.remove(titleElementC);
				} else {
					readElement.remove(titleElementC);
				}
			}
		}

		return readElement;
	}

	/*
	 * 退出阅读模式
	 */
	private Node addQuitReadNode(WebContext ctx, Node readNode) {
		Element eleNode = (Element) readNode;
		Element divElement = DocumentHelper.createElement("DIV");
		divElement.add((Element) eleNode.clone());
		Element quitElementDiv = DocumentHelper.createElement("DIV");
		String url = ctx.getUrl();
		if (url != null && url.length() > 0) {

			byte[] b = (url.substring(7) + "&rm=1").getBytes();
			url = Base64.encode(b);
			url = "http://TT01_" + url;
			Element hrefElement = quitElementDiv.addElement("A");
			hrefElement.addAttribute("id", "quitReadModel");
			hrefElement.addAttribute("href", url); // rm=1 是不进入阅读模式
			hrefElement.addText("退出阅读模式");
		}
		divElement.add((Element) quitElementDiv.clone());
		return divElement;
	}

	/**
	 * 抽取节点 保留keepTags下的节点 其他节点全部抽走
	 * 
	 * **/
	@SuppressWarnings("unchecked")
	static
	void removeTag(Element element, String[] keepTagsForMode, WebContext ctx) {
		String url = ctx.getUrl();
		List<Node> childrenList = element.content(); // 获取当前节点子内容
		for (int i = 0; i < childrenList.size(); i++) {
			Node child = childrenList.get(i); // element的孩子节点
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element eleChild = (Element) child; // element的孩子节点
				String tagName = eleChild.getName();

				if (!isKeepTags(keepTagsForMode, tagName)) {
					eleChild.detach();
					continue;
				}

				if ("IMG".equalsIgnoreCase(tagName)) {
					eleChild = processImg(url, tagName, eleChild);
				}

				if ("DIV".equalsIgnoreCase(tagName)) {
					eleChild = processDiv(url, tagName, eleChild);
				}
				if (url.indexOf("xilu.com") != -1) {
					eleChild = procesXiLu(eleChild, tagName);
				}
				if ("A".equalsIgnoreCase(tagName)) {
					List<Element> elementAs = eleChild.elements();
					boolean aIsImg = false;
					for (Element ele : elementAs) {
						if (ele.getName().equalsIgnoreCase("IMG")) {
							aIsImg = true;
							break;
						}
					}
					if (aIsImg) {
						eleChild.detach();
						continue;
					}
				}
				if ("TD".equalsIgnoreCase(tagName)) {
					if (url.indexOf("read.xxsy") != -1) {
						String className = eleChild.attributeValue("class");
						if (className != null
								&& "link_14".equalsIgnoreCase(className)) {
							eleChild.detach();
						}

					} else if (url.indexOf("china.com") != -1) {
						String className = eleChild.attributeValue("class");
						if (className != null
								&& "f12_006AA2".equalsIgnoreCase(className)) {
							eleChild.detach();
						}
					}
				}

				removeTag((Element) child, keepTagsForMode, ctx);
			}
		}
	}

	private static Element processImg(String url, String tagName, Element eleChild) {
		if (url.indexOf("zongheng.com") != -1) {
			String id = eleChild.attributeValue("id");
			if (id != null && "wbReadLogo".equalsIgnoreCase(id)) {
				eleChild.detach();
			}
		} else if (url.indexOf("lifeweek.com.cn") != -1) {
			String id = eleChild.attributeValue("id");
			if (id != null && "ctrlfscont".equalsIgnoreCase(id)) {
				eleChild.detach();
			}
		}
		return eleChild;
	}

	private static Element processDiv(String url, String tagName, Element eleChild) {

		String id = eleChild.attributeValue("id");

		if (id != null && id.length() > 0) {
			if (isCompareTwoText("qianlong.com", url)) { // 千龙网站处理
				if ("Recommendation".equalsIgnoreCase(id)
						|| "LiuyanCount".equalsIgnoreCase(id)
						|| "secondaryFunction".equalsIgnoreCase(id)
						|| "adtextbottom".equalsIgnoreCase(id)
						|| "rel".equalsIgnoreCase(id)
						|| "speakFunction".equalsIgnoreCase(id)
						|| "hitsasp".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			} else if (url.indexOf("online.sh.cn") != -1) { // online.sh.cn
				// 处理
				if ("related".equalsIgnoreCase(id)
						|| "related1".equalsIgnoreCase(id)
						|| "channel_search".equalsIgnoreCase(id)
						|| "flow".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			} else if (url.indexOf("qq.com") != -1) {
				if (id != null && "videoplayer_Aritcle_QQ".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			} else if (url.indexOf("china.com") != -1) {
				if (id != null && "chan_newsInfo".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			} else if (url.indexOf("ihongpan.com") != -1) {
				if (id != null
						&& ("newsc_tj".equalsIgnoreCase(id)
								|| "newsc_inf".equalsIgnoreCase(id)
								|| "newsc_pl".equalsIgnoreCase(id)
								|| "news_share".equalsIgnoreCase(id)
								|| "newsc_off".equalsIgnoreCase(id) || "newsc_guanzhu"
								.equalsIgnoreCase(id))) {
					eleChild.detach();
				}
			} else if (url.indexOf("cnwest.com") != -1) {
				if (id != null && "chan_mainBlk_mid".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			}
		}

		if (url.indexOf("zongheng.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null && "text".equalsIgnoreCase(className)) {
				eleChild.detach();
			}
			if (className != null && "chose".equalsIgnoreCase(className)) {
				eleChild.detach();
			}
			if (className != null && "button".equalsIgnoreCase(className)) {
				eleChild.detach();
			}

		} else if (url.indexOf("readnovel.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null) {
				if ("bottomTools1".equalsIgnoreCase(className)) {
					eleChild.detach();
				} else if ("bottomTools2".equalsIgnoreCase(className)) {
					eleChild.detach();
				} else if ("userZone clearfix user1"
						.equalsIgnoreCase(className)) {
					eleChild.detach();
				} else if ("bottomIntro".equalsIgnoreCase(className)) {
					eleChild.detach();
				}
			}

		} else if (url.indexOf("xs8.cn") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null) {
				if ("Page".equalsIgnoreCase(className)) {
					eleChild.detach();
				}
			}
		} else if (url.indexOf("china.com") != -1) {
			id = eleChild.attributeValue("id");
			if (id != null && id.length() > 0) {
				if ("vf".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			}
		} else if (url.indexOf("chinanews.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null
					&& ("left_time".equalsIgnoreCase(className)
							|| "bshare-custom".equalsIgnoreCase(className)
							|| "left_name".equalsIgnoreCase(className) || "left_xgxw_gai"
							.equalsIgnoreCase(className))) {
				eleChild.detach();
			}
		} else if (url.indexOf("fmx.cn") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null && ("newread_fy".equalsIgnoreCase(className))) {
				eleChild.detach();
			}
		} else if (url.indexOf("lifeweek.com.cn") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null
					&& ("share".equalsIgnoreCase(className) || "pageturn"
							.equalsIgnoreCase(className))) {
				eleChild.detach();
			}
		} else if (url.indexOf("tom.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null && ("operate".equalsIgnoreCase(className))) {
				eleChild.detach();
			}
		} else if (url.indexOf("chinanews.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null && ("blue_16b".equalsIgnoreCase(className))) {
				eleChild.detach();
			}
		} else if (url.indexOf("hszw.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null
					&& (("navbar".equalsIgnoreCase(className)) || "bottomlink tc"
							.equalsIgnoreCase(className))) {
				eleChild.detach();
			}
		} else if (url.indexOf("sohu.com") != -1) {
			String className = eleChild.attributeValue("class");
			if (className != null
					&& (("tvsubject bgA clear".equalsIgnoreCase(className)) || "editer"
							.equalsIgnoreCase(className))) {
				eleChild.detach();
			}

		}

		return eleChild;
	}

	private static Element procesXiLu(Element eleChild, String tagName) {
		if ("P".equalsIgnoreCase(tagName)) {
			String id = eleChild.attributeValue("id");
			if (id != null && id.length() > 0) {
				if ("bianlun".equalsIgnoreCase(id)) {
					eleChild.detach();
				}
			}
		}
		if ("IMG".equalsIgnoreCase(tagName)) {
			String src = eleChild.attributeValue("src");
			if (src != null && src.length() > 0) {
				if ("http://share.baidu.com/static/images/type-button-1.jpg"
						.equalsIgnoreCase(src.trim())) {
					eleChild.detach();
				}
			}
		}
		if ("H2".equalsIgnoreCase(tagName)) {
			String eleText = eleChild.getText();
			if (eleText != null && eleText.length() > 0) {
				if ("时尚资讯".equalsIgnoreCase(eleText)) {
					eleChild.detach();
				}
			}
		}

		return eleChild;
	}

	public static boolean isKeepTags(String[] keepTagName, String tagName) {
		boolean isKeepTags = false;
		for (int i = 0; i < keepTagName.length; i++) {
			if (tagName.equalsIgnoreCase(keepTagName[i])) {
				isKeepTags = true;
				break;
			}
		}
		return isKeepTags;
	}

	/**
	 * 增加上一页，下一页的链接
	 * **/
	private static Node addHrefToReadNode(WebContext ctx,
			HashMap<String, Element> elementListLink, Node readNode) {
		Element divElement = DocumentHelper.createElement("DIV");

		Element readElement = (Element) readNode;

		divElement.add((Element) readElement.clone());
		if (elementListLink != null) {
			if (elementListLink.size() != 0) {
				Element hrefDivElement = DocumentHelper.createElement("DIV");
				hrefDivElement.addAttribute("ID", "divHref");
				hrefDivElement.addAttribute("class", "divHref");
				if (elementListLink.get("previousChpterLink") != null) {
					Element pElement = elementListLink
							.get("previousChpterLink");
					if (pElement.attributeValue("href") != null
							&& pElement.attributeValue("href").length() > 0) {
						Element element = hrefDivElement.addElement("A");
						/*
						 * String link = pElement.attributeValue("href");
						 * if(link!=null){ String linkHref =
						 * RepairUrl.fillWithGetUrl(ctx, link);
						 * element.addAttribute("href",linkHref); }
						 */
						element.addAttribute("href", pElement
								.attributeValue("href"));
						element.addAttribute("ID", "previousChpterLink");
						element.addText(pElement.getTextTrim());
					}
				}
				if (elementListLink.get("previousPageLink") != null) {
					Element pElement = elementListLink.get("previousPageLink");
					if (pElement.attributeValue("href") != null
							&& pElement.attributeValue("href").length() > 0) {
						Element element = hrefDivElement.addElement("A");
						/*
						 * String link = pElement.attributeValue("href");
						 * if(link!=null){ String linkHref =
						 * RepairUrl.fillWithGetUrl(ctx, link);
						 * element.addAttribute("href",linkHref); }
						 */
						element.addAttribute("href", pElement
								.attributeValue("href"));
						element.addAttribute("ID", "previousPageLink");
						element.addText(pElement.getTextTrim());
					}
				}
				if (elementListLink.get("menu") != null) {
					Element pElement = elementListLink.get("menu");
					if (pElement.attributeValue("href") != null
							&& pElement.attributeValue("href").length() > 0) {
						Element element = hrefDivElement.addElement("A");
						/*
						 * String link = pElement.attributeValue("href");
						 * if(link!=null){ String linkHref =
						 * RepairUrl.fillWithGetUrl(ctx, link);
						 * element.addAttribute("href",linkHref); }
						 */
						element.addAttribute("href", pElement
								.attributeValue("href"));
						element.addAttribute("ID", "menu");

						element.addText(pElement.getTextTrim());
					}
				}
				if (elementListLink.get("nextPageLink") != null) {
					Element pElement = elementListLink.get("nextPageLink");
					Element element = hrefDivElement.addElement("A");
					/*
					 * String link = pElement.attributeValue("href");
					 * if(link!=null){ String linkHref =
					 * RepairUrl.fillWithGetUrl(ctx, link);
					 * element.addAttribute("href",linkHref); }
					 */
					element.addAttribute("href", pElement
							.attributeValue("href"));
					element.addAttribute("ID", "nextPageLink");
					element.addText(pElement.getTextTrim());
				}
				if (elementListLink.get("nextChpterLink") != null) {
					Element pElement = elementListLink.get("nextChpterLink");
					Element element = hrefDivElement.addElement("A");
					/*
					 * String link = pElement.attributeValue("href");
					 * if(link!=null){ String linkHref =
					 * RepairUrl.fillWithGetUrl(ctx, link);
					 * element.addAttribute("href",linkHref); }
					 */
					element.addAttribute("href", pElement
							.attributeValue("href"));
					element.addAttribute("ID", "nextChpterLink");
					element.addText(pElement.getTextTrim());
				}
				divElement.add(hrefDivElement);
			}
		}

		return divElement;
	}

	/**
	 * 识别上一页下一页，上一章，下一章
	 * **/
	public static HashMap<String, Element> getElementNextLink(Element element,
			WebContext ctx) {

		String url = ctx.getUrl();
		List<Element> elements = element.elements();
		if (elements.size() == 0) { // 没有子元素
			if ("A".equalsIgnoreCase(element.getName())) {
				String elementText = element.getTextTrim();
				if (isCompareTwoText("上一章", elementText)) {
					mapLink.put("previousChpterLink", element);
					String href = element.attributeValue("href");
					if (href != null && !(url + "#").equalsIgnoreCase(href)) {
						element.getParent().remove(element);
						element.detach();
					}
				} else if (isCompareTwoText("上一页", elementText)
						|| isCompareTwoText("上页", elementText)) {
					mapLink.put("previousPageLink", element);
					String href = element.attributeValue("href");
					if (href != null && !(url + "#").equalsIgnoreCase(href)) {
						element.getParent().remove(element);
						element.detach();
					}
				} else if (isCompareTwoText("下一页", elementText)
						|| isCompareTwoText("下页", elementText)) {
					mapLink.put("nextPageLink", element);
					String href = element.attributeValue("href");
					if (href != null && !(url + "#").equalsIgnoreCase(href)) {
						element.getParent().remove(element);
						element.detach();
					}
				} else if (isCompareTwoText("下一章", elementText)) {
					mapLink.put("nextChpterLink", element);
					String href = element.attributeValue("href");
					if (href != null && !(url + "#").equalsIgnoreCase(href)) {
						element.getParent().remove(element);
						element.detach();
					}
				} else if (isExistMenu(elementText)) {
					mapLink.put("menu", element);
					String href = element.attributeValue("href");
					if (href != null && !(url + "#").equalsIgnoreCase(href)) {
						element.getParent().remove(element);
						element.detach();
					}
				}
			}
		} else { // 有子元素
			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				getElementNextLink(e, ctx);
			}
		}
		return mapLink;
	}

	private static boolean isExistMenu(String elementText) {
		if (elementText == null || elementText.length() < 2) {
			return false;
		}
		if (elementText.indexOf("目录") != -1) {
			return true;
		}
		return false;
	}

	private static boolean isCompareTwoText(String tagText, String elementText) {
		if (tagText == null || elementText == null) {
			return false;
		}
		tagText = tagText.trim();
		elementText = elementText.trim();
		if (tagText.length() == elementText.length()) {
			if (tagText.equalsIgnoreCase(elementText)) {
				return true;
			}
		} else if (tagText.length() < elementText.length()) {
			for (int i = 0; i < (elementText.length() - tagText.length()); i++) {
				if (tagText.equalsIgnoreCase(elementText.substring(i, i
						+ tagText.length()))) {
					return true;
				}
			}
		}

		/*
		 * if (elementText.length() < tagText.length()) { return false; } else
		 * if (tagText.length() == elementText.length()) { if
		 * (tagText.equals(elementText)) { return true; } } else { if
		 * (tagText.equals(elementText.substring(0, tagText.length()))) { return
		 * true; } }
		 */
		return false;
	}

	/*
	 * 20111121huchangan将pImageCount改为局部变量，将此对象传入getElementListPTextLength方法
	 */
	private static int[] getElementListPTextLength(Element element, WebContext ctx,
			int[] pImageCount) {
		List<Element> elements = element.elements();
		String url = ctx.getUrl();

		if (url.indexOf("http://military.china.com/top01") != -1) {
			if ("DIV".equals(element.getName())) {
				String className = element.attributeValue("class");
				if (className != null
						&& "chan_newsPic".equalsIgnoreCase(className)) {
					pImageCount[0] = pImageCount[0] + 100;
				}
			}
		}
		
		/** 20120328针对与搜狐军事网站新闻只有图片没有内容的情况做特殊处理  start*/
		if (url.indexOf("http://mil.news.sohu.com") != -1) {
			if ("DIV".equals(element.getName())) {
				String className = element.attributeValue("class");
				if (className != null
						&& "text clear".equalsIgnoreCase(className)) {
					pImageCount[0] = pImageCount[0] + 250;
				}
			}
		}
		/** 20120328针对与搜狐军事网站新闻只有图片没有内容的情况做特殊处理  end*/

		if (url.indexOf("17k.com") != -1 || url.indexOf("xs8.cn") != -1
				|| url.indexOf("zhulang.com") != -1
				|| url.indexOf("fmx.cn") != -1) {
			if ("DIV".equalsIgnoreCase(element.getName())) {
				pImageCount[0] = pImageCount[0] + element.getText().length();
			}
		} else if ("P".equalsIgnoreCase(element.getName())) {
			if (elements.size() == 0) {
				int textLength = element.getText().length();
				if (textLength > 10) {
					pImageCount[0] = pImageCount[0] + textLength;
				}
			} else {
				String eleText = element.getTextTrim().trim();
				eleText = eleText.replaceAll("&nbsp;", "");
				if (eleText.trim().length() > 20) {
					pImageCount[0] = pImageCount[0] + eleText.length();
				}

				// List<Element> elementPs = element.elements();
				for (Element eleP : elements) {
					if (eleP.getName().equalsIgnoreCase("img")) {
						if (ctx.getUrl().indexOf(
								"huanqiu.com") != -1) {
							if (element.attributeValue("class") != null
									&& "hq2010_0501".equalsIgnoreCase(element
											.attributeValue("class"))) {
								pImageCount[1] = pImageCount[1] + 100;
							}
						} else if (ctx.getUrl()
								.indexOf("xilu.com") != -1) {
							pImageCount[1] = pImageCount[1] + 10;
						} else {
							pImageCount[1] = pImageCount[1] + 1;
						}

					} else if (eleP.getName().equalsIgnoreCase("SPAN")
							|| eleP.getName().equalsIgnoreCase("BR")) {
						if (eleP.getTextTrim() != null) {
							if (eleP.getTextTrim().length() > 10) {
								pImageCount[0] = pImageCount[0]
										+ eleP.getTextTrim().length();
							}
						}
					}
				}
			}
		} else if ("IMG".equalsIgnoreCase(element.getName())) {
			String titleImg = element.attributeValue("alt");
			String altImg = element.attributeValue("alt");
			if (titleImg != null && altImg != null) {
				if (titleImg.equalsIgnoreCase(altImg)) {
					pImageCount[2] = pImageCount[2] + 5;
				}
			}

		} else {
			if (elements.size() != 0) { // 有子元素
				for (int i = 0; i < elements.size(); i++) {
					Element e = elements.get(i);
					getElementListPTextLength(e, ctx, pImageCount);
				}
			}
		}
		return pImageCount;
	}

	private static double weightNode(Element e, WebContext ctx, int[] pCount) {
		if (e == null)
			return 0;
		SequenceUtils su = new SequenceUtils();
		String title = su.getHeadTitle(ctx.getHtmlHead());
		// 标题相似度
		double simWithTitle = 0;
		if (title != null && title.length() > 0) {
			LevenshteinDistance ld = new LevenshteinDistance();
			simWithTitle = ld.sim(title, su.getNodeText(e));
		}
		int textLength = su.getTextLength(e);
		double weight = simWithTitle * SIMWITHTITLEWEIGHT + pCount[0]
				* TEXTPCOUNTWEIGHT + Math.abs(textLength - pCount[0])
				* TEXTLENGTHWEIGHT + pCount[1] * PIMAGECOUNTWEIGHT + pCount[2]
				* 10;
		return (Math.round(weight * 1000) / 1000.0);
	}

}
