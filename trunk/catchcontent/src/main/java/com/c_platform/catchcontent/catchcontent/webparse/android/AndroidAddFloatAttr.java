package com.c_platform.catchcontent.catchcontent.webparse.android;

import java.util.Iterator;

import org.dom4j.Element;

import com.c_platform.catchcontent.catchcontent.webparse.common.AddFloatAttr;

public class AndroidAddFloatAttr implements AddFloatAttr {
	public AndroidAddFloatAttr() {
	}

	@SuppressWarnings("unchecked")
	public void doAddFloatLeftAttr(Element eleChild, Element parentElement) {
		String name = eleChild.getName();
		if (IMG.equalsIgnoreCase(name)) {
			if (A.equalsIgnoreCase(parentElement.getName())) {
				Element grandElement = parentElement.getParent();
				if (grandElement.elements().size() > 1) {
					Iterator<Element> it = grandElement.elementIterator();
					String xpathString = eleChild.getPath();
					boolean tg = true;
					Element itElement;
					while (it.hasNext()) {
						itElement = it.next();
						if (itElement.element("IMG") != null) {
							if (!xpathString.equalsIgnoreCase(itElement
									.element("IMG").getPath())) {
								tg = false;
								break;
							}
						} else {
							tg = false;
							break;
						}
					}
					if (tg) {
						String style = parentElement.attributeValue(STYLE) == null ? "display:inline;"
								: "display:inline;"
										+ parentElement.attributeValue(STYLE);
						parentElement.addAttribute(STYLE, style);
						// if(!parentElement.getUniquePath().equalsIgnoreCase(getLastElement(grandElement).getUniquePath()))
						// {
						// String style =
						// parentElement.attributeValue(STYLE)==null?"float:left;":"float:left;"+parentElement.attributeValue(STYLE);
						// parentElement.addAttribute(STYLE, style);
						// }
					}
				}
			}
		}
	}
}