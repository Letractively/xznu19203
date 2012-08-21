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
	 * 1.��ץȡ��ҳ 2.Html��ȫ��ʽ�� 3.Html��ǩ���� 4.ȥ��js,css 5.�ֿ� 6.ȡ���� 7.����xhtml
	 */

	// ��httpclientץȡ��ҳ����
	public String catchHtmlWithHttpClient() {
		String resource = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("==============ץȡ�ɹ�=============");
				resource = EntityUtils.toString(response.getEntity(), charset);
				System.out.println(resource);
			} else {
				System.out.println("==============ץȡʧ��=============");
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

	// ��ǩ��ȫ
	public Document htmlDocumentParse(String source) {
		DOMReader dr = new DOMReader();
		// ʹ��nekohtml��ȫhtml��ǩ
		HTMLConfiguration htmlConfig = new HTMLConfiguration();
		htmlConfig.setFeature(
				"http://cyberneko.org/html/features/balance-tags", true);
		// ��ǩ��д
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/names/elems", "upper");
		// ����Сд
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/names/attrs", "lower");
		// �����ַ�����
		htmlConfig.setProperty(
				"http://cyberneko.org/html/properties/default-encoding",
				charset);
		// �Ƿ����������ռ�
		htmlConfig.setFeature("http://xml.org/sax/features/namespaces", false);
		// �Ƿ����<script>Ԫ���е�<!-- -->��ע�ͷ�
		htmlConfig
				.setFeature(
						"http://cyberneko.org/html/features/scanner/script/strip-comment-delims",
						false);
		// �Ƿ���HTML�¼��йص�infoset������ڽ����ܵ��С�
		htmlConfig.setFeature(
				"http://cyberneko.org/html/features/augmentations", true);
		// �������ַ�ʵ�����ã��磦#x20;���Ƿ�(#x20)�������Ӧ���ĵ�������
		htmlConfig
				.setFeature(
						"http://apache.org/xml/features/scanner/notify-char-refs",
						true);
		// ������XML�ڽ����ַ�ʵ�����ã��磦amp;���Ƿ�(amp)�������Ӧ���ĵ���������
		htmlConfig.setFeature(
				"http://apache.org/xml/features/scanner/notify-builtin-refs",
				true);
		// ������HTML�ڽ����ַ�ʵ�����ã��磦copy;���Ƿ�(copy)�������Ӧ���ĵ���������
		htmlConfig
				.setFeature(
						"http://cyberneko.org/html/features/scanner/notify-builtin-refs",
						true);
		DOMParser parser = new DOMParser(htmlConfig);
		// cyberneko��������httpclient��ȡ������source������w3c��Document����
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
	 * ��element����תΪString
	 * @param element
	 * @param charset
	 * @param istrans
	 * @return
	 */
	private String formatXml(Element element, String charset, boolean istrans) {
		org.dom4j.io.OutputFormat format = org.dom4j.io.OutputFormat
				.createCompactFormat();// �Խ��ո�ʽ���
		format.setEncoding(charset);
		format.setSuppressDeclaration(false); // ȥ��<?xml version="1.0" encoding="UTF-8"?>
		format.setXHTML(true); // ����ΪXHTML��ʽ
		format.setExpandEmptyElements(true); // <tagName/> to
		// <tagName></tagName>.
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans); // ��ת��
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
