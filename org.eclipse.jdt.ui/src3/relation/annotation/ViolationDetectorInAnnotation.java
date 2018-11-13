/*
 * @(#) ViolationDetectorInAnnotation.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import metadata.invariant.pbse.STR;
import relation.PatternAnalyser;
import relation.type.Configuration;
import relation.type.FieldNameAnnotAttr;
import relation.type.FieldNameClassAnnotAttr;
import relation.type.MethodReturnTypeAnnotationAttrName;
import relation.type.RefRelation;
import relation.type.RelationEnum;
import util.UtilDirScan;
import util.UtilFile;
import util.UtilPrint;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jun 22, 2011
 * @since JDK1.6
 */
public class ViolationDetectorInAnnotation {
	List<File>		_annotatedJavaSrcList;
	List<String>	_annotatedJavaSrcStrList;
	List<String>	_resultlist		= new ArrayList<String>();

	RelationEnum	_curRelation	= RelationEnum.INVALID;

	public ViolationDetectorInAnnotation() {}

	public List<String> getAnnotatedJavaSrcStrList() {
		return _annotatedJavaSrcStrList;
	}

	/** @METHOD */
	public void readTargetJavaSrc(String path) {
		_annotatedJavaSrcList = UtilDirScan.getResult(path, STR.file_java);
		_annotatedJavaSrcStrList = UtilFile.convertStrPath(_annotatedJavaSrcList);
	}

	/** @METHOD */
	public void setCurRelation(String[] tokens) {
		int len = tokens.length;
		if (len >= 3) {
			String combinedStr = tokens[0] + tokens[1];
			if (combinedStr.equalsIgnoreCase(STR.relation_className_annotationName)) {
				
				if (tokens[2].equalsIgnoreCase(STR.at_configuration))
					_curRelation = RelationEnum.CONFIGURATION;
			}
			if (tokens[0].equalsIgnoreCase(STR.relation_method_returntype) &&
					tokens[1].equalsIgnoreCase(STR.relation_annotation_attribute) &&
					tokens[2].equalsIgnoreCase("NAME")) {
				
				_curRelation = RelationEnum.METHOD_RETURNTYPE_ANNOTATION_ATTRIBUTE_NAME;
			}
			if (tokens[0].startsWith(STR.relation_field)
					// tokens[0].equalsIgnoreCase("FIELD_NAME")
					&&
					tokens[1].equalsIgnoreCase(STR.relation_annotation_attribute)) {
				
				_curRelation = RelationEnum.FIELDNAME_ANNOTATTR;
			}
		}
	}

	/**
	 * @METHOD
	 * @param tokens
	 * @param pattern
	 */
	public void findViolation(String pattern, String relation) {
		String[] tokens = UtilStr.trim(relation.split(","));
		setCurRelation(tokens);
		ViolationFinder vf = new ViolationFinder(this);

		switch (_curRelation) {
		case METHOD_RETURNTYPE_ANNOTATION_ATTRIBUTE_NAME:
			MethodReturnTypeAnnotationAttrName mta = new RefRelation("METHOD_RETURNTYPE_ANNOTATION_ATTRIBUTE_NAME");
			vf.findViolation(mta, pattern, relation);
			break;
		case CONFIGURATION:
			Configuration cfg = new RefRelation("METHOD_RETURNTYPE_ANNOTATION_ATTRIBUTE_NAME");
			vf.findViolation(cfg, pattern, relation);
			break;
		case FIELDNAME_ANNOTATTR:
			FieldNameAnnotAttr fna = new RefRelation("FIELDNAME_ANNOTATTR");
			boolean result = vf.findViolation(fna, pattern, relation); 
			
			if (!result) {
				FieldNameClassAnnotAttr fnca = new RefRelation("FIELDNAME_ANNOTATTR");
				vf.findViolation(fnca, pattern, relation);
			}
			break;
		case CLASSNTABLE:
			break;
		case INVALID:
			break;
		}
	}

	/** @METHOD */
	String findViolation(String annotation, String pcName, String pattern) {
		PatternAnalyser patternAnalyzer = new PatternAnalyser();
		Stack<String> ptrnOprs = patternAnalyzer.parsePattern(pattern);

		int size = ptrnOprs.size();
		for (int i = 0; i < size; i++) {
			String theOpr = ptrnOprs.pop();
			// System.out.println("[DBG]    => " + theOpr);

			switch (patternAnalyzer.getPtrnOpr(theOpr)) {
			case ENDSWITH: {
				boolean flag = pcName.endsWith(annotation.replace("@", ""));
				return String.valueOf(flag);
			}
			case MATCH: {
				boolean flag = pcName.equals(annotation);
				return String.valueOf(flag);
			}
			case PREFIX:
				break;
			case UPPERCASE:
				break;
			default:
				break;
			}
		}
		return new String();
	}

	/** @METHOD */
	public String findViolation(String[] annotAttrValues, String[] pcnames, String pattern) {
		PatternAnalyser patternAnalyzer = new PatternAnalyser();
		Stack<String> ptrnOprs = patternAnalyzer.parsePattern(pattern);

		boolean flag = false;
		int size = ptrnOprs.size();
		for (int i = 0; i < size; i++) {
			String theOpr = ptrnOprs.pop();
			// System.out.println("[DBG]    => " + theOpr);

			switch (patternAnalyzer.getPtrnOpr(theOpr)) {
			case MATCH:
				flag = UtilStr.match(annotAttrValues, pcnames);
				break;
			case CONTAINS:
				if (pcnames.length >= annotAttrValues.length)
					flag = UtilStr.contains(pcnames, annotAttrValues);
				else
					flag = false;
				break;
			case ENDSWITH:
				break;
			case PREFIX:
				break;
			case UPPERCASE:
				break;
			default:
				break;
			}
		}
		return String.valueOf(flag);
	}

	/** @METHOD */
	public void displayViolationList() {
		UtilPrint.printArrayList(_resultlist);
	}

	/** @METHOD */
	public void displayViolationList(Configuration parm, HashMap<String, String> violationlist, int javafilecnt) {
		System.out.println("------------------------------------------");
		System.out.println("[DBG] Violatioin List");
		UtilPrint.printMap("[Class Name]", "[Annotation]", violationlist);
		System.out.println("------------------------------------------");
		System.out.println("[DBG]" + javafilecnt + " java files are extracted, and the classes are checked.");
		System.out.println("[DBG]" + violationlist.size() + " files have been violated.");
		System.out.println("------------------------------------------");
	}

	/** @METHOD */
	public void displayViolationList(MethodReturnTypeAnnotationAttrName parm, HashMap<String, List<String>> violationlist, int javafilecnt) {
		// System.out.println("------------------------------------------");
		// System.out.println("[DBG] Violatioin List");
		// UtilPrint.printMap("[Method Return Type]", "[Annotation Attribute Name]", violationlist);
		System.out.println("------------------------------------------");
		System.out.println("[DBG]" + javafilecnt + " java files are extracted, and the classes are checked.");
		System.out.println("[DBG]" + violationlist.size() + " files have been violated.");
		System.out.println("------------------------------------------");
	}

	/** @METHOD */
	public void displayViolationList(FieldNameAnnotAttr parm, HashMap<String, List<String>> violationlist, int javafilecnt, int fieldcnt) {
		System.out.println("------------------------------------------");
		System.out.println("[DBG] Violatioin List");
		System.out.println("------------------------------------------");
		int violatedfieldcnt = 0;
		for (Map.Entry<String, List<String>> e : violationlist.entrySet()) {
			String javafile = e.getKey();
			List<String> fields = e.getValue();
			violatedfieldcnt += fields.size();
			System.out.println(javafile);
			UtilPrint.printArrayList(fields);
		}
		System.out.println("------------------------------------------");
		System.out.print("[DBG]" + javafilecnt + " java files are extracted, and ");
		System.out.println(fieldcnt + " fields are checked.");
		System.out.print("[DBG]" + violationlist.size() + " files have ");
		System.out.println(violatedfieldcnt + " fields violated.");
		System.out.println("------------------------------------------");
	}

}
