/*
 * @(#) MainOmissionFinder.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation;

import java.util.List;
import java.util.Stack;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.PatternAnalyser;
import relation.PbsePatternHelper;
import relation.annotation.ctrl.CtrlClazz;
import relation.progelem.AnnotatedMethodDecl;
import util.UtilAST;
import util.UtilDirScan;
import util.UtilStr;
import dsl.gen.DSLGen;

/**
 * @author Myoungkyu Song
 * @date Jul 28, 2011
 * @since JDK1.6
 */
public class MainOmissionFinder {
	private MainRecommender		recommenderRef;
	private PbsePatternHelper	pbsePtrnHelperRef;
	private DSLGen				pbseGenRef;
	private CtrlClazz				clazzRef;

	String							pathToInspect;
	List<String>					javafilesToInspect;
	List<AnnotatedMethodDecl>	allMethodToInspect;

	String							omissionWrnMsg	= "[DBG] The Method (%s/%s) needs %s Annotation.";

	/** @METHOD */
	public static void main(String[] args) {
		System.out.println("==========================================");
		new MainOmissionFinder();
		System.out.println("==========================================");
	}

	/** @METHOD */
	public MainOmissionFinder() {
		// String relation = Comm.getParm(STR.parm_relation);
		// String tokens[] = UtilStr.trim(relation.split(","));

		recommenderRef = new MainRecommender(); // (tokens, tokens[0]);
		pbsePtrnHelperRef = new PbsePatternHelper(); 
		pbseGenRef = new DSLGen();
		clazzRef = new CtrlClazz();

		/** Get the target files to inspect. */
		pathToInspect = Comm.getParm(STR.parm_inspection_target);
		javafilesToInspect = UtilDirScan.getResultViaStr(pathToInspect, STR.file_java);

		/** Write PBSE. */
		// pbsePtrnHelperRef.writePBSE(recommenderRef);
		pbseGenRef.pbseGenProc();
		System.out.println("------------------------------------------");

		/** Main module */
		checkOmissionStart();
	}

	/** @METHOD */
	private void checkOmissionStart() {
		/** Detect omissioin */
		String pattern = recommenderRef.getPattern();
		List<String> patterns = pbsePtrnHelperRef.getElements(pattern);

		if (patterns.contains("$METHOD")) {
			allMethodToInspect = UtilAST.getMethodList(javafilesToInspect);
			clazzRef.setAllMethodToInspect(allMethodToInspect);
		}
		PatternAnalyser patternAnalyzer = new PatternAnalyser();
		Stack<String> ptrnOprs = patternAnalyzer.parsePattern(pattern);

		/** Operations */
		for (int j = 0; j < ptrnOprs.size(); j++) {
			switch (patternAnalyzer.getPtrnOpr(ptrnOprs.pop())) {
			case CONTAINS:
				checkOmission();
				break;
			default:
				break;
			}
		}
	}

	/** @METHOD */
	private void checkOmission() {
		int cntOmission = 0;
		int totalMethod = 0;

		String pattern = recommenderRef.getPattern();
		List<String> patterns = pbsePtrnHelperRef.getElements(pattern);

		/** Check all java files */
		for (int i = 0; i < javafilesToInspect.size(); i++) {
			String javafile = javafilesToInspect.get(i);

			/** Check the methods within the java file. */
			if (patterns.contains("$METHOD")) {
				List<AnnotatedMethodDecl> methodsToInspect = UtilAST.getMethodList(javafile);

				for (int j = 0; j < methodsToInspect.size(); j++) {
					AnnotatedMethodDecl method = methodsToInspect.get(j);

					/** Check $var whether to contain */
					if (checkOmission4Method(method, patterns)) {
						cntOmission++;
					}
					totalMethod++;
				}
			}
		}
		System.out.println("[DBG] Check " + javafilesToInspect.size() + " files.");
		System.out.println("[DBG] Check " + totalMethod + " methods.");
		if (Comm.getParm(STR.parm_relation).split(",").length > 1) {
			String theAnn = Comm.getParm(STR.parm_relation).split(",")[2];
			System.out.println("[DBG] " + theAnn + " Omission: " + cntOmission + " methods.");
		}
	}

	/**
	 * @return
	 * @METHOD
	 */
	boolean checkOmission4Method(AnnotatedMethodDecl pMethod, List<String> patterns) {
		String relation = Comm.getParm(STR.parm_relation);
		String relationElems[] = relation.split(",");
		String metadata = relationElems[1].trim(); // XML or Java 5 Annotation
		String theAnnotation = relationElems[2].trim();

		/** Check superclass whether to contain */
		if (patterns.contains("$SUPERCLASS")) {

			if (UtilStr.isNull(pMethod.superclazz)) {
				return false;
			}
			clazzRef.setClazz(pMethod.superclazz);

			if (clazzRef.containMethod(pMethod) && metadata.equals("ANNOTATION")) {

				if (pMethod.markerAnnotationList.contains(theAnnotation) == false) {
					System.out.println(String.format(omissionWrnMsg, pMethod.name, pMethod.clazz, theAnnotation));
					System.out.println("------------------------------------------");
					return true;
				}
			}
		}
		return false;
	}
	// ]]] END-of-CLASS "MainOmissionFinder"
}
