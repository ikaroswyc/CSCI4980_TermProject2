/*
 * @(#) StrPerm.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.util;

import java.util.ArrayList;

/**
 * @author Myoungkyu Song
 * @date Oct 29, 2010
 * @since JDK1.6
 */
public class StrPerm {

	String	delim;

	public StrPerm() {}

	public StrPerm(String delim) {
		this.delim = delim;
	}

	/**
	 * @METHOD
	 */
	public void perm(String[] array, ArrayList<String> ans) {
		perm("", array, ans);
	}

	/**
	 * @METHOD
	 */
	void perm(String prefix, String[] array, ArrayList<String> ans) {
		int N = array.length;
		if (N == 0 && !prefix.isEmpty())
			ans.add(prefix);

		else {
			for (int i = 0; i < N; i++) {
				String str;

				if (prefix.isEmpty())
					str = prefix + array[i];
				else
					str = prefix + delim + array[i];

				perm(str, addArray(subArray(array, 0, i), subArray(array, i + 1, N)), ans);
			}
		}
	}

	/**
	 * @METHOD
	 */
	String[] addArray(String[] array1, String[] array2) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < array1.length; i++) {
			list.add(array1[i]);
		}
		for (int i = 0; i < array2.length; i++) {
			list.add(array2[i]);
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * @METHOD
	 */
	String[] subArray(String[] array, int index1, int index2) {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = index1; i < index2; i++) {
			list.add(array[i]);
		}
		String[] newArray = (String[]) list.toArray(new String[0]);
		return newArray;
	}
}
