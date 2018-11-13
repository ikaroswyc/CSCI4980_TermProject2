/*
 * @(#) MainUsingAt.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingAt;

import java.util.ArrayList;

import metadata.invariant.pbse.Comm;
import metadata.invariant.usingAt.infer.AddAtColumn;
import metadata.invariant.usingAt.infer.AddAtTable;
import util.UtilLog;

/**
 * @author Myoungkyu Song
 * @date Jan 2, 2011
 * @since JDK1.6
 */
public class MainUsingAt {

	public static void main(String[] args) {
		UtilLog.set(UtilLog.__ALL);
		new MainUsingAt().main();
	}

	final private String			arguments_list_file	= "args_for_annotation.txt";
	final private int				success					= 1;
	private ArrayList<String>	_testdirlist			= null;

	private Comm					_refComm					= null;
	private HandleAtAST			_handleAtAst			= null;
	private AddAtTable			_refAddAtTable			= null;
	private AddAtColumn			_refAddAtColumn		= null;

	public MainUsingAt() {
		_testdirlist = new ArrayList<String>();
		_refComm = new Comm();
		_handleAtAst = new HandleAtAST();
		_refAddAtTable = new AddAtTable(_handleAtAst);
		_refAddAtColumn = new AddAtColumn(_handleAtAst);
	}

	/**
	 * @METHOD
	 */
	private int main() {
		// * - read arguments as test directories.
		// *
		_refComm.readArgs(_testdirlist, arguments_list_file);

		// * - infer adding @Table annotation.
		// *
		infer();

		return success;
	}

	/**
	 * @METHOD
	 */
	private void infer() {

		for (int i = 0; i < _testdirlist.size(); i++) {
			String dir = _testdirlist.get(i);
			System.out.println("==========================================");
			System.out.println();
			System.out.println("[DBG] #" + (i + 1) + " Package: " + dir);
			System.out.println("\n------------------------------------------");

			// * - parse and get all JPA-related annotations.
			// *
			_handleAtAst.handle(dir);

			// * - infer all JPA's @Table-related annotations.
			// *
			_refAddAtTable.infer();

			// * - infer all JPA's @Column-related annotations.
			// *
			_refAddAtColumn.infer();
		}
	}
}
