/*
 * @(#) HandleXML.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingXML;

import java.io.File;
import java.util.ArrayList;

import metadata.invariant.pbse.STR;
import util.UtilDirScan;
import util.UtilFile;
import util.UtilSAX;


/**
 * @author Myoungkyu Song
 * @date Jan 6, 2011
 * @since JDK1.6
 */
public class HandleXML {

	ArrayList<File>	_allXMLFiles	= null, _hibernateXMLFiles = null;
	ArrayList<File>	_hibernateMapXMLFiles	= null, _hibernateMapJavaFiles = null;
	ArrayList<String>	_testdirlist				= null;
	ArrayList<String>	_pojoClassNameList		= null, _classNameSubStrList = null;
	ArrayList<String>	_pojoTableNameList		= null, _pojoColumnMapList = null;

	public HandleXML() {
		_hibernateXMLFiles = new ArrayList<File>();
		_hibernateMapXMLFiles = new ArrayList<File>();
		_hibernateMapJavaFiles = new ArrayList<File>();

		_pojoClassNameList = new ArrayList<String>();
		_pojoTableNameList = new ArrayList<String>();
		_pojoColumnMapList = new ArrayList<String>();

		_classNameSubStrList = new ArrayList<String>();
	}

	/**
	 * @METHOD
	 */
	private void init() {
		_hibernateXMLFiles.clear();
		_hibernateMapXMLFiles.clear();
		_hibernateMapJavaFiles.clear();

		_pojoClassNameList.clear();
		_pojoTableNameList.clear();

		_classNameSubStrList.clear();
	}

	public void handle(String dirPath) {

		init();

		// * Get all XML files within the given package.
		// *
		getAllXMLFiles(dirPath);

		// * Get hibernate XML files within the given package.
		// *
		getHibernateXMLFiles();

		// * Get hibernate DB mapping class within the given package.
		// *
		findHibernateMappingClass();
	}

	/**
	 * @METHOD
	 */
	private void getAllXMLFiles(String testdir) {
		_allXMLFiles = UtilDirScan.getResult(testdir, ".xml");
		System.out.println();
		System.out.println("[DBG] The size of all XML: " + _allXMLFiles.size());
	}

	/**
	 * @METHOD
	 */
	public void getHibernateXMLFiles() {

		for (int i = 0; i < _allXMLFiles.size(); i++) {
			File elem = _allXMLFiles.get(i);
			if (elem.getPath().toString().endsWith(".hbm.xml")) {
				_hibernateXMLFiles.add(elem);
			}
		}
		System.out.println("[DBG] The size of hibernate mapping XML : " + _hibernateXMLFiles.size());
	}

	/**
	 * @METHOD
	 */
	private void findHibernateMappingClass() {
		// * Parse XML
		// *
		UtilSAX utilSAX = new UtilSAX();

		for (int i = 0; i < _hibernateXMLFiles.size(); i++) {
			File elem = _hibernateXMLFiles.get(i);

			if (UtilFile.contains("<class", elem)) {
				utilSAX.getValue(elem.getPath());
			}
		}
		System.out.println();

		// * Get parsed data from 'UtilSAX' reference.
		// *
		ArrayList<String> tableMapList = utilSAX.getTableMapList();

		for (int i = 0; i < tableMapList.size(); i++) {
			String elem = tableMapList.get(i).trim();
			String[] tokens = elem.split(",");

			String xmlfileName = tokens[0];
			String className = tokens[1];
			String tableName = tokens[2];

			// * Get table mapping information.
			// *
			_hibernateMapXMLFiles.add(new File(xmlfileName));
			_hibernateMapJavaFiles.add(new File(xmlfileName.replace(".hbm.xml", STR.file_java)));
			_pojoClassNameList.add(className);
			_pojoTableNameList.add(tableName);
		}

		// * Get column mapping information.
		// *
		_pojoColumnMapList = utilSAX.getColumnMapList();

		System.out.println();
		System.out.println("[DBG] The size of hibernate mapping class (POJO): " + _hibernateMapXMLFiles.size() + "/" + _allXMLFiles.size());
		System.out.println("------------------------------------------");
	}

	public ArrayList<File> getHibernateMapXMLFiles() {
		return _hibernateMapXMLFiles;
	}

	public ArrayList<File> getHibernateMapJavaFiles() {
		return _hibernateMapJavaFiles;
	}

	public ArrayList<String> getPojoTableNameList() {
		return _pojoTableNameList;
	}

	public ArrayList<String> getPojoColumnMapList() {
		return _pojoColumnMapList;
	}

	public ArrayList<String> getPojoClassNameList() {
		return _pojoClassNameList;
	}

	public ArrayList<String> getClassNameSubStrList() {
		return _classNameSubStrList;
	}
}
