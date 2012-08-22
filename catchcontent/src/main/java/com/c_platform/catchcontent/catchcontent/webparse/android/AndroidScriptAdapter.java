package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.util.StringUtil;
import com.c_platform.catchcontent.catchcontent.webparse.common.ScriptAdapter;

public class AndroidScriptAdapter implements ScriptAdapter {
	public AndroidScriptAdapter() {
	}

	public int doProcessScript(Element eleChild, Element parentElement,
			List<Node> childrenList, int i, String url) {
		String tagName = eleChild.getName();
		if (SCRIPT.equalsIgnoreCase(tagName)) {
			String sPath = eleChild.getPath();
			int bodyIndex = sPath.lastIndexOf("BODY");
			if (!StringUtil.matchRegax(url, "http://www.tianya.cn[/]?$")
					&& !StringUtil.matchRegax(url, "baidu.com")
					&& bodyIndex + 4 < sPath.length() && bodyIndex >= 0
					&& eleChild.hasContent()) // ||
												// "".equals(eleChild.getTextTrim()
			{
				String jsString = eleChild.getText();
				if (jsString.indexOf(".write") >= 0
						|| jsString.indexOf(".innerHTML=") >= 0
						|| jsString.indexOf("广告") >= 0) {
					if ("BODY".equalsIgnoreCase(parentElement.getName())
							|| jsString.indexOf("input ") >= 0
							|| jsString.indexOf("select ") >= 0
							|| jsString.indexOf("ul ") >= 0) {
						// 不处理

					} else {
						childrenList.remove(i--);
						parentElement.setContent(childrenList);
					}
				}
			}
		}
		return i;
	}

}