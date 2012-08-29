package com.c_platform.parsebody.parsebody.vips;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import com.c_platform.parsebody.parsebody.util.CommonPath;

public class XslConverterUtils {

	// 使用XSLT模板对Document进行转化
	public static Document getStyleDocument(Document document, String xsltfile)
			throws Exception {
		InputStream in = null;
		if (CommonPath.path == null || CommonPath.path.equals("")) {
			in = ClassLoader.getSystemResourceAsStream(xsltfile);
		} else {
			String[] ss = xsltfile.split("/");
			String filename = ss[ss.length - 1];
			File file = new File(CommonPath.path + filename);
			in = new FileInputStream(file);
		}
		DocumentSource docSource = new DocumentSource(document);
		DocumentResult docResult = new DocumentResult();
		TransformerFactory factory = TransformerFactory.newInstance();
		Templates cachedXSLT = factory.newTemplates(new StreamSource(in));
		Transformer transformer = cachedXSLT.newTransformer();
		if (transformer != null) {
			transformer.transform(docSource, docResult);
		}
		Document resultDoc = docResult.getDocument();
		return resultDoc;
	}

	// 根据指定的元素创建一个Document
	@SuppressWarnings("unchecked")
	private static Document createDocument(Element element) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(element.getName());
		List<Attribute> attrs = element.attributes();// 添加element元素的属性到根元素结点
		if (attrs != null) {
			for (Attribute attr : attrs) {
				root.addAttribute(attr.getName(), attr.getValue());
			}
		}
		root.appendContent(element);
		return document;
	}

	// 对元素节点使用xslt模板进行转化
	public static Element useXslConverter(Element element, String xsltfile)
			throws Exception {
		Document tempDoc = createDocument(element);
		Document resultDoc = getStyleDocument(tempDoc, xsltfile);
		Element divElement = resultDoc.getRootElement().element("body")
				.element("DIV");
		return divElement;

	}
}
