package com.c_platform.parsebody.parsebody.filter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.c_platform.parsebody.parsebody.WebContext;

public class HtmlElementFilter {

	public static WebContext tagFilter(WebContext ctx) throws Exception {
		ArrayList<Document> domList = (ArrayList<Document>) ctx.getContent();
		ArrayList<String> resultList = new ArrayList<String>();
		if (domList != null && domList.size() > 0) {
			Document contextDoc;
			for (int i = 0, j = domList.size(); i < j; i++) {
				contextDoc = domList.get(i);

				resultList.add(formateXml(contextDoc.getRootElement(),
						ctx.getCharset(), false));
			}
			ctx.setContent(resultList);
		}
		return ctx;
	}

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
