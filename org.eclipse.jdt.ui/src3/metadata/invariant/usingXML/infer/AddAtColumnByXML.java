/*
 * @(#) AddAtColumnByXML.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingXML.infer;

import java.io.File;
import java.util.ArrayList;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.transform.RelationShip;
import metadata.invariant.pbse.visitor.ClassVisitor;
import metadata.invariant.usingXML.HandleASTByXML;
import metadata.invariant.usingXML.HandleXML;
import util.UtilAST;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jan 6, 2011
 * @since JDK1.6
 */
public class AddAtColumnByXML {

	HandleXML			_handleXML						= null;
	HandleASTByXML		_handleAST						= new HandleASTByXML();
	ArrayList<String>	_fieldDeclListForColumnMap	= new ArrayList<String>();	;

	public AddAtColumnByXML(HandleXML handleXML) {
		_handleXML = handleXML;
	}

	/**
	 * @METHOD
	 */
	public void infer() {
		// * - infer adding @Column
		// * - get the field declaration of the given class.
		// * - filter and extract only mapping column fields.
		inferAddAtColumn();

		// * - find the relationship between two words: field name and column name.
		//
		inferAddAtColumnName();
	}

	// =======================================================================
	//	
	// --- infer to add '@Column' annotation to the field declaration.
	//	
	// =======================================================================

	/**
	 * @METHOD
	 * - infer adding @Column.
	 * - get the field declaration of the givenclass.
	 * - filter and extract only mapping column fields.
	 */
	private void inferAddAtColumn() {
		System.out.println("*");
		System.out.println("*    [ Infer adding @Column annotation ]");
		System.out.println("*");

		// * - find and add all field declarations within the each class.
		// *
		findAddFieldDeclAST();

		// * - debug msg
		// * _handleAST.displayAST();
		// *

		// * - find the field declaration for mapping column.
		// *
		findFieldDeclUsingPojoColumnMap();

		// * - find the most similar start-with-sub-string among the mapping
		// field declaration.
		// *
		findMostSimilarStartWithSubStr();
	}

	/**
	 * @METHOD
	 */
	public void findAddFieldDeclAST() {
		ClassVisitor visitor = new ClassVisitor();

		ArrayList<File> hibernateMapJavaFiles = _handleXML.getHibernateMapJavaFiles();
		// * - From each given file reference, the each field declaration is
		// extracted by using AST visitor.
		// *
		for (int i = 0; i < hibernateMapJavaFiles.size(); i++) {

			File f = hibernateMapJavaFiles.get(i);
			UtilAST.startCustomVisit(f.getPath(), visitor);
			ArrayList<String> fieldList = visitor.getFieldList();

			_handleAST.addAST(f, fieldList);
		}
	}

	/**
	 * @METHOD
	 */
	private void findFieldDeclUsingPojoColumnMap() {
		ArrayList<String> pojoColumnMapList = _handleXML.getPojoColumnMapList();
		ArrayList<String> pojoColumnMapList4Display = new ArrayList<String>();

		// * - field declaration list within the given package.
		// *
		_fieldDeclListForColumnMap = new ArrayList<String>();
		// * - append the field declaration to the list.
		// *
		for (int i = 0; i < pojoColumnMapList.size(); i++) {

			String[] tokens = pojoColumnMapList.get(i).split(",");

			// * - get the field declaration with the given class and short
			// field names.
			// *
			String fieldDecl = _handleAST.getFieldDecl(UtilStr.getShortClassName(tokens[0]), tokens[2]);
			_fieldDeclListForColumnMap.add(fieldDecl);
			pojoColumnMapList4Display.add(pojoColumnMapList.get(i) + " [" + fieldDecl + "]");
		}
		// * - display column and field names
		// *
		String[] columnhead = { "CLASS NAME", "COLUMN", "FULL FIELD DECLARATION" };
		UtilStr.displayRelationShipBetweenTwoWord(pojoColumnMapList4Display, columnhead);
	}

	/**
	 * @METHOD
	 */
	private void findMostSimilarStartWithSubStr() {
		ArrayList<String> fieldDeclSubStrList = new ArrayList<String>();
		// * - find the most similar startsWith-substring
		// *
		for (int i = 0; i < _fieldDeclListForColumnMap.size(); i++) {
			String fieldDecl1 = _fieldDeclListForColumnMap.get(i);

			for (int j = 0; j < _fieldDeclListForColumnMap.size(); j++) {
				if (i >= j)
					continue;

				String fieldDecl2 = _fieldDeclListForColumnMap.get(j);

				String result = UtilStr.getMostSimilarStartWithSubStr(fieldDecl1, fieldDecl2);
				fieldDeclSubStrList.add(result);
			}
		}

		int index1 = 0, tmplen = fieldDeclSubStrList.get(0).length();
		// * - find the lest length string in the listbuf.
		// *
		for (int i = 0; i < fieldDeclSubStrList.size(); i++) {

			int elem = fieldDeclSubStrList.get(i).length();
			if (elem < tmplen) {
				index1 = i;
				tmplen = elem;
			}
		}
		System.out.println();
		System.out.println("[DBG] \t=>\t" + fieldDeclSubStrList.get(index1));
		System.out.println();
	}

	// =======================================================================
	//	
	// --- infer to the colunm name, such as @Column(name = "?").
	//	
	// =======================================================================
	
	/**
	 * @METHOD
	 */
	private void inferAddAtColumnName() {
		System.out.println("------------------------------------------");
		System.out.println("*");
		System.out.println("*    [ Infer @Column(name = \"?\"), name attibute's value ]");
		System.out.println("*");

		ArrayList<String> pojoColumnMapList = _handleXML.getPojoColumnMapList();
		String columnName = null, fieldName = null, relationship = null;
		// *
		RelationShip findRelationship = new RelationShip();
		ArrayList<String> relationshipList = new ArrayList<String>();
		// *
		StringBuilder sbuf1 = new StringBuilder();
		ArrayList<String> display4List = new ArrayList<String>();
		// *
		// *
		for (int i = 0; i < pojoColumnMapList.size(); i++) {
			String[] tokens = pojoColumnMapList.get(i).split(",");
			columnName = tokens[1];
			fieldName = tokens[2];

			relationship = findRelationship.result(columnName, fieldName, STR.var_fieldname);
			if (relationshipList.contains(relationship) == false) {
				relationshipList.add(relationship);
			}

			// ----------
			String elem = columnName + "," + fieldName;
			sbuf1.setLength(0);
			sbuf1.append(relationship + ",");
			sbuf1.append(elem);
			display4List.add(sbuf1.toString());
		}

		// * Display relationship between two words: class name and table name.
		// *
		String[] columnhead = { "TRANSFORMATION RULE", "COLUMN NAME", "FIELD NAME" };
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
