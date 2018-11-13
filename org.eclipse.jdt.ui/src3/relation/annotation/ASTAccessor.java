/*
 * @(#) RelationWithAnnotation.java
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
import metadata.invariant.pbse.transform.RelationShip;
import metadata.invariant.pbse.visitor.ClassVisitor;
import relation.progelem.AnnotatedClassDecl;
import relation.progelem.AnnotatedMethodDecl;
import relation.type.RelationEnum;
import util.UtilAST;
import util.UtilDirScan;
import util.UtilList;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jul 1, 2011
 * @since JDK1.6
 */
public class ASTAccessor {
	RelationEnum	_curRelation;
	List<String>	_javafilelist;

	/** @METHOD */
	public ASTAccessor(List<String> plist) {
		_javafilelist = plist;
	}

	/** @METHOD */
	public ASTAccessor() {
		_javafilelist = null;
	}

	/** @METHOD */
	public HashMap<String, List<AnnotatedMethodDecl>> getAnnotatedMethodList(List<String> plist)
	{
		_javafilelist = plist;
		return getAnnotatedMethodList();
	}

	/** @METHOD */
	public List<String> getAnnotatedFieldList(List<String> plist)
	{
		_javafilelist = plist;
		return getAnnotatedFieldList();
	}

	/** @METHOD */
	public List<String> getAnnotatedFieldList(String pFile)
	{
		_javafilelist = new ArrayList<String>();
		_javafilelist.add(pFile);
		return getAnnotatedFieldList();
	}

	/** @METHOD */
	public List<String> getAnnotatedFieldListInDetail(List<String> plist)
	{
		_javafilelist = plist;
		return getAnnotatedFieldListInDetail();
	}

	/** @METHOD */
	public List<String> getAnnotatedFieldListInDetail(String pFile)
	{
		_javafilelist = new ArrayList<String>();
		_javafilelist.add(pFile);
		return getAnnotatedFieldListInDetail();
	}

	/** @METHOD */
	public List<AnnotatedMethodDecl> getMethodList(List<String> javafilelist)
	{
		_javafilelist = javafilelist;
		List<AnnotatedMethodDecl> methodlist = new ArrayList<AnnotatedMethodDecl>();
		for (int i = 0; i < _javafilelist.size(); i++) {
			String filename = _javafilelist.get(i);
			ClassVisitor visitor = new ClassVisitor();
			UtilAST.startCustomVisit(filename, visitor);
			methodlist.addAll(visitor.methodDeclRefList);
		}
		return methodlist;
	}

	/** @METHOD */
	public HashMap<String, List<AnnotatedMethodDecl>> getAnnotatedMethodList()
	{
		HashMap<String, List<AnnotatedMethodDecl>> annotatedMethods =
				new HashMap<String, List<AnnotatedMethodDecl>>();
		for (int i = 0; i < _javafilelist.size(); i++) {
			String filename = _javafilelist.get(i);

			// [[ debugging - file - ShortcutsOptionPane ]]
			// if (filename.contains("ShortcutsOptionPane.java"))
			// System.out.println();

			ClassVisitor visitor = new ClassVisitor();
			UtilAST.startCustomVisit(filename, visitor);
			List<AnnotatedMethodDecl> methodlist = new ArrayList<AnnotatedMethodDecl>();
			methodlist.addAll(visitor.methodDeclRefList);
			String classname = visitor.getClassName();
			if (classname != null)
				annotatedMethods.put("_c_" + classname, methodlist);
			else {
				classname = visitor.getIfaceName();
				annotatedMethods.put("_i_" + classname, methodlist);
			}
		}
		return annotatedMethods;
	}

	/** @METHOD */
	private List<String> getAnnotatedFieldListInDetail()
	{
		List<String> fieldlist = new ArrayList<String>();
		for (int i = 0; i < _javafilelist.size(); i++) {
			String filename = _javafilelist.get(i);
			// * visitor
			ClassVisitor visitor = new ClassVisitor();
			UtilAST.startCustomVisit(filename, visitor);

			if (visitor.getFieldList().isEmpty() == false) {
				List<String> tmplist = visitor.getFieldList();
				filename = UtilStr.getShorfileName(filename);
				UtilList.addStrToList(filename, tmplist);
				fieldlist.addAll(tmplist);
			}
		}
		return fieldlist;
	}

	/** @METHOD */
	private List<String> getAnnotatedFieldList()
	{
		List<String> fieldlist = new ArrayList<String>();
		for (int i = 0; i < _javafilelist.size(); i++) {
			String filename = _javafilelist.get(i);

			// for debugging....

			if (UtilStr.getShorfileName(filename).equals("FileOrDirectoryCopyPackagingElement.java"))
				System.out.println("[DBG] " + UtilStr.getShorfileName(filename));

			// * visitor
			ClassVisitor visitor = new ClassVisitor();
			UtilAST.startCustomVisit(filename, visitor);
			// * filter
			if (visitor.getFieldList().isEmpty() == false) {

				// UtilPrint.printArrayList(visitor.getFieldList());
				List<String> tmplist = visitor.getFieldList();
				fieldlist.addAll(tmplist);
			}
		}
		return fieldlist;
	}

	/** @METHOD */
	public List<String> getAnnotatedJavaClassList(String tmpdir)
	{
		List<File> annotatedJavaSrcList = UtilDirScan.getResult(tmpdir, STR.file_java);

		//System.out.println("[DBG] " + annotatedJavaSrcList.size() + " Files Checked.");

		List<String> annotatedJavaClassList = new ArrayList<String>();
		for (int i = 0; i < annotatedJavaSrcList.size(); i++) {
			File javafile = annotatedJavaSrcList.get(i);

			// String markerAnnotationClass = getAnnotationFromJavaSrc(javafile.getAbsolutePath());
			// if (markerAnnotationClass != null) {
			// System.out.println("[DBG]" + markerAnnotationClass);
			// annotatedJavaClassList.add(markerAnnotationClass);
			// }
			annotatedJavaClassList.addAll(getAnnotationFromJavaSrcNew(javafile.getAbsolutePath()));
		}
		return annotatedJavaClassList;
	}

	public List<String> getAnnotationFromJavaSrcNew(String javafile)
	{
		List<String> classList = new ArrayList<String>();

		ClassVisitor visitor = new ClassVisitor();
		UtilAST.startCustomVisit(javafile, visitor);

		List<AnnotatedClassDecl> classDeclRefList = visitor.classDeclRefList;
		for (int i = 0; i < classDeclRefList.size(); i++) {
			AnnotatedClassDecl elem = classDeclRefList.get(i);

			List<String> markerAnnot = elem.markerAnnotationList;
			if (markerAnnot.isEmpty())
				continue;

			String classname = elem.name;
			classList.add(classname);
		}
		return classList;
	}

	/** @METHOD */
	public String getAnnotationFromJavaSrc(String javafile)
	{
		ClassVisitor visitor = new ClassVisitor();
		UtilAST.startCustomVisit(javafile, visitor);
		String classname = visitor.getClassName();

		String relation = Comm.getParm("relation");
		String annotation = relation.split(",")[2];

		// if (!annotation.equals(visitor.getClassAnnotation()))
		// return null;
		//
		// if (visitor.getClassAnnotation() == null)
		// return null;

		return classname == null ? visitor.getIfaceName() : classname;
	}

	/**
	 * @return
	 * @METHOD
	 */
	public List<AnnotatedClassDecl> getAnnotatedClassList(String pJavafile)
	{
		ClassVisitor visitor = new ClassVisitor();
		UtilAST.startCustomVisit(pJavafile, visitor);
		return visitor.classDeclRefList;
	}

	/**
	 * @return
	 * @METHOD
	 */
	RelationEnum setRelation(String[] pattern)
	{
		// * <<< 1st Case >>>
		if (pattern.length == 3) {
			String combined = pattern[0].trim() + pattern[1].trim();
			// * <<< 2nd Case >>>
			if (combined.equalsIgnoreCase(STR.relation_className_annotationName)) {
				// * <<< 3rd Case >>>
				if (pattern[2].trim().contains(STR.at_configuration))
					_curRelation = RelationEnum.CONFIGURATION;
			}
		}
		return _curRelation;
	}

	/** @METHOD */
	public List<String> findRelation(List<String> annotatedJavaClassList)
	{
		List<String> relationList = new ArrayList<String>();
		List<String> relationSubList = new ArrayList<String>();
		for (int i = 0; i < annotatedJavaClassList.size(); i++) {
			String word = annotatedJavaClassList.get(i);
			relationSubList = findRelation(word);
			if (relationSubList.isEmpty() == false)
				relationList.addAll(relationSubList);
		}
		return relationList;
	}

	/** @METHOD */
	List<String> findRelation(String word)
	{
		List<String> relationList = new ArrayList<String>();
		switch (_curRelation) {
		case CONFIGURATION: {
			String className = (word);
			String relationResult = RelationShip.findClassAnnotationName(className, STR.at_configuration);
			if (relationResult.isEmpty() == false) {
				relationList.add(relationResult);
			}
			break;
		}
		}
		return relationList;
	}

}
