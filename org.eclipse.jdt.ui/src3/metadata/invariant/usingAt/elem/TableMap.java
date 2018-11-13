/*
 * @(#) TableMap.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingAt.elem;

import java.io.File;

/**
 * @author Myoungkyu Song
 * @date Jan 3, 2011
 * @since JDK1.6
 */
public class TableMap {
	File		_file;
	String	_nameOfTable;
	String	_nameOfMapClass;
	String	_nameOfPackage;

	public TableMap(File file, String nameOfTable, String nameOfMapClass, String nameOfPackage) {
		_file = file;
		_nameOfTable = nameOfTable;
		_nameOfMapClass = nameOfMapClass;
		_nameOfPackage = nameOfPackage;
	}

	public File getFile() {
		return _file;
	}

	public String getNameOfTable() {
		return _nameOfTable;
	}

	public String getNameOfMapClass() {
		return _nameOfMapClass;
	}

	public String getNameOfPackage() {
		return _nameOfPackage;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_file.getPath() + "\n\t" + "Package: ");
		sb.append(_nameOfPackage + "\n\t" + "Table: ");
		sb.append(_nameOfTable + "\n\t" + "Class: ");
		sb.append(_nameOfMapClass);
		return sb.toString();
	}
}
