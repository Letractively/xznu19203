package com.c_platform.parsebody.parsebody.filter;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.parsebody.parsebody.WebContext;
import com.c_platform.parsebody.parsebody.sequence.SequenceUtils;

public class SequenceFilter {

	private static String[] titleFromBody = { "H1", "H2", "DIV", "FONT",
			"SPAN", "B" };

	public static WebContext sequence(WebContext ctx) {
		ArrayList<Node[]> blocks = (ArrayList<Node[]>) ctx.getContent();

		// 判断是否应该进阅读模式 ---start-----------------------------
		boolean isReadMode = false;
		isReadMode = isEnterReadModel(blocks, ctx); // 判断是否进入阅读模式
		return ctx;
	}

	/**
	 * 判断是否进入阅读模式
	 * 
	 * **/
	private static boolean isEnterReadModel(ArrayList<Node[]> blocks,
			WebContext ctx) {
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

	private static boolean isEnterReadModelByUrl(ArrayList<Node[]> blocks,
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

	private static boolean isExistTwoUrl(String strName, String url) {
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

	private static boolean isEndTwoUrl(String url) {
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

	private static boolean isEndOneUrl(String url) {
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

	/**
	 * 判断否应该进入阅读模式： title与H1或者H2里面相同。
	 * **/
	@SuppressWarnings("unchecked")
	private static boolean findTitleFromBody(ArrayList<Node[]> blocks,
			WebContext ctx) {

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
	private static boolean isTitleExistBodyNode(WebContext ctx,
			Element element, String titleName) {
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

	private static boolean isTitleTage(String[] titlefrombody2, String tagName) {
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
}
