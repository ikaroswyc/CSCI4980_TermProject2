/*
 * @(#) AtJoinTableInverseJoinColumns.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.at;

import java.util.ArrayList;

/**
 * @author Myoungkyu Song
 * @date Nov 2, 2010
 * @since JDK1.6
 * @Example
 * -- AtJoinTableJoinColumns ..> AtJoinColumnName
 * -- AtJoinTableInverseJoinColumns ..> AtJoinColumnName
 */
public class AtJoinTableInverseJoinColumns {
	private final String						annotation	= "@JoinTable";
	private ArrayList<AtJoinColumnName>	joinColumns	= new ArrayList<AtJoinColumnName>();

	public AtJoinTableInverseJoinColumns(ArrayList<AtJoinColumnName> joinCols) {
		this.joinColumns = joinCols;
	}

	public String getAnnotation() {
		return annotation;
	}

	public ArrayList<AtJoinColumnName> getJoinColumns() {
		return joinColumns;
	}

}
