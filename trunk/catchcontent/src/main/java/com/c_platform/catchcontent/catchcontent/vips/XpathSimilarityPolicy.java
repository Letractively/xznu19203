package com.c_platform.catchcontent.catchcontent.vips;

/**
 * TODO NODE的xpath相似度计算方法
 * 
 * @author tao.zhang
 * @version C02 2010-4-26
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class XpathSimilarityPolicy {
	/**
	 * desc 相似度sim判断规则: 1、SIM取值范围确定为0—100 2、两个xpath完全相同则SIM为100
	 * 3、SIM的确定分别由以下三个参数及其权重确定： sameLength:两个xpath的相同部分的深度值（权重最大）
	 * diffLength:两个xpath的不同部分深度的差距 xpathStrSame:两个xpath的不相同部分的差距
	 * 4、sameLength的权重的确定 5、当十位数确定后，只需通过后面两个参数确定个位数即可
	 * 
	 * @param xpath1
	 *            , xpath2
	 * @return sim
	 */
	public int praseXpathSimilarity(String xpath1, String xpath2) {
		if (xpath1 == null || xpath2 == null || "".equalsIgnoreCase(xpath1)
				|| "".equalsIgnoreCase(xpath2)) {
			return 0;
		}
		int sim = 0;// 相似度
		if (xpath1.equalsIgnoreCase(xpath2)) {
			return 100;
		}
		String[] xa1 = xpathToArray(xpath1);
		String[] xa2 = xpathToArray(xpath2);
		if (xa1 == null || xa2 == null) {
			return 0;// 需修改20100315
		}
		int maxLength = Math.max(xa1.length, xa2.length);
		int minLength = Math.min(xa1.length, xa2.length);
		int sameLength = compareXpathArraySameLength(xa1, xa2);// 数组中相同部分数据的长度
		int diffLength = maxLength - sameLength;// 数组中不相同部分数据的长度
		int xpathStrSame = 0; // xpath中某一段Str的相似度
		if (sameLength != minLength) {
			xpathStrSame = compareXpath(xa1[sameLength], xa2[sameLength]);
		}
		int unitsDigital = getUnitsDigital(diffLength, xpathStrSame);// 相似度SIM的个位数
		double tensDigital = getTensDigital(maxLength, sameLength);// 相似度SIM的十位数

		sim = (int) (tensDigital * 10 + unitsDigital);
		sim = ((sim < 0 ? 0 : sim) > 100 ? 100 : sim);
		return sim;
	}

	/**
	 * desc 取相似度的个位数 首先根据diffLength确定区间值,然后根据xpathStrSame确定精确值
	 * （或者是否可以改进为通过赋予diffLength和xpathStrSame不同的阈值来确定unitsDigital?）
	 * 
	 * @param diffLength
	 *            , xpathStrSame
	 * @return unitsDigital
	 */
	private int getUnitsDigital(int diffLength, int xpathStrSame) {
		int unitsDigital = 0;
		if (diffLength >= 0 && diffLength <= 2) {
			if (xpathStrSame >= 9 || xpathStrSame == 0) {
				unitsDigital = 7;
			} else if (xpathStrSame >= 4 && xpathStrSame <= 8) {
				unitsDigital = 8;
			} else {
				unitsDigital = 9;
			}
		} else if (diffLength >= 3 && diffLength <= 5) {
			if (xpathStrSame >= 9 || xpathStrSame == 0) {
				unitsDigital = 3;
			} else if (xpathStrSame >= 4 && xpathStrSame <= 6) {
				unitsDigital = 4;
			} else if (xpathStrSame >= 7 && xpathStrSame <= 9) {
				unitsDigital = 5;
			} else {
				unitsDigital = 6;
			}
		} else if (diffLength >= 6) {
			if (xpathStrSame >= 9 || xpathStrSame == 0) {
				unitsDigital = 0;
			} else if (xpathStrSame >= 4 && xpathStrSame <= 8) {
				unitsDigital = 1;
			} else {
				unitsDigital = 2;
			}
		}
		return unitsDigital;
	}

	/**
	 * desc 取相似度的十位数 即maxLength>10时进行归十处理 为何不取=10时，因为maxLength=0的情况为0
	 * 
	 * @param maxLength
	 *            , sameLength
	 * @return tensDigital
	 */
	private double getTensDigital(int maxLength, int sameLength) {
		double tensDigital;
		if (maxLength > 10) {
			tensDigital = ((double) sameLength) * 10 / (maxLength);
		} else {
			tensDigital = sameLength;
		}
		return tensDigital;
	}

	/**
	 * desc 把xpath转换为数组，返回的数组元素从BODY之后开始
	 * 
	 * @param xpath
	 * @return xpathArray
	 */
	private String[] xpathToArray(String xpath) {
		if (xpath == null || "".equalsIgnoreCase(xpath)) {
			return null;
		}
		if (xpath.indexOf("BODY") + 5 > xpath.length()) { // 判断情况"/*[name()='HTML']/BODY"的出现
			return null;
		}
		String tmpXpath = xpath.substring(xpath.indexOf("BODY") + 5);
		String[] xpathArray = tmpXpath.split("/");
		return xpathArray;
	}

	/**
	 * desc 返回两个数组相同部分数据的长度
	 * 
	 * @param xa1
	 *            , xa2
	 * @return pos
	 */
	private int compareXpathArraySameLength(String[] xa1, String[] xa2) {
		if (xa1 == null || xa2 == null) {
			return -1;
		}
		int minLength = Math.min(xa1.length, xa2.length);
		int pos = -1;
		for (int i = 0; i < minLength; i++) {
			if (!xa1[i].equalsIgnoreCase(xa2[i])) {
				pos = i;
				i = minLength;
			}
		}
		if (pos == -1) {
			pos = minLength;
		}
		return pos;
	}

	// 
	/**
	 * desc 比较两个XpathString之间的差距，返回差距值 如果参数为null或者无法计算差距值，返回0
	 * 
	 * @param x1
	 *            , x2
	 * @return int
	 */
	private int compareXpath(String x1, String x2) {
		if (x1 == null || x2 == null || "".equalsIgnoreCase(x1)
				|| "".equalsIgnoreCase(x2)) {
			return 0;
		}
		try {
			if (x1.indexOf("[") != -1 && x2.indexOf("[") != -1
					&& x1.indexOf("]") != -1 && x2.indexOf("]") != -1) {
				if (x1.substring(0, x1.indexOf("[")).equalsIgnoreCase(
						x2.substring(0, x2.indexOf("[")))) {
					return Math.abs(Integer.parseInt(x1.substring(x1
							.indexOf("[") + 1, x1.indexOf("]")))
							- Integer.parseInt(x2.substring(
									x2.indexOf("[") + 1, x2.indexOf("]"))));
				}
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

}
