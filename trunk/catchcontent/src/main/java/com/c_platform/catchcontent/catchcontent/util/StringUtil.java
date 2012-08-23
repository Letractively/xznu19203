package com.c_platform.catchcontent.catchcontent.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

	// --Commented out by Inspection (5/21/10 11:12 AM):private static final
	// String defCharSet = "UTF-8";

	/**
	 * TODO 流转字符
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStreamToString(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		if (in != null) {
			in.close();
		}
		return buffer.toString();
	}

	// --Commented out by Inspection START (5/21/10 11:12 AM):
	// /**
	// * TODO 字符转流
	// * @param str
	// * @param charSet
	// * @return
	// */
	// public static InputStream stringToInputStream(String str, String charSet)
	// {
	// if (charSet == null)
	// {
	// charSet = defCharSet;
	// }
	// InputStream inputStream = null;
	// try
	// {
	// inputStream = new ByteArrayInputStream(str.getBytes(charSet));
	// }
	// catch (UnsupportedEncodingException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return inputStream;
	// }
	// --Commented out by Inspection STOP (5/21/10 11:12 AM)
	// --Commented out by Inspection START (5/21/10 11:12 AM):
	// /**
	// * TODO 流转二进制
	// * @param iStrm
	// * @return
	// * @throws IOException
	// */
	// public static byte[] InputStreamToByte(InputStream iStrm)
	// throws IOException
	// {
	// ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
	// int ch;
	// while ((ch = iStrm.read()) != -1)
	// {
	// bytestream.write(ch);
	// }
	// byte data[] = bytestream.toByteArray();
	// bytestream.close();
	// return data;
	// }
	// --Commented out by Inspection STOP (5/21/10 11:12 AM)

	/**
	 * TODO 去除回车等符号
	 * 
	 * @param str
	 * @return str
	 */
	public static String replaceBlank(String str) {
		Pattern p = Pattern.compile("\t|\r|\n");
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	/**
	 * 半角转全角
	 * 
	 * @param input
	 *            String.
	 * @return 全角字符串.
	 */
	public static String toSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000'; // 采用十六进制,相当于十进制的12288
			} else if (c[i] < '\177') { // 采用八进制,相当于十进制的127
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * 
	 * @param input
	 *            String.
	 * @return 半角字符串
	 */
	public static String toDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	/**
	 * TODO 正则匹配
	 * 
	 * @param str
	 *            目标字符串
	 * @param regax
	 *            正则表达式
	 * @return
	 */
	public static boolean matchRegax(String str, String regax) {
		if (str == null || "".equals(str)) {
			return false;
		}
		Pattern p = Pattern.compile(regax);
		Matcher m = p.matcher(str.toLowerCase());
		return m.find();
	}

	/**
	 * TODO 判断是否是数字.
	 * 
	 * @param str
	 *            字符串
	 * @return 是数字返回true
	 */
	public static boolean isNumeric(String str) {
		if ("".equals(str)) {
			return false;
		}
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * TODO 过滤字符串中的'/'
	 * 
	 * @param str
	 * @return String
	 */
	public static String replace(String str) {
		str = str.replaceAll("\\/", "");
		return str;
	}

	/**
	 * TODO 获得匹配的字符串
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String getMatchString(String str, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str.toLowerCase());
		if (m.find()) {
			return m.group(0);
		}
		return "";
	}

	public static String getFileType(String type) {
		if ("BMP".equalsIgnoreCase(type)) {
			type = "jpg";
		} else if ("GIF".equalsIgnoreCase(type)) {
			type = "png";
		}
		return type;
	}

//	/**
//	 * TODO 保存是key，
//	 * 
//	 * @param WebContext
//	 *            ctx, String url,JsonMessage json,DeviceInfo deviceInfo,int
//	 *            index
//	 * @return String
//	 */
//	public static String getKey(WebContext ctx, String url, JsonMessage json,
//			DeviceInfo deviceInfo, int index) {
//		StringBuffer sbKey = new StringBuffer();
//
//		if (url.endsWith("/")) {
//			url = url.substring(0, url.lastIndexOf("/"));
//		}
//
//		if (index == 1) {// 目标html
//			sbKey.append(url);
//			sbKey.append("-");
//			// sbKey.append(uaStr);
//			// sbKey.append("-");
//			sbKey.append(json.getBody().getPs());
//			// sbKey.append("-");
//			// sbKey.append(json.getEngine_id());
//			// sbKey.append("-");
//			// sbKey.append(json.getBody().getBm());
//			if (isReadModel(ctx)) {
//				sbKey.append("-");
//				sbKey.append(ctx.getRm());
//			}
//			if (isRemainText(ctx)) {
//				sbKey.append("-");
//				sbKey.append(ctx.getHtmlHead());
//			}
//		} else if (index == 2) {// 压缩图
//			sbKey.append(url);
//			// sbKey.append(uaStr);
//			sbKey.append("-");
//			sbKey.append(deviceInfo.getScreenWidth());
//			sbKey.append("-");
//			sbKey.append(json.getBody().getImgset().get("imgql"));
//			// sbKey.append("-");
//			// sbKey.append(json.getEngine_id());
//			// sbKey.append("-");
//			// sbKey.append(json.getBody().getBm());
//		} else if (index == 3) {// js,css
//			sbKey.append(url);
//			// sbKey.append("-");
//			// sbKey.append(uaStr);
//			// sbKey.append("-");
//			// sbKey.append(json.getEngine_id());
//			// sbKey.append("-");
//			// sbKey.append(json.getBody().getBm());
//		} else if (index == 4) {// 原图
//			sbKey.append(url);
//			// sbKey.append("-");
//			// sbKey.append(uaStr);
//			// sbKey.append("-");
//			// sbKey.append(json.getBody().getBm());
//		} else if (index == 5) {// 原图
//			sbKey.append(url);
//			// sbKey.append("-");
//			// sbKey.append(uaStr);
//			// sbKey.append("-");
//			// sbKey.append(json.getBody().getBm());
//		} else if (index == 6) {// adrmini
//			sbKey.append(url);
//			sbKey.append("-");
//			// sbKey.append(uaStr);
//			// sbKey.append("-");
//			sbKey.append(json.getBody().getPs());
//			// sbKey.append("-");
//			// sbKey.append(json.getEngine_id());
//			// sbKey.append("-");
//			// if (!isSIDHtml(ctx)) {
//			// sbKey.append(deviceInfo.getScreenWidth());
//			// sbKey.append("-");
//			// sbKey.append(json.getBody().getImgset().get("imgql"));
//			// sbKey.append("-");
//			// }
//			// sbKey.append(json.getBody().getBm());
//			if (isReadModel(ctx)) {
//				sbKey.append("-");
//				sbKey.append(ctx.getRm());
//			}
//			if (isRemainText(ctx)) {
//				sbKey.append("-");
//				sbKey.append(ctx.getHtmlHead());
//			}
//
//		}
//		// System.out.println(("------------"+sbKey.toString())+
//		// "======"+urlToMD5(sbKey.toString()));
//		// return String.valueOf(sbKey.toString().toLowerCase().hashCode());
//		return urlToMD5(sbKey.toString());
//	}
//
//	public static boolean isHtml(String typeStr) {
//		String[] html = { "html", "htm", "shtml", "jsp", "asp", "aspx", "php",
//				"com", "cn", "net" };
//		String[] list = typeStr.split("\\/");
//		for (int i = 0; i < html.length; i++) {
//			if (list[0].equalsIgnoreCase(html[i])) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	// html是否支持SID
//	public static boolean isSIDHtml(WebContext ctx) {
//		String serverAddress = "http://tiantianterm.com:888/agent/SessionBrowserService";
//		boolean flag = false;
//		if (ctx.getServerAddress() != null
//				&& !ctx.getServerAddress().equals("")) {
//			serverAddress = ctx.getServerAddress();
//		}
//		if (serverAddress.indexOf("SessionBrowserService") > -1) {
//			flag = true;
//		}
//		return flag;
//
//	}
//
//	private static boolean isBornPage(WebContext ctx) {
//		boolean isBornPage = false;
//		if (ctx.isBornPage()) {
//			isBornPage = true;
//		}
//		return isBornPage;
//	}
//
//	private static boolean isRemainText(WebContext ctx) {
//		boolean flag = false;
//		if (ctx.isRemainText()) {
//			flag = true;
//		}
//		return flag;
//	}
//
//	public static boolean isReadModel(WebContext ctx) {
//		boolean isReadMode = false;
//		String rm = ctx.getRm();
//		if (ctx.isRss()) {
//			isReadMode = false;
//		} else if ("1".equals(rm)) {
//			isReadMode = true;
//		}
//
//		return isReadMode;
//	}
//
//	// cacheKey MD5加密
//	public static String urlToMD5(String sbKey) {
//		try {
//			String encoderKey = URLEncoder.encode(sbKey, "UTF-8");
//			if (encoderKey.length() >= 250) {
//				sbKey = MD5.compute(sbKey);
//			}
//		} catch (UnsupportedEncodingException e) {
//			logger.error("cacheKey to URLEncoder", e);
//		}
//		return sbKey;
//	}

}
