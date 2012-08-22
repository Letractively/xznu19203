package com.c_platform.catchcontent.catchcontent.webparse.android;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.c_platform.catchcontent.catchcontent.vips.NodeUtils;
import com.c_platform.catchcontent.catchcontent.webparse.common.ImgAdapter;

public final class AndroidImgAdapter implements ImgAdapter {
	/**
	 * 
	 * TODO 为img添加display:block属性
	 * 
	 * @param eleChild
	 *            当前节点
	 * @param parentElement
	 *            当前节点父节点
	 * @param needAddBlockAttr
	 *            是否需要添加display:block标志
	 */
	public void doAddBlockAttr(Element eleChild, Element parentElement,
			boolean needAddBlockAttr, double sreenWidth, double sreenHeight) {
		if (IMG.equalsIgnoreCase(eleChild.getName())) {
			// 解决部分real_src才是真实地址情况
			String real_srcString = eleChild.attributeValue("real_src");
			if (real_srcString != null && !"".equals(real_srcString.trim())) {
				eleChild.addAttribute("src", real_srcString);
			} else if ((real_srcString = eleChild.attributeValue("lsrc")) != null
					&& !"".equals(real_srcString.trim())) {
				eleChild.addAttribute("src", real_srcString);
			} else if ((real_srcString = eleChild.attributeValue("file")) != null
					&& !"".equals(real_srcString.trim())) {
				eleChild.addAttribute("src", real_srcString);
			}
			// 新浪微博中的dynamic-src地址是假的，去除掉
			if (eleChild.asXML().indexOf("dynamic-src") != -1
					&& eleChild.asXML().indexOf("sinaimg.cn") != -1) {
				changeToImgSrc(eleChild, new String[] { "data-ks-lazyload",
						"lazyload-src", "original", "src2" });
			} else {
				changeToImgSrc(eleChild, new String[] { "data-ks-lazyload",
						"dynamic-src", "lazyload-src", "original", "src2" });
			}

			if (needAddBlockAttr) {
				if (NodeUtils.getVirtualTextNodeNumber(parentElement) >= 1) {
					// <IMG height="94" width="124"/><SPAN
					// class="title">林家小妹《遇鬼篇》第三十一集：</SPAN>
					String style = eleChild.attributeValue(STYLE) == null ? "display:block; margin:0 auto;"
							: "display:block; margin:0 auto;" + eleChild.attributeValue(STYLE);
					eleChild.addAttribute(STYLE, style);
					style = parentElement.attributeValue(STYLE) == null ? "text-align:center; width:100%"
                            : "text-align:center; width:100%" + parentElement.attributeValue(STYLE);
					parentElement.addAttribute(STYLE, style);
				} else if (NodeUtils.getVirtualTextNodeNumber(parentElement) < 1
						&& "".equals(parentElement.getTextTrim())) {
					String style = parentElement.attributeValue(STYLE) == null ? "display:block;"
							: "display:block;" + parentElement.attributeValue(STYLE);
					parentElement.addAttribute(STYLE, style);

				} else if (NodeUtils.getVirtualTextNodeNumber(parentElement) < 1
						&& !"".equals(parentElement.getTextTrim())) {
					if (eleChild.getPath().indexOf("UL/LI/") >= 0) {
						String style = eleChild.attributeValue(STYLE) == null ? "display:block;"
								: "display:block;"
										+ eleChild.attributeValue(STYLE);
						eleChild.addAttribute(STYLE, style);
						parentElement.addAttribute(STYLE, style);
					} else {
					    String style = eleChild.attributeValue(STYLE) == null ? "display:block; margin:0 auto;"
	                            : "display:block; margin:0 auto;" + eleChild.attributeValue(STYLE);
	                    eleChild.addAttribute(STYLE, style);
	                    style = parentElement.attributeValue(STYLE) == null ? "text-align:center; width:100%"
	                            : "text-align:center; width:100%" + parentElement.attributeValue(STYLE);
	                    parentElement.addAttribute(STYLE, style);
					}
				} else if (eleChild.getPath().indexOf("UL/LI/") < 0) {
					// 添加display:block属性
					String style = eleChild.attributeValue(STYLE) == null ? "display:block;"
							: "display:block;" + eleChild.attributeValue(STYLE);
					eleChild.addAttribute(STYLE, style);
					style = parentElement.attributeValue(STYLE) == null ? "text-align:center; width:100%"
                            : "text-align:center; width:100%" + parentElement.attributeValue(STYLE);
					parentElement.addAttribute(STYLE, style);
				}
			} else {
				// 对于<a href="..." target="_blank"><img
				// src="1.jpg">胡锦涛深情演唱俄国民歌</a>情况的处理
				if (parentElement.elements().size() == 1
						&& !"".equals(parentElement.getTextTrim())) {
					// <IMG height="94" width="124"/><SPAN
					// class="title">林家小妹《遇鬼篇》第三十一集：</SPAN>
					String style = eleChild.attributeValue(STYLE) == null ? "display:block; margin:0 auto;"
							: "display:block; margin:0 auto;" + eleChild.attributeValue(STYLE);
					eleChild.addAttribute(STYLE, style);
					style = parentElement.attributeValue(STYLE) == null ? "text-align:center; width:100%"
                            : "text-align:center; width:100%" + parentElement.attributeValue(STYLE);
                    parentElement.addAttribute(STYLE, style);
				}
			}
		}
	}

	private void changeToImgSrc(Element element, String[] attrs) {
		Attribute dataks = null;
		for (String attr : attrs) {
			dataks = element.attribute(attr);
			if (dataks != null) {
				break;
			}
		}
		if (dataks != null) {
			element.addAttribute("src", dataks.getValue());
			element.remove(dataks.detach());
		}
	}
}
