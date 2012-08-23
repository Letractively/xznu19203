package com.c_platform.catchcontent.catchcontent.sequence;

import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.xml.sax.InputSource;

import com.c_platform.catchcontent.catchcontent.WebContext;
import com.c_platform.catchcontent.catchcontent.denoise.LevenshteinDistance;
import com.c_platform.catchcontent.catchcontent.scale.ScaleByIm4java;

/**
 * TODO 排序、合并所需方法
 */
public class SequenceUtils {
	private static final String NONATIVEHREF = "nonativehref";
	private static final String NATIVEHREF = "nativehref";

	private static final String STYLE = "style";
	private static final String SCRIPT = "script";
	private static final String NOSCRIPT = "noscript";
	private static final String H1 = "h1";

	private static final Double SimWithTitleH1 = 0.20;// 与title的相似度阈值
	private static final Double SimWithTitleNoH1 = 0.50;// 与title的相似度阈值

	private static final int DefalutImgSize = 25;// 图片无大小时默认的字数

	/**
	 * TODO 判断数据库中返回的blockOrders是否合法
	 * 
	 * @param size
	 * @param blockOrders
	 * @return boolean
	 */
	public boolean isLegalBlockOrder(int size, String blockOrders) {
		boolean result = true;
		String[] boArray = blockOrders.split(",");
		if (boArray.length > size) {
			result = false;
		} else {
			try {
				for (String index : boArray) {
					if (Integer.parseInt(index) >= size) {
						result = false;
						break;
					}
				}
			} catch (Exception ex) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * desc 返回Element内的文本长度，如果样式为"display:none;"，不参加统计
	 * 
	 * @param n
	 * @return int
	 */
	public int getTextLength(Node n) {
		int textLength = 0;
		if (n == null || STYLE.equalsIgnoreCase(n.getName())
				|| SCRIPT.equalsIgnoreCase(n.getName())
				|| NOSCRIPT.equalsIgnoreCase(n.getName()))
			return textLength;
		if (n.getNodeType() == Node.TEXT_NODE) {
			if (n.getText() != null) {
				char[] text = n.getText().toCharArray();
				for (char c : text) {
					if (c != '\t' && c != '\n' && c != ' ') {
						textLength++;
					}
				}
			}
			return textLength;
		}
		if (n.getNodeType() != Node.ELEMENT_NODE) {
			return textLength;
		}
		Element e = (Element) n;
		if (e.attributeValue("style") != null
				&& e.attributeValue("style").toLowerCase().replaceAll(" ", "")
						.indexOf("display:none") != -1) {
			return textLength;
		} else if (e.attributeValue("class") != null
				&& e.attributeValue("class").toLowerCase().replaceAll(" ", "")
						.indexOf("disp_none") != -1) {// 特殊处理http://www.kuwo.cn/下class中diplay:none的问题
			return textLength;
		} else if (e.attributeValue("class") != null
				&&
				/*
				 * && e.attributeValue("class").toLowerCase().replaceAll(" ",
				 * "") .indexOf("hide") != -1)
				 */
				(e.attributeValue("class").toLowerCase().replaceAll(" ", "")
						.indexOf("hide") != -1 || e.attributeValue("class")
						.toLowerCase().replaceAll(" ", "").indexOf("hidden") != -1)) {// 特殊处理凤凰论坛下class中hide的问题//以及163下texarea内class为hidden
			return textLength;
		} else if ("textarea".equalsIgnoreCase(e.getName()) && e.hasContent()) {// 特殊处理淘宝的textarea中为HTML的部分
			e = praseTextareaToElement(e);
			textLength = getTextLength(e);
			return textLength;
		} else if (e.attributeValue("navigator-colum") != null
				&& e.attributeValue("navigator-colum").toLowerCase()
						.replaceAll(" ", "").indexOf("true") != -1) {
			return textLength + 10;
		}
		for (int i = 0; i < e.nodeCount(); i++) {
			Node nChild = e.node(i);
			if (nChild.getNodeType() == Node.ELEMENT_NODE) {
				Element eChild = (Element) nChild;
				if ("select".equalsIgnoreCase(e.getName())
						&& "option".equalsIgnoreCase(eChild.getName())) {// 父节点为select子节点为option
					textLength += getTextLength(nChild);
					break;
				} else {
					textLength += getTextLength(nChild);
				}
			} else {
				textLength += getTextLength(nChild);
			}
		}
		return textLength;
	}

	/**
	 * TODO 获得该节点下图片标签的大小，通过图片压缩率计算而得 默认一张图片大小为30个文字 如果样式为"display:none"不参与统计
	 * 
	 * @param e
	 * @param ctx
	 * @return imgSize
	 */
	public double getElementImgSize(Element e, WebContext ctx) {
		if (e == null || e.getNodeType() == Node.TEXT_NODE)
			return 0;
		if (e.attributeValue("navigator-colum") != null
				&& e.attributeValue("navigator-colum").toLowerCase()
						.replaceAll(" ", "").indexOf("true") != -1) {
			return 0;
		}
		if (e.attributeValue("style") != null
				&& e.attributeValue("style").toLowerCase().replaceAll(" ", "")
						.indexOf("display:none") != -1) {
			return 0;
		}
		if ("textarea".equalsIgnoreCase(e.getName()) && e.hasContent()) {// 特殊处理淘宝的textarea中为HTML的部分
			e = praseTextareaToElement(e);
			return getElementImgSize(e, ctx);
		}
		if (e.attributeValue("class") != null
				&&
				/*
				 * && e.attributeValue("class").toLowerCase().replaceAll(" ",
				 * "") .indexOf("hide") != -1)
				 */
				(e.attributeValue("class").toLowerCase().replaceAll(" ", "")
						.indexOf("hide") != -1 || e.attributeValue("class")
						.toLowerCase().replaceAll(" ", "").indexOf("hidden") != -1)) {// 特殊处理凤凰论坛下class中hide的问题//以及163下texarea内class为hidden
			return 0;
		}
		if (e.attributeValue("class") != null
				&& e.attributeValue("class").toLowerCase().replaceAll(" ", "")
						.indexOf("disp_none") != -1) {// 特殊处理http://www.kuwo.cn/下class中diplay:none的问题
			return 0;
		}
		if ("img".equalsIgnoreCase(e.getQName().getName())) {
			if (e.attributeValue("height") != null
					&& e.attributeValue("width") != null) {
				try {
					double iH = Double.parseDouble(e.attributeValue("height"));
					double iW = Double.parseDouble(e.attributeValue("width"));
					ScaleByIm4java scaleByIm4java = new ScaleByIm4java();
					double scale = scaleByIm4java.getScale(getWH(ctx), iW, iH);
					return iH * scale;// 一个字大约是400个像素
				} catch (Exception ep) {
					return DefalutImgSize;
				}
			} else {
				return DefalutImgSize;// 图片无高宽属性则默认为20个字
			}
		}
		double imgNum = 0;
		for (int i = 0; i < e.nodeCount(); i++) {
			if (e.node(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element eChild = (Element) e.node(i);
			imgNum += getElementImgSize(eChild, ctx);
		}
		return imgNum;
	}

	/**
	 * desc 返回Element内所有的文本
	 * 
	 * @param n
	 * @return String
	 */
	public String getNodeText(Node n) {
		StringBuffer text = new StringBuffer();
		if (n == null || STYLE.equalsIgnoreCase(n.getName())
				|| SCRIPT.equalsIgnoreCase(n.getName())
				|| NOSCRIPT.equalsIgnoreCase(n.getName()))
			return text.toString();
		if (n.getNodeType() == Node.TEXT_NODE) {
			if (n.getText() != null) {
				char[] t = n.getText().toCharArray();
				for (char c : t) {
					if (c != '\t' && c != '\n' && c != ' ') {
						text.append(c);
					}
				}
			}
			return text.toString();
		}
		if (n.getNodeType() != Node.ELEMENT_NODE) {
			return text.toString();
		}
		Element e = (Element) n;
		if ("textarea".equalsIgnoreCase(e.getName()) && e.hasContent()) {// 特殊处理淘宝的textarea中为HTML的部分
			e = praseTextareaToElement(e);
			text = new StringBuffer(getNodeText(e));
			return text.toString();
		}
		for (int i = 0; i < e.nodeCount(); i++) {
			Node nChild = e.node(i);
			text.append(getNodeText(nChild));
		}
		return text.toString();
	}

	/**
	 * desc 返回node内所有的<a>标签内的文本
	 * 
	 * @param n
	 * @return String
	 */
	private String getANodeText(Node n) {
		StringBuffer text = new StringBuffer();
		if (n == null)
			return text.toString();
		if ("a".equalsIgnoreCase(n.getName())) {
			text = new StringBuffer(getNodeText(n));
		}
		if (n.getNodeType() != Node.ELEMENT_NODE) {
			return text.toString();
		}
		Element e = (Element) n;
		if ("textarea".equalsIgnoreCase(e.getName()) && e.hasContent()) {// 特殊处理淘宝的textarea中为HTML的部分
			e = praseTextareaToElement(e);
			text = new StringBuffer(getANodeText(e));
			return text.toString();
		}
		for (int i = 0; i < e.nodeCount(); i++) {
			Node nChild = e.node(i);
			text.append(getANodeText(nChild));
		}
		return text.toString();
	}

	/**
	 * desc 该节点是否为content节点
	 * 
	 * @param e
	 * @param title
	 * @param keywords
	 *            []
	 * @return boolean
	 */
	public boolean isContent(Element e, String title, String[] keywords) {
		LevenshteinDistance ld = new LevenshteinDistance();
		if (e == null || title == null || title.equalsIgnoreCase(""))
			return false;
		if (hrefCount(e) > 0) {
			if (getTextLength(e) / hrefCount(e) < 5)
				return false;
			double atextLength = getANodeText(e).length();
			double textLength = getNodeText(e).length();
			if (atextLength / textLength > 0.4) {
				return false;
			}
		}
		// 长度为标题3倍以下的，直接为title
		if (getTextLength(e) >= 10 * title.length())
			return true;
		if (getTextLength(e) >= 5 * title.length() && hrefCount(e) < 5)
			return true;
		if (keywords != null) {
			if (keywordsTimes(e, keywords) > 3 * keywords.length)
				return true;
		}
		if (ld.sim(title, getNodeText(e)) > 0.1)
			return true;

		if (getTextLength(e) >= 5 * title.length() && hrefCount(e) >= 5) {
			// 未处理//长度为标题的5-10倍，且hrefCount>=5的节点，未判断
		}
		if (getTextLength(e) < 5 * title.length()
				&& getTextLength(e) >= 3 * title.length()) {
			// 未处理//长度为标题的3-5倍的节点未处理
		}
		return false;
	}

	/**
	 * desc 该节点是否为relatedcontent节点
	 * 
	 * @param e
	 * @return boolean
	 */
	private boolean isRelatedContent(Element e) {
		// 如果超链接文本占到80%，且该节点下有关键词出现，相似度较高，则设置为主题相关节点，节点内文本长度>=标题或关键字长度

		return false;
	}

	/**
	 * desc 统计关键词在node的text中出现的次数
	 * 
	 * @param e
	 * @param keywords
	 *            []
	 * @return int
	 */
	public int keywordsTimes(Element e, String[] keywords) {
		int times = 0;
		if (e == null || keywords == null)
			return times;
		if (keywords.length == 0)
			return times;
		for (String kw : keywords) {
			times += getNodeText(e).split(kw).length - 1;
		}
		return times;
	}

	/**
	 * desc 寻找Node及其子节点中是否有title节点(根据<h1>和LD算法)
	 * 
	 * @param n
	 * @param title
	 * @return boolean
	 */
	public boolean findTitlefromNode(Node n, String title) {
		LevenshteinDistance ld = new LevenshteinDistance();
		SequenceUtils su = new SequenceUtils();
		if (n == null)
			return false;
		if (title == null || title.equalsIgnoreCase(""))
			return false;
		if (H1.equalsIgnoreCase(n.getName())) {
			double sim = ld.sim(su.getNodeText(n), title);
			if (sim > SimWithTitleH1)
				return true;
		} else if (n.getNodeType() == Node.TEXT_NODE) {
			double sim = ld.sim(n.getText(), title);
			if (sim > SimWithTitleNoH1)
				return true;
		} else if (n.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) n;
			for (int i = 0; i < e.nodeCount(); i++) {
				if (e.node(i) == null)
					continue;
				if (findTitlefromNode(e.node(i), title))
					return true;
			}
		}
		return false;
	}

	/**
	 * desc 取html的head中的title
	 * 
	 * @param src
	 * @return title
	 */
	public String getHeadTitle(Element head) {
		Node title = null;
		if (head == null) {
			return null;
		}
		title = head.selectSingleNode("//TITLE");
		if (title != null) {
			return title.getText();
		}
		return null;
	}

	/**
	 * desc 取html的head中的keywords
	 * 
	 * @param src
	 * @return keywords[]
	 */
	public String[] getHeadKeywords(Element head) {
		String keywords[] = null;
		if (head == null)
			return keywords;
		List<Element> metas = head.elements("META");
		String metacontent = "";
		if (metas != null && metas.size() > 0) {
			for (Element meta : metas) {
				String metaname = meta.attribute("name") == null ? "" : meta
						.attribute("name").getText().trim();
				if ("keywords".equalsIgnoreCase(metaname)) {
					metacontent = meta.attribute("content") == null ? "" : meta
							.attribute("content").getText().trim();
					break;
				}
			}
		}
		if (!metacontent.equals("")) {
			metacontent = metacontent.replaceAll("\\p{Punct}", " ").replaceAll(
					"\\pP", " ").trim();// 利用java正则去除全角半角标点
			metacontent = metacontent.replace("  ", " ").trim();
			keywords = metacontent.split(" ");
		}
		return keywords;
	}

	/**
	 * desc 计算该节点下href的数量,返回-1表示节点无NATIVEHREF属性
	 * 
	 * @param e
	 * @return int
	 */
	public int hrefCount(Element e) {
		if (e == null)
			return -1;
		if (e.attributeValue(NATIVEHREF) != null
				&& e.attributeValue(NONATIVEHREF) != null) {
			int nativehref = Integer.parseInt(e.attributeValue(NATIVEHREF));
			int nonativehref = Integer.parseInt(e.attributeValue(NONATIVEHREF));
			return nativehref + nonativehref;
		}
		return -1;
	}

	/**
	 * TODO 特殊处理淘宝的textarea中的html代码为Element
	 * 
	 * @param e
	 * @return Element
	 * @throws Exception
	 */
	private Element praseTextareaToElement(Element e) {
		Element result = null;
		if (e == null || !"textarea".equalsIgnoreCase(e.getName())) {
			return e;
		}
		String strContent = e.getTextTrim();
		Pattern p = Pattern.compile("^[^<]?(<(.)+>)[^>]?$|^#([\\w-]+)$");
		Matcher m = p.matcher(strContent);
		if (m.find()) {
			// 识别<textarea></textarea>中存放html内容的情况
			strContent = strContent.replaceAll("&lt;", "<").replaceAll("&gt;",
					">");
			InputSource inputSource = new InputSource(new StringReader(
					strContent));
			try {
				Element eleContent = parse(inputSource); // 解析数据
				result = eleContent.element("BODY").createCopy();
				result.setName("span");
			} catch (Exception ex) {
				// 出错不做任何处理
			}
		} else {
			for (int i = 0; i < e.nodeCount(); i++) {
				result = e.createCopy("span");
			}
		}
		return result;
	}

	/**
	 * TODO 标签补全
	 * 
	 * @param source
	 * @return Element
	 * @throws Exception
	 */
	private Element parse(InputSource source) throws Exception {
		DOMParser parser = new DOMParser();
		// 是否允许命名空间
		parser.setFeature("http://xml.org/sax/features/namespaces", false);
		// 是否允许增补缺失的标签。如果要以XML方式操作HTML文件，此值必须为真
		parser.setFeature("http://cyberneko.org/html/features/balance-tags",
				true);
		// 是否剥掉<script>元素中的<!-- -->等注释符
		parser
				.setFeature(
						"http://cyberneko.org/html/features/scanner/script/strip-comment-delims",
						true);
		parser.setFeature("http://cyberneko.org/html/features/augmentations",
				true);
		parser.setProperty("http://cyberneko.org/html/properties/names/elems",
				"lower");
		parser.setProperty(
				"http://cyberneko.org/html/properties/default-encoding",
				"UTF-8");

		// 添加管道过滤器
//		ClassifyFilter classifyFilter = new ClassifyFilter();
//		XMLDocumentFilter[] filters = { classifyFilter };
//		parser.setProperty("http://cyberneko.org/html/properties/filters",
//				filters);
		// cyberneko解析：将httpclient读取的数据source解析成w3c的Document对象
		parser.parse(source);
		org.w3c.dom.Document doc = parser.getDocument();
		DOMReader dr = new DOMReader();
		return dr.read(doc).getRootElement();
	}

	/**
	 * TODO 获得设备屏幕的高宽
	 * 
	 * @param ctx
	 * @return Element
	 * @throws Exception
	 */
	private double[] getWH(WebContext ctx) {
		// TODO Auto-generated method stub
		double[] wh_temp = new double[2];
		String[] screens = ctx.getScreen();
		double width = Integer.parseInt(screens[1]);
		double height = Integer.parseInt(screens[0]);
		wh_temp[0] = width;
		wh_temp[1] = height;
		return wh_temp;
	}

	/**
	 * 
	 * 判断nodeH1里面的text是否和titleName相同
	 * 
	 * **/
	public boolean fromTitleJudgmentIsReadMode(Node nodeH1, String titleName) {

		String h1Name = nodeH1.getText();

		if (h1Name == null) {
			h1Name = getElementTitleName((Element) nodeH1);
		}

		if (h1Name == null || h1Name.length() == 0 || titleName == null
				|| h1Name.trim().length() == 0) {
			return false;
		}

		if (titleName.indexOf("-") != -1) {
			titleName = titleName.substring(0, titleName.indexOf("-"));
		}

		if (h1Name.lastIndexOf("(") != -1) {
			h1Name = h1Name.substring(0, h1Name.indexOf("("));
		}

		if (h1Name.indexOf("第") != -1 && h1Name.indexOf("章") != -1) {
			if (h1Name.indexOf("第") + 2 == h1Name.indexOf("章")) {
				h1Name = h1Name.substring(h1Name.indexOf("章") + 1);
			}
		}

		h1Name = h1Name.trim();
		titleName = titleName.trim();

		if (h1Name.length() == titleName.length()) {
			if ((h1Name.trim()).equalsIgnoreCase(titleName.trim())) {
				return true;
			}
		} else if (titleName.length() > h1Name.length()) {
			for (int i = 0; i < (titleName.length() - h1Name.length() + 1); i++) {
				if ((h1Name.trim()).equals(titleName.substring(i, h1Name
						.length()
						+ i))) {
					return true;
				}
			}
		} else if (titleName.length() < h1Name.length()) {
			for (int i = 0; i < (h1Name.length() - titleName.length() + 1); i++) {
				if ((titleName.trim()).equals(h1Name.substring(i, titleName
						.length()
						+ i))) {
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * 去除NodeH1或者是NodeH2中的A标签 因为小说网站的title
	 */
	@SuppressWarnings("unchecked")
	void removeTagLen(Element element) {
		List<Node> childrenList = element.content(); // 获取当前节点子内容
		for (int i = 0; i < childrenList.size(); i++) {
			Node child = childrenList.get(i); // element的孩子节点

			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element eleChild = (Element) child; // element的孩子节点
				String tagName = eleChild.getName();
				String tagText = eleChild.getText();
				if ("A".equals(tagName)) {
					eleChild.detach();
					continue;
				}
				removeTagLen((Element) child);
			}
		}
	}

	private String getElementTitleName(Element nodeH1) {
		String titleName = null;
		List<Element> elements = nodeH1.elements();
		if (elements.size() == 0) { // 没有子元素
			titleName = nodeH1.getText();
			return titleName;
		} else { // 有子元素
			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				getElementTitleName(e);
			}
		}
		return titleName;
	}
}
