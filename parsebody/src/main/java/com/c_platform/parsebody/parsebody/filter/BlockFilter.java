package com.c_platform.parsebody.parsebody.filter;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.parsebody.parsebody.WebContext;
import com.c_platform.parsebody.parsebody.denoise.DenoisePolicy;
import com.c_platform.parsebody.parsebody.vips.AddDocAttrForElementUtils;
import com.c_platform.parsebody.parsebody.vips.NavigatorIdentityUtils;

public class BlockFilter {

	private static final int pDOC = 50;

	private static final int DEFAULT_BLOCK_SIZE = 200;

	private static final String HEAD = "HEAD";

	private static final String BODY = "BODY";

	private static final String FRAMESET = "FRAMESET";

	public static final String TYPE = "application/rss+xml";

	public static final String REL = "alternate";

	public static final String METANAME = "generator";

	public static final String METACONTENT = "Discuz";

	public static WebContext block(WebContext ctx) throws Exception{
		DenoisePolicy denoisePolicy = new DenoisePolicy();
		Document doc = (Document) ctx.getContent();
		;

		ArrayList<Node[]> result = new ArrayList<Node[]>();

		Element xhtml = doc.getRootElement();

		String url = ctx.getUrl();
		if (ctx.isRss()) {// 过滤广告
			xhtml = denoisePolicy.filterJsAdvertise(url, xhtml, "2");// RSS模式
		} else {
			xhtml = denoisePolicy.filterJsAdvertise(url, xhtml, "1");// 云加速模式
		}

		Element head = xhtml.element(HEAD);
		Element body = xhtml.element(BODY);
		Element frameset = xhtml.element(FRAMESET);

		int blockSize;
		blockSize = DEFAULT_BLOCK_SIZE;
		// ctx.setRootDoc(doc); // 将去过广告的内容保持到rootDoc中

		// 对daqi,党史今天frameset做跳转处理
		Element bodyElement = null;
		Element metaElement = null;
		if (body == null && frameset != null) {
			String urlString = "";
			String frameName = "FRAME";
			if (url.indexOf("bbs.daqi.com") > -1) {
				frameName = "//*[@name=\"bbsurl\"]";
			}
			Element frameElement = (Element) frameset
					.selectSingleNode(frameName);
			if (frameElement != null) {
				Attribute src = frameElement.attribute("src");
				if (src != null) {
					urlString = frameElement.attributeValue("src");
				}
				// if (enginId.indexOf("adrmini") >= 0
				// || enginId.indexOf("iph") >= 0) {
				// // urlString =modifyFrameSrc(urlString, ctx);
				// urlString = RepairUrl.fillWithGetUrl(ctx,
				// urlString);
				// }
				bodyElement = DocumentHelper.createElement("BODY");
				// String location = "javascript:window.location.replace(\'"
				// + urlString + "\');";
				if (!("".equals(urlString) || "#".equals(urlString))) {
					// bodyElement.addAttribute("onload", location);
					metaElement = DocumentHelper.createElement("META")
							.addAttribute("http-equiv", "Refresh")
							.addAttribute("content", "0;url=" + urlString);
					head.add(metaElement.detach());
				}
				xhtml.add(bodyElement);
				xhtml.remove(frameset.detach());
			}
		}

		if (body == null || body.elements().size() <= 0) {
			result.add(new Node[] { body });
		} else {
			String currentPageDomain = denoisePolicy.getCurrentPageDomain(url);
			if (!currentPageDomain.equals("")) {
				denoisePolicy.addHrefForNode(body, currentPageDomain);
			}
			result = blocking(body, blockSize);
		}

		ctx.setHtmlHead(head);
		ctx.setContent(result);
		return ctx;
	}
	
	public static ArrayList<Node[]> blocking(Element body, int blockSize)
			throws Exception {
		System.out.println(body.asXML());
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

//		NavigatorIdentityUtils.addAttrForElement(result);
		return result;
	}
}
