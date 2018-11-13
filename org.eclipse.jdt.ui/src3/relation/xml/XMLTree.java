/*
 * @(#) XMLTree.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Myoungkyu Song
 * @date Jul 20, 2011
 * @since JDK1.6
 */
public class XMLTree {
	public String			xmlfile;
	public String			base;
	public List<String>	others;

	public XMLTree() {
		others = new ArrayList<String>();
	}

	public XMLTree(String xmlfile) {
		this();
		this.xmlfile = xmlfile;
	}

	public XMLTree(String packaze, String clazz, String base, List<String> others) {
		this();
		this.base = base;
		this.others = others;
	}

	public String toString() {
		return xmlfile + "\n\t" + base + ": " + others.toString();
	}
}
