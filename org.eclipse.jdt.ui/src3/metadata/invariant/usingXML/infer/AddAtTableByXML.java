/*
 * @(#) AddAtTable.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingXML.infer;

import java.util.ArrayList;

import metadata.invariant.pbse.transform.RelationShip;
import metadata.invariant.usingXML.HandleXML;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Dec 28, 2010
 * @since JDK1.6
 */
public class AddAtTableByXML {
	final String		hibernateCfgFile	= "hibernate.cfg.xml";
	final String		mappingElement		= "mapping";
	final String		resource				= "resource";

	ArrayList<String>	_testdirlist		= null;

	HandleXML			_handleXML			= null;

	public AddAtTableByXML(HandleXML handleXML) {
		_handleXML = handleXML;
	}

	/**
	 * @METHOD
	 */
	public void infer() {
		System.out.println("*");
		System.out.println("*    [ Infer adding @Table annotation ]");
		System.out.println("*");

		// * Find the most similar startsWith-substring.
		// *
		findMostSimilarStartWithSubStr();

		// * Infer @Table(name = "?"), name attibute's value.
		// *
		inferAddAtTableName();
	}

	/**
	 * @METHOD
	 */
	private void findMostSimilarStartWithSubStr() {

		System.out.println("------------------------------------------");
		System.out.println("[DBG] \t| FULL CLASS NAME");
		System.out.println("--------+---------------------------------");

		// * - to display and list-up the interesting data.  
		for (int i = 0; i < _handleXML.getPojoClassNameList().size(); i++) {
			String elem = _handleXML.getPojoClassNameList().get(i);
			System.out.println("[DBG] \t" + elem);
		}
		System.out.println("--------+---------------------------------");

		// * - find the most similar startsWith-substring.
		// *
		String mostSimilarStartWithSubStr = UtilStr.findMostSimilarStartWithSubStr(_handleXML.getPojoClassNameList());
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

		ArrayList<String> tableNameList = _handleXML.getPojoTableNameList();
		ArrayList<String> classNameList = _handleXML.getPojoClassNameList();

		RelationShip findRelationship = new RelationShip();
		ArrayList<String> relationshipList = new ArrayList<String>();

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

			// ----------
			String elem = elem1 + "," + elem2;
			sbuf1.setLength(0);
			sbuf1.append(relationship + ",");
			sbuf1.append(elem);
			display4List.add(sbuf1.toString());
		}

		// * Display relationship between two words: class name and table name.
		// *
		String[] columnhead = { "TRANSFORMATION RULE", "TABLE NAME", "CLASS NAME" };
		UtilStr.displayRelationShipBetweenTwoWord(display4List, columnhead);

		System.out.println();
		for (int i = 0; i < relationshipList.size(); i++) {
			String elem = relationshipList.get(i);
			if (elem == null)
				continue;
			System.out.println("[DBG] \t=>\t" + elem);
		}
		System.out.println();
		System.out.println("------------------------------------------");
	}
}
