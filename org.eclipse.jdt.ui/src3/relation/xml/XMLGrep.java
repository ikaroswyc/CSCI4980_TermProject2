package relation.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.RuntimeErrorException;

import util.UtilFile;
import util.UtilMap;
import util.UtilPrint;
import util.UtilStr;

public class XMLGrep {
	/** @METHOD */
	protected HashMap<String, List<String>> grepXMLTag(List<String> xmllist, String[] checkstr) {
		HashMap<String, List<String>> file_tags = new HashMap<String, List<String>>();
		for (int i = 0; i < xmllist.size(); i++) {
			String xmlfile = xmllist.get(i);

			// if (xmlfile.contains("loan-broker-sync-vm-profiling-config.xml"))
			// System.out.println(xmlfile);
			// continue;

			String filebody = UtilFile.fileRead(new File(xmlfile));
			String flatfilebody = filebody.replace("\n", "");
			if (flatfilebody.contains("!=")) {
				flatfilebody = flatfilebody.replace("!=", "!!");
			}
			List<String> taglist = new ArrayList<String>();
			int startpos = 0;
			for (int j = startpos; j < flatfilebody.length(); j++) {
				int pos1 = flatfilebody.indexOf(checkstr[1], startpos);
				if (pos1 == -1)
					break;
				else if (flatfilebody.charAt(pos1 - 1) != ' ') {
					startpos = pos1 + 1;
					continue;
				}
				int pos2 = flatfilebody.lastIndexOf("<", pos1);
				int pos3 = flatfilebody.indexOf(">", pos1);
				startpos = pos1 + 1;
				String tagstr = flatfilebody.substring(pos2, pos3 + 1);

				// System.out.println("[DBG]" + tagstr);
				if (UtilStr.countSubString(tagstr, checkstr[2]) != 1) {
					taglist.add(tagstr);
				}
			}
			if (!taglist.isEmpty())
				file_tags.put(xmlfile, taglist);
		}
		return file_tags;
	}

	/**
	 * @param xmllist
	 * @METHOD
	 */
	protected List<XMLTree> getAllValues(String[] checkstr, List<String> xmllist) {
		// Grep all XML tag with the given property.
		HashMap<String, List<String>> file_tags = grepXMLTag(xmllist, checkstr);

		// HashMap<String, List<String[]>> file_pairs = new HashMap<String, List<String[]>>();
		List<XMLTree> xmltrees = new ArrayList<XMLTree>();
		char delimeter = '=';

		for (Entry<String, List<String>> e : file_tags.entrySet()) {
			String xmlfile = e.getKey();
			List<String> taglist = e.getValue();

			// System.out.println("[DBG]file: " + xmlfile);
			// if (xmlfile.contains("loan-broker-sync-vm-profiling-config.xml"))
			// System.out.println(xmlfile);
			// List<String[]> pairs = new ArrayList<String[]>();

			for (int i = 0; i < taglist.size(); i++) {
				String tagelem = taglist.get(i);
				XMLTree xmltreeRef = new XMLTree(xmlfile);
				List<String[]> pair = getPair(xmltreeRef, tagelem, checkstr[1].trim(), delimeter);

				if (pair != null && !pair.isEmpty()) {
					// pairs.addAll(pair);
					xmltrees.add(xmltreeRef);
				}
			}
			// file_pairs.put(xmlfile, pairs);
		}
		return xmltrees;
	}

	/** @METHOD */
	List<String[]> getPair(XMLTree xmltreeRef, String tagline, String base, char delimeter) {
		int cnt = 0;
		for (int i = 0; i < tagline.length(); i++) {
			if (tagline.charAt(i) == '=')
				cnt++;
		}
		if (cnt == 1)
			return null;
		List<String[]> pairlist = new ArrayList<String[]>();
		for (int i = 0; i < tagline.length(); i++) {
			if (tagline.charAt(i) == delimeter) {
				String[] pair = getPair(tagline, i, delimeter);
				if (pair != null) {
					pairlist.add(pair);

					String key = pair[0].trim();
					String val = pair[1].trim();

					if (key.startsWith(base)) {
						xmltreeRef.base = val;
					}
					else {
						xmltreeRef.others.add(key + "=" + val);
					}
				}
			}
		}
		return pairlist;
	}

	/** @METHOD */
	String[] getPair(String tagline, int pos, char delimeter) {
		boolean flag = false;
		int end = -1;
		for (int i = pos + 1; i < tagline.length(); i++) {
			if (!flag && tagline.charAt(i) != ' ')
				flag = true;
			else if (flag && (tagline.charAt(i) == ' ' || tagline.charAt(i) == '>')) {
				end = i;
				break;
			}
		}
		flag = false;
		int start = -1;
		for (int i = pos - 1; i > -1; i--) {
			if (!flag && tagline.charAt(i) != ' ')
				flag = true;
			else if (flag && (tagline.charAt(i) == ' ' || tagline.charAt(i) == '<')) {
				start = i;
				break;
			}
		}
		String[] pairs = tagline.substring(start, end).split(String.valueOf(delimeter));
		if (pairs.length != 2)
			throw new RuntimeErrorException(null, "getPairs");
		pairs[0] = pairs[0].replace("\"", "").replace("/", "");
		pairs[1] = pairs[1].replace("\"", "").replace("/", "");

		return (pairs[0].isEmpty() || pairs[1].isEmpty()) ? null : pairs;
	}

	/** @METHOD */
	String getValue(String[] mostPromisingOtherAttr, List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String elem = list.get(i);
			String elempair[] = elem.split("=");
			if (elempair.length != 2)
				throw new RuntimeException(this.getClass().getName());
			String key = elempair[0];
			String val = elempair[1];
			for (int j = 0; j < mostPromisingOtherAttr.length; j++) {
				String most = mostPromisingOtherAttr[j];
				if (key.equals(most)) {
					return val;
				}
			}
		}
		return null;
	}

	/** @METHOD */
	String[] getMostPromisingOtherAttr(List<XMLTree> xmltrees) {
		Map<String, Integer> otherAttr_freq = getFreqOtherAttr(xmltrees);

		otherAttr_freq = UtilMap.sortByValue(otherAttr_freq);
		// UtilPrint.printMapGeneral(otherAttr_freq);
		Iterator<Map.Entry<String, Integer>> itr = otherAttr_freq.entrySet().iterator();
		String second = null, first = itr.next().getKey();
		if (itr.hasNext())
			second = itr.next().getKey();
		return new String[] { first, second };
	}

	/** @METHOD */
	Map<String, Integer> getFreqOtherAttr(List<XMLTree> xmltrees) {
		Map<String, Integer> otherAttr_freq = new HashMap<String, Integer>();
		for (int i = 0; i < xmltrees.size(); i++) {
			XMLTree xmltree = (XMLTree) xmltrees.get(i);

			List<String> others = xmltree.others;
			for (int j = 0; j < others.size(); j++) {
				String key_value_of_other = others.get(j);
				String key_other = key_value_of_other.split("=")[0];

				if (otherAttr_freq.containsKey(key_other)) {
					Integer freq = otherAttr_freq.get(key_other);
					otherAttr_freq.put(key_other, freq + 1);
				}
				else {
					otherAttr_freq.put(key_other, 1);
				}
			}
		}
		return otherAttr_freq;
	}

	/** @METHOD */
	void print(HashMap<String, List<String>> map) {
		for (Map.Entry<String, List<String>> e : map.entrySet()) {
			String file = e.getKey();
			List<String> tags = e.getValue();
			System.out.println(file);
			UtilPrint.printArrayList(tags);
		}
	}

	/** @METHOD */
	void print(List<?> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof XMLTree) {
				XMLTree xmltree = (XMLTree) list.get(i);
				System.out.println(xmltree);
			}
			else if (list.get(i) instanceof String[]) {
				String[] elem = (String[]) list.get(i);
				System.out.println("\t" + elem[0] + ": " + elem[1]);
			}
		}
	}
}
