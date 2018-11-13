/*
 * @(#) MethodMatcher.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation.match;

import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.Comm;
import relation.ReturnValue;
import relation.progelem.AnnotatedMethodDecl;
import util.UtilAST;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jul 27, 2011
 * @since JDK1.6
 */
public class MethodMatcher {
	List<AnnotatedMethodDecl>	allMethodlist;
	List<AnnotatedMethodDecl>	subMethodlist;

	public MethodMatcher() {
		allMethodlist = new ArrayList<AnnotatedMethodDecl>();
		subMethodlist = new ArrayList<AnnotatedMethodDecl>();
	}

	public List<AnnotatedMethodDecl> getAllMethodlist() {
		return allMethodlist;
	}

	public List<AnnotatedMethodDecl> getSubMethodlist() {
		return subMethodlist;
	}

	/** @METHOD */
	public List<String> getRelations(String markerAnnot, List<String> javafileList) {
		List<List<AnnotatedMethodDecl>> methodlist = UtilAST.getMethodList(markerAnnot, javafileList);
		allMethodlist = methodlist.get(0);
		subMethodlist = methodlist.get(1);

		System.out.println("[DBG] " + subMethodlist.size() + " Annotations Checked.");

		List<String> relationList = new ArrayList<String>();
		int inSuperclazz = 0, inValid = 0;
		ReturnValue val = ReturnValue.invalid;

		String relation = Comm.getParm("relation");
		String constAnnotName = relation.split(",")[2].trim();
		if (constAnnotName.equals("@Test")) {
			for (int i = 0; i < subMethodlist.size(); i++) {
				AnnotatedMethodDecl method = subMethodlist.get(i);
				// System.out.println("[DBG]" + method.modifier);

				if (method.modifier.equals("public"))
					relationList.add("MATCH($METHOD.MODIFIER, PUBLIC)");
				else
					relationList.add("INVALID PATTERN");
			}
		}
		else {
			for (int i = 0; i < subMethodlist.size(); i++) {
				AnnotatedMethodDecl method = subMethodlist.get(i);
				
				System.out.println("[DBG]" + method.clazz);

				if ((val = hasMethodInSuperClass(method, method.superclazz)) == ReturnValue.ok) {
					inSuperclazz++;
					relationList.add("CONTAINS($METHOD, $SUPERCLASS)");
				}
				else if (val == ReturnValue.invalid) {
					continue;
				}
				else if ((val = hasMethodInGrandSuperClass(method, getSuperClass(method.superclazz))) == ReturnValue.ok) {
					inSuperclazz++;
					relationList.add("CONTAINS($METHOD, $SUPERCLASS)");
				}
				else if (val == ReturnValue.nok) {
					inValid++;
					relationList.add("INVALID PATTERN");
				}
			}
		}
		return relationList;

		// double probability = 100.0 * ((double) inSuperclazz / (double) _subMethodlist.size());
		// if (probability > 95.0) {
		// System.out.println("CONTAINS($METHOD, $SUPERCLASS)");
		// System.out.println("==========================================");
		// System.out.println("[TEST_EXIT]");
		// System.out.println("==========================================");
		// System.exit(-1);
		// }

		// ------------------------------------------------------------
		/*
		 * // We have the following empirical rules (y: mean, s:standard deviation)
		 * // * About 68% of the data fall between y - s and y + s
		 * // * About 95% of the data fall between y - 2s and y + 2s
		 * // * About 99% of the data fall between y - 3s and y + 3s
		 */
		// ------------------------------------------------------------
		/*
		 * // double y = (yescnt + nocnt) / 2;
		 * // double s = Math.sqrt((1 / (n - 1)) * (Math.pow((y - yescnt), 2) + Math.pow((y - nocnt), 2)));
		 * // double ci95 = 2;
		 */
		// ------------------------------------------------------------
		// int n = 2;
		// System.out.println("[DBG] yescnt: " + inSuperclazz);
		// System.out.println("[DBG] nocnt: " + inValid);
		//
		// double SSE1 = Math.pow(inSuperclazz, 2) + Math.pow(inValid, 2);
		// double SSE2 = Math.pow(inSuperclazz + inValid, 2);
		// double SSE3 = SSE2 / n;
		// double SSE = SSE1 - SSE3; // 79600.5
		// double s = Math.sqrt(SSE / (n - 1));
		//
		// System.out.println("[DBG] SSE: " + SSE);
		// System.out.println("[DBG] s: " + s);
		// double y = (inSuperclazz + inValid) / n;
		// double ci95 = 2.0;
		// double ci = y + ci95 * s;
		// System.out.println("[DBG] y:" + y);
		// System.out.println("[DBG] ci: " + ci);
		//
		// System.out.println("------------------------------------------");
		// System.out.println("[DBG] Yes Count: " + inSuperclazz + ", No Count: " + inValid);
		// System.out.println("[DBG]" + _allMethodlist.size());
		// System.out.println("[DBG]" + _subMethodlist.size());
		// System.out.println("==========================================");
		// System.out.println("[TEST_EXIT]");
		// System.out.println("==========================================");
		// System.exit(-1);
		// ------------------------------------------------------------
	}

	/** @METHOD */
	public ReturnValue hasMethodInGrandSuperClass(AnnotatedMethodDecl pMethod, String pSuperclazz) {
		return hasMethodInSuperClass(pMethod, pSuperclazz);
	}

	/** @METHOD */
	public String getSuperClass(String pClazz) {
		for (int i = 0; i < allMethodlist.size(); i++) {
			AnnotatedMethodDecl method = allMethodlist.get(i);
			String clazzShort = UtilStr.getShortClassName(method.clazz);
			if (clazzShort.equals(pClazz)) {
				return method.superclazz;
			}
		}
		return null;
	}

	/** @METHOD */
	public ReturnValue hasMethodInSuperClass(AnnotatedMethodDecl pMethod, String pSuperclazz) {
		List<AnnotatedMethodDecl> searchedMethods = getMethodsInClass(pSuperclazz);
		for (int i = 0; i < searchedMethods.size(); i++) {
			AnnotatedMethodDecl method = searchedMethods.get(i);
			if (method.name.equals(pMethod.name))
				return ReturnValue.ok;
		}
		if (searchedMethods.isEmpty()) {
			return ReturnValue.invalid;
		}
		return ReturnValue.nok;
	}

	/** @METHOD */
	public List<AnnotatedMethodDecl> getMethodsInClass(String pClazz) {
		List<AnnotatedMethodDecl> searchedMethods = new ArrayList<AnnotatedMethodDecl>();
		for (int i = 0; i < allMethodlist.size(); i++) {
			AnnotatedMethodDecl method = allMethodlist.get(i);
			String clazzName1 = UtilStr.getShortClassName(method.clazz);
			String clazzName2 = UtilStr.getShortClassName(pClazz);

			if (clazzName1.equals(clazzName2)) {
				searchedMethods.add(method);
			}
		}
		return searchedMethods;
	}

}
