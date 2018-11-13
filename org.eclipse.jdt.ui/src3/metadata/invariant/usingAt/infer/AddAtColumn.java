/*
 * @(#) AddAtColumn.java
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
import metadata.invariant.usingAt.elem.ColumnMap;

import org.eclipse.core.runtime.Assert;

import util.UtilMap;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jan 3, 2011
 * @since JDK1.6
 */
public class AddAtColumn {

	String		_methodModifier	= null, _methodReturnType = null, _methodShortName = null;
	HandleAtAST	_handleAtAst		= null;

	public AddAtColumn(HandleAtAST handleAtAst) {
		_handleAtAst = handleAtAst;
	}

	/**
	 * @METHOD
	 */
	public void infer() {
		// * - infer adding @Column
		// * - get the field declaration of the given class.
		// * - filter and extract only mapping column fields.
		System.out.println("*");
		System.out.println("*    [ Infer adding @Column annotation ]");
		System.out.println("*");
		inferAddAtColumn();

		// * - find the relationship between two words: field name and column name.
		//
		System.out.println();
		System.out.println("*");
		System.out.println("*    [ Infer @Column(name = \"?\"), name attibute's value ]");
		inferAddAtColumnName();
	}

	// =============================================================
	//
	// -- infer to add '@Column' to the method declaration.
	// 
	// =============================================================

	/**
	 * @METHOD
	 */
	private void inferAddAtColumn() {
		ArrayList<ColumnMap> list = _handleAtAst.getAtColumnMapList();
		ArrayList<String> list4display = new ArrayList<String>();
		ArrayList<String> listMethodModifer = new ArrayList<String>();
		ArrayList<String> listMethodName = new ArrayList<String>();

		StringBuilder sbuf1 = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			// File file = list.get(i).getFile();
			// System.out.println("[DBG] file name: " + file.getName());

			HashMap<String, String> columnMap = list.get(i).getColumnMap();

			for (Map.Entry<String, String> e : columnMap.entrySet()) {
				String declOfMethod = e.getValue();

				sbuf1.setLength(0);
				findMethodDecl(declOfMethod, sbuf1);
				listMethodModifer.add(sbuf1.toString().split(STR.delim)[0]);
				listMethodName.add(sbuf1.toString().split(STR.delim)[2]);
				list4display.add(sbuf1.toString());
			}
		}
		System.out.println("------------------------------------------");

		// * - find the most similar startsWith-substring which is ranked to recommend.
		// *
		HashMap<String, Integer> methodModiferRank = findRankMethodModifier(listMethodModifer);
		HashMap<String, Integer> methodNameRank = findRankMethodName(listMethodName);

		String mostFreqMethodModifier = null, mostFreqMethodName = null;

		mostFreqMethodModifier = methodModiferRank.entrySet().iterator().next().getKey();
		mostFreqMethodName = methodNameRank.entrySet().iterator().next().getKey();

		System.out.println("[DBG]\t=>\t" + mostFreqMethodModifier + " " + mostFreqMethodName);
	}

	/**
	 * @METHOD
	 */
	private void findMethodDecl(String declOfMethod, StringBuilder sbuf) {
		int pos1 = declOfMethod.indexOf(" ");
		int pos2 = declOfMethod.lastIndexOf(" ");

		Assert.isTrue(pos1 > -1);
		Assert.isTrue(pos2 > -1);

		sbuf.append(declOfMethod.substring(0, pos1) + STR.delim);
		sbuf.append(declOfMethod.substring(pos1, pos2) + STR.delim);
		sbuf.append(declOfMethod.substring(pos2));
	}

	/**
	 * @METHOD
	 */
	private HashMap<String, Integer> findRankMethodModifier(ArrayList<String> listMethodModifer) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for (int i = 0; i < listMethodModifer.size(); i++) {
			String elem = listMethodModifer.get(i);
			if (map.containsKey(elem)) {
				Integer val = map.get(elem) + 1;
				map.put(elem, val);
			}
			else {
				map.put(elem, 1);
			}
		}
		map = (HashMap<String, Integer>) UtilMap.sortByValue(map);

		return map;
	}

	/**
	 * @METHOD
	 */
	private HashMap<String, Integer> findRankMethodName(ArrayList<String> listMethodName) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for (int i = 0; i < listMethodName.size(); i++) {
			String methodName1 = listMethodName.get(i);

			for (int j = 0; j < listMethodName.size(); j++) {
				if (i >= j)
					continue;

				String methodName2 = listMethodName.get(j);

				String result = UtilStr.getMostSimilarStartWithSubStr(methodName1, methodName2);
				if (map.containsKey(result)) {
					Integer val = map.get(result) + 1;
					map.put(result, val);
				}
				else {
					map.put(result, 1);
				}
			}
		}
		map = (HashMap<String, Integer>) UtilMap.sortByValue(map);
		return map;
	}

	// =============================================================
	//
	// -- infer to add the name of column, such as @Column(name="?").
	// 
	// =============================================================

	/**
	 * @METHOD
	 */
	private void inferAddAtColumnName() {
		ArrayList<ColumnMap> list = _handleAtAst.getAtColumnMapList();

		ArrayList<String> listOfMethod = new ArrayList<String>();
		ArrayList<String> listOfColumn = new ArrayList<String>();
		ArrayList<String> relationshipList = new ArrayList<String>();
		ArrayList<String> display4List = new ArrayList<String>();

		RelationShip findRelationship = new RelationShip();
		HashMap<String, Integer> relationshipMap = new HashMap<String, Integer>();

		StringBuilder sbuf1 = new StringBuilder();
		String nameOfMethod = null, nameOfColumn = null;

		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> columnMap = list.get(i).getColumnMap();

			for (Map.Entry<String, String> e : columnMap.entrySet()) {
				sbuf1.setLength(0);
				String declOfMethod = e.getValue();
				findMethodDecl(declOfMethod, sbuf1);

				nameOfMethod = sbuf1.toString().split(STR.delim)[2].trim();
				nameOfColumn = e.getKey().trim();

				listOfMethod.add(nameOfMethod);
				listOfColumn.add(nameOfColumn);

				String relationship = findRelationship.result(nameOfColumn, nameOfMethod, STR.var_fieldname);

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
				sbuf1.setLength(0);
				sbuf1.append(relationship + STR.delim + nameOfColumn + STR.delim + nameOfMethod);
				display4List.add(sbuf1.toString());
			}
		}
		// * Display relationship between two words: class name and table name.
		// *
		inferAddAtColumnName_print(display4List, relationshipMap);
	}

	private void inferAddAtColumnName_print(ArrayList<String> display4List, HashMap<String, Integer> relationshipMap) {
		String[] columnhead = { "TRANSFORMATION RULE", "COLUMN NAME", "FIELD NAME" };
		UtilStr.displayRelationShipBetweenTwoWord(display4List, columnhead, STR.delim);

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
