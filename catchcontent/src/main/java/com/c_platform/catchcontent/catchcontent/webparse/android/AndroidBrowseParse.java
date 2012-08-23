/*
 * 文 件 名：AndroidBrowseParse.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：针对Android平台浏览器的过滤
 * 修 改 人：江伟
 * 修改时间：2010-6-7
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.webparse.common.ASpanAdapter;
import com.c_platform.catchcontent.catchcontent.webparse.common.AddDisplayInlineAttr;
import com.c_platform.catchcontent.catchcontent.webparse.common.AddFloatAttr;
import com.c_platform.catchcontent.catchcontent.webparse.common.ParseHtml;
import com.c_platform.catchcontent.catchcontent.webparse.common.ParseHtmlUtil;

public class AndroidBrowseParse extends ParseHtml {
	private final ASpanAdapter androidASpanAdapter = new AndroidASpanAdapter();
	private final AddFloatAttr androidAddFloatAttr = new AndroidAddFloatAttr();
	private final AddDisplayInlineAttr androidAddDisplayInlineAttr = new AndroidAddDisplayInlineAttr();

	/**
	 * 
	 * TODO 标签过滤执行器
	 * 
	 * @param element
	 *            当前节点
	 * @throws Exception
	 *             异常
	 */
	@SuppressWarnings("unchecked")
	public void modifyElement(Element element) throws Exception {
		List<Node> childrenList = element.content(); // 获取当前节点子内容
		int tmpIndex;
		for (int i = 0; i < childrenList.size(); i++) {
			Node child = childrenList.get(i); // element的孩子节点

			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element eleChild = (Element) child; // element的孩子节点
				String tagName = eleChild.getName().toUpperCase();
				if (this.acceptElementTag != null
						&& this.acceptElementTag.containsKey(tagName)) {
					try {
						tmpIndex = doAcceptProcess(tagName, eleChild, i,
								childrenList, element);
						if (tmpIndex != i) {
							i = tmpIndex;
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (this.removeElementTag != null
						&& this.removeElementTag.contains(tagName)) {
					i = doRemoveProcess(i, childrenList, element);
					continue;
				}

				modifyElement((Element) child);
				// table 转换 真正的性能瓶颈
				doChangeTable(eleChild, element, childrenList, i);
			}
		}
	}

	public int doAcceptProcess(String tagName, Element eleChild, int i,
			List<Node> childrenList, Element element) throws Exception {
		HashSet<String> hashSet = this.acceptElementTag.get(tagName);
		int tmpIndex;
		// 删除冗余的BR
		tmpIndex = doDeleteBR(eleChild, element, childrenList, i);
		if (tmpIndex != i) {
			return tmpIndex;
		}
		// 清除BR标签子内容
		if (BR.equalsIgnoreCase(tagName) || IMG.equalsIgnoreCase(tagName)
				|| INPUT.equalsIgnoreCase(tagName)) {
			eleChild.clearContent(); // 清除BR标签子内容
		}
		boolean needAddBlockAttr = ParseHtmlUtil.needToChangeImagElement(
				eleChild, REGEX_UNIT);

		// 标签折叠处理
		/* 该标签为接受的标签时的逻辑处理 */
		String navigatorValue = eleChild.attribute(NAVIGATOR_COLUM) == null ? null
				: eleChild.attribute(NAVIGATOR_COLUM).getValue();
		if (navigatorValue != null && "true".equalsIgnoreCase(navigatorValue)) {
			attrAdapter.doFilterAttribute(eleChild, hashSet, enginId);
			tmpIndex = doFoldElement(eleChild, i, hashSet); // 生成折叠标签
			eleChild.addAttribute(TAG_TYPE, TAG_TYPE_VALUE);
		} else {
			// 将较小的UL的eleChild替换为单行显示
			tmpIndex = ulStyleAdapt.doChangeUlElement(eleChild, element, i,
					hashSet, enginId);
		}
		if (tmpIndex != i) {
			return tmpIndex;
		}

		// 修复不合法的table标签属性
		doRepairTableAttribute(eleChild);

		// 过滤属性
		attrAdapter.doFilterAttribute(eleChild, hashSet, enginId);

		// 添加display:block属性
		androidImgAdapter.doAddBlockAttr(eleChild, element, needAddBlockAttr,
				this.sreenWidth, this.sreenHeight);

		// 处理对于mini版天天浏览器请求的网页处理
		doFilterForUniversal(eleChild, tagName);

		// 对需要做缩放的元素进行宽高缩放处理
		// doScaleWD(eleChild,tagName);

		androidAddFloatAttr.doAddFloatLeftAttr(eleChild, element);

		// 改变A标签显示样式
		tmpIndex = androidASpanAdapter.doChangeASPANElement(eleChild, element,
				childrenList, i);
		// 改变连续显示H1-6标签显示样式
		androidAddDisplayInlineAttr.doAddDisplayInlineAttr(eleChild, element,
				childrenList, i);

		// 改变连续式H1标签时显示样式
		doChangeH3Element(eleChild, element, childrenList, i);
		if (tmpIndex != i) {
			return tmpIndex;
		}
		if (TABLE.equalsIgnoreCase(eleChild.getName())) {
			eleChild.addAttribute("width", "100%");
		}
		return i;
	}

	/**
	 * 
	 * TODO 处理对于mini版天天浏览器请求的网页处理
	 * 
	 * @param eleChild
	 * @param tagName
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	private void doFilterForUniversal(Element eleChild, String tagName) throws UnsupportedEncodingException,
			URISyntaxException {
		// 补全META中跳转url
//		if (enginId.indexOf("adrmini") != -1 || enginId.indexOf("tianyi") != -1) {
//			List<Element> metaList = eleChild.elements("META");
//			for (Element meta : metaList) {
//				if (meta.attribute("http-equiv") != null
//						&& meta.attributeValue("http-equiv").equalsIgnoreCase(
//								"refresh")) {
//					String oldurl;
//					String content;
//					String newurl = "";
//					if ((content = meta.attributeValue("content")) != null
//							&& !meta.attributeValue("content").equals("")) {
//						content = content.toLowerCase();
//						oldurl = content.substring(content.indexOf("url=") + 4);
//						newurl = RepairUrl.fillWithGetUrl(ctx, oldurl);
//						content = content.replace(oldurl, newurl);
//						meta.attribute("content").setValue(content);
//					}
//				}
//			}
//		}

		// 将所有图片标签 的src属性修改为天天浏览器代理服务器get请求url
//		if ((enginId.indexOf("adrmini") >= 0 || enginId.indexOf("tianyi") >= 0 || enginId
//				.indexOf("iph") >= 0)) {
//			if (eleChild.attributeValue("src") != null
//					&& "".equals(eleChild.attributeValue("src").trim())) {
//				eleChild.detach();
//			}
//			if (("img".equalsIgnoreCase(tagName) || "frame"
//					.equalsIgnoreCase(tagName))
//					&& eleChild.attribute("src") != null
//					&& !"".equals(eleChild.attributeValue("src").trim())) {
//				String src = eleChild.attributeValue("src");
//				// eleChild.attribute("src").detach();
//				// eleChild.addAttribute("lazy_src",
//				// RepairUrl.fillWithGetUrl(ctx,
//				// src)
//				// + "&isImg=1");
//				eleChild.addAttribute("src", src);
//			} else if ("iframe".equalsIgnoreCase(tagName)
//					&& eleChild.attribute("src") != null
//					&& !"".equals(eleChild.attributeValue("src").trim())) {
//				String src = eleChild.attributeValue("src");
//				eleChild.attribute("src").detach();
//				eleChild.addAttribute("src", src);
//			}
//		}
	}

}