package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.catchcontent.catchcontent.util.StringUtil;
import com.c_platform.catchcontent.catchcontent.webparse.common.IframeAdapter;

public class AndroidIframeAdapter implements IframeAdapter {

	public int doProcessIframe(Element eleChild, Element parentElement,
			List<Node> childrenList, int i) {
		if (IFRAME.equalsIgnoreCase(eleChild.getName())) {
			String widthString = "";
			if ((widthString = eleChild.attributeValue("width")) != null) {
				widthString = widthString.replaceAll(REGEX_UNIT, "");
				if (StringUtil.isNumeric(widthString)
						&& Double.valueOf(widthString) > 800) {
					String heightString = "";
					if ((heightString = eleChild.attributeValue("height")) != null) {
						heightString = heightString.replaceAll(REGEX_UNIT, "");
						if (StringUtil.isNumeric(heightString)
								&& Double.valueOf(heightString) < 140) {
							childrenList.remove(i--);
							parentElement.setContent(childrenList);
						}
					}
				}
			}
		}
		return i;
	}

}
