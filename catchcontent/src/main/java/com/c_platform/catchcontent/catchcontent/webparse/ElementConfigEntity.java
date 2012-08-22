/*
 * 文 件 名：ElementTags.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：HTML过滤配置文件实现类
 * 修 改 人：江伟
 * 修改时间：2010-3-30
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.webparse;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO HTML过滤配置文件实现类
 * 
 * @author 江伟
 * @version C02 2010-3-30
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class ElementConfigEntity {
	private ConcurrentHashMap<String, HashSet<String>> acceptElementTag;

	private HashSet<String> removeElementTag;

	private ConcurrentHashMap<String, String> replaceElementTag;

	public ConcurrentHashMap<String, HashSet<String>> getAcceptElementTag() {
		return acceptElementTag;
	}

	public void setAcceptElementTag(
			ConcurrentHashMap<String, HashSet<String>> acceptElementTag) {
		this.acceptElementTag = acceptElementTag;
	}

	public HashSet<String> getRemoveElementTag() {
		return removeElementTag;
	}

	public void setRemoveElementTag(HashSet<String> removeElementTag) {
		this.removeElementTag = removeElementTag;
	}

	public ConcurrentHashMap<String, String> getReplaceElementTag() {
		return replaceElementTag;
	}

	public void setReplaceElementTag(
			ConcurrentHashMap<String, String> replaceElementTag) {
		this.replaceElementTag = replaceElementTag;
	}
}
