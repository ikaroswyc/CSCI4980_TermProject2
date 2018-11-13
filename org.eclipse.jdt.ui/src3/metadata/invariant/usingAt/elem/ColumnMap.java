/*
 * @(#) ColumnMap.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingAt.elem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Myoungkyu Song
 * @date Jan 6, 2011
 * @since JDK1.6
 */
public class ColumnMap {
	private File							_file;
	private String							_className;
	private HashMap<String, String>	_columnMap	= new HashMap<String, String>();

	public ColumnMap(File file) {
		_file = file;
	}

	public File getFile() {
		return _file;
	}

	public void setFile(File file) {
		this._file = file;
	}

	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		this._className = className;
	}

	public HashMap<String, String> getColumnMap() {
		return _columnMap;
	}

	public void setColumnMap(HashMap<String, String> columnMap) {
		this._columnMap = columnMap;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_file.getPath() + "\n");

		for (Map.Entry<String, String> e : _columnMap.entrySet()) {
			sb.append("\t" + e.getKey() + " | " + e.getValue() + "\n");
		}
		return sb.toString();
	}
}
