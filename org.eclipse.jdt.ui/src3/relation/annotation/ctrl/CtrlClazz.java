/*
 * @(#) ClazzHdlr.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation.ctrl;

import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.Comm;
import relation.progelem.AnnotatedClassDecl;
import relation.progelem.AnnotatedMethodDecl;
import util.UtilAST;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jul 29, 2011
 * @since JDK1.6
 */
public class CtrlClazz implements CtrlFieldType {
	private String								clazz;
	private List<AnnotatedMethodDecl>	allMethodToInspect;

	/** @METHOD */
	public boolean containMethod(AnnotatedMethodDecl pMethod) {
		if (UtilStr.isNull(clazz)) {
			return false;
		}
		if (contains(pMethod, clazz)) {
			return true;
		}
		setClazz(getSuperClass(clazz));

		return containMethod(pMethod);
	}

	/** @METHOD */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/** @METHOD */
	private String getSuperClass(String pClazz) {

		for (int i = 0; i < this.allMethodToInspect.size(); i++) {
			AnnotatedMethodDecl method = this.allMethodToInspect.get(i);

			String clazz = UtilStr.getShortClassName(method.clazz);
			String superClazz = UtilStr.getShortClassName(method.superclazz);

			if (clazz.equals(pClazz))
				return superClazz;
		}
		return null;
	}

	/** @METHOD */
	boolean contains(AnnotatedMethodDecl pMethod, String pClazz) {
		List<AnnotatedMethodDecl> methodsSearched = getMethodsInClass(pMethod, pClazz);
		for (int i = 0; i < methodsSearched.size(); i++) {
			AnnotatedMethodDecl method = methodsSearched.get(i);

			if (method.compare(pMethod)) {
				return true;
			}
		}
		return false;
	}

	/** @METHOD */
	private List<AnnotatedMethodDecl> getMethodsInClass(AnnotatedMethodDecl pMethod, String pClazz) {
		List<AnnotatedMethodDecl> methodsSearched = new ArrayList<AnnotatedMethodDecl>();
		for (int i = 0; i < this.allMethodToInspect.size(); i++) {
			AnnotatedMethodDecl method = this.allMethodToInspect.get(i);

			String myClazz = UtilStr.getShortClassName(method.clazz);
			String yrClazz = UtilStr.getShortClassName(pClazz);

			if (myClazz.equals(yrClazz))
				methodsSearched.add(method);
		}
		return methodsSearched;
	}

	/** @METHOD */
	public void setAllMethodToInspect(List<AnnotatedMethodDecl> allMethodToInspect) {
		this.allMethodToInspect = allMethodToInspect;
	}

	/**
	 * @return
	 * @METHOD
	 * @Callback cbfGetAnnotatedClassList(..)
	 */
	public List<AnnotatedClassDecl> getClassList(List<String> javafiles) {

		List<AnnotatedClassDecl> classlist = new ArrayList<AnnotatedClassDecl>();
		for (String javafile : javafiles) {
			List<AnnotatedClassDecl> classList = UtilAST.getClassList(javafile, this);

			if (classList.isEmpty())
				continue;

			for (AnnotatedClassDecl annotatedClassDecl : classList)
				classlist.add(annotatedClassDecl);
		}
		return classlist;
	}

	/**
	 * @return
	 * @METHOD
	 * @Callback cbfGetAnnotatedClassList(..)
	 */
	public List<AnnotatedClassDecl> getClassList(String javafile) {
		List<AnnotatedClassDecl> classlist = new ArrayList<AnnotatedClassDecl>();
		classlist = UtilAST.getClassList(javafile, this);
		return classlist;
	}

	/** @METHOD */
	public List<AnnotatedClassDecl> cbfGetAnnotatedClassList(List<AnnotatedClassDecl> pClazzlist) {

		List<AnnotatedClassDecl> filteredClassList = new ArrayList<AnnotatedClassDecl>();

		for (AnnotatedClassDecl theClazz : pClazzlist) {
			if (theClazz.normalAnnotationList.isEmpty())
				continue;

			for (String normalAnnot : theClazz.normalAnnotationList) {
				if (normalAnnot.startsWith(Comm.getAnnotationInCmdline()) == false)
					continue;

				filteredClassList.add(theClazz);
			}
		}
		return filteredClassList;
	}
}
