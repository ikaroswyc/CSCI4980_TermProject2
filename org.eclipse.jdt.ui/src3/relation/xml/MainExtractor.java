/*
 * @(#) MainExtractor.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.xml;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import dsl.Starter;

/**
 * @author Myoungkyu Song
 * @date Jul 16, 2011
 * @since JDK1.6
 */
public class MainExtractor extends Starter {
	public static void main(String[] args) {
		new MainExtractor();
	}

	String			_pathdir;
	String			_xmlsuffix;
	String			_relationParm[];
	String			_relationSavePath;

	RelationInXML	_relation;

	public MainExtractor() {
		super();

		_pathdir = Comm.getParm(STR.parm_path);
		_xmlsuffix = Comm.getParm("xml-suffix");
		_relationParm = Comm.getParm(STR.parm_relation).split(",");
		_relationSavePath = Comm.getParm(STR.parm_relation_save);

		// System.out.println("==========================================");
		// System.out.println("[DBG] Path: " + _pathdir + " (" + UtilFile.fileExists(_pathdir) + ")");
		// System.out.println("[DBG] Suffix: " + _xmlsuffix);
		// System.out.println("[DBG] Relation Parameters: " + UtilStr.arrayToStr(_relationParm));
		// System.out.println("[DBG] Save Relations: " + _relationSavePath);
		// System.out.println("------------------------------------------");

		// * Find the relation and save the result.
		_relation = new RelationInXML(_pathdir, _relationParm, _relationSavePath, _xmlsuffix);
		_relation.findRelationAndSave(_relationSavePath);
		System.out.println("==========================================");
	}
}
