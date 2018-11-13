/*
 * @(#) FindRelationShip.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.transform;

import java.util.Formatter;

import metadata.invariant.pbse.STR;
import util.UtilAST;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Dec 29, 2010
 * @since JDK1.6
 */
public class RelationShip {
	final String	rule1				= "$toUpperCase($CLASSNAME)";
	final String	rule2				= "$toUpperCase($split('_', $CLASSNAME))";
	final String	rule1_format	= "$toUpperCase(%s)";
	final String	rule2_format	= "$toUpperCase($split('_', %s))";
	final String	relation_tmpl	= "[class, name, table]";

	String			_keyStr;
	String			_valStr;

	public RelationShip() {
		//
	}

	/** @METHOD */
	public void setInfo(String[] tokens) {
		if (tokens.length >= 3) {
			_keyStr = "$" + tokens[0];
			_valStr = "$" + tokens[1] + "." + tokens[2];
		}
		else {
			_keyStr = "$" + tokens[0].trim().toUpperCase();
			_valStr = "$" + tokens[1].trim().toUpperCase();
		}
	}

	/** @METHOD */
	public String findRelation(String key, String val) {
		if (_keyStr.endsWith("TYPE")) {
			int pos = key.lastIndexOf(".") + 1;
			key = pos == -1 ? key : key.substring(pos);
		}
		if (key.equals(val)) {
			return "MATCH(" + _valStr + ", " + _keyStr + ")";
		}
		else if (key.toLowerCase().equals(val)) {
			return "MATCH(" + _valStr + ", " + "LOWERCASE(" + _keyStr + ")" + ")";
		}
		else if (UtilStr.lwFirstChar(key).equals(val)) {
			return "LOWFIRSTCHAR(" + _keyStr + ")";
		}
		else if (UtilStr.lwFirstChar(val).equals(key)) {
			return "LOWFIRSTCHAR(" + _valStr + ")";
		}
		else if (UtilStr.upFirstChar(key).equals(val)) {
			return "UPFIRSTCHAR(" + _keyStr + ")";
		}
		else if (UtilStr.upFirstChar(val).equals(key)) {
			return "UPFIRSTCHAR(" + _valStr + ")";
		}
		else if (key.length() < val.length()) {
			if (val.startsWith(key)) {
				return "STARTSWITH(" + _valStr + ", " + _keyStr + ")";
			}
			else if (val.endsWith(key)) {
				return "ENDSWITH(" + _valStr + ", " + _keyStr + ")";
			}
			else if (val.endsWith(UtilStr.upFirstChar(key))) {
				// String sub = val.replace(UtilStr.upFirstChar(key), STR.str_empty);
				return "PREFIX(*) + " + "ENDSWITH(" + _valStr + ", UPFIRSTCHAR(" + _keyStr + "))";
				// return "PREFIX(" + sub + ") + " + "ENDSWITH(" + _valStr + ", UPFIRSTCHAR(" + _keyStr + "))";
			}
			else if (UtilStr.split(key, "-").toLowerCase().equals(val)) {
				return _valStr + " <- LOWERCASE(SPLIT(" + _keyStr + "))";
			}
		}
		else if (key.length() > val.length()) {
			if (key.startsWith(val)) {
				return "STARTSWITH(" + _keyStr + ", " + _valStr + ")";
			}
			else if (key.endsWith(val)) {
				return "ENDSWITH(" + _keyStr + ", " + _valStr + ")";
			}
			else if (key.endsWith(UtilStr.upFirstChar(val))) {
				// String sub = key.replace(UtilStr.upFirstChar(val), STR.str_empty);
				return "PREFIX(*) + " + "ENDSWITH(" + _keyStr + ", UPFIRSTCHAR(" + _valStr + "))";
				// return "PREFIX(" + sub + ") + " + "ENDSWITH(" + _keyStr + ", UPFIRSTCHAR(" + _valStr + "))";
			}
			else if (UtilStr.split(val, "-").toLowerCase().equals(key)) {
				return _keyStr + " <- LOWERCASE(SPLIT(" + _valStr + "))";
			}
		}
		return "INVALID PATTERN";
	}

	/** @METHOD */
	public static String findClassAnnotationName(String className, String annotationName) {
		className = UtilAST.getOnlyClassName(className);
		annotationName = annotationName.replace("@", STR.str_empty);

		if (annotationName.equals((className.toUpperCase()))) {
			return "UPPERCASE($CLASSNAME)";
		}
		else if (className.contains("Config")) {
			return "className.contains(Config)";
		}
		else if (className.startsWith(annotationName)) {
			// String substr = className.replace(annotationName, STR.str_empty);
			String[] strArray = className.split(annotationName);
			if (strArray.length == 1)
				return "STARTSWITH($CLASSNAME, $ANNOTATIONNAME)";
			else
				return new String("INVALID PATTERN");
		}
		else if (classNameEndsWith(className, annotationName) /* className.endsWith(annotationName) */) {
			// String substr = className.replace(annotationName, STR.str_empty);
			String[] strArray = className.split(annotationName);
			if (strArray.length == 1)
				return "ENDSWITH($CLASSNAME, $ANNOTATIONNAME)";
			else
				return new String("INVALID PATTERN");
		}
		System.out.println("[DBG] invalid: " + className);
		return new String("INVALID PATTERN");
	}

	/**
	 * @METHOD
	 */
	static boolean classNameEndsWith(String className, String annotationName) {
		boolean result = false;
		for (int i = 0; i < className.length(); i++) {

			if (className.charAt(i) == annotationName.charAt(0)) {
				result = compareTwoStr(i, className, annotationName);
				if (result)
					return result;
			}
		}
		return false;
	}

	static boolean compareTwoStr(int start, String targetStr, String comparedStr) {
		int lenTargetStr = targetStr.length();
		int lenComparedStr = comparedStr.length();
		int i = start;

		for (int j = 0; i < lenTargetStr && j < comparedStr.length(); i++, j++) {

			if (targetStr.charAt(i) != comparedStr.charAt(j))
				break;
		}

		if (lenTargetStr - start > lenComparedStr)
			return false;

		return (lenTargetStr == i);
	}

	/** @METHOD */
	public static String findClassTable(String className, String tableName) {
		// * Remove the package name which is included into "className" string.
		className = UtilAST.getOnlyClassName(className);

		String classNameUpperCase = className.toUpperCase();
		String tableNameRemovedUnderScore = tableName.replace(STR.str_underScore, STR.str_empty);

		if (tableName.equals((className.toUpperCase()))) {
			return "UPPERCASE($CLASSNAME)";
		}
		else if (tableName.startsWith(classNameUpperCase)) {
			String substr = tableName.replace(classNameUpperCase, STR.str_empty);
			return "SUFFIX(" + substr + ") + " + "UPPERCASE(SPLIT('_', $CLASSNAME))";
		}
		else if (tableName.endsWith(classNameUpperCase)) {
			String substr = tableName.replace(classNameUpperCase, STR.str_empty);
			return "PREFIX(" + substr + ") + " + "UPPERCASE($CLASSNAME)";
		}
		// ---
		else if (tableNameRemovedUnderScore.equals(classNameUpperCase)) {
			return "UPPERCASE(SPLIT('_', $CLASSNAME))";
		}
		else if (tableNameRemovedUnderScore.endsWith(classNameUpperCase)) {
			String substr = tableName.replace(classNameUpperCase, STR.str_empty);
			return "PREFIX(" + substr + ") + " + "UPPERCASE(SPLIT('_', $CLASSNAME))";
		}
		else if (tableNameRemovedUnderScore.endsWith(classNameUpperCase)) {
			String substr = tableName.replace(classNameUpperCase, STR.str_empty);
			return "SUFFIX(" + substr + ") + " + "UPPERCASE(SPLIT('_', $CLASSNAME))";
		}
		else {
			// <<< SUB STRING CASE >>>
			// The sub-string of the class name is belong to the table name.

			// * STEP #1: Find the longest sub-string from the class name.
			//
			String substr = UtilStr.findLongestSubStr(classNameUpperCase, tableNameRemovedUnderScore);

			// * STEP #2: Transformation of the class name.
			//
			String[] strArray = classNameUpperCase.split(substr);
			String removeStr = "UPPERCASE(" + STR.var_classnamebig + ").REMOVE(";
			String flattedStr = UtilStr.arrayToStr(strArray);
			removeStr += flattedStr;
			removeStr += ")";

			// * STEP #3: Make the table name with the sub-string of the class name.
			//
			if (tableName.startsWith(substr)) {
				String token = tableName.replace(substr, STR.str_empty);
				return (removeStr + " + SUFFIX(" + token + ")");
			}
			else if (tableName.endsWith(substr)) {
				String token = tableName.replace(substr, STR.str_empty);
				return ("PREFIX(" + token + ") + " + removeStr);
			}
			else {
				strArray = tableName.split(substr);
				String addStr = "ADD(";
				flattedStr = UtilStr.arrayToStr(strArray);
				addStr += flattedStr;
				addStr += ")";
				return addStr + " + " + removeStr;
			}
		}
	}

	/** @METHOD */
	public String result(String word1, String word2) {
		boolean lengthEqual = false;

		// <<< CASE >>>
		// The passed parameter is class name, which contains the package name
		//
		if (word1.contains("."))
			word1 = UtilAST.getOnlyClassName(word1);
		if (word2.contains("."))
			word2 = UtilAST.getOnlyClassName(word2);

		String longStr = "", shortStr = "";
		if (word1.length() > word2.length()) {
			longStr = word1;
			shortStr = word2;
		}
		else if (word1.length() < word2.length()) {
			longStr = word2;
			shortStr = word1;
		}
		else
			lengthEqual = true;

		// <<< CASE >>>
		// If two strings are matching exactly, while case is insensitive.
		//
		if (lengthEqual) {
			if (word1.toUpperCase().equals(word2)) {
				return "$TOUPPERCASE($WORDS)";
			}
			else if (word2.toUpperCase().equals(word1)) {
				return "$TOUPPERCASE($WORDS)";
			}
		}
		// <<< CASE >>>
		// If the long string contains the under score
		// character ('_'), while they are matching.
		//
		else if ((longStr.replace("_", "")).equals(shortStr.toUpperCase())) {
			return "WORDS1 <- $TOUPPERCASE($SPLIT('_', $WORDS2))";
		}
		// <<< CASE >>>
		// If the long string contains the short string,
		// while being insensitive case.
		//
		else if (longStr.contains(shortStr.toUpperCase())) {
			return case1(longStr, shortStr);
		}

		// <<< OLD PROCESSING >>>
		//
		// String className = word2.substring(word2.lastIndexOf(".") + 1);
		// String word1_removed_underScore = word1.replace("_", "").toUpperCase();
		// String word2_uppercase = word2.toUpperCase();
		//
		// if (word1.contains(className.toUpperCase())) {
		// if (word1.length() == className.length()) {
		// // TODO
		// }
		// else {
		// String subStr = word1.replace(className.toUpperCase(), rule1);
		// return subStr;
		// }
		// }
		// else if (word1_removed_underScore.contains(word2_uppercase)) {
		// String subStr = word1_removed_underScore.replace(word2_uppercase, rule2);
		// return subStr;
		// }
		return null;
	}

	/** @METHOD */
	private String case1(String longStr, String shortStr) {
		String token1 = longStr.replace(shortStr.toUpperCase(), "");

		if (longStr.startsWith(token1)) {
			return "$WORDS1 <- $PEFIX(" + token1 + ")" + " + $TOUPPERCASE($WORDS2)";
		}
		else if (longStr.endsWith(token1)) {
			return "$WORDS1 <- $TOUPPERCASE($WORDS2)" + "$SUFFIX(" + token1 + ")";
		}
		System.err.println("[WARNING] FindRelationShip - case1");
		return null;
	}

	/** @METHOD */
	public String result(String word1, String word2, String variable) {
		Formatter formatter = new Formatter();
		String newStr = null, subStr = null;
		String className = word2.substring(word2.lastIndexOf(".") + 1);

		String word1_removed_underScore = null, word2_incase = null;

		word1_removed_underScore = word1.replace("_", "").toUpperCase();
		word2_incase = word2.toUpperCase();

		if (word1.contains(className.toUpperCase())) {
			if (word1.length() == className.length()) {
				// TODO
			}
			else {
				newStr = formatter.format(rule1_format, variable).toString();
				subStr = word1.replace(className.toUpperCase(), newStr);
				return subStr;
			}
		}
		else if (word1_removed_underScore.contains(word2_incase)) {
			newStr = formatter.format(rule2_format, variable).toString();
			subStr = word1_removed_underScore.replace(word2_incase, newStr);
			return subStr;
		}
		else if (word2_incase.contains(word1_removed_underScore)) {
			newStr = formatter.format(rule2_format, variable).toString();
			subStr = word2_incase.replace(word1_removed_underScore, newStr);
			return subStr;
		}
		return null;
	}

}
