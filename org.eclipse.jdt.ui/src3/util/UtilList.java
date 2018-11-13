/*
 * @(#) UtilList.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */

package util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Myoungkyu Song
 * @version May 20, 2010
 * @since JDK1.6
 */
public class UtilList {

	/** @METHOD */
	public static void addStrToList(String str, List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, str + "+" + list.get(i));
		}
	}

	/** @METHOD */
	public static int getFrequency(String elem, List<String> list) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			if (elem.equals(list.get(i)))
				count++;
		}
		return count;
	}

	/**
	 * @param array
	 * @return
	 */
	public static ArrayList<String> toArrayList(String[] array) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * @param list1 to return
	 * @param list2 to copy from original list
	 */
	public static void removeDuplicateElement(ArrayList<String> list1, ArrayList<String> list2) {
		ArrayList<String> list3 = new ArrayList<String>();

		for (int i = 0; i < list1.size(); i++) {
			String file1 = list1.get(i);
			String filePath = file1;
			file1 = file1.substring(file1.lastIndexOf("\\") + 1);

			boolean flag = true;
			for (int j = 0; j < list2.size(); j++) {
				String file2 = list2.get(j);
				file2 = file2.substring(file2.lastIndexOf("\\") + 1);

				if (file1.equals(file2)) {
					flag = false;
				}
			}

			if (flag) {
				list3.add(filePath);
			}
		}

		list1.clear();

		for (int i = 0; i < list3.size(); i++) {
			list1.add(list3.get(i));
		}
	}

	/**
	 * @param oldlist to copy
	 * @param newlist to store
	 */
	public static void addAllList(ArrayList<String> oldlist, ArrayList<String> newlist) {
		for (int i = 0; i < oldlist.size(); i++) {
			String x = oldlist.get(i);

			if (!newlist.contains(x))
				newlist.add(x);
		}
	}

	/**
	 * @param prefix whose string is filtered
	 * @param orgList which original list
	 * @return filtered list object
	 */
	public static ArrayList<String> filterStartWith(String prefix, ArrayList<String> orgList) {
		ArrayList<String> resultList = new ArrayList<String>();
		for (int i = 0; i < orgList.size(); i++) {
			String elem = orgList.get(i);
			if (!elem.trim().startsWith(prefix))
				resultList.add(elem);
		}
		return resultList;
	}

	/**
	 * @param str the string to search
	 * @param i the index from which to search
	 * @param bodyMetaMetadata the list contents
	 * @return the result of "Where" statement.
	 */
	public static String getNearestNeighbor(String str, int i, ArrayList<String> bodyMetaMetadata) {
		String result = null;
		for (int j = i; j < bodyMetaMetadata.size(); j++) {
			String elem = bodyMetaMetadata.get(j);
			if (elem.contains(str)) {
				result = elem;
				break;
			}
		}
		return result;
	}

	public static String getNearestNeighbor(String[] str, int i, ArrayList<String> bodyMetaMetadata) {
		String result = null;
		for (int j = i; j < bodyMetaMetadata.size(); j++) {
			String elem = bodyMetaMetadata.get(j);

			for (int k = 0; k < str.length; k++) {
				if (elem.contains(str[k])) {
					result = elem;
					return result;
				}
			}
		}
		return result;
	}
}
