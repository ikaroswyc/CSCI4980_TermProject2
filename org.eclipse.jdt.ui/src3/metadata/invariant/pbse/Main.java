/*
 * @(#) Main.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse;

import metadata.invariant.pbse.at.AtColumn;
import metadata.invariant.pbse.at.AtJoinTable;
import metadata.invariant.pbse.at.AtTable;
import metadata.invariant.pbse.visitor.Visitor;
import util.UtilAST;
import util.UtilFile;

/**
 * @author Myoungkyu Song & John Edstrom
 * @date Oct 26, 2010
 * @since JDK1.6
 */
public class Main {
	public static void main(String[] args) {
		String fileToRead = STR.file_test + "\\" + "Student.java";
		new Main().main(fileToRead);
	}

	/**
	 * @METHOD
	 */
	public void main(String fileToRead) {

		UtilFile.printStreamBegin(STR.file_test + "\\" + STR.file_jpa_pbse);
		System.out.println("==========================================");
		System.out.println("[DBG] Visiting...");
		System.out.println("------------------------------------------");
		Visitor visitor = UtilAST.startVisit(fileToRead);

		// (1) @Table
		AtTable atTable = new AtTable(visitor);
		atTable.atTableName();

		// (2) @Column
		AtColumn atColumn = new AtColumn(visitor);
		atColumn.atColumnName();

		// (3) @JoinTable.name
		AtJoinTable atJoinTable = new AtJoinTable(visitor);
		atJoinTable.atJoinTableName();

		// (4) @JoinTable.JoinColumns.JoinColumn
		atJoinTable.atJoinTableJoinColumns();

		// (5) @JoinTable.InverseJoinColumns.JoinColumn
		atJoinTable.atJoinTableInverseJoinColumns();

		System.out.println("==========================================");
		UtilFile.printStreamClose();
	}

}
