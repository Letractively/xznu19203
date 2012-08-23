/*
 * 文 件 名：ResourceUtil.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：HTML过滤配置文件读取
 * 修 改 人：江伟
 * 修改时间：2010-3-30
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.c_platform.catchcontent.catchcontent.webparse.common.ElementConfigEntity;


/**
 * TODO HTML过滤配置文件读取
 * 
 * @author 江伟
 * @version C02 2010-3-30
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class ResourceUtil {
	private static String ELEMENT_TAGS_CONFIG_PATH = "com/c_platform/cc/catchcontent/xml/elementTagsConfig.xml";

//	private static String COMMON_JS_PATH = "com/huawei/nwa/networkagent/common/commonJavaScript.js";
//
//	private static String READ_JS_PATH = "com/huawei/nwa/networkagent/common/readModelJavaScript.js";
//	
//	private static String ADR_JS_PATH = "com/huawei/nwa/networkagent/common/commonAdverAndWeiBo.js";

	private static String[] TAGS_CONFIG_NODE_NAME = new String[] {
			"acceptElementsTag", "removeElementsTag", "repalceElementsTag",
			"commonAttribute" };

	private static ConcurrentHashMap<String, Object> elementConfigMap = new ConcurrentHashMap<String, Object>();

	private final static String LEAF_ITEM = "item";

	private final static String ENGINE_VERSION = "version";

	private final static String ENGINE_NODE = "engine";

	private final static String TO_TAG = "toTag";

	private final static String REG_EXP = "regExp";

	private final static String DEFAULT = "default";

	public final static String DOMAIN = "networkbackstage/resource";

	public final static String COMMONCSS = "networkbackstage/resource/commonStyle.css";

	public final static String DATA_IMAGE = "data:image/";

	public final static String HuaWeiLocation = "?huaweilocation=";

	public static String defineVersion;

	public static ConcurrentHashMap<String, Object> getElementConfigMap() {
		return elementConfigMap;
	}

	/**
	 * desc
	 * 
	 * @throws IOException
	 */
	public static boolean doReadElementTagsConfigFile() {
		boolean result = true;
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder;
		NodeList engineNodList = null;
		InputStream inputStream = null;
		try {
			dombuilder = domfac.newDocumentBuilder();
			if (CommonPath.path == null || CommonPath.path.equals("")) {
				inputStream = ClassLoader
						.getSystemResourceAsStream(ELEMENT_TAGS_CONFIG_PATH);
			} else {
				File file = new File(CommonPath.path + "elementTagsConfig.xml");
				inputStream = new FileInputStream(file);
			}
			Document doc = dombuilder.parse(inputStream);
			engineNodList = doc.getElementsByTagName(ENGINE_NODE);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (engineNodList != null && engineNodList.getLength() > 0) {
			Element engine = null;
			String vsersionId = "";
			ElementConfigEntity elementConfigEntity = new ElementConfigEntity();
			Element element = null;
			for (int i = 0; i < engineNodList.getLength(); i++) {
				engine = (Element) engineNodList.item(i);
				vsersionId = engine.getAttribute(ENGINE_VERSION).toLowerCase();
				if (engine.getAttribute(DEFAULT) != null
						&& "true"
								.equalsIgnoreCase(engine.getAttribute(DEFAULT))) {
					defineVersion = vsersionId;
				}
				element = (Element) engine.getElementsByTagName(
						TAGS_CONFIG_NODE_NAME[0]).item(0); // 读取acceptElementsTag
				NodeList nodeList = element.getElementsByTagName(LEAF_ITEM);
				elementConfigEntity
						.setAcceptElementTag(getAcceptHashMap(nodeList));
				element = (Element) engine.getElementsByTagName(
						TAGS_CONFIG_NODE_NAME[1]).item(0); // 读取removeElementsTag
				nodeList = element.getElementsByTagName(LEAF_ITEM);
				elementConfigEntity.setRemoveElementTag(getNodeValue(nodeList));

				element = (Element) engine.getElementsByTagName(
						TAGS_CONFIG_NODE_NAME[2]).item(0); // 读取repalceElementsTag
				nodeList = element.getElementsByTagName(LEAF_ITEM);
				elementConfigEntity
						.setReplaceElementTag(getReplaceElementTags(nodeList));
				elementConfigMap.put(vsersionId, elementConfigEntity);
			}
		}

		return result;
	}

//	/**
//	 * desc 读取公共JS文件
//	 * 
//	 * @return
//	 * @throws IOException
//	 */
//	public static String getCommonJS() throws IOException {
//		InputStream in = null;
//		if (CommonPath.path == null || CommonPath.path.equals("")) {
//			in = ClassLoader.getSystemResourceAsStream(COMMON_JS_PATH);
//		} else {
//			File file = new File(CommonPath.path + "commonJavaScript.js");
//			in = new FileInputStream(file);
//		}
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		String result = null;
//		StringBuffer sb = new StringBuffer();
//		while ((result = br.readLine()) != null) {
//			sb.append(result);
//		}
//		in.close();
//		br.close();
//		return sb.toString();
//	}

	
//	/**
//	 * desc 读取公共广告js文件
//	 * 
//	 * @return
//	 * @throws IOException
//	 */
//	public static String getCommonAdrAndWeiBoJS() throws IOException {
//		InputStream in = null;
//		if (CommonPath.path == null || CommonPath.path.equals("")) {
//			in = ClassLoader.getSystemResourceAsStream(ADR_JS_PATH);
//		} else {
//			File file = new File(CommonPath.path + "commonAdver.js");
//			in = new FileInputStream(file);
//		}
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		String result = null;
//		StringBuffer sb = new StringBuffer();
//		while ((result = br.readLine()) != null) {
//			sb.append(result);
//		}
//		in.close();
//		br.close();
//		return sb.toString();
//	}

//	/**
//	 * 读取阅读模式的JS文件
//	 * 
//	 * @return
//	 * @throws IOException
//	 */
//	public static String getReadCommonJS() throws IOException {
//		InputStream in = null;
//		if (CommonPath.path == null || CommonPath.path.equals("")) {
//			in = ClassLoader.getSystemResourceAsStream(READ_JS_PATH);
//		} else {
//			File file = new File(CommonPath.path + "readModelJavaScript.js");
//
//			in = new FileInputStream(file);
//		}
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		String result = null;
//		StringBuffer sb = new StringBuffer();
//		while ((result = br.readLine()) != null) {
//			sb.append(result);
//		}
//		in.close();
//		br.close();
//		return sb.toString();
//	}

	private static ConcurrentHashMap<String, HashSet<String>> getAcceptHashMap(
			NodeList nodeList) {
		int listLen = nodeList.getLength();
		ConcurrentHashMap<String, HashSet<String>> hashMap = new ConcurrentHashMap<String, HashSet<String>>();
		Element element = null;

		for (int i = 0; i < listLen; i++) {
			element = (Element) nodeList.item(i);
			NodeList attrList = element.getElementsByTagName("attribute");
			HashSet<String> hashSet = new HashSet<String>();
			for (int j = 0, len = attrList.getLength(); j < len; j++) {
				hashSet.add(attrList.item(j).getTextContent().toLowerCase());
			}
			hashMap.put(element.getAttribute("name").toUpperCase(), hashSet);
		}
		return hashMap;
	}

	private static HashSet<String> getNodeValue(NodeList nodeList) {
		int listLen = nodeList.getLength();
		HashSet<String> hashSet = new HashSet<String>();
		for (int i = 0; i < listLen; i++) {
			hashSet.add(nodeList.item(i).getTextContent().toUpperCase());
		}
		return hashSet;
	}

	private static ConcurrentHashMap<String, String> getReplaceElementTags(
			NodeList nodeList) {
		Element element = null;
		int listLen = nodeList.getLength();
		ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<String, String>();

		for (int i = 0; i < listLen; i++) {
			element = (Element) nodeList.item(i);
			hashMap.put(element.getTextContent().toUpperCase(), element
					.getAttribute(TO_TAG).toUpperCase());
		}
		return hashMap;
	}

	private static ConcurrentHashMap<String, String> getTagAttributes(
			NodeList nodeList) {
		Element element = null;
		int listLen = nodeList.getLength();
		ConcurrentHashMap<String, String> hMap = new ConcurrentHashMap<String, String>();
		for (int i = 0; i < listLen; i++) {
			element = (Element) nodeList.item(i);
			hMap.put(element.getTextContent().toLowerCase(), element
					.getAttribute(REG_EXP));
		}
		return hMap;
	}
}
