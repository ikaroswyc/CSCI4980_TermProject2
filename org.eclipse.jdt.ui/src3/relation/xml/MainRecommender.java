/*
 * @(#) MainRecommender.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.xml;

import java.util.HashMap;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.PbsePatternHelper;
import relation.Recommand;
import util.UtilFile;
import util.UtilMap;
import util.UtilPrint;
import dsl.gen.DSLGen;

/**
 * @author Myoungkyu Song
 * @date Jul 17, 2011
 * @since JDK1.6
 */
public class MainRecommender {
	public static void main(String[] args) {
		new MainRecommender();
	}

	String							pathdir;
	String							relationParm[];
	String							relationSavePath;
	Recommand						recommand;
	PbsePatternHelper				pbsePtrnHelperRef;
	DSLGen							pbseGenRef;

	String							_pattern;
	HashMap<String, Integer>	_freqStatMap;

	public MainRecommender() {
		recommand = new Recommand();
		pbsePtrnHelperRef = new PbsePatternHelper(pathdir, relationSavePath);
		pbseGenRef = new DSLGen();

		pathdir = Comm.getParm(STR.parm_path);
		relationParm = Comm.getParm(STR.parm_relation).split(",");
		relationSavePath = Comm.getParm(STR.parm_relation_save);

		// ------------------------------------------
		// * Read the saved file.
		// ------------------------------------------
		List<String> relationClassTableList = UtilFile.fileRead2List(relationSavePath);
		UtilPrint.printArrayList(relationClassTableList);
		System.out.println("------------------------------------------");

		// ------------------------------------------
		// * Create the recommended list.
		// ------------------------------------------
		HashMap<String, Integer> freqNumMap = recommand.createRecommendedList(relationClassTableList);
		freqNumMap = (HashMap<String, Integer>) UtilMap.sortByValue(freqNumMap);
		System.out.println("[Key/Num]");
		UtilPrint.printMapGeneral(freqNumMap);
		System.out.println("------------------------------------------");

		// ------------------------------------------
		// * Compute the statistic of the recommended list.
		// ------------------------------------------
		_freqStatMap = recommand.statRecommendedList(freqNumMap, relationClassTableList.size());
		_freqStatMap = (HashMap<String, Integer>) UtilMap.sortByValue(_freqStatMap);
		_pattern = _freqStatMap.keySet().iterator().next();
		System.out.println("[Key/Percentage]");
		UtilPrint.printMapGeneral(_freqStatMap, " %");
		System.out.println("------------------------------------------");

		// ------------------------------------------
		// * Generate a PBSE program with the first item.
		// ------------------------------------------

		// String[] relationVar = Comm.getParm("relation-var").split(",");
		// pbsePtrnHelperRef.writeRelationClassTable(relationVar, _pattern);
		// pbsePtrnHelperRef.displayPBSE();
		pbseGenRef.pbseGenProc();
		System.out.println("------------------------------------------");
	}

	/** @METHOD */
	public String getPattern() {
		return _pattern;
	}

	/** @METHOD */
	public HashMap<String, Integer> getFreqStatMap() {
		return _freqStatMap;
	}
}
