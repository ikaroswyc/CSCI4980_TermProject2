/*
 * @(#) ParameterHelper.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl.gen;

import java.util.Collections;
import java.util.Stack;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.PatternAnalyser;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Aug 11, 2011
 * @since JDK1.6
 */
public class ParameterHelper extends STR {
	String	relationparm	= "";

	public ParameterHelper() {
		relationparm = Comm.getParm("relation");
	}

	/** @METHOD */
	public boolean relationContains(String progConst) {
		if (relationparm.contains(progConst.toUpperCase()) ||
				relationparm.contains(progConst))
			return true;
		return false;
	}

	/** @METHOD */
	public boolean relationVarContains(String elem) {
		String p0 = Comm.getParm("relation-var");
		if (p0.contains(elem.toUpperCase()) ||
				p0.contains(elem) ||
				p0.contains(elem.toLowerCase()))
			return true;
		return false;
	}

	/** @METHOD */
	public String getRelationParameter(String key) {
		String relation = Comm.getParm("relation"); // className, annotationName, @Configuration

		if (relation.split(",").length == 4 && key.equals(parm_annotation)) {
			String annotation = relation.split(",")[3];
			annotation = annotation.replace("@", "");
			return annotation.trim();
		}
		else if (relation.split(",").length > 1 && key.equals(parm_annotation)) {
			String annotation = relation.split(",")[2];
			annotation = annotation.replace("@", "");
			return annotation.trim();
		}
		else if (relation.split(",").length > 1 && key.equals(parm_progconst)) {
			String programConstruct = relation.split(",")[1];
			return programConstruct.trim();
		}
		else if (relation.split(",").length == 3 && key.equals(parm_tag)) {
			String metadataTag = relation.split(",")[1].trim();
			return metadataTag;
		}
		else if (relation.split(",").length == 3 && key.equals(parm_attr)) {
			String metadataAttribute = relation.split(",")[2].trim();
			return metadataAttribute;
		}
		assert (relation.split(",").length > 1);
		return null;
	}

	/** @METHOD */
	public String getPatternParameter(String key) {

		String[] keys = key.split("\\|");
		for (int i = 0; i < keys.length; i++) {

			String k = keys[i].trim();
			String line = getPattern();
			String parmarray[] = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).split(",");

			for (int j = 0; j < parmarray.length; j++) {
				String elem = parmarray[j].trim();

				if (elem.toUpperCase().contains(k.toUpperCase())) {
					return elem;
				}
			}
		}

		// String line = getPattern();
		// String parmarray[] = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).split(",");
		// for (int i = 0; i < parmarray.length; i++) {
		// String elem = parmarray[i].trim();
		//
		// if (elem.toUpperCase().contains(key.toUpperCase())) {
		// return elem;
		// }
		// }
		return null;
	}

	/** @METHOD */
	public String getPattern() {
		String pattern = Comm.getParm("pattern");
		return pattern;
	}

	/** @METHOD */
	public boolean hasVarInPattern(String key, String pattern) {
		String patternVars[] = getPatternParameters(pattern);
		for (int i = 0; i < patternVars.length; i++) {
			String elem = patternVars[i];
			if (elem.trim().toUpperCase().equals(key.trim().toUpperCase()))
				return true;
		}
		return false;
	}

	/** @METHOD */
	public String[] getPatternParameters(String pattern) {
		int bgn = -1, end = -1;
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (ch == '(')
				bgn = i;
			else if (ch == ')') {
				end = i;
				break;
			}
		}
		assert ((bgn != -1) && (end != -1));
		String arrayptrnparm[] = pattern.substring(bgn + 1, end).split(",");
		assert (arrayptrnparm.length != 0);
		return arrayptrnparm;
	}

	/** @METHOD */
	protected String getRex(String pattern) {

		PatternAnalyser ptrnAnal = new PatternAnalyser();
		Stack<String> stackOfPtrn = ptrnAnal.parsePattern(pattern);
		String combinedOperations = "";
		String array[] = { var_classname.toUpperCase(), var_fieldname.toUpperCase(), var_method.toUpperCase() };

		for (int i = 0; i < array.length; i++) {

			String topElem = stackOfPtrn.peek();
			String curElem = array[i];

			if (UtilStr.contains(topElem, curElem)) {
				String chOf2nd = String.valueOf(curElem.charAt(1)).toLowerCase();
				combinedOperations = "(" + chOf2nd + "." + "name" + ")";
				break;
			}
		}
		Collections.reverse(stackOfPtrn);

		for (String thePtrn : stackOfPtrn) {

			if (thePtrn.startsWith(pbse_ptrn_prefix)) {

				String substitute = "(" + "^/" +
						UtilStr.getStrBetween(thePtrn, "(", ")") + "/$" + ")";
				combinedOperations = (substitute + combinedOperations);
			}
			else if (thePtrn.startsWith(pbse_ptrn_uppercase)) {
				String substitute = "(" + "s/[a-z]/[A-Z]/" + ")";
				combinedOperations = (substitute + combinedOperations);
			}
			else if (thePtrn.startsWith(pbse_ptrn_lowfirstchar)) {
				String substitute = "(" + "s/^[a-z]/[A-Z]/" + ")";
				combinedOperations = (substitute + combinedOperations);
			}
		}
		return combinedOperations;
	}
}
