/*
 * @(#) CtrlField.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation.ctrl;

import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.progelem.AnnotatedClassDecl;
import relation.progelem.AnnotatedFieldDecl;
import util.UtilAST;
import util.UtilDirScan;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Aug 2, 2011
 * @since JDK1.6
 */
public class CtrlField implements CtrlFieldType {
	private String	path;
	private String	relation;

	public CtrlField() {
		path = Comm.getParm(STR.parm_path);
		relation = Comm.getParm(STR.parm_relation);
	}

	/** @METHOD */
	public List<String> getRelations() {
		String[] tokens = relation.split(",");
		assert (tokens.length > 1);
		String annotParm = tokens[1].trim();

		List<String> relationList = new ArrayList<String>();
		List<String> javafileList = UtilDirScan.getResultViaStr(path, STR.file_java);

		if (annotParm.equalsIgnoreCase(STR.relation_annotation_attribute)) {
			// ****************************************************
			// Backward Compatible Version: Before Wed Aug 3, 2011
			// ****************************************************
			String annotation = tokens[3].trim();
			List<String> fieldList1 = UtilAST.getFieldList(javafileList, false);
//			List<String> fieldList1 = UtilAST.getFieldList(javafileList, true);
			List<String> fieldList2 = new ArrayList<String>();
			// [[ FILTER ]]
			for (int i = 0; i < fieldList1.size(); i++) {
				String elem = fieldList1.get(i);
				if (elem.contains(annotation) && UtilStr.hasAttributeStr(elem)) {
					System.out.println(UtilStr.getSize(elem, '(') + ": " + elem);
					fieldList2.add(elem);
				}
			}
			// [[ Relation Between Pair ]]
			List<String[]> pairlist = UtilAST.getASTPair("FIELD_NAME", STR.relation_annotation_attribute, fieldList2);
			relationList = UtilAST.getRelations(pairlist, tokens);

			// ****************************************************
			// Upgrade Version: After Wed Aug 3, 2011
			// ****************************************************
			if (relationList.isEmpty()) {
				relationList = getRelations(javafileList);
			}
		}
		return relationList;
	}

	/** @METHOD */
	private List<String> getRelations(List<String> javafiles) {

		List<String> relationlist = new ArrayList<String>();

		for (String javafile : javafiles) {
			List<AnnotatedClassDecl> classList = UtilAST.getClassList(javafile, this);

			if (classList.isEmpty())
				continue;

			for (AnnotatedClassDecl annotatedClassDecl : classList) {

				String theNormalAnnot = annotatedClassDecl.getNormalAnnotation(Comm.getAnnotationInCmdline());
				List<AnnotatedFieldDecl> thefieldNameList = annotatedClassDecl.fieldDeclList;

				String relation = getRelation(theNormalAnnot, thefieldNameList);

				if (relation.startsWith("(X)")) {
					String fieldNames[] = annotatedClassDecl.getFieldNameList();
					String attr = Comm.getAttributeInCmdline();

					boolean bRelation = UtilStr.contains(fieldNames, UtilStr.getValues(theNormalAnnot, attr));

					if (bRelation)
						relationlist.add("CONTAINS($METADATA.ATTRVAL, $FIELD)"); // System.out.println("[DBG](C): " + theNormalAnnot);
					else
						relationlist.add("INVALID PATTERN");
				}
				else
					relationlist.add("MATCH($METADATA.ATTRVAL, $FIELD)");
			}
		}
		return relationlist;
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

	/** @METHOD */
	String getRelation(String pAnnotation, List<AnnotatedFieldDecl> pFieldNames) {

		String attr = Comm.getAttributeInCmdline();
		String anno = Comm.getAnnotationInCmdline();
		assert (pAnnotation.startsWith(anno));
		// ***********************************************************************************
		// pAnnotation ==>
		// @XmlType(name="",propOrder={"totalCount","from","count","tooManyResults","data"})
		// ***********************************************************************************

		String attrValues[] = UtilStr.getValues(pAnnotation, attr);
		if (attrValues == null)
			return "(N): " + pAnnotation;
		else if (compare(attrValues, pFieldNames) == true)
			return "(O):" + pAnnotation;
		else
			return "(X): " + pAnnotation;
	}

	/** @METHOD */
	private boolean compare(String[] attrValues, List<AnnotatedFieldDecl> oldFieldDeclList) {

		List<AnnotatedFieldDecl> newFieldDeclList = new ArrayList<AnnotatedFieldDecl>();

		for (AnnotatedFieldDecl fieldDecl : oldFieldDeclList) {
			if (UtilStr.contains(attrValues, fieldDecl.name)) {
				newFieldDeclList.add(fieldDecl);
			}
		}
		if (AnnotatedFieldDecl.Util.compare(oldFieldDeclList, newFieldDeclList)) {
			return true;
		}
		return false;
	}
}
