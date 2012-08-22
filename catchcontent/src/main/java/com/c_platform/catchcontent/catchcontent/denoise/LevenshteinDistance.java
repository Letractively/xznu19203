/*
 * 文 件 名：LevenshteinDistance.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：计算两个字符串相似度的LD算法
 * 修 改 人：zhangtao
 * 修改时间：2010-4-6
 * 修改内容：新增
 */
package com.c_platform.catchcontent.catchcontent.denoise;

/**
 * TODO 计算两个字符串相似度的LD算法
 * 
 * @author zhangtao
 * @version C02 2010-4-6
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class LevenshteinDistance {

	/**
	 * desc 取最小值
	 * 
	 * @param int, int, int
	 * 
	 * @return int
	 */
	private int min(int one, int two, int three) {
		int min = one;
		if (two < min) {
			min = two;
		}
		if (three < min) {
			min = three;
		}
		return min;
	}

	/**
	 * desc 计算str1和str2的矩阵
	 * 
	 * @param str1
	 *            , str2
	 * 
	 * @return int
	 */
	private int ld(String str1, String str2) {
		int d[][]; // 矩阵
		int n = str1.length();
		int m = str2.length();
		int i; // 遍历str1的
		int j; // 遍历str2的
		char ch1; // str1的
		char ch2; // str2的
		int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) { // 初始化第一列
			d[i][0] = i;
		}
		for (j = 0; j <= m; j++) { // 初始化第一行
			d[0][j] = j;
		}
		for (i = 1; i <= n; i++) { // 遍历str1
			ch1 = str1.charAt(i - 1);
			// 去匹配str2
			for (j = 1; j <= m; j++) {
				ch2 = str2.charAt(j - 1);
				if (ch1 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
						+ temp);
			}
		}
		return d[n][m];
	}

	/**
	 * desc 相似度计算,如果两个字符串之一为null时，返回0
	 * 
	 * @param string
	 *            , string
	 * 
	 * @return double
	 */
	public double sim(String str1, String str2) {
		if (str1 == null || str2 == null || str1.equalsIgnoreCase("")
				|| str2.equalsIgnoreCase("")) {
			return 0;
		}
		int ld = ld(str1, str2);
		return 1 - (double) ld / Math.max(str1.length(), str2.length());
	}

}
