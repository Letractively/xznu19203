package com.c_platform.parsebody.parsebody;

import org.dom4j.Element;

public class WebContext {
	private String url;
	private String charset;
	private String[] screen;
	private Object content;
	private boolean isRss;
	private boolean isIframe;
	private Element htmlHead; 
	private Element elementHTitle;
	private String contentBody;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String[] getScreen() {
		return screen;
	}
	public void setScreen(String[] screen) {
		this.screen = screen;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	public boolean isRss() {
		return isRss;
	}
	public void setRss(boolean isRss) {
		this.isRss = isRss;
	}
	public boolean isIframe() {
		return isIframe;
	}
	public void setIframe(boolean isIframe) {
		this.isIframe = isIframe;
	}
	public Element getHtmlHead() {
		return htmlHead;
	}
	public void setHtmlHead(Element htmlHead) {
		this.htmlHead = htmlHead;
	}
	public Element getElementHTitle() {
		return elementHTitle;
	}
	public void setElementHTitle(Element elementHTitle) {
		this.elementHTitle = elementHTitle;
	}
	public String getContentBody() {
		return contentBody;
	}
	public void setContentBody(String contentBody) {
		this.contentBody = contentBody;
	}
	
	
}
