/*
 * @(#) Main.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.xml;

import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.Recommand;

/**
 * @author Myoungkyu Song
 * @date Jun 6, 2011
 * @since JDK1.6
 */
public class MainViolationFinder {
	String						_pathdir;
	String						_relationParm[];
	String						_relationSavePath;
	String						_inspectTargetPath;

	Recommand					_recommand;
	ViolationDetectorInXML	_violationDetector;

	public static void main(String[] args) {
		new MainViolationFinder();
	}

	public MainViolationFinder() {
		_pathdir = Comm.getParm(STR.parm_path);
		_relationParm = Comm.getParm(STR.parm_relation).split(",");
		_relationSavePath = Comm.getParm(STR.parm_relation_save);
		_inspectTargetPath = Comm.getParm(STR.parm_inspection_target);

		_recommand = new Recommand();
		_violationDetector = new ViolationDetectorInXML(_inspectTargetPath);

		System.out.println("==========================================");
		MainRecommender recommenderRef = new MainRecommender();
		String pattern = recommenderRef.getPattern();

		// * Read all relation and save them.
		_violationDetector.readRelation(_relationParm, Comm.getParm("xml-suffix"));

		List<String> relationList = new ArrayList<String>();;
		int choice = 2; // * alternative way to use a log file.
		switch (choice) {
		case 0:
			_violationDetector.save(_inspectTargetPath);
			break;
		case 1:
			relationList = _violationDetector.read(_inspectTargetPath);
			break;
		case 2:
			relationList = _violationDetector.getRelationList();
			break;
		}

		// * Detect violation.
		List<String> violateList = _violationDetector.findViolation(pattern, relationList, _relationParm);
		System.out.println("[DBG] <<< FIND VIOLATION BASED ON \"" + recommenderRef.getFreqStatMap().get(pattern) + "%\" HIGHEST FREQUENCY >>>");
		// UtilPrint.printArrayList(violateList);

		System.out.println("------------------------------------------");
		List<String> counter = new ArrayList<String>();

		for (int i = 0, cnt = 0; i < violateList.size(); i++) {
			String elem = violateList.get(i);
			if (elem.contains("Location")) {
				System.out.print(cnt++ + ": ");

				if (!counter.contains(elem))
					counter.add(elem);
			}
			System.out.println(elem);
		}
		System.out.println("------------------------------------------");
		System.out.println("[DBG] XML file " + _violationDetector.getXMLfileList().size() + " checked.");
		System.out.println("[DBG] XML file " + counter.size() + " and " +
				", XML item " + (violateList.size() / 2) + " violated.");
		System.out.println("==========================================");
	}
}
