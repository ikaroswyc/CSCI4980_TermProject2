/*
 * @(#) MethodDecl.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.progelem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Myoungkyu Song
 * @date Jun 27, 2011
 * @since JDK1.6
 */
public class AnnotatedMethodDecl {
	public String			superclazz;
	public String			clazz;
	public String			type;
	public String			name;
	public String			parm;
	// can be separated with "," comma.
	public String			modifier;
	// bitwise expression: public static - 1001, private - 10, protected - 100.
	public int				lineNumber;
	public int				startPoint;

	public List<String>	normalAnnotationList	= new ArrayList<String>();
	public List<String>	markerAnnotationList	= new ArrayList<String>();

	public AnnotatedMethodDecl() {
		/* ^.^ */
	}

	String	lineSep	= System.getProperty("line.separator");

	public AnnotatedMethodDecl(String methodName, String methodRType, String methodParm, String superclass, String clazz) {
		this.name = methodName;
		this.type = methodRType;
		this.parm = methodParm;
		this.superclazz = superclass;
		this.clazz = clazz;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public int getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(int startPoint) {
		this.startPoint = startPoint;
	}

	public void copyFrom(AnnotatedMethodDecl src) {
		this.name = src.name;
		this.type = src.type;
		this.parm = src.parm;
		this.normalAnnotationList.addAll(src.normalAnnotationList);
		this.markerAnnotationList.addAll(src.markerAnnotationList);
	}

	/** @METHOD */
	public boolean compare(AnnotatedMethodDecl pMethod) {
		if (pMethod.name.trim().equals(this.name.trim()) && compareParm(pMethod.parm.trim(), this.parm.trim()))
			return true;
		return false;
	}

	/** @METHOD */
	public boolean compareParm(String s1, String s2) {
		if (s1.isEmpty() && s2.isEmpty())
			return true;
		else if (s1.isEmpty() || s2.isEmpty())
			return false;

		String[] sar1 = s1.split(",");
		String[] sar2 = s2.split(",");
		if (Math.min(sar1.length, sar2.length) != Math.max(sar1.length, sar2.length)) {
			return false;
		}
		int len = sar1.length;
		for (int i = 0; i < len; i++) {
			String subs1 = sar1[i].trim();
			String subs2 = sar2[i].trim();
			int pos1 = subs1.lastIndexOf(" ");
			int pos2 = subs2.lastIndexOf(" ");
			subs1 = subs1.substring(0, pos1).trim();
			subs2 = subs2.substring(0, pos2).trim();
			if (subs1.equals(subs2) == false)
				return false;
		}
		return true;
	}

	public String toString() {
		String dotline = "\n------------------------------------------";
		String normalAnnotations = "";
		for (String elem : normalAnnotationList) {
			normalAnnotations += (elem + " ");
		}
		String markerAnnotations = "";
		for (String elem : markerAnnotationList) {
			markerAnnotations += (elem + " ");
		}
		return "   " + "Loc: " + lineNumber + lineSep + "   " + //
				"Modi: " + modifier + lineSep + "   " + //
				"Type: " + type + lineSep + "   " + //
				"Name: " + name + lineSep + "   " + //
				"Parm: " + (parm.isEmpty() ? "-" : parm) + lineSep + "   " + //
				"Supr: " + superclazz + lineSep + "   " + //
				"Clzz: " + clazz + lineSep + "   " + //
				"@Normal: " + (normalAnnotations.trim().isEmpty() ? "-" : normalAnnotations.trim()) + lineSep + "   " + //
				"@Marker: " + (markerAnnotations.trim().isEmpty() ? "-" : markerAnnotations.trim()) + dotline;

	}

}
