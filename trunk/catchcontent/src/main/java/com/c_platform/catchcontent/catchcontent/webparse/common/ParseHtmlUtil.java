package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import com.c_platform.catchcontent.catchcontent.util.StringUtil;
import com.c_platform.catchcontent.catchcontent.vips.CommonDivideUtils;
import com.c_platform.catchcontent.catchcontent.vips.NodeUtils;

public class ParseHtmlUtil {
	private final static String IMG = "img"; // img标签

	// public final static DocumentFactory m_factory =
	// DocumentFactory.getInstance();

	/**
	 * TODO 判断目标字符串标签数组中是否含有该标签
	 * 
	 * @param str
	 *            匹配目标
	 * @param strArray
	 *            字符串数组
	 * @return 有true;没有false
	 */
	public static boolean hasTag(String str, String[] strArray) {
		for (String item : strArray) {
			if (str.equalsIgnoreCase(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO 判断目标字符串标签数组中是否含有该标签
	 * 
	 * @param str
	 *            匹配目标
	 * @param strArray
	 *            字符串数组
	 * @return 有true;没有false
	 */
	public static boolean matchRegax(String str, String[] strArray) {
		Pattern p;
		Matcher m;
		for (String regex : strArray) {
			p = Pattern.compile(regex);
			m = p.matcher(str.toLowerCase());
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * TODO 获取折叠标签标题名称
	 * 
	 * @param node
	 *            当前节点
	 * @param resultBuffer
	 *            当前节点内含有文本内容
	 * @return 当前节点的标题文本内容
	 */
	@SuppressWarnings("unchecked")
	public static StringBuffer getNodeTitle(Node node, StringBuffer resultBuffer) {
		if (resultBuffer != null && resultBuffer.length() > 30) {
			return resultBuffer; // 如果收集的标题名称超过30就停止收集
		}

		if (node.getNodeType() == Node.TEXT_NODE
				&& !CommonDivideUtils.isNullContentElement(node)) {
			assert resultBuffer != null;
			resultBuffer.append(node.getText().trim());
			resultBuffer.append(" "); // 收集的标题名称
			return resultBuffer;
		} else if (node.getNodeType() == Node.ELEMENT_NODE) {
			List<Node> children = ((Element) node).content();
			if (children != null && children.size() > 0) {
				for (Node child : children) {
					resultBuffer = getNodeTitle(child, resultBuffer); // 递归子内容
				}
			}
		}
		return resultBuffer;
	}

	/**
	 * 
	 * TODO 获取上一个兄弟元素
	 * 
	 * @param list
	 *            节点集合
	 * @param m
	 *            指针
	 * @return 当前element的前一个兄弟element
	 */
	public static Element getPreElement(List<Node> list, int m) {
		Node n;
		while ((m = m - 1) >= 0) {
			n = list.get(m);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) n;
			}
		}
		return null;
	}

	/**
	 * TODO 获取当前节点内部的文本字节大小
	 * 
	 * @param node
	 *            当前节点
	 * @param result
	 *            文本字节长度
	 * @return 字节大小
	 */
	@SuppressWarnings("unchecked")
	public static long getTextLen(Node node, long result) {
		if (node.getNodeType() == Node.TEXT_NODE
				&& !CommonDivideUtils.isNullContentElement(node)) {
			result += node.getText().getBytes().length;
			return result;
		} else if (node.getNodeType() == Node.ELEMENT_NODE) {
			List<Node> children = ((Element) node).content();
			if (children != null && children.size() > 0) {
				for (Node child : children) {
					result = getTextLen(child, result);
				}
			}
		}
		return result;
	}

	/**
	 * TODO 判断当前的UL是否为大块
	 * 
	 * @param element
	 *            当前节点
	 * @return 大块返回true,否则返回false
	 */
	@SuppressWarnings("unchecked")
	public static boolean needChangeUL(Element element, int bigUlBound) {
		List<Element> childrenList = element.elements("LI");
		long len = 0;
		int size = childrenList.size();
		for (Element ec : childrenList) {
			len += ParseHtmlUtil.getTextLen(ec, 0);// 计算字符长度
		}
		return size > 0 && len / size < bigUlBound;
	}

	/**
	 * 
	 * TODO 标签补全
	 * 
	 * @param source
	 *            源
	 * @return 过滤后的内容
	 * @throws Exception
	 *             异常
	 */
	public static Element parse(InputSource source) throws Exception {
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
		ClassifyFilter classifyFilter = new ClassifyFilter();
		XMLDocumentFilter[] filters = { classifyFilter };
		parser.setProperty("http://cyberneko.org/html/properties/filters",
				filters);
		// cyberneko解析：将httpclient读取的数据source解析成w3c的Document对象
		parser.parse(source);
		org.w3c.dom.Document doc = parser.getDocument();
		DOMReader dr = new DOMReader();
		return dr.read(doc).getRootElement();
	}

	/**
	 * 
	 * TODO 判断是否需要改变该图片是否折行
	 * 
	 * @param eleChild
	 *            当前节点
	 * @return 是否需要改变图片元素的属性
	 */
	public static boolean needToChangeImagElement(Element eleChild,
			String regexUnit) {
		String tagName = eleChild.getName();
		if (IMG.equalsIgnoreCase(tagName)) {
			if (StringUtil.matchRegax(eleChild.getPath(), "/[H|h][1-4]/")) {
				// 如果其父节点中含有标题标签则不折行
				return false;
			}
			if ("td".equalsIgnoreCase(eleChild.getParent().getName())) {
				// 如果其父节点是TD则不折行
				return false;
			}
			int parentVirtualNodeNum = NodeUtils
					.getVirtualTextNodeNumber(eleChild.getParent());
			if (eleChild.attribute("width") != null) {
				int imgwidth = 0;
				String tmpString = eleChild.attributeValue("width").replaceAll(
						regexUnit, "");
				if (StringUtil.isNumeric(tmpString)) {
					imgwidth = Integer.valueOf(tmpString);
				}
				if (imgwidth > 90) {
					// 如果img标签含有width属性，并且width值大于90则折行
					return true;
				} else if (parentVirtualNodeNum >= 1) {
					// 如果img标签含有width属性，其父节点还含有是虚拟文本节点的子节点，则折行
					return true;
				}
			}
			if (eleChild.attribute("height") != null) {
				int imgHeight = 0;
				String tmpString = eleChild.attributeValue("height")
						.replaceAll(regexUnit, "");
				if (StringUtil.isNumeric(tmpString)) {
					imgHeight = Integer.valueOf(tmpString);
				}
				if (imgHeight > 50) {
					// 如果img标签含有Height属性，并且Height值大于90则折行
					return true;
				} else if (parentVirtualNodeNum >= 1) {
					// 如果img标签含有Height属性，其父节点还含有是虚拟文本节点的子节点，则折行
					return true;
				}
			}
			if (eleChild.attribute("alt") != null
					&& !"".equals(eleChild.attributeValue("alt"))
					&& parentVirtualNodeNum >= 1) {
				// 如果img标签含有alt属性且值不为空，其父节点还含有是虚拟文本节点的子节点，则折行
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * TODO 添加方法注释
	 * 
	 * @param element
	 *            当前节点
	 * @return 是否为合法的table
	 */
	public static boolean isLegalTable(Element element, String[] tabLib) {
		boolean result = false;
		String xpath = element.getPath();
		String[] xpathArray = xpath.split("/");

		String name = element.getName();
		if ("table".equalsIgnoreCase(name)) {
			result = true;
		} else if ("td".equalsIgnoreCase(name)) {
			// td标签的父节点必须含有tr
			for (int t = xpathArray.length - 2; t >= 0; t--) {
				if (hasTag(xpathArray[t], tabLib)) {
					if ("tr".equalsIgnoreCase(xpathArray[t])) {
						result = true;
						break;
					}
				}
			}
		} else if ("tr".equalsIgnoreCase(name) || "th".equalsIgnoreCase(name)) {
			// tr,th标签的父节点必须含有table
			for (int t = xpathArray.length - 2; t >= 0; t--) {
				if (hasTag(xpathArray[t], tabLib)) {
					if ("table".equalsIgnoreCase(xpathArray[t])) {
						result = true;
						break;
					}
				}
			}
		}
		return !result;
	}

	/**
	 * 
	 * TODO 判断当前节点是否只含有文本节点
	 * 
	 * @param element
	 *            当前节点
	 * @return 是否仅含有文本节点
	 */
	@SuppressWarnings("unchecked")
	public static boolean haveOnlyTextNode(Element element) {
		if (element.isTextOnly()) {
			return true;
		}
		boolean result = true;
		for (Iterator<Element> iterator = element.elementIterator(); iterator
				.hasNext();) {
			Element ec = iterator.next();
			if (hasTag(ec.getName(), new String[] { "img", "input", "select",
					"div", "ul" })) {
				// "img","input","select","div"除外
				return false;
			} else {
				result = result && haveOnlyTextNode(ec);
			}
		}
		return result;
	}

	public static int getLastElementIndex(List<Node> nodeList) {
		for (int p = nodeList.size() - 1; p >= 0; p--) {
			if (nodeList.get(p).getNodeType() == Node.ELEMENT_NODE) {
				return p;
			}
		}
		return -1;
	}

	/**
	 * desc 将 Element对象转换为String
	 * 
	 * @param element
	 * @param charset
	 * @param istrans
	 * @return String
	 * @throws IOException
	 */
	public static String formateXml(Element element, String charset,
			boolean istrans) throws IOException {
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
}
