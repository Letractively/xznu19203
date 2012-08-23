package com.c_platform.catchcontent.catchcontent.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortUtil {
	/**
	 * shell(希尔排序)
	 * 
	 * @param array
	 * @return
	 */
	public static double[] sort(double[] array) {
		int h = 1;
		double temp;
		while (h * 3 + 1 < array.length + 1) {
			h = h * 3 + 1;
		}

		while (h != 0) {
			for (int i = 0; i < h; i++) {
				for (int j = i; j < array.length - h; j += h) {
					temp = array[j + h];
					int k;
					for (k = j; k > i - h; k -= h) {
						if (temp > array[k]) {
							array[k + h] = array[k];
						} else {
							break;
						}
					}
					array[k + h] = temp;
				}
			}
			h = (h - 1) / 3;
		}
		return array;
	}

	public static ArrayList<Object[]> sortList(ArrayList<Object[]> li) {
		Collections.sort(li, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Object[] a = (Object[]) o1;
				Object[] b = (Object[]) o2;
				if (Double.valueOf(a[0].toString()) < Double.valueOf(b[0]
						.toString())) {
					return 1;
				}
				return -1;
			}
		});
		return li;
	}
}
