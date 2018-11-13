/*
 * @(#) MostFrequent.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class MostFrequent {
	static MostFrequent	mostFrequent	= null;

	/**
	 * @METHOD
	 */
	public static MostFrequent getInstance() {
		if (mostFrequent == null) {
			mostFrequent = new MostFrequent();
		}
		return mostFrequent;
	}

	/**
	 * @METHOD
	 */
	public String getMostFrequent(ArrayList<String> list) {
		HashMap<String, Integer> words = new HashMap<String, Integer>();

		for (int i = 0; i < list.size(); i++) {
			String elem = list.get(i);

			if (elem == null)
				continue;

			if (!words.containsKey(elem)) {
				words.put(elem, 1);
			}
			else {
				words.put(elem, words.get(elem) + 1);
			}
		}
		
//		for (Map.Entry<String, Integer> e : words.entrySet())
//			System.out.println(e.getKey() + ": " + e.getValue());
		
		return getMostFrequent(words);
	}

	/**
	 * @METHOD
	 */
	String getMostFrequent(HashMap<String, Integer> words) {
		TreeMap<Integer, List<String>> wordFreqs = new TreeMap<Integer, List<String>>(new Comparator<Integer>() {
			// Descending order
			public int compare(Integer a, Integer b) {
				return a == b ? 0 : (a < b ? 1 : -1);
			}
		});

		for (Map.Entry<String, Integer> e : words.entrySet()) {
			if (!wordFreqs.containsKey(e.getValue())) {
				ArrayList<String> wordList = new ArrayList<String>();
				wordList.add(e.getKey());
				wordFreqs.put(e.getValue(), wordList);
			}
			else {
				wordFreqs.get(e.getValue()).add(e.getKey());
			}
		}

		int highestFreq = wordFreqs.firstKey();
		// System.out.println(wordFreqs.get(highestFreq) + " => " + highestFreq);
		
//		for (int i = 0; i < wordFreqs.get(highestFreq).size(); i++) {
//			String elem = wordFreqs.get(highestFreq).get(i);
//			System.out.println("#->" + elem);
//		}
		
		return wordFreqs.get(highestFreq).get(0);
	}
}
