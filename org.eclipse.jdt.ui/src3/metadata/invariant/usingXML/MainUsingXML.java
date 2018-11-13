/*
 * @(#) Main.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingXML;

import java.util.ArrayList;

import metadata.invariant.pbse.Comm;
import metadata.invariant.usingXML.infer.AddAtColumnByXML;
import metadata.invariant.usingXML.infer.AddAtTableByXML;

/**
 * @author Myoungkyu Song
 * @date Dec 27, 2010
 * @since JDK1.6
 */
public class MainUsingXML {
	final int			success					= 0;
	final String		hibernateCfgFile		= "hibernate.cfg.xml";
	final String		mappingElement			= "mapping";
	final String		resource					= "resource";
	final String		arguments_list_file	= "args_for_xml.txt";

	ArrayList<String>	_testdirlist			= null;

	private Comm		_refComm					= null;
	HandleXML			_handleXML				= null;
	AddAtTableByXML	_refAddAtTable			= null;
	AddAtColumnByXML	_refAddAtColumn		= null;

	public static void main(String[] args) {
		new MainUsingXML().main();
	}

	public MainUsingXML() {
		_testdirlist = new ArrayList<String>();
		_refComm = new Comm();
		_handleXML = new HandleXML();
		_refAddAtTable = new AddAtTableByXML(_handleXML);
		_refAddAtColumn = new AddAtColumnByXML(_handleXML);
	}

	/**
	 * @METHOD
	 */
	private int main() {
		// * Read arguments as test directories.
		// *
		_refComm.readArgs(_testdirlist, arguments_list_file);

		// * Infer adding @Table annotation.
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

			// *
			// *
			_handleXML.handle(dir);

			// *
			// *
			_refAddAtTable.infer();

			// *
			// *
			_refAddAtColumn.infer();
		}
	}
}
