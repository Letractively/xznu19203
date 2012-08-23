package com.c_platform.catchcontent.catchcontent;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import com.c_platform.catchcontent.catchcontent.denoise.DenoisePolicy;
import com.c_platform.catchcontent.catchcontent.denoise.LevenshteinDistance;
import com.c_platform.catchcontent.catchcontent.sequence.MergePolicy;
import com.c_platform.catchcontent.catchcontent.sequence.SequenceUtils;
import com.c_platform.catchcontent.catchcontent.util.Base64;
import com.c_platform.catchcontent.catchcontent.util.ResourceUtil;
import com.c_platform.catchcontent.catchcontent.util.SortUtil;
import com.c_platform.catchcontent.catchcontent.vips.AddDocAttrForElementUtils;
import com.c_platform.catchcontent.catchcontent.vips.NavigatorIdentityUtils;
import com.c_platform.catchcontent.catchcontent.webparse.android.AndroidBrowseParse;
import com.c_platform.catchcontent.catchcontent.webparse.common.ElementConfigEntity;

public class CatchContentTest {
	/**
	 * sequence 定义的变量
	 */
	private static final int DEFAULT_BLOCK_SIZE = 200;

	private static final String NONATIVEHREF = "nonativehref";

	private static final String NATIVEHREF = "nativehref";

	private static final String NODETYPE = "nodetype"; // 节点类型：noise(已过滤删除)、beforetitle(navi)、title、content、titleandcontent、relateedcontent、other

	private static final double INDEXWEIGHT = 10; // 原来位置的权重 10/20/40

	private static final double KEYWORDSTIMESWEIGHT = 3; // 关键词出现次数的权重 0、1、2、3、4

	private static final double HREFWEIGHT = 10; // 链接数量的权重 10

	private static final double TEXTLENGTHWEIGHT_seq = 0.01; // 文本长度的权重
																// 50/100/200

	private static final double SIMWITHTITLEWEIGHT_seq = 10; // 与标题相似度的权重 0.01

	private static final double NONATIVEHREFTHRERATIO = 0.6; // 该节点下外部链接的比例阈值

	private static final double NONATIVEHREFTHRENO = 10; // 该节点下外部链接的数量阈值

	private static String[] titleFromBody = { "H1", "H2", "DIV", "FONT",
			"SPAN", "B" };

	final double TEXTLENGTHWEIGHT = 0.1; // 文本长度的权重 50/100/200

	final double SIMWITHTITLEWEIGHT = 0; // 与标题相似度的权重 0.01

	final double TEXTPCOUNTWEIGHT = 5;

	final double PIMAGECOUNTWEIGHT = 5; // 正文中图片的权重

	final String[] keepTags = { "p", "b", "small", "big", "i", "tt", "br",
			"center", "em", "strong", "dfn", "code", "samp", "div", "span",
			"h1", "h2", "img", "font", "label", "td", "tr", "table", "tbody",
			"a", "mutilpagenav" };

	final String[] keepTagsSecond = { "p", "b", "i", "br", "em", "strong",
			"font", "table", "td", "tt" };
	static HashMap<String, Element> mapLink = new HashMap<String, Element>();
	private WebContext ctx = new WebContext();

	@Test
	public void createTest() {
		ctx.setUrl("http://news.163.com/12/0821/03/89DCJG5K00011229.html");
		ctx.setCharset("gb2312");
		ctx.setScreen(new String[] { "480", "320" });
		ctx.setIframe(false);
		ctx.setRss(true);

		ResourceUtil.doReadElementTagsConfigFile();

		String result = catchHtmlWithHttpClient(ctx);
		ctx.setContent(result);
		Document doc = htmlDocumentParse(result);
		ctx.setContent(doc);
		ctx.setContent(block((Document) ctx.getContent()));
		ctx.setContent(sequence((ArrayList<Node[]>) ctx.getContent()));
		ctx.setContent(wrap((ArrayList<Node[]>) ctx.getContent()));
		ctx.setContent(tagFilter((ArrayList<Document>) ctx.getContent()));
		ArrayList<String> results = (ArrayList<String>) ctx.getContent();
		for (int i = 0; i < results.size(); i++) {
			FileWriter fw;
			try {
				fw = new FileWriter("D://test/test_" + i + ".html");
				fw.write(results.get(i), 0, results.get(i).length());
				fw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String catchHtmlWithHttpClient(WebContext ctx) {
		String resource = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(ctx.getUrl());
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				resource = EntityUtils.toString(response.getEntity(),
						ctx.getCharset());
			} else {
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return resource;
	}

	public Document htmlDocumentParse(String source) {
		DOMReader dr = new DOMReader();
		HTMLConfiguration htmlConfig = new HTMLConfiguration();
		htmlConfig.setFeature(
				"http://cyberneko.org/html/features/balance-tags", true);
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/names/elems", "upper");
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/names/attrs", "lower");
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/default-encoding",
				ctx.getCharset());
		htmlConfig.setFeature("http://xml.org/sax/features/namespaces", false);
		htmlConfig
				.setFeature(
						"http://cyberneko.org/html/features/scanner/script/strip-comment-delims",
						false);
		htmlConfig.setFeature(
				"http://cyberneko.org/html/features/augmentations", true);
		htmlConfig
				.setFeature(
						"http://apache.org/xml/features/scanner/notify-char-refs",
						true);
		htmlConfig.setFeature(
				"http://apache.org/xml/features/scanner/notify-builtin-refs",
				true);
		htmlConfig
				.setFeature(
						"http://cyberneko.org/html/features/scanner/notify-builtin-refs",
						true);
		DOMParser parser = new DOMParser(htmlConfig);
		XMLInputSource xmlInputSource = new XMLInputSource(null, null, null,
				new StringReader(source), null);
		try {
			parser.parse(xmlInputSource);
		} catch (XNIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.w3c.dom.Document doc = parser.getDocument();
		Document dom4jDoc = dr.read(doc);
		return dom4jDoc;
	}

	// 分块
	private ArrayList<Node[]> block(Document doc) {
		final int pDoc = 50;
		final String HEAD = "HEAD";
		final String BODY = "BODY";

		Element xhtml = doc.getRootElement();
		DenoisePolicy denoisePolicy = new DenoisePolicy();
		ArrayList<Node[]> result = new ArrayList<Node[]>();
		// 过滤广告
		xhtml = denoisePolicy.filterJsAdvertise(ctx.getUrl(), xhtml, "2");
		Element head = xhtml.element(HEAD);
		Element body = xhtml.element(BODY);
		int blockSize = 200;
		if (body == null || body.elements().size() <= 0) {
			result.add(new Node[] { body });
		} else {
			try {
				result = blocking(body, blockSize);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ctx.setHtmlHead(head);
		ctx.setContent(result);
		return result;
	}

	// 排序及阅读模式
	private ArrayList<Node[]> sequence(ArrayList<Node[]> result) {
		// 判断是否进入阅读模式
		boolean isReadMode = false;
		isReadMode = isEnterReadModel(result, ctx);
		if (!isReadMode) {
			// 确定分页大小
			int blockSize = 200;
			MergePolicy mergePolicy = null;
			String ps = "800";
			try {
				int pageSize = Integer.parseInt(ps);
				mergePolicy = new MergePolicy(pageSize, 2 * pageSize,
						4 * pageSize);
			} catch (Exception e) {
				mergePolicy = new MergePolicy(400, 800, 1600);
			}
			ArrayList<Node[]> blocksAfterMerge = mergePolicy.merge(result,
					blockSize, ctx);
			return blocksAfterMerge;
		}

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

			Element elementHTitle = getTitleElement(ctx, readNode);
			Node[] nodeAdd = new Node[4];

//			nodeAdd[0] = getElementTitle(elementHTitle, ctx, readNode);
			nodeAdd[0] = elementHTitle;
			nodeAdd[1] = DocumentHelper.createElement("DIV").addAttribute(
					"class", "line");
			nodeAdd[2] = DocumentHelper.createElement("DIV").addAttribute(
					"class", "line1");
			nodeAdd[3] = readNode;
			readNodeList.add(nodeAdd);
			ctx.setContent(readNodeList);
		}
		return readNodeList;
	}

	/**
	 * 
	 * TODO 删除head中的script标签
	 * 
	 * @param element
	 */
	private void removeScript(Element element) {
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

	private ArrayList<Document> wrap(ArrayList<Node[]> result) {
		final String HEAD = "HEAD";
		final String BODY = "BODY";
		final String HTML = "HTML";
		ArrayList<Node[]> temp = new ArrayList<Node[]>();
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
		return blocks;
	}

	/**
	 * 标签过滤
	 */
	private ArrayList<String> tagFilter(ArrayList<Document> domList) {
		ArrayList<String> resultList = new ArrayList<String>();
		if (domList != null && domList.size() > 0) {
			Document contextDoc;
			for (int i = 0, j = domList.size(); i < j; i++) {
				contextDoc = domList.get(i);

				if (null != contextDoc) {
					// String engineId = ctx.getJsonMessage().getEngine_id()
					// .toLowerCase(); // 获取当前设备的引擎版本
					ElementConfigEntity elementConfigEntity;
					try {
						elementConfigEntity = getElementTags("aaa");
						// 根据引擎版本engineId获取匹配的过滤配置文件
						// String url = ctx.getLocationURL() == null ? ctx
						// .getJsonMessage().getBody().getUrl() : ctx
						// .getLocationURL();
						String url = ctx.getUrl();
						double[] sreenWH = new double[] { 480, 320 };
						contextDoc = new AndroidBrowseParse().doParse(
								contextDoc, elementConfigEntity, url, sreenWH,
								ctx);

						resultList.add(formateXml(contextDoc.getRootElement(),
								ctx.getCharset(), false));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			ctx.setContent(resultList);

		}
		return resultList;
	}

	public ArrayList<Node[]> blocking(Element body, int blockSize)
			throws Exception {
		// 对body下子节点是文本节点处理如www.d3zw.com小说
		ArrayList<Node[]> result = new ArrayList<Node[]>();
		List<Node> nodes = body.content();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getNodeType() == Node.TEXT_NODE
					&& nodes.get(i).asXML().trim().length() > 0) {
				Element div = DocumentHelper.createElement("P").addAttribute(
						"class", "content");
				div.add(nodes.get(i).detach());
				body.add(div);
			}
		}

		// 新版本分块实现
		// denoisePolicy.addHrefForNode(body);//放到blocking之外，
		AddDocAttrForElementUtils.addDocForNode(body);
		AddDocAttrForElementUtils.treeWalkByDoc(body, result);

		NavigatorIdentityUtils.addAttrForElement(result);
		return result;
	}

	public String formateXml(Element element, String charset, boolean istrans)
			throws IOException {
		OutputFormat format = OutputFormat.createCompactFormat(); // 以紧凑格式输出
		// format.setTrimText(false); //不需要去除空格或换行
		format.setEncoding(charset); // 设置字符集
		format.setSuppressDeclaration(false); // 去除<?xml version="1.0"
												// encoding="UTF-8"?>
		format.setXHTML(true); // 设置为XHTML格式
		format.setExpandEmptyElements(true); // <tagName/> to
												// <tagName></tagName>.
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans); // 反转义
		xw.write(element);
		xw.flush();
		xw.close();
		return sw
				.toString()
				.replaceAll(
						"(</\\s*(IMG|INPUT|BR|LINK|META|HR|FRAME|EMBED|AREA|BASE|BASEFONT|BGSOUND|COL)>)|(tagtype=\"custom\")|(table-tag=\"true\")|(&amp;nbsp;)",
						"");
	}

	/*
	 * desc 根据引擎版本engineId获取匹配的过滤配置文件
	 * 
	 * @param enginId 设备引擎ID
	 * 
	 * @return WebElementRemover
	 * 
	 * @throws Exception
	 */
	private ElementConfigEntity getElementTags(String engineId)
			throws Exception {
		if (ResourceUtil.getElementConfigMap() == null) {
			boolean result = ResourceUtil.doReadElementTagsConfigFile();
			if (!result) {
				// 如果读取配置文件失败则写错误日志
				throw new Exception("Failed read elementTagsConfig.xml.");
			}
		}
		if (!ResourceUtil.getElementConfigMap().containsKey(engineId)) {
			// 如果未匹配到引擎版本，则按默认版本处理
			engineId = "aaa";
		}
		return (ElementConfigEntity) ResourceUtil.getElementConfigMap().get(
				engineId);
	}

	/**
	 * 阅读模式相关函数
	 */
	@SuppressWarnings("unchecked")
	private Node getSencondMaxReadNode(Node readNode, WebContext ctx) {
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

	// 获取标题
	private Element getTitleElement(WebContext ctx, Node readNode) {
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

	private HashMap<String, Element> dealWidthNextPage(String url,
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

	/**
	 * 小说或者新闻的标题加在正文上。
	 * 
	 * **/
	private Node addTitleHNode(WebContext ctx, Node readNode) {
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


	/**
	 * 抽取节点 保留keepTags下的节点 其他节点全部抽走
	 * 
	 * **/
	@SuppressWarnings("unchecked")
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

	private Element processImg(String url, String tagName, Element eleChild) {
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

	private Element processDiv(String url, String tagName, Element eleChild) {

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

	private Element procesXiLu(Element eleChild, String tagName) {
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

	public boolean isKeepTags(String[] keepTagName, String tagName) {
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
	private Node addHrefToReadNode(WebContext ctx,
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
						element.addAttribute("href",
								pElement.attributeValue("href"));
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
						element.addAttribute("href",
								pElement.attributeValue("href"));
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
						element.addAttribute("href",
								pElement.attributeValue("href"));
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
					element.addAttribute("href",
							pElement.attributeValue("href"));
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
					element.addAttribute("href",
							pElement.attributeValue("href"));
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
	private int[] getElementListPTextLength(Element element, WebContext ctx,
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

		/** 20120328针对与搜狐军事网站新闻只有图片没有内容的情况做特殊处理 start */
		if (url.indexOf("http://mil.news.sohu.com") != -1) {
			if ("DIV".equals(element.getName())) {
				String className = element.attributeValue("class");
				if (className != null
						&& "text clear".equalsIgnoreCase(className)) {
					pImageCount[0] = pImageCount[0] + 250;
				}
			}
		}
		/** 20120328针对与搜狐军事网站新闻只有图片没有内容的情况做特殊处理 end */

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
						if (ctx.getUrl().indexOf("huanqiu.com") != -1) {
							if (element.attributeValue("class") != null
									&& "hq2010_0501".equalsIgnoreCase(element
											.attributeValue("class"))) {
								pImageCount[1] = pImageCount[1] + 100;
							}
						} else if (ctx.getUrl().indexOf("xilu.com") != -1) {
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

	private double weightNode(Element e, WebContext ctx, int[] pCount) {
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

	/**
	 * 判断是否进入阅读模式的函数
	 * 
	 */
	/**
	 * 判断是否进入阅读模式
	 * 
	 * **/
	private boolean isEnterReadModel(ArrayList<Node[]> blocks, WebContext ctx) {
		String url = ctx.getUrl();
		if (url == null) {
			return false;
		}
		if (url.indexOf("dt.wx.91.com") != -1) {
			String endOneUrl = url.substring(url.lastIndexOf("?") + 1);
			if (endOneUrl != null) {
				if (endOneUrl.indexOf("ChapterID") != -1) {
					return true;
				}
			}
		}
		if (url.indexOf("wccdaily.com") != -1) {
			return false;
		}
		if (url.indexOf("williamlong.info/") > 0) {
			return false;
		}
		if (url.indexOf("pic.news.sohu.com") != -1) {
			return false;
		}
		boolean isEnterReadModelByUrl = isEnterReadModelByUrl(blocks, ctx);
		if (isEnterReadModelByUrl) {
			return findTitleFromBody(blocks, ctx);
		}
		return false;
	}

	private boolean isEnterReadModelByUrl(ArrayList<Node[]> blocks,
			WebContext ctx) {
		if (blocks == null || ctx == null) {
			return false;
		}
		String url = ctx.getUrl();
		if (url == null) {
			return false;
		}

		if (url.equalsIgnoreCase("xinhuanet.com")) {
			return false;
		}

		/**
		 * 幻剑书盟 的下一页是js写的无法加载。不进入阅读模式 连城读书的内容是通过js写的，不进入阅读模式
		 * 注：连城读书，即使不进入阅读模式也没有正文内容
		 * **/
		if (url.startsWith("http://html.hjsm.tom.com")
				|| url.startsWith("http://www.lcread.com")) {
			return false;
		}
		if ((url.lastIndexOf("#") != -1) && isExistTwoUrl("kanshu", url)) {
			url = url.substring(0, url.lastIndexOf("#"));
		}
		// 判断url 是否是以(.html,shtml,htm)结尾
		boolean isEndWithHtml = false;
		if (url.indexOf("http://lz.book.sohu.com/serialize") == -1
				&& (url.endsWith(".html") || url.endsWith(".HTML")
						|| url.endsWith(".shtml") || url.endsWith(".SHTML")
						|| url.endsWith(".htm") || url.endsWith(".HTM"))) {
			isEndWithHtml = true;
		}

		if (url.indexOf("jvcxp.com") != -1) {
			return false;
		}

		// 结尾是.html但是不符合条件
		boolean isNoOneEndWidthHtml = false;
		boolean isNoTwoEndWidthHtml = false;

		if (isEndWithHtml) {
			isNoOneEndWidthHtml = isEndOneUrl(url);
		}
		if (isNoOneEndWidthHtml) {
			isNoTwoEndWidthHtml = isEndTwoUrl(url);
		}

		return isNoTwoEndWidthHtml;
	}

	/**
	 * 判断否应该进入阅读模式： title与H1或者H2里面相同。
	 * **/
	@SuppressWarnings("unchecked")
	private boolean findTitleFromBody(ArrayList<Node[]> blocks, WebContext ctx) {

		if (blocks == null || ctx == null) {
			return false;
		}
		String url = ctx.getUrl();
		if (url != null) {
			if ("read.xxsy.net".equalsIgnoreCase(url.substring(7, 20))) {
				return true;
			}
		}
		String titleName = null;
		Element htmlHead = ctx.getHtmlHead();
		if (htmlHead != null) {
			Element titleElement = htmlHead.element("TITLE");
			if (titleElement != null) {
				titleName = titleElement.getTextTrim();
				if (url.indexOf("http://novel.hongxiu.com/a/318276/3688611.shtml") != -1) {
					if (titleName != null) {
						Element titleH1 = DocumentHelper.createElement("H1");
						titleH1.setText(titleName.substring(0, 20));
						ctx.setElementHTitle(titleH1);
					}
				}
			}
		}

		boolean findTitlefromNode = false;

		for (Node[] node : blocks) {
			for (int i = 0; i < node.length; i++) {
				if (node[i] != null) {
					// ctx 里面没有放置title
					if (titleName == null) {
						List<Element> titleNodes = node[i]
								.selectNodes("//TITLE");
						if (titleNodes.size() != 0) {
							Element titleElement = titleNodes.get(0);
							if (titleElement != null) {
								titleName = titleElement.getTextTrim();

							}
						}

					}

					if (titleName == null) {
						return false;
					}
					findTitlefromNode = isTitleExistBodyNode(ctx,
							(Element) node[i], titleName);

					if (findTitlefromNode) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 判断标题是否在element 对象中 如果element中存在标题 则放在ctx中
	 * 
	 * **/
	private boolean isTitleExistBodyNode(WebContext ctx, Element element,
			String titleName) {
		List<Element> elements = element.elements();
		boolean findTitlefromNode = false;
		SequenceUtils su = new SequenceUtils();
		String url = ctx.getUrl();
		if (elements.size() == 0) {
			if (url != null) {
				if (url.indexOf("zaobao.com") != -1
						|| url.indexOf("news.dayoo.com") != -1) {
					titleFromBody[titleFromBody.length - 1] = "P";
				}
			}
			if (url.indexOf("http://novel.hongxiu.com/a/318276") != -1) {
				return true;

			} else if (isTitleTage(titleFromBody, element.getName())) {
				if (element.getTextTrim() != null
						&& element.getTextTrim().length() > 0) {
					if (element.getTextTrim().length() <= titleName.length()) {
						findTitlefromNode = su.fromTitleJudgmentIsReadMode(
								element, titleName);

						if (findTitlefromNode) {
							ctx.setElementHTitle(element);
							return true;
						}
					}
				}
			}
		} else if (!element.getName().equalsIgnoreCase("form")) {
			for (int i = 0; i < elements.size(); i++) {
				if (isTitleExistBodyNode(ctx, elements.get(i), titleName)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isTitleTage(String[] titlefrombody2, String tagName) {
		if (tagName == null || tagName.length() == 0) {
			return false;
		} else {
			tagName = tagName.trim();
		}

		for (int i = 0; i < titlefrombody2.length; i++) {
			if (tagName.equalsIgnoreCase(titlefrombody2[i])) {
				return true;
			}
		}
		return false;
	}

	// ----------------阅读模式代码 end------------------------------

	/**
	 * TODO 对噪音块进行排序
	 * 
	 * @param beforeSequenceByNoise
	 * @param ctx
	 * @return afterSequenceByNoiseBlocks
	 */
	private ArrayList<Node[]> sequenceByNoise(
			ArrayList<Node[]> beforeSequenceByNoise, WebContext ctx) {
		if (beforeSequenceByNoise == null || beforeSequenceByNoise.size() <= 0)
			return null;
		SequenceUtils su = new SequenceUtils();
		ArrayList<Node[]> afterSequenceByNoiseBlocks = new ArrayList<Node[]>();
		ArrayList<Node[]> noiseBlocks = new ArrayList<Node[]>();
		ArrayList<Object[]> ls = new ArrayList<Object[]>();
		ArrayList<Node[]> lastBlocks = getLastBlocks(beforeSequenceByNoise);
		// 区分噪音与非噪音节点
		for (Node[] ns : beforeSequenceByNoise) {
			if (ns == null)
				continue;
			if (ns.length == 0)
				continue;
			Node[] noNoiseNodes = new Node[ns.length];
			Node[] noiseNodes = new Node[ns.length];
			int i = 0, j = 0;
			for (Node n : ns) {
				if (n == null || n.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element e = (Element) n;
				DenoisePolicy dp = new DenoisePolicy();
				if ((su.hrefCount(e) >= NONATIVEHREFTHRENO && dp
						.noNativeHrefProportion(e) < NONATIVEHREFTHRERATIO)
						|| su.hrefCount(e) < NONATIVEHREFTHRENO) {// 节点下链接数量少于10个，添加
					noNoiseNodes[i] = n;
					i++;
				} else {
					noiseNodes[i] = n;
					j++;
				}
			}
			afterSequenceByNoiseBlocks.add(noNoiseNodes);
			noiseBlocks.add(noiseNodes);
		}
		// 对于噪音节点二次排序
		for (int i = 0; i < noiseBlocks.size(); i++) {
			Node[] ns = noiseBlocks.get(i);
			if (ns[0] == null || ns[0].getNodeType() != Node.ELEMENT_NODE)
				continue;
			double weight = weightNode((Element) ns[0], ctx, noiseBlocks.size()
					- i);
			ls.add(new Object[] { weight, ns });
		}
		ls = SortUtil.sortList(ls);
		for (Object[] objs : ls) {
			afterSequenceByNoiseBlocks.add((Node[]) objs[1]);
		}
		afterSequenceByNoiseBlocks.addAll(lastBlocks);
		return afterSequenceByNoiseBlocks;
	}

	/**
	 * desc 按照权重对title/content之后的进行排序，返回blocksAfterSequence
	 * 
	 * @param beforeSequenceByWeight
	 * @param tag
	 * @param ctx
	 * @return blocksAfterSequence
	 */
	private ArrayList<Node[]> sequenceByWeight(
			ArrayList<Node[]> beforeSequenceByWeight, int tag, WebContext ctx) {
		ArrayList<Node[]> blocksAfterSequence = new ArrayList<Node[]>();
		ArrayList<Object[]> ls = new ArrayList<Object[]>();
		// 统计blocks下节点的数量
		int nodeSum = 0;
		for (Node[] ns : beforeSequenceByWeight) {
			nodeSum += ns.length;
		}
		// 开始排序
		if (tag == 0) { // 即无title时，按照顺序输出，不排序
			blocksAfterSequence = beforeSequenceByWeight;
		} else if (tag == 1) { // 即只发现了title，修改为按照顺序输出，不排序
			blocksAfterSequence = beforeSequenceByWeight;
		} else if (tag == 2 || tag == 3) { // 即有title和content，计算content之后的node，按照权重排序
			ArrayList<Node[]> lastBlocks = getLastBlocks(beforeSequenceByWeight);// 存储最后节点

			int nodeIndex = 0;// 初始化nodeIndex
			int hasAdd = 0;// 选择添加至blocksAfterSequence还是ls
			for (Node[] ns : beforeSequenceByWeight) {
				for (int i = 0; i < ns.length; i++, nodeIndex++) {
					if (ns[i] == null)
						continue;
					Element e = (Element) ns[i];
					if (hasAdd == 0) {
						blocksAfterSequence.add(ns);
					} else {
						double weight = weightNode(e, ctx, nodeSum - nodeIndex);
						// logger.info("====weight:" + weight);
						ls.add(new Object[] { weight, ns });
					}
					if ("content".equalsIgnoreCase(e.attributeValue(NODETYPE))
							|| "titleandcontent".equalsIgnoreCase(e
									.attributeValue(NODETYPE))) {
						hasAdd = 1;
					}
				}
			}
			// 对ls（即content或者title之后的）进行排序，然后添加至blocksAfterSequence
			ls = SortUtil.sortList(ls);
			for (Object[] objs : ls) {
				blocksAfterSequence.add((Node[]) objs[1]);
			}
			blocksAfterSequence.addAll(lastBlocks);
		}
		return blocksAfterSequence;
	}

	/**
	 * desc 获得最后的blocks,注意blocks也发生变化
	 * 
	 * @param beforeGet
	 * @return lastBlocks
	 */
	private ArrayList<Node[]> getLastBlocks(ArrayList<Node[]> beforeGet) {
		if (beforeGet == null || beforeGet.size() == 0)
			return null;
		int beforeGetSize = beforeGet.size();
		int lastNum = Math.max(5, beforeGetSize / 5); // 最后有几个节点是lastblocks
		lastNum = Math.min(lastNum, beforeGetSize);
		SequenceUtils su = new SequenceUtils();
		for (int i = beforeGetSize - 1; i >= 0; i--) {
			Node n = beforeGet.get(i)[0];
			if (n != null) {
				String tmp = su.getNodeText(n).toLowerCase();
				if (tmp.contains("copyright") || tmp.contains("版权")) {
					lastNum = beforeGetSize - i;
					break;
				}
			}
		}
		ArrayList<Node[]> lastBlocks = new ArrayList<Node[]>();// 存储最后节点
		for (int i = 0; i < lastNum; i++) {
			lastBlocks.add(0, beforeGet.get(beforeGet.size() - 1));
			beforeGet.remove(beforeGet.size() - 1);
		}
		return lastBlocks;
	}

	/**
	 * desc 为blocks添加nodetype属性，返回tag，即是否有title或者content等 tag:0无title; 1有title;
	 * 2有title和content; 3有content; //暂时不辨识4、5; 4有relatedcontent; 5 other
	 * 
	 * @param beforeAdd
	 * @param ctx
	 * @return Object[](tag和blocksAfterAddNodetype)
	 */
	private Object[] addNodetypeForNode(ArrayList<Node[]> beforeAdd,
			WebContext ctx) {
		SequenceUtils su = new SequenceUtils();
		String title = su.getHeadTitle(ctx.getHtmlHead());
		String[] keywords = su.getHeadKeywords(ctx.getHtmlHead());
		int tag = 0; // 返回标记，
		int nodeIndex = 0; // node在原来blocks中的顺序
		int titleIndex = 0; // 如果发现了title,title在原来blocks中的顺序
		for (Node[] ns : beforeAdd) {
			int j = 0;
			for (int i = 0; i < ns.length; i++, nodeIndex++) {
				Node n = ns[i];
				if (n == null || n.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element e = (Element) n;

				switch (tag) {
				case 0:// 无title,寻找title
					if (!su.findTitlefromNode(n, title)) {// 非title增加属性beforetitle
						e.addAttribute(NODETYPE, "beforetitle");
					} else {// title,判断是否有content,有content为titleandcontent,没有content则为title
						if (su.getNodeText(e).length() < 3 * title.length()) {
							e.addAttribute(NODETYPE, "title");
							titleIndex = nodeIndex;
							tag = 1;
						} else if (su.isContent(e, title, keywords)) {
							e.addAttribute(NODETYPE, "titleandcontent");
							tag = 2;
						} else {
							e.addAttribute(NODETYPE, "title");
							titleIndex = nodeIndex;
							tag = 1;
						}
					}
					break;
				case 1:// 有title,如果在五个节点内没有content，说明没有content
					if (nodeIndex - titleIndex > 5)
						break;
					if (su.isContent(e, title, keywords)) {
						e.addAttribute(NODETYPE, "content");
						tag = 3;
					}
					break;
				case 2:// do nothing//后期增加识别relatedcontent
					break;
				case 3:// do nothing//后期增加识别relatedcontent
					break;
				case 4:// do nothing
					break;
				}
			}
			j++;
		}
		return new Object[] { tag, beforeAdd };
	}

	/**
	 * desc 计算该block的权重
	 * 
	 * @param e
	 * @param ctx
	 * @param nodeIndex
	 * @return double
	 */

	private double weightNode(Element e, WebContext ctx, int nodeIndex) {
		if (e == null)
			return 0;
		SequenceUtils su = new SequenceUtils();
		String title = su.getHeadTitle(ctx.getHtmlHead());
		String[] keywords = su.getHeadKeywords(ctx.getHtmlHead());

		int keywordsTimes = 0;
		double simWithTitle = 0;
		if (keywords != null) {
			keywordsTimes = su.keywordsTimes(e, keywords);
		}
		if (title != null && title.length() > 0) {
			LevenshteinDistance ld = new LevenshteinDistance();
			simWithTitle = ld.sim(title, su.getNodeText(e));
		}

		int textLength = su.getTextLength(e);
		double nativehref = 0;
		double nonativehref = 0;
		if (e.attributeValue(NATIVEHREF) != null
				&& e.attributeValue(NONATIVEHREF) != null) {
			nativehref = Double.parseDouble(e.attributeValue(NATIVEHREF));
			nonativehref = Double.parseDouble(e.attributeValue(NONATIVEHREF));
		}
		double nativeHrefRatio = 1;
		if (nativehref + nonativehref > 0) {
			nativeHrefRatio = nativehref / nativehref + nonativehref;
		}

		/**
		 * keywordsTimes 关键词出现的次数 simWidthTitle 与标题相似度的 nodeIndex 原来位置
		 * textLength 文本长度 nativeHrefRatio 链接数
		 * 
		 * KEYWORDSTIMESWEIGHT = 3 SIMWITHTITLEWEIGHT = 15 INDEXWEIGHT = 10
		 * TEXTLENGTHWEIGHT = 10 HREFWEIGHT = 0.1
		 * 
		 * **/

		double weight = keywordsTimes * KEYWORDSTIMESWEIGHT + simWithTitle
				* SIMWITHTITLEWEIGHT_seq + nodeIndex * INDEXWEIGHT + textLength
				* TEXTLENGTHWEIGHT_seq + nativeHrefRatio * HREFWEIGHT; // 权重计算公式

		return (Math.round(weight * 1000) / 1000.0);
	}

	/**
	 * desc 对blocks进行格式化后输出
	 * 
	 * @param blocks
	 * @param storeAddress
	 * @return void
	 */
	private void formateAndOutputBlocks(ArrayList<Node[]> blocks,
			String storeAddress) throws IOException {
		int j = 1;
		for (Node[] ns : blocks) {
			OutputFormat format = OutputFormat.createPrettyPrint();
			StringWriter sw = new StringWriter();
			XMLWriter writer = new XMLWriter(sw, format);
			for (int i = 0; i < ns.length; i++) {
				if (ns[i] == null)
					continue;
				writer.write(ns[i]);
			}
			writer.close();
			byte[] output = sw.toString().getBytes();
			FileOutputStream fos = new FileOutputStream(storeAddress + j
					+ ".txt");
			fos.write(output, 0, output.length);
			fos.close();
			j++;
		}
	}

	private boolean isEndOneUrl(String url) {
		if (url == null) {
			return false;
		}

		String endOneUrl = url.substring(url.lastIndexOf("/") + 1);
		if (endOneUrl == null) {
			return false;
		}

		String bbsStartUrl = url.substring(7, 10);
		if (bbsStartUrl != null && bbsStartUrl.length() > 0) {
			if ("bbs".equalsIgnoreCase(bbsStartUrl)) {
				return false;
			}
		}

		String endOneBeforePoint = endOneUrl.substring(0,
				endOneUrl.indexOf("."));
		if (url.indexOf("huanxia.com") != -1) {

			if (endOneBeforePoint.startsWith("book")
					&& endOneBeforePoint.indexOf("_") == -1) {
				return false;
			}
		}

		// index
		if (endOneBeforePoint.equalsIgnoreCase("index")) {
			return false;
		} else if (endOneBeforePoint.equalsIgnoreCase("register")
				|| endOneBeforePoint.equalsIgnoreCase("login")
				|| endOneBeforePoint.startsWith("do")
				|| endOneBeforePoint.startsWith("index")
				|| endOneBeforePoint.startsWith("Book")) { // 注册,登录
			return false;
		} else if (endOneBeforePoint.equalsIgnoreCase("default")
				|| endOneBeforePoint.equalsIgnoreCase("list")
				|| endOneBeforePoint.equalsIgnoreCase("catalog")
				|| endOneBeforePoint.equalsIgnoreCase("news_default")) {
			return false;
		}
		return true;
	}

	private boolean isExistTwoUrl(String strName, String url) {
		if (strName == null || url == null) {
			return false;
		}
		for (int i = 0; i < (url.length() - strName.length()); i++) {
			if (strName
					.equalsIgnoreCase(url.substring(i, i + strName.length()))) {
				return true;
			}
		}
		return false;
	}

	private boolean isEndTwoUrl(String url) {
		if (url == null) {
			return false;
		}
		String urlBeforeLast = url.substring(0, url.lastIndexOf("/"));
		String endTwoUrl = urlBeforeLast.substring(urlBeforeLast
				.lastIndexOf("/") + 1);
		if (endTwoUrl == null) {
			return false;
		}
		if (endTwoUrl.equalsIgnoreCase("info")) {
			return false;
		} else if (endTwoUrl.equalsIgnoreCase("showchapter")) {
			return false;
		} else if (endTwoUrl.equalsIgnoreCase("book")) {
			return false;
		} else if (endTwoUrl.equalsIgnoreCase("list")) {
			return false;
		} else if (endTwoUrl.equalsIgnoreCase("artinfo")) {
			return false;
		} else if (isExistTwoUrl("cc222", url)
				&& endTwoUrl.equalsIgnoreCase("novel")) {
			return false;
		}
		return true;
	}

}
