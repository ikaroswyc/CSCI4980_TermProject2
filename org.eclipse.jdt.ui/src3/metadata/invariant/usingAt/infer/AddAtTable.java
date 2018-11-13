/*
 * @(#) AddAtTable.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingAt.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.transform.RelationShip;
import metadata.invariant.usingAt.HandleAtAST;
import metadata.invariant.usingAt.elem.TableMap;
import util.UtilLog;
import util.UtilMap;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jan 6, 2011
 * @since JDK1.6
 */
public class AddAtTable {

	private HandleAtAST	_handleAtAst;

	public AddAtTable(HandleAtAST handleAtAst) {
		_handleAtAst = handleAtAst;
	}

	/**
	 * @METHOD
	 */
	public void infer() {
		System.out.println("------------------------------------------");
		System.out.println("*");
		System.out.println("*    [ Infer adding @Table annotation ]");
		System.out.println("*");

		// * - infer adding the location of @Table
		// * - by means of the most similar startsWith-substring.
		// *
		findMostSimilarStartWithSubStr();

		// * - infer @Table(name = "?"), name attibute's value.
		// *
		inferAddAtTableName();
	}

	/**
	 * @METHOD
	 */
	private void findMostSimilarStartWithSubStr() {

		ArrayList<String> list = new ArrayList<String>();

		UtilLog.w("------------------------------------------");
		UtilLog.w("\tFULL CLASS NAME");
		UtilLog.w("------------------------------------------");

		// * - to display and list-up the interesting data.
		// *
		for (int i = 0; i < _handleAtAst.getAtTableMapList().size(); i++) {
			TableMap elem = _handleAtAst.getAtTableMapList().get(i);
			String nameOfPackage = elem.getNameOfPackage();
			UtilLog.w("\t" + nameOfPackage);
			list.add(nameOfPackage);
		}
		System.out.println("--------+---------------------------------");

		// * - find the most similar startsWith-substring.
		// *
		String mostSimilarStartWithSubStr = UtilStr.findMostSimilarStartWithSubStr(list);
		System.out.println();
		System.out.println("[DBG] \t=>\t" + mostSimilarStartWithSubStr);
		System.out.println();
	}

	/**
	 * @METHOD
	 */
	private void inferAddAtTableName() {
		System.out.println("------------------------------------------");
		System.out.println("*");
		System.out.println("*    [ Infer @Table(name = \"?\"), name attibute's value ]");
		System.out.println("*");

		ArrayList<String> tableNameList = new ArrayList<String>();
		ArrayList<String> classNameList = new ArrayList<String>();

		for (int i = 0; i < _handleAtAst.getAtTableMapList().size(); i++) {
			TableMap elem = _handleAtAst.getAtTableMapList().get(i);
			tableNameList.add(elem.getNameOfTable());
			classNameList.add(elem.getNameOfMapClass());
		}

		RelationShip findRelationship = new RelationShip();
		ArrayList<String> relationshipList = new ArrayList<String>();
		HashMap<String, Integer> relationshipMap = new HashMap<String, Integer>();

		StringBuilder sbuf1 = new StringBuilder();
		ArrayList<String> display4List = new ArrayList<String>();

		// * Find the relationship between two words: table name and full class name.
		// *
		for (int i = 0; i < tableNameList.size(); i++) {
			String elem1 = tableNameList.get(i);
			String elem2 = classNameList.get(i);

			String relationship = findRelationship.result(elem1, elem2);

			if (relationshipList.contains(relationship) == false) {
				relationshipList.add(relationship);
			}

			if (relationshipMap.containsKey(relationship)) {
				relationshipMap.put(relationship, (relationshipMap.get(relationship) + 1));
			}
			else {
				relationshipMap.put(relationship, 1);
			}

			// ----------
			String elem = elem1 + STR.delim + elem2;
			sbuf1.setLength(0);
			sbuf1.append(relationship + STR.delim);
			sbuf1.append(elem);
			display4List.add(sbuf1.toString());
		}

		// * Display relationship between two words: class name and table name.
		// *
		String[] columnhead = { "TRANSFORMATION RULE", "TABLE NAME", "CLASS NAME" };
		UtilStr.displayRelationShipBetweenTwoWord(display4List, columnhead, STR.delim);

		System.out.println("*");
		System.out.println("*    [ The recommend list for the invariant candidates ]");
		System.out.println("*");
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("[DBG] Frequency |\t\tTRANSFORMATION RULE");
		System.out.println("----------------+-------------------------------------------------------------------");

		HashMap<String, Integer> map = (HashMap<String, Integer>) UtilMap.sortByValue(relationshipMap);
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			System.out.println("\t" + e.getValue() + "\t| " + e.getKey());
		}
		System.out.println("----------------+-------------------------------------------------------------------");
	}
}
