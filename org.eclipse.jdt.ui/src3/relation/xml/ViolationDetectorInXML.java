/*
 * @(#) ViolationDetector.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.PatternAnalyser;
import relation.PtrnOprEnum;
import relation.type.RelationEnum;
import util.UtilAST;
import util.UtilDirScan;
import util.UtilFile;
import util.UtilStr;
import util.UtilXML;

/**
 * @author Myoungkyu Song
 * @date Jun 16, 2011
 * @since JDK1.6
 */
public class ViolationDetectorInXML {
	String			dirXML;
	RelationEnum	curRelation;
	PtrnOprEnum		curPtrnOpr;

	List<String>	_xmlfileList;
	List<String>	_relationList;
	XMLGrep			_xmlgrep;
	List<XMLTree>	_xmltrees;

	public ViolationDetectorInXML(String dirXML) {
		this.dirXML = dirXML;
		_xmlgrep = new XMLGrep();
		_xmltrees = new ArrayList<XMLTree>();
	}

	public List<String> getXMLfileList() {
		return _xmlfileList;
	}

	public List<String> getRelationList() {
		return _relationList;
	}

	/** @METHOD */
	public void readRelation(String[] relationParm, String xmlsuffix) {
		_xmlfileList = UtilDirScan.getResultViaStr(dirXML, xmlsuffix);

		String parm = Comm.getParm(STR.parm_relation);
		if (parm.startsWith("?"))
			readAllRelation(_xmlfileList);
		else
			_relationList = readAllRelation(_xmlfileList, relationParm);
	}

	/** @METHOD */
	public void save(String inspectTargetPath) {
		UtilFile.fileWrite(inspectTargetPath, _relationList);
	}

	/** @METHOD */
	public List<String> read(String inspectTargetPath) {
		_relationList = UtilFile.fileRead2List(inspectTargetPath);
		return _relationList;
	}

	/** @METHOD */
	void readAllRelation(List<String> xmllist) {
		String parm = Comm.getParm(STR.parm_relation);
		String parmUpdated = parm.replace("?", " ");
		String[] checkstr = parmUpdated.split(","); // " ", class, "="

		// Extract all pair from tag.
		_xmltrees = _xmlgrep.getAllValues(checkstr, xmllist);
		// xmlgrep.print(_xmltrees);
	}

	/** @METHOD */
	List<String> readAllRelation(List<String> xmlfileList, String[] relationParm) {
		List<String> relationList = new ArrayList<String>();
		List<String> relationParmList = new ArrayList<String>();
		Collections.addAll(relationParmList, relationParm);

		if (UtilStr.compare(STR.relation_class_table, relationParmList))
			curRelation = RelationEnum.CLASSNTABLE;

		for (int i = 0; i < xmlfileList.size(); i++) {
			String xmlfile = xmlfileList.get(i);
			List<String> relationSubList = UtilXML.readXMLTags(xmlfile, relationParmList);
			if (relationSubList != null) {
				relationList.addAll(relationSubList);

				for (int j = 0; j < relationSubList.size(); j += 2) {
					XMLTree xmlelem = new XMLTree(xmlfile);
					xmlelem.base = relationSubList.get(j + 1);
					xmlelem.others.add(relationParm[1].trim() + "=" + relationSubList.get(j));
					_xmltrees.add(xmlelem);
				}
			}
		}
		// _xmlgrep.print(_xmltrees);
		return relationList;
	}

	/** @METHOD */
	public List<String> findViolation(String pattern, List<String> relationList, String[] relationParm) {
		List<String> violateList = new ArrayList<String>();
		String parm = Comm.getParm(STR.parm_relation);

		if (parm.startsWith("?")) {
			String[] mostPromisingOtherAttr = _xmlgrep.getMostPromisingOtherAttr(_xmltrees);

			for (int i = 0; i < _xmltrees.size(); i++) {
				XMLTree treeElem = _xmltrees.get(i);
				String elem1 = treeElem.base;
				String elem2 = _xmlgrep.getValue(mostPromisingOtherAttr, treeElem.others);
				String newElem1 = elem1, newElem2 = elem2;

				if (elem2 == null)
					continue;

				if (elem1.contains("."))
					newElem1 = UtilStr.getShortClassName(elem1);
				if (elem2.contains("."))
					newElem2 = UtilStr.getShortClassName(elem2);

				List<String> sublist = findViolation(newElem2, newElem1, pattern);
				if (!sublist.isEmpty()) {
					violateList.add("* Location: " + treeElem.xmlfile);
					violateList.addAll(sublist);
				}
			}
		}
		else {
			List<String> relationParmList = new ArrayList<String>();
			Collections.addAll(relationParmList, relationParm);

			String x = relationParmList.toString().replace(" ", "");
			String y = STR.relation_class_table.replace(" ", "");
			if (x.equalsIgnoreCase(y)) {
				curRelation = RelationEnum.CLASSNTABLE;
			}
			violateList.addAll(findViolation(relationList, pattern));
		}
		return violateList;
	}

	/** @METHOD */
	List<String> findViolation(List<String> relationList, String pattern) {
		List<String> violateList = new ArrayList<String>();
		for (int i = 0; i < relationList.size(); i += 2) {
			String elem1 = relationList.get(i);
			String elem2 = relationList.get(i + 1);
			String newElem1 = elem1, newElem2 = elem2;

			if (curRelation == null) {
				if (elem1.contains("."))
					newElem1 = UtilStr.getShortClassName(elem1);
				if (elem2.contains("."))
					newElem2 = UtilStr.getShortClassName(elem2);

				List<String> sublist = findViolation(newElem2, newElem1, pattern);
				if (!sublist.isEmpty()) {
					violateList.add("location: " + elem2);
					violateList.addAll(sublist);
				}
			}
			else {
				switch (curRelation) {
				case CLASSNTABLE: {
					String className = UtilAST.getOnlyClassName(elem1);
					String tableName = elem2;
					violateList.addAll(findViolation(className, tableName, pattern));
					break;
				}
				}
			}
		}
		if (!_xmltrees.isEmpty()) {
			String[] mostPromisingOtherAttr = _xmlgrep.getMostPromisingOtherAttr(_xmltrees);
			violateList.clear();
			for (int i = 0; i < _xmltrees.size(); i++) {
				XMLTree xmlelem = _xmltrees.get(i);

				String elem1 = xmlelem.base;
				String elem2 = _xmlgrep.getValue(mostPromisingOtherAttr, xmlelem.others);
				String newElem1 = elem1, newElem2 = elem2;

				if (elem2 == null)
					continue;

				if (elem1.contains("."))
					newElem1 = UtilStr.getShortClassName(elem1);
				if (elem2.contains("."))
					newElem2 = UtilStr.getShortClassName(elem2);

				List<String> sublist = findViolation(newElem1, newElem2, pattern);
				if (!sublist.isEmpty()) {
					violateList.add("* Location: " + xmlelem.xmlfile);
					violateList.add("(" + newElem1 + "," + newElem2 + ")");
				}
			}
		}
		return violateList;
	}

	/** @METHOD */
	List<String> findViolation(String org, String transformed, String pattern) {
		List<String> violateList = new ArrayList<String>();

		PatternAnalyser patternAnalyzer = new PatternAnalyser();
		Stack<String> ptrnOprs = patternAnalyzer.parsePattern(pattern);
		String curTrans = "";

		int size = ptrnOprs.size();
		for (int i = 0; i < size; i++) {
			String theOpr = ptrnOprs.pop();

			switch (patternAnalyzer.getPtrnOpr(theOpr)) {
			case PREFIX:
				String prefix = theOpr.substring(theOpr.indexOf('(') + 1, theOpr.lastIndexOf(')'));
				curTrans = prefix + curTrans;
				break;
			case UPPERCASE:
				curTrans = org.toUpperCase();
				break;
			case LOWFIRSTCHAR:
				curTrans = UtilStr.lwFirstChar(org);
				break;
			case MATCH:
				curTrans = org;
				break;
			default:
				break;
			}
		}
		if (curTrans.equals(transformed) == false) {
			violateList.add("(" + curTrans + ", " + transformed + ")");
		}
		return violateList;
	}
}
