/*
 * @(#) MainRecommender.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.Recommand;
import relation.annotation.ctrl.CtrlField;
import relation.annotation.match.MethodMatcher;
import relation.progelem.AnnotatedMethodDecl;
import relation.type.RelationEnum;
import util.UtilAST;
import util.UtilDirScan;
import util.UtilFile;
import util.UtilMap;
import util.UtilPrint;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jun 29, 2011
 * @since JDK1.6
 */
public class MainRecommender {
	private final String					path;
	private RelationEnum					curRelation;

	private Recommand						recommand;
	private ASTAccessor					relationWAnno;
	private MethodMatcher				methodMatcherRef;

	private String							pattern;

	private HashMap<String, Integer>	freqNumMap;
	private HashMap<String, Integer>	freqStatMap;

	HashMap<String, Integer> getFreqNumMap() {
		return freqNumMap;
	}

	HashMap<String, Integer> getFreqStatMap() {
		return freqStatMap;
	}

	MethodMatcher getMethodMatcherRef() {
		return methodMatcherRef;
	}

	public MainRecommender() {
		path = Comm.getParm(STR.parm_path);
		recommand = new Recommand();
		relationWAnno = new ASTAccessor();

		String relation = Comm.getParm(STR.parm_relation);
		String tokens[] = UtilStr.trim(relation.split(","));
		String firstToken = tokens[0].trim();

		// [[ CASE 1 ]]
		if (firstToken.toUpperCase().startsWith(STR.relation_method)) {
			methodHandler(tokens);
		}
		// [[ CASE 2 ]]
		else if (firstToken.toUpperCase().startsWith("CLASS")) {
			classHandler(tokens);
		}
		// [[ CASE 3 ]]
		else if (firstToken.toUpperCase().startsWith(STR.relation_field)) {
			fieldHandler(tokens);
		}
		System.out.println("==========================================");
	}

	// /**
	// * @param tokens
	// * @param firstToken
	// * @METHOD
	// */
	// public MainRecommender(String[] tokens, String firstToken) {
	// this();
	//
	// // [[ CASE 1 ]]
	// if (firstToken.toUpperCase().startsWith(STR.relation_method)) {
	// methodHandler(tokens);
	// }
	// // [[ CASE 2 ]]
	// else if (firstToken.toUpperCase().startsWith("CLASS")) {
	// classHandler(tokens);
	// }
	// // [[ CASE 3 ]]
	// else if (firstToken.toUpperCase().startsWith(STR.relation_field)) {
	// fieldHandler(tokens);
	// }
	// System.out.println("==========================================");
	// }

	/** @METHOD */
	void methodHandler(String[] tokens) {
		// ===============================================================
		// Since it takes too much time, I use this log file temporalily.
		// ===============================================================
		boolean enablelogfile = Boolean.valueOf(Comm.getParm("enable-logfile"));
		if (enablelogfile && UtilFile.fileExists("log/pattern.log")) {
			this.pattern = UtilFile.fileRead(new File("log/pattern.log"));
		}
		else {
			List<String> relationlist = getRelations(tokens);
			UtilPrint.printArrayList(relationlist);
			// ===============================================================
			// * Handle annotated Java sources which have been grep.
			// ===============================================================
			handleAnnotationJavaSrc(relationlist);

			UtilFile.fileWrite("log/pattern.log", this.pattern);
		}
	}

	/** @METHOD */
	void classHandler(String[] tokens) {
		List<String> annotatedJavaClassList = relationWAnno.getAnnotatedJavaClassList(path);
		UtilPrint.printArrayList(annotatedJavaClassList);
		System.out.println("------------------------------------------");

		// * Handle annotated Java sources which have been grep.
		handleAnnotationJavaSrc(annotatedJavaClassList, tokens);
	}

	/** @METHOD */
	void fieldHandler(String[] tokens) {
		List<String> relationlist = getRelations(tokens);
		UtilPrint.printArrayList(relationlist);
		System.out.println("------------------------------------------");

		// * Handle annotated Java sources which have been grep.
		handleAnnotationJavaSrc(relationlist);
	}

	/** @METHOD */
	private List<String> getRelations(String[] tokens) {
		String lparm = tokens[0].trim();
		String rparm = tokens[1].trim();

		List<String> relationList = new ArrayList<String>();
		List<String> javafileList = UtilDirScan.getResultViaStr(path, STR.file_java);

		//System.out.println("[DBG] " + javafileList.size() + " Files Checked");

		if (lparm.toUpperCase().startsWith(STR.relation_method)) {
			// [[ CASE ]]
			if (lparm.equalsIgnoreCase(STR.relation_method_returntype) && rparm.equalsIgnoreCase(STR.relation_annotation_attribute)) {
				List<AnnotatedMethodDecl> methodlist = UtilAST.getMethodList(javafileList);
				List<String[]> pairlist = UtilAST.getASTPair("METHOD_RTYPE", "ANNOTATION_ATTR", methodlist);
				relationList = UtilAST.getRelations(pairlist, tokens);
				curRelation = RelationEnum.METHOD_RETURNTYPE_ANNOTATION_ATTRIBUTE_NAME;
			}
			else if (lparm.equalsIgnoreCase(STR.relation_method) && rparm.equalsIgnoreCase("ANNOTATION")) {
				// [[ CASE ]]
				String markerAnnot = tokens[2];
				methodMatcherRef = new MethodMatcher();
				relationList = methodMatcherRef.getRelations(markerAnnot, javafileList);
			}
		}
		else if (lparm.toUpperCase().startsWith(STR.relation_field)) {
			// [[ CASE ]]
			if (rparm.equalsIgnoreCase(STR.relation_annotation_attribute)) {
				CtrlField ctrlfield = new CtrlField();
				relationList = ctrlfield.getRelations();
				curRelation = RelationEnum.FIELDNAME_ANNOTATTR;
			}
		}
		return relationList;
	}

	/** @METHOD */
	void handleAnnotationJavaSrc(List<String> annotatedJavaClassList, String[] arrayElem) {
		// * Find the relation between the program construct and the corresponding metadata.
		curRelation = relationWAnno.setRelation(arrayElem);
		List<String> relationList = relationWAnno.findRelation(annotatedJavaClassList);
		UtilPrint.printArrayList(relationList);
		System.out.println("------------------------------------------");

		// * Create the recommended list.
		HashMap<String, Integer> freqNumMap = recommand.createRecommendedList(relationList);
		freqNumMap = (HashMap<String, Integer>) UtilMap.sortByValue(freqNumMap);
		System.out.println("[Key/Num]");
		UtilPrint.printMapGeneral(freqNumMap);
		System.out.println("------------------------------------------");

		// * Compute the statistic of the recommended list.
		HashMap<String, Integer> freqStatMap = recommand.statRecommendedList(freqNumMap, relationList.size());
		freqStatMap = (HashMap<String, Integer>) UtilMap.sortByValue(freqStatMap);
		pattern = freqStatMap.keySet().iterator().next();

		System.out.println("[Key/Percentage]");
		UtilPrint.printMapGeneral(freqStatMap);
		String line = "------------------------------------------\n";
		line += "RECOMMENDATION: ";
		System.out.println("[DBG]" + line + pattern);
	}

	/** @METHOD */
	private void handleAnnotationJavaSrc(List<String> relationList) {
		// * Create the recommended list.
		freqNumMap = recommand.createRecommendedList(relationList);
		freqNumMap = (HashMap<String, Integer>) UtilMap.sortByValue(freqNumMap);
		System.out.println("[Key/Num]");
		UtilPrint.printMapGeneral(freqNumMap);
		System.out.println("------------------------------------------");

		// * Compute the statistic of the recommended list.
		freqStatMap = recommand.statRecommendedList(freqNumMap, relationList.size());
		freqStatMap = (HashMap<String, Integer>) UtilMap.sortByValue(freqStatMap);
		pattern = freqStatMap.keySet().iterator().next();

		System.out.println("[Key/Percentage]");
		UtilPrint.printMapGeneral(freqStatMap);
		System.out.println("------------------------------------------");
		System.out.println("[DBG] RECOMMENDATION: ");
		System.out.println("[DBG]" + pattern);
	}

	/** @METHOD */
	public String getPattern() {
		return pattern;
	}

	/** @METHOD */
	public RelationEnum getCurRelation() {
		return curRelation;
	}

	/** @METHOD */
	public static void main(String[] args) {
		// String relation = Comm.getParm(STR.parm_relation);
		// String tokens[] = UtilStr.trim(relation.split(","));
		// new MainRecommender(tokens, tokens[0]);
		new MainRecommender();
	}
}
