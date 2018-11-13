/*
 * @(#) StrComb.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Myoungkyu Song
 * @date Oct 29, 2010
 * @since JDK1.6
 */
public class StrComb {
	String	delim;
	StrPerm	perm;

	public StrComb() {}

	public StrComb(String delim) {
		this.delim = delim;
		perm = new StrPerm(delim);
	}

	public static void main(String[] args) {
		String[] array = { "Student", "Course", "courses" };
		StrComb c = new StrComb("_");
		ArrayList<String> ans = new ArrayList<String>();
		c.combine(array, ans);
		for (String s : ans)
			System.out.println(s);
	}

	public Map<String, String> combine(Map<String, String> variables) {
		Map<String, String> ans = new HashMap<String, String>();
		combineHelper(variables, ans);
		return ans;
	}

	private void combineHelper(Map<String, String> variables, Map<String, String> out) {
		String[] vars = new String[variables.size()];
		String[] names = new String[variables.size()];
		int i = 0;
		for (String s : variables.keySet()) {
			vars[i] = s;
			names[i] = variables.get(s);
			i++;
		}
		ArrayList<String> ans1 = new ArrayList<String>();
		ArrayList<String> ans2 = new ArrayList<String>();
		combine(vars, ans1);
		combine(names, ans2);
		for (i = 0; i < ans1.size(); i++) {
			out.put(ans1.get(i), ans2.get(i));
		}
	}

	/**
	 * @METHOD
	 */
	public void combine(String[] array, ArrayList<String> ans) {
		combine("", array, ans);
	}

	/**
	 * @METHOD
	 */
	void combine(String prefix, String[] array, ArrayList<String> ans) {
		perm.perm(prefix.split(delim), ans);

		for (int i = 0; i < array.length; i++) {
			String combine;

			if (prefix.isEmpty())
				combine = prefix + array[i];
			else
				combine = prefix + delim + array[i];

			combine(combine, subArray(array, i + 1), ans);
		}
	}

	/**
	 * @METHOD
	 */
	String[] subArray(String[] array, int index) {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = index; i < array.length; i++) {
			list.add(array[i]);
		}
		String[] newArray = (String[]) list.toArray(new String[0]);
		return newArray;
	}
}
