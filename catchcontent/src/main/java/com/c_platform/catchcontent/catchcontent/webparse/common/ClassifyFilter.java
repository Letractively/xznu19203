/*
 * 文 件 名：ClassifyFilter.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：收集javascript和CSS
 * 修 改 人：江伟
 * 修改时间：2010-3-30
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.webparse.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

import com.c_platform.catchcontent.catchcontent.util.UrlUtil;

/**
 * TODO 收集javascript和CSS
 * 
 * @author 江伟
 * @version C02 2010-3-30
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class ClassifyFilter extends DefaultFilter {
	private static final String DEPTH_NAME = "depth";

	private static final String STYLE = "style";

	private StringBuffer styleBuffer = null;

	protected int fElementDepth;

	private int delMark = 0;

	private int notCollectCss = 0;

	private ArrayList<StringBuffer> scriptList = new ArrayList<StringBuffer>();

	private ArrayList<StringBuffer> styleList = new ArrayList<StringBuffer>();

	private URL baseUrl; // url

	public URL getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(URL url) {
		this.baseUrl = url;
	}

	public ArrayList<StringBuffer> getScriptList() {
		return scriptList;
	}

	public ArrayList<StringBuffer> getStyleList() {
		return styleList;
	}

	/**
	 * Start document.
	 */
	public void startDocument(XMLLocator locator, String encoding,
			NamespaceContext nscontext, Augmentations augs) throws XNIException {
		fElementDepth = 0;
		super.startDocument(locator, encoding, nscontext, augs);
	}

	public void startElement(QName element, XMLAttributes attrs,
			Augmentations augs) throws XNIException {
		if (this.baseUrl != null) {
			try {
				int attrCount = attrs != null ? attrs.getLength() : 0;
				for (int i = 0; i < attrCount; i++) {
					String aname = attrs.getQName(i);
					/**
					 * update by jiangjun 2011-5-24 补全file的相对地址
					 */
					if (aname
							.matches("href|src|action|lsrc|real_src|dynamic-src|file")) {
						String avalue = attrs.getValue(i);
						if (null != avalue & !"".equals(avalue)
								& avalue.indexOf("javascript:") < 0) {
							attrs
									.setValue(i, UrlUtil.guessURL(baseUrl,
											avalue));
						}
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String depth = String.valueOf(fElementDepth);
		attrs.addAttribute(new QName(element.localpart, DEPTH_NAME, DEPTH_NAME,
				element.uri), "String", depth);
		if (baseUrl!=null && baseUrl.toString().indexOf("app.wumii.com") == -1
				&& STYLE.equalsIgnoreCase(element.rawname)) {
			if (attrs.getValue("media") != null
					&& !"screen".equalsIgnoreCase(attrs.getValue("media"))) {
				notCollectCss = 1;
			}
			styleBuffer = new StringBuffer();
		} else {
			super.startElement(element, attrs, augs);
			fElementDepth++;
		}

	}

	protected StringBuffer printAttributeValue(String text) {
		StringBuffer sb = new StringBuffer();
		int length = text.length();
		for (int j = 0; j < length; j++) {
			char c = text.charAt(j);
			if (c == '"') {
				sb.append("&quot;");
			} else {
				sb.append(c);
			}
		}
		return sb;
	}

	public void characters(XMLString text, Augmentations augs)
			throws XNIException {

		if (styleBuffer != null && notCollectCss == 0) {
			styleBuffer.append(text.ch, text.offset, text.length);
			return;
		}
		if (notCollectCss != 1) {
			super.characters(text, augs);
		}
	}

	public void endElement(QName element, Augmentations augs)
			throws XNIException {
		try {
			if (STYLE.equalsIgnoreCase(element.rawname) && styleBuffer != null) {
				styleList.add(styleBuffer);
				return;
			}
			notCollectCss = 0;
			super.endElement(element, augs);
			fElementDepth--;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			styleBuffer = null;
		}
	}

	@Override
	public void emptyElement(QName element, XMLAttributes attributes,
			Augmentations augs) throws XNIException {
		if ("base".equalsIgnoreCase(element.rawname)) {
			String hrefValue = attributes.getValue("href");
			if (hrefValue != null) {
				try {
					this.baseUrl = new URL(hrefValue);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (this.baseUrl != null) {
			try {
				int attrCount = attributes != null ? attributes.getLength() : 0;
				for (int i = 0; i < attrCount; i++) {
					String aname = attributes.getQName(i);
					/**
					 * update by jiangjun 2011-5-24 补全file的相对地址
					 */
					if (aname
							.matches("href|src|action|lsrc|real_src|dynamic-src|file")) {
						String avalue = attributes.getValue(i);
						if (null != avalue & !"".equals(avalue)
								& avalue.indexOf("javascript:") < 0) {
							// 朱磊改，例如：http://www.qiushibaike.net/页面中图片相对地址为
							// ../../aaa.jpg 目前已经是根目录了，上一级不存在
							if (avalue.indexOf(baseUrl.getHost()) == -1
									&& avalue.startsWith("..")) {
								avalue = avalue.substring(avalue
										.lastIndexOf("../") + 3);
							}

							attributes.setValue(i, UrlUtil.guessURL(baseUrl,
									avalue));
						}
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if ("link".equalsIgnoreCase(element.rawname)) {
			String typeValue = attributes.getValue("type");
			if (typeValue != null) {
				if (typeValue.indexOf("css") >= 0) {
					return;
				}
			} else {
				return;
			}
		}
		if (delMark == 1) {
			delMark = 0;
			return;
		}
		String depth = String.valueOf(fElementDepth);
		attributes.addAttribute(new QName(null, DEPTH_NAME, DEPTH_NAME, null),
				"String", depth);
		super.emptyElement(element, attributes, augs);
	}

	@Override
	public void comment(XMLString text, Augmentations augs) throws XNIException {
		// TODO Auto-generated method stub
		return;
	}
}
