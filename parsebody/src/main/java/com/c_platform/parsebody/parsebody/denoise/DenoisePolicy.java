package com.c_platform.parsebody.parsebody.denoise;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.parsebody.parsebody.sequence.SequenceUtils;
import com.c_platform.parsebody.parsebody.util.ForumUtil;

//去噪
public class DenoisePolicy {
	private static final String NONATIVEHREF = "nonativehref";

	private static final String NATIVEHREF = "nativehref";

	private static final double NONATIVEHREFTHRESHOLD = 0.8; // 该节点下外部链接的比例阈值

	private static final String NODETYPE = "nodetype"; // 节点类型：title、content、noise、navi、other

	private static final String HEAD = "HEAD";
	private static final String BODY = "BODY";
	private static final String HTML = "HTML";

	/**
	 * desc 赋予该节点下有Href属性
	 * 
	 * @param node
	 * @return void
	 */
	@SuppressWarnings("unchecked")
	public void addHrefForNode(Node node, String currentPageDomain) {
		int nativeHref = 0;
		int noNativeHref = 0;
		if (node == null || currentPageDomain == null
				|| currentPageDomain.equals(""))
			return;
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return;
		Element e = (Element) node;
		List<Node> children = ((Element) node).content();
		if (children != null && children.size() > 0) {
			for (Node child : children) {
				if (child == null)
					continue;
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element eleChild = (Element) child;
				addHrefForNode(child, currentPageDomain);
				if (eleChild.attributeValue(NATIVEHREF) != null
						&& eleChild.attributeValue(NONATIVEHREF) != null) {
					nativeHref += Integer.parseInt(eleChild
							.attributeValue(NATIVEHREF));
					noNativeHref += Integer.parseInt(eleChild
							.attributeValue(NONATIVEHREF));
				}
				if (eleChild.attributeValue("href") != null) {// 只要节点中存在href属性
					if (isNativeHref(eleChild, currentPageDomain)) {
						nativeHref++;
					} else {
						noNativeHref++;
					}
				}
			}// end of for
		}
		e.addAttribute(NONATIVEHREF, Integer.toString(noNativeHref));
		e.addAttribute(NATIVEHREF, Integer.toString(nativeHref));
	}

	/**
	 * desc 判断某节点是否存在href链接，如果href存在且属于当前服务器域名，返回true
	 * 
	 * @param e
	 * @return boolean
	 */
	private boolean isNativeHref(Element e, String currentPageDomain) {
		if (e == null)
			return false;
		try {
			URL url = new URL(e.attributeValue("href"));
			if (url.getHost().contains(currentPageDomain.toLowerCase())) {
				return true;
			}
		} catch (MalformedURLException e1) {
			return true;
		}
		return false;
	}

	/**
	 * desc 取该URL的域名，返回baidu.com//sina.com等
	 * 
	 * @param url
	 * @return void
	 */
	public String getCurrentPageDomain(String url) {
		URL tmpUrl;
		try {
			tmpUrl = new URL(url);
		} catch (MalformedURLException e) {
			return "";
		}
		String domain = tmpUrl.getHost();
		if (domain.lastIndexOf(".") == -1)
			return ""; // 例如host为localhost:8080
		String tmp = domain.substring(domain.lastIndexOf(".") + 1);// 取com或者cn
		Pattern pattern = Pattern.compile("[0-9]*");
		if (pattern.matcher(tmp).matches())
			return ""; // 例如127.0.0.1,判断tmp是否为1
		domain = domain.substring(0, domain.lastIndexOf("."));// 取http://www.baidu或者http://www.sina.com
		String comORnet = domain.substring(domain.lastIndexOf(".") + 1);
		if ("com".equalsIgnoreCase(comORnet)
				|| "net".equalsIgnoreCase(comORnet)
				|| "edu".equalsIgnoreCase(comORnet)
				|| "org".equalsIgnoreCase(comORnet)) {
			domain = domain.substring(0, domain.lastIndexOf("."));
		} else {
			comORnet = tmp;
		}
		domain = domain.substring(Math.max(domain.lastIndexOf("."), domain
				.lastIndexOf("/")) + 1);// 取baidu
		domain += "." + comORnet;
		return domain;
	}

	/**
	 * desc 对Blocks进行去噪，返回afterDenoiseBlocks
	 * 
	 * @param beforeDenoiseBlocks
	 */
	public ArrayList<Node[]> denoiseBlocks(ArrayList<Node[]> beforeDenoiseBlocks) {
		if (beforeDenoiseBlocks == null)
			return null;
		if (beforeDenoiseBlocks.size() == 0)
			return null;
		SequenceUtils su = new SequenceUtils();
		ArrayList<Node[]> afterDenoiseBlocks = new ArrayList<Node[]>();
		for (Node[] ns : beforeDenoiseBlocks) {
			if (ns == null)
				continue;
			if (ns.length == 0)
				continue;
			Node[] newNodes = new Node[ns.length];
			int i = 0;
			for (Node n : ns) {
				if (n == null)
					continue;
				Element e = (Element) n;
				if (su.hrefCount(e) < 6) {// 节点下链接数量少于6个，添加
					newNodes[i] = n;
					i++;
				} else if (noNativeHrefProportion(e) < NONATIVEHREFTHRESHOLD) {// 节点下链接数量大于等于6个，再来考虑是否外链比例
					newNodes[i] = n;
					i++;
				}
			}
			afterDenoiseBlocks.add(newNodes);
		}
		return afterDenoiseBlocks;
	}

	/**
	 * desc 计算该节点下外链占到的比例
	 * 
	 * @param e
	 * @return double
	 */
	public double noNativeHrefProportion(Element e) {
		if (e == null)
			return -1;
		if (e.attributeValue(NATIVEHREF) != null
				&& e.attributeValue(NONATIVEHREF) != null) {
			double nativehref = Double
					.parseDouble(e.attributeValue(NATIVEHREF));
			double nonativehref = Double.parseDouble(e
					.attributeValue(NONATIVEHREF));
			if (nativehref + nonativehref == 0)
				return 0;
			return nonativehref / (nativehref + nonativehref);
		}
		return -1;
	}

	/**
	 * desc 对Blocks进行去噪，增加nodetype属性
	 * 
	 * @param blocks
	 * @return ArrayList<Node[]> blocks
	 */
	@SuppressWarnings("unused")
	private ArrayList<Node[]> addDenoiseAttribute(ArrayList<Node[]> blocks) {
		for (Node[] ns : blocks) {
			for (Node n : ns) {
				Element e = (Element) n;
				if (noNativeHrefProportion(e) >= NONATIVEHREFTHRESHOLD) {
					e.addAttribute(NODETYPE, "noise");
				}
			}
		}
		return blocks;
	}

	/**
	 * desc 过滤（JS控制的弹出）广告(互联网模式)
	 * 
	 * @param url
	 * @param element
	 * @param model
	 *            (0:缩放模式,1:自适应模式-互联网模式,2:RSS模式)
	 * @return Element html
	 */
	@SuppressWarnings("unchecked")
	public Element filterJsAdvertise(String url, Element element, String model) {
		if (url == null || "".equalsIgnoreCase(url.trim()) || element == null)
			return element;

		ArrayList<Element> headFilter = new ArrayList<Element>();
		ArrayList<Element> headReserve = new ArrayList<Element>();
		ArrayList<Element> bodyFilter = new ArrayList<Element>();
		ArrayList<Element> bodyReserve = new ArrayList<Element>();

		if ("0".equalsIgnoreCase(model)) {// 互联网模式/缩放模式
			if (headFilter.size() > 0)
				for (Node n : headFilter) {
					n.detach();
				}
			if (headReserve.size() > 0)
				element = reserveHead(element, headReserve);
			if (bodyFilter.size() > 0)
				for (Node n : bodyFilter) {
					n.detach();
				}
			if (bodyReserve.size() > 0)
				element = reserveBody(element, bodyReserve);
		} else if ("1".equalsIgnoreCase(model) || "2".equalsIgnoreCase(model)) {// 云加速模式/自适应模式//RSS模式
			if (headFilter.size() > 0)
				for (Node n : headFilter) {
					n.detach();
				}
			if (headReserve.size() > 0)
				element = reserveHead(element, headReserve);
			if (bodyFilter.size() > 0)
				for (Node n : bodyFilter) {
					n.detach();
				}
			if (bodyReserve.size() > 0)
				element = reserveBody(element, bodyReserve);
		}
		return element;
	}

	private Element reserveHead(Element element, ArrayList<Element> headReserve) {
		Element head = element.element(HEAD);
		Element body = element.element(BODY);
		Element result = DocumentHelper.createElement(HTML);
		Element resultHead = result.addElement(HEAD);
		if (head != null) {
			if (head.attributeCount() != 0) {
				resultHead.appendAttributes(head);
			}
			for (Element e : headReserve) {
				resultHead.add(e.createCopy());
			}
		}
		Element resultBody = result.addElement(BODY);
		if (body != null) {
			if (body.attributeCount() != 0) {
				resultBody.appendAttributes(body);
			}
			resultBody.appendContent(body);
		}
		return result;
	}

	private Element reserveBody(Element element, ArrayList<Element> bodyReserve) {
		Element head = element.element(HEAD);
		Element body = element.element(BODY);

		Element result = DocumentHelper.createElement(HTML);
		Element resultHead = result.addElement(HEAD);
		if (head != null) {
			ForumUtil.removeLinkAndScriptInHead(head);// 把js和css去掉，减少流量，并且发现有的网页存在js的话会导致内容丢失
			if (head.attributeCount() != 0) {
				resultHead.appendAttributes(head);
			}
			resultHead.appendContent(head);
		}
		Element resultBody = result.addElement(BODY);
		if (body != null) {
			if (body.attributeCount() != 0) {
				resultBody.appendAttributes(body);
			}
			for (Element e : bodyReserve) {
				resultBody.add(e.createCopy());
			}
		}
		return result;
	}

}