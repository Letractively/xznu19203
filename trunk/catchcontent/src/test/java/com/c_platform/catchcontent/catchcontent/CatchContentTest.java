package com.c_platform.catchcontent.catchcontent;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.junit.Test;

import com.c_platform.catchcontent.catchcontent.denoise.DenoisePolicy;
import com.c_platform.catchcontent.catchcontent.sequence.MergePolicy;
import com.c_platform.catchcontent.catchcontent.vips.AddDocAttrForElementUtils;
import com.c_platform.catchcontent.catchcontent.vips.NavigatorIdentityUtils;

public class CatchContentTest {
	private WebContext ctx = new WebContext();

	@Test
	public void createTest() {
		ctx.setUrl("http://news.163.com/12/0821/03/89DCJG5K00011229.html");
		ctx.setCharset("gb2312");
		ctx.setScreen(new String[] { "480", "320" });
		ctx.setIframe(false);
		ctx.setRss(true);

		String result = catchHtmlWithHttpClient(ctx);
		ctx.setContent(result);
		Document doc = htmlDocumentParse(result);
		ctx.setContent(doc);
		ctx.setContent(block((Document) ctx.getContent()));
		ctx.setContent(sequence((ArrayList<Node[]>) ctx.getContent()));
		ctx.setContent(wrap((ArrayList<Node[]>) ctx.getContent()));

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
		System.out.println(result.size());
		return result;
	}

	// 排序
	private ArrayList<Node[]> sequence(ArrayList<Node[]> result) {
		// 确定分页大小
		int blockSize = 200;
		MergePolicy mergePolicy = null;
		String ps = "800";
		try {
			int pageSize = Integer.parseInt(ps);
			mergePolicy = new MergePolicy(pageSize, 2 * pageSize, 4 * pageSize);
		} catch (Exception e) {
			mergePolicy = new MergePolicy(400, 800, 1600);
		}
		ArrayList<Node[]> blocksAfterMerge = mergePolicy.merge(result,
				blockSize, ctx);
		System.out.println(blocksAfterMerge.size());
		return blocksAfterMerge;
	}

	/**
	 * 
	 * TODO 删除head中的script标签
	 * 
	 * @param element
	 */
	private void removeScript(Element element) {
		System.out.println(element.asXML());
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
		// comxhtml.addElement(HEAD).appendContent();
		removeScript(comxhtml);
		Element combody = null;
		// if (!ctx.isBodyEnd()) {
		// if (ctx.getRootDoc().getRootElement().element(BODY) != null) {
		// combody = ctx.getRootDoc().getRootElement().element(BODY)
		// .createCopy();
		// combody.clearContent();
		// comxhtml.add(combody);
		// }
		//
		// }
		/**
		 * 余下正文合并
		 */
		// int index = ctx.getHtmlIndex();
		// if (ctx.isRemainText() && index < result.size() - 1) {
		// Node[] thisNodes = result.get(index);
		// Node[] tmp_ex = null;
		// for (int offset = 0; offset < result.size(); offset++) {
		// if (index == offset) {
		// offset++;
		// }
		// Node[] nodes = result.get(offset);
		// if (offset < index) {
		// temp.add(nodes);
		// } else {
		// tmp_ex = new Node[thisNodes.length + nodes.length];
		// System.arraycopy(thisNodes, 0, tmp_ex, 0, thisNodes.length);
		// System.arraycopy(nodes, 0, tmp_ex, thisNodes.length,
		// nodes.length);
		// thisNodes = tmp_ex;
		// }
		// }
		// if (tmp_ex != null) {
		// temp.add(tmp_ex);
		// }
		// result = temp;
		// }
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
		for (Document d : blocks) {
			System.out.println("=================================");
			System.out.println(d.asXML());
			String str = d.asXML();
			try {
				FileWriter fw = new FileWriter("d:\\test.html");
				fw.write(str,0,str.length());
				fw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("---------------------------------");
		}
		return blocks;
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

	
}
