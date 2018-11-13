/*
 * @(#) AnnotatedClassDecl.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.progelem;

import java.util.ArrayList;
import java.util.List;

import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Aug 1, 2011
 * @since JDK1.6
 */
public class AnnotatedClassDecl {
	public String							superclazz;
	public String							name;

	public List<AnnotatedFieldDecl>	fieldDeclList			= new ArrayList<AnnotatedFieldDecl>();

	public List<String>					normalAnnotationList	= new ArrayList<String>();
	public List<String>					markerAnnotationList	= new ArrayList<String>();

	public AnnotatedClassDecl(String name, String superclazz) {
		this.name = name;
		this.superclazz = superclazz;
	}

	public void addFieldDeclList(AnnotatedFieldDecl parm) {
		fieldDeclList.add(parm);
	}

	public void addMarkerAnnotationList(String parm) {
		markerAnnotationList.add(parm);
	}

	public void addNormalAnnotationList(String parm) {
		normalAnnotationList.add(parm);
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
		return "   " + "Name: " + name +
				"\n   " + "Supr: " + superclazz +
				"\n   " + "@Normal: " + (normalAnnotations.trim().isEmpty() ? "-" : normalAnnotations.trim()) +
				"\n   " + "@Marker: " + (markerAnnotations.trim().isEmpty() ? "-" : markerAnnotations.trim()) +
				dotline + "\n" +
				UtilStr.toStringList(fieldDeclList) + dotline;
	}

	/** @METHOD */
	public String[] getFieldNameList() {
		List<String> fields = new ArrayList<String>();

		for (AnnotatedFieldDecl fieldDecl : fieldDeclList) {
			fields.add(fieldDecl.name.trim());
		}
		return (String[]) fields.toArray(new String[fields.size()]);
	}

	/** @METHOD */
	public String getNormalAnnotation(String pAnnotationInCmdline) {
		for (String normalAnnotation : normalAnnotationList) {
			if (normalAnnotation.contains(pAnnotationInCmdline.trim()))
				return normalAnnotation.trim();
		}
		return null;
	}
}
