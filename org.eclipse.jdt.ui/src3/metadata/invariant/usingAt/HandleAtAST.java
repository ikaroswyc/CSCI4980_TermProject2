/*
 * @(#) HandleAtAST.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingAt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import metadata.invariant.pbse.STR;
import metadata.invariant.usingAt.elem.ColumnMap;
import metadata.invariant.usingAt.elem.TableMap;
import util.UtilDirScan;
import util.UtilFile;

/**
 * @author Myoungkyu Song
 * @date Jan 3, 2011
 * @since JDK1.6
 */
public class HandleAtAST {
	final String						__AT_TABLE			= STR.at_table;
	final String						__CLASS				= " class ";
	final String						__WORKING_DIR		= "test4\\src\\wfe-3.3-r2885\\wfe\\src\\";

	private ArrayList<File>			_allJavaFiles		= null;
	private ArrayList<File>			_fileHasAtTable	= null;
	private ArrayList<File>			_fileHasAtColumn	= null;
	private ArrayList<TableMap>	_atTableMapList	= null;
	private ArrayList<ColumnMap>	_atColumnMapList	= null;

	public ArrayList<TableMap> getAtTableMapList() {
		return _atTableMapList;
	}

	public ArrayList<ColumnMap> getAtColumnMapList() {
		return _atColumnMapList;
	}

	public HandleAtAST() {
		_fileHasAtTable = new ArrayList<File>();
		_fileHasAtColumn = new ArrayList<File>();
		_atTableMapList = new ArrayList<TableMap>();
		_atColumnMapList = new ArrayList<ColumnMap>();
	}

	/**
	 * @METHOD
	 */
	public void handle(String dirPath) {

		// * - get all java files within the given package.
		// *
		_allJavaFiles = UtilDirScan.getResult(dirPath, STR.file_java);
		System.out.println();
		System.out.println("[DBG] The size of all Java files: " + _allJavaFiles.size());

		// _fileHaveAtTable = new ArrayList<File>();

		// * - parse and get the JPA mapping java files within the given package (i.e., containing @annotation.)
		// *
		for (int j = 0; j < _allJavaFiles.size(); j++) {
			File elem = _allJavaFiles.get(j);

			boolean hasAtTable = UtilFile.contains(STR.at_table, elem);

			if (hasAtTable) {
				_fileHasAtTable.add(elem);
			}
			if (hasAtTable && UtilFile.contains(STR.at_column, elem)) {
				_fileHasAtColumn.add(elem);
			}
		}
		// * - parse to get @Table-related JPA annotations.
		// *
		handleAtTable();

		// * - parse to get @Column-related JPA annotations.
		// *
		handleAtColumn();
	}

	// ==================================================================
	//
	// -- handle @Table
	//
	// ==================================================================

	/**
	 * @METHOD
	 */
	private void handleAtTable() {
		String line = null, theline = null;
		String nameOfTable = null, nameOfMapClass = null, nameOfPackage = null;
		boolean flagToFindNameOfMapClass = false;

		for (int i = 0; i < _fileHasAtTable.size(); i++) {
			File file = _fileHasAtTable.get(i);
			// System.out.println(file.getPath());

			try {
				BufferedReader input = new BufferedReader(new FileReader(file));
				while ((line = input.readLine()) != null) {

					theline = line.trim();

					if (theline.contains(__AT_TABLE)) {
						// * - find the table name.
						// *
						nameOfTable = findNameOfTable(theline).replace("\"", "").trim();
						flagToFindNameOfMapClass = true;
					}
					else if (flagToFindNameOfMapClass && theline.contains(" class ")) {
						// * - find the mapping class name.
						// *
						nameOfMapClass = findNameOfClass(theline);
						// * - find the package name.
						// *
						nameOfPackage = findNameOfPackage(file.getPath());
						_atTableMapList.add(new TableMap(file, nameOfTable, nameOfMapClass, nameOfPackage));
						flagToFindNameOfMapClass = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[DBG] The size of the files having @Table: " + _fileHasAtTable.size());
	}

	/**
	 * @METHOD
	 */
	private String findNameOfPackage(String path) {
		String result = null;
		result = path.replace(__WORKING_DIR, "").replace(STR.file_java, "").replace("\\", ".");
		return result;
	}

	/**
	 * @METHOD
	 */
	private String findNameOfTable(String theline) {
		String nameOfTable = null;

		int pos1 = theline.indexOf(__AT_TABLE);
		int pos2 = theline.indexOf(")", pos1) + 1;

		theline = theline.substring(pos1, pos2); // * - have @Table annotation.
		nameOfTable = theline.substring(theline.indexOf("=") + 1, theline.indexOf(")")).trim();

		if (nameOfTable.contains(",")) { // * - handle exceptional case.
			nameOfTable = nameOfTable.substring(0, nameOfTable.indexOf(","));
		}
		return nameOfTable;
	}

	/**
	 * @METHOD
	 */
	private String findNameOfClass(String theline) {
		String result = null;
		int pos1 = theline.indexOf(__CLASS);
		int pos2 = theline.indexOf(" ", pos1 + __CLASS.length());
		result = theline.substring(pos1 + __CLASS.length(), pos2);
		return result;
	}

	// ==================================================================
	//
	// -- handle @Column
	//
	// ==================================================================

	/**
	 * @METHOD
	 */
	private void handleAtColumn() {
		ColumnMap columnMap = null;

		for (int i = 0; i < _fileHasAtColumn.size(); i++) {

			File file = _fileHasAtColumn.get(i);

			columnMap = new ColumnMap(file);
			columnMap.setColumnMap(findColumnMap(file));

			_atColumnMapList.add(columnMap);
		}

		// for (int i = 0; i < _atColumnMapList.size(); i++) {
		// ColumnMap elem = _atColumnMapList.get(i);
		// System.out.println(elem);
		// }
	}

	/**
	 * @METHOD
	 */
	private HashMap<String, String> findColumnMap(File file) {

		// System.out.println("[DBG] file: " + file.getName());

		// * - convert file to list
		// *
		ArrayList<String> fileContents = UtilFile.fileRead2List(file.getPath());
		HashMap<String, String> mapOfColumn = new HashMap<String, String>();
		boolean flagToFindNameOfMethod = false;
		String declMethod = null, nameOfColumn = null;

		// * - loop the file body in order to parse JPA-related annotations.
		// *
		for (int j = 0; j < fileContents.size(); j++) {
			String theline = fileContents.get(j).trim();

			// * - find the column name.
			// *
			if (theline.contains(STR.at_column)) {
				nameOfColumn = findNameOfColumn(theline);
				flagToFindNameOfMethod = true;
			}
			// * - find the method name including the column annotation.
			// *
			else if (flagToFindNameOfMethod && theline.contains("{")) {
				declMethod = findNameOfMethod(fileContents, j);
				int pos1 = declMethod.lastIndexOf("(");

				declMethod = declMethod.substring(0, pos1).trim();
				// theNameOfMethod = theNameOfMethod.substring(pos2, pos1).trim();
				// System.out.println("[DBG] \t" + declMethod);

				// * - list-up the column and mehtod names.
				// *
				mapOfColumn.put(nameOfColumn, declMethod);
				flagToFindNameOfMethod = false;
			}
		}
		return mapOfColumn;
	}

	/**
	 * @METHOD
	 */
	private String findNameOfMethod(ArrayList<String> fileContents, int j) {
		String result = null;

		for (int i = j; i > -1; i--) {
			String theline = fileContents.get(i);

			if (theline.contains("(")) {
				result = theline;
				break;
			}
		}
		return result;
	}

	/**
	 * @METHOD
	 */
	private String findNameOfColumn(String theline) {
		int pos1 = theline.indexOf("\"");
		int pos2 = theline.indexOf("\"", pos1 + 1);
		String nameOfColumn = theline.substring(pos1 + 1, pos2);
		return nameOfColumn;
	}
}
