package com.c_platform.catchcontent.catchcontent;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.Element;

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
import org.dom4j.io.DOMReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

public class CatchContentTest {

	// private static final String url =
	// "http://news.163.com/12/0821/03/89DCJG5K00011229.html";
	private static final String url = "http://www.baidu.com";
	private static final String charset = "gb2312";

	@Test
	public void createTest() {
		System.out
				.println("=============Catch Content Test Begin================");
		htmlDocumentParse(catchHtmlWithHttpClient());
	}

	/**
	 * 1.先抓取网页 2.Html补全格式化 3.Html标签解析 4.去除js,css 5.分块 6.取正文 7.返回xhtml
	 */

	// 用httpclient抓取网页内容
	public String catchHtmlWithHttpClient() {
		String resource = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("==============抓取成功=============");
				resource = EntityUtils.toString(response.getEntity(), charset);
				System.out.println(resource);
			} else {
				System.out.println("==============抓取失败=============");
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

	// 标签补全
	public Document htmlDocumentParse(String source) {
		DOMReader dr = new DOMReader();
		// 使用nekohtml补全html标签
		HTMLConfiguration htmlConfig = new HTMLConfiguration();
		htmlConfig.setFeature(
				"http://cyberneko.org/html/features/balance-tags", true);
		// 标签大写
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/names/elems", "upper");
		// 属性小写
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/names/attrs", "lower");
		// 设置字符编码
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/default-encoding",
				charset);
		// 是否允许命名空间
		htmlConfig.setFeature("http://xml.org/sax/features/namespaces", false);
		// 是否剥掉<script>元素中的<!-- -->等注释符
		htmlConfig
				.setFeature(
						"http://cyberneko.org/html/features/scanner/script/strip-comment-delims",
						false);
		// 是否将与HTML事件有关的infoset项包括在解析管道中。
		htmlConfig.setFeature(
				"http://cyberneko.org/html/features/augmentations", true);
		// 当遇到字符实体引用（如＆#x20;）是否将(#x20)报告给相应地文档处理器
		htmlConfig
				.setFeature(
						"http://apache.org/xml/features/scanner/notify-char-refs",
						true);
		// 当遇到XML内建的字符实体引用（如＆amp;）是否将(amp)报告给相应地文档处理器。
		htmlConfig.setFeature(
				"http://apache.org/xml/features/scanner/notify-builtin-refs",
				true);
		// 当遇到HTML内建的字符实体引用（如＆copy;）是否将(copy)报告给相应地文档处理器。
		htmlConfig
				.setFeature(
						"http://cyberneko.org/html/features/scanner/notify-builtin-refs",
						true);
		DOMParser parser = new DOMParser(htmlConfig);
		// cyberneko解析：将httpclient读取的数据source解析成w3c的Document对象
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
		System.out.println(dom4jDoc.asXML());
		return dom4jDoc;
	}
	
	private void block(Document doc){
		
	}

	/**
	 * 将element对象转为String
	 * @param element
	 * @param charset
	 * @param istrans
	 * @return
	 */
	private String formatXml(Element element, String charset, boolean istrans) {
		org.dom4j.io.OutputFormat format = org.dom4j.io.OutputFormat
				.createCompactFormat();// 以紧凑格式输出
		format.setEncoding(charset);
		format.setSuppressDeclaration(false); // 去除<?xml version="1.0" encoding="UTF-8"?>
		format.setXHTML(true); // 设置为XHTML格式
		format.setExpandEmptyElements(true); // <tagName/> to
		// <tagName></tagName>.
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans); // 反转义
		try {
			xw.write(element);
			xw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				xw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sw.toString();

	}
}
