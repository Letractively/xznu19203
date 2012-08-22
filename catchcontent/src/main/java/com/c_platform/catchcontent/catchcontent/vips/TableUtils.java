package com.c_platform.catchcontent.catchcontent.vips;

import java.util.List;

import org.dom4j.Element;

public class TableUtils {

	private final static String[] TH_TD = { "TH", "TD" };

	// 用于获取Table标签中的Th或Td的数量
	@SuppressWarnings("unchecked")
	public static int getTdOrThInTableNumber(Element element) {
		int count = 0;
		List<Element> children = element.elements();
		if (children != null) {
			for (Element child : children) {
				if (CommonDivideUtils.hasTag(child.getName(), TH_TD)) {
					if (!isNullElement(child)) {
						if (!CommonDivideUtils.isNullContentElement(child)) {
							count++;
						}
					}
				} else {
					count += getTdOrThInTableNumber(child);
				}
			}
		}
		return count;
	}

	// 获取TABLE元素中TR含有TD数量的最大值
	@SuppressWarnings("unchecked")
	public static int getTDMaxNumber(Element element) {
		int count = 0;
		int temp = 0;
		Element tbody = element.element("TBODY");
		if (tbody != null) {
			List<Element> trs = tbody.elements();
			if (trs != null) {
				for (Element tr : trs) {
					if (tr.elements() != null) {
						temp = tr.elements().size();
						if (count < temp) {
							count = temp;
						}
					}
				}
			}
		}
		return count;
	}

	// 判断一个元素的子元素中是否含有需要转换的元素
	@SuppressWarnings("unchecked")
	public static boolean isExistConverterElement(Element element,
			String[] converters) {
		boolean flag = false;
		List<Element> children = element.elements();
		if (children != null) {
			for (Element child : children) {
				if (CommonDivideUtils.hasTag(child.getName(), converters)) {
					flag = true;
					break;
				} else {
					if (isExistConverterElement(child, converters)) {
						flag = true;
						break;
					} else {
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	// 判断一个元素是否是空元素
	private static boolean isNullElement(Element element) {
		boolean flag = false;
		if (element != null && element.elements() != null
				&& element.elements().size() == 0) {
			String text = element.getText().trim();
			if (text != null) {
				char[] chs = text.toCharArray();
				for (char ch : chs) {
					if ((int) ch != 160 && !Character.isWhitespace(ch)) {
						flag = false;
						break;
					} else {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	// 识别出用Table实现论坛帖子
	// 判断依据：（1）TR中是否含有相同数量的TD标签 （2）TR的数量比较大
	@SuppressWarnings("unchecked")
	public static boolean isForumIdentity(Element element) {

		boolean flag = false;
		Element tbody = element.element("TBODY");
		if (tbody != null) {
			List<Element> trs = tbody.elements();
			if (trs != null) {
				if (trs.size() >= 10) { // 论坛帖子数量设定要求大于等于10
					Element tr0 = trs.get(0);
					int temp = 0;
					if (tr0.elements() != null) {
						temp = tr0.elements().size();
					}
					for (Element tr : trs) {
						if (tr.elements() != null) {
							List<Element> children = tr.elements();
							for (Element child : children) {
								if ("TABLE".equalsIgnoreCase(child.getName())) {
									return false;
								}
							}
							int number = children.size();
							if (number == temp) {
								flag = true;
							} else {
								flag = false;
								break;
							}
						}
					}
				}
			}
		}
		return flag;
	}

}
