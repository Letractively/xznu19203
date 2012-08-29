package com.c_platform.parsebody.parsebody.filter;

import java.io.IOException;
import java.io.StringReader;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;

import com.c_platform.parsebody.parsebody.WebContext;

public class HtmlDocumentFilter {

	public static WebContext htmlDocumentParse(WebContext ctx) {
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
				new StringReader((String) ctx.getContent()), null);
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
		ctx.setContent(dom4jDoc);
		return ctx;
	}
}
