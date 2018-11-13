/*
 * @(#) AtColumn.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.at;

import java.util.ArrayList;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.programconstructs.FieldDecl;
import metadata.invariant.pbse.programconstructs.FullFieldDecl;
import metadata.invariant.pbse.util.MostFrequent;
import metadata.invariant.pbse.visitor.Visitor;
import util.UtilAt;
import util.UtilFile;
import util.UtilPBSE;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class AtColumn {

	Visitor	visitor;

	public AtColumn() {}

	public AtColumn(Visitor visitor) {
		this.visitor = visitor;
	}

	/**
	 * @METHOD
	 */
	public void atColumnName() {
		// printAtColumnName();

		System.out.println("==========================================");
		System.out.println("[DBG] * @Column.name");
		System.out.println("------------------------------------------");

		// get the most frequent transformation rule among all transformations.
		ArrayList<String> traxList = new ArrayList<String>();
		for (FullFieldDecl fullFieldDecl : visitor.getFullFieldDeclList()) {
			// System.err.println(fullFieldDecl.getFieldDecl().getName());
			traxList.add(fullFieldDecl.getTransforms());
		}

		String mostFreStr = MostFrequent.getInstance().getMostFrequent(traxList);

		String whereStat = UtilPBSE.getWhereStat(visitor, mostFreStr);
		UtilFile.println(STR.pbse_fieldIter);
		UtilFile.println(String.format(STR.pbse_where, whereStat));
		UtilFile.println("\t" + STR.pbse_field + "=" + STR.at_column);

		String attrval = mostFreStr.replace(STR.var_attrval, STR.at_column_name);
		attrval = attrval.replace(STR.var_fieldname, STR.pbse_fieldname);
		attrval = attrval.replace(STR.var_classname, STR.pbse_classname);
		attrval = attrval.replace(STR.var_otherclassname, STR.pbse_otherclassname);
		UtilFile.println("\t" + attrval);
	}

	/**
	 * @METHOD
	 */
	void printAtColumnName() {
		System.out.println("==========================================");
		System.out.println("[DBG] + All Transformation of @Column.name");
		System.out.println("------------------------------------------");

		ArrayList<FullFieldDecl> fullFieldDeclList = visitor.getFullFieldDeclList();
		for (int i = 0; i < fullFieldDeclList.size(); i++) {
			FullFieldDecl elem = fullFieldDeclList.get(i);

			String fullClassname = elem.getPackageName() + ".";
			fullClassname += elem.getClassName();

			Annotation at = elem.getAtColumnName();

			if (at == null)
				continue;

			String annotation = at.getAnnotation() + "=" + at.getName();

			FieldDecl fieldDecl = elem.getFieldDecl();
			String fullfieldname = fieldDecl.getModifier() + " ";
			fullfieldname += fieldDecl.getType() + " ";
			fullfieldname += fieldDecl.getName();

			String trax = elem.getTransforms();

			StringBuilder sb = new StringBuilder();
			sb.append(trax + ": ");
			sb.append(fullClassname + ", ");
			sb.append(fullfieldname + " => ");
			sb.append(annotation);

			System.out.println("[DBG] " + sb.toString());
		}
	}

	/**
	 * @METHOD
	 */
	public static String makeAtColumnName(String annotation_field, String type, String shortfieldname) {
		int pos1 = annotation_field.lastIndexOf(type);
		int pos2 = annotation_field.lastIndexOf(")", pos1);
		String annot = annotation_field.substring(0, pos2 + 1);
		String attrVal = "";

		int pos3 = annot.lastIndexOf(STR.at_column);
		if (pos3 != -1) {
			String strHasAtColumn = annot.substring(pos3);
			int pos4 = strHasAtColumn.indexOf("(");
			int pos5 = strHasAtColumn.indexOf(")");

			String strVal = strHasAtColumn.substring(pos4 + 1, pos5);
			attrVal = UtilAt.getAttrValue(strVal, "name");
		}
		else {
			attrVal = "-";
		}
		return STR.at_column_name + "=" + attrVal;
	}

	/**
	 * @param pairAnnotationAttrvalue
	 * @METHOD
	 */
	public static void handleAtColumn(String pairAnnotationAttrvalue, Visitor visitor) {
		// * Get information from visitor.
		//
		String packageName = visitor.getPackageName();
		String className = visitor.getClassName();
		ArrayList<FieldDecl> fieldDeclList = visitor.getFieldDeclList();
		ArrayList<FullFieldDecl> fullFieldDeclList = visitor.getFullFieldDeclList();

		// * Pop a field declaration.
		//
		FieldDecl fieldecl = fieldDeclList.get(fieldDeclList.size() - 1);
		FullFieldDecl fullFieldDecl = null;

		String tokens1[], tokens2[];
		String colAnnotation;

		// handle @Id
		if (pairAnnotationAttrvalue.endsWith(STR.at_id)) {

			tokens1 = pairAnnotationAttrvalue.split(":");
			colAnnotation = tokens1[0];
			tokens2 = colAnnotation.split("=");
		}
		else {
			tokens2 = pairAnnotationAttrvalue.split("=");
		}

		String attrVal = tokens2[1];
		AtColumnName atColumnName = new AtColumnName(attrVal);

		// create the one full field declaration.
		fullFieldDecl = new FullFieldDecl(packageName, className, fieldecl, atColumnName);
		if (pairAnnotationAttrvalue.endsWith(STR.at_id)) {
			fullFieldDecl.setAtId(new AtId());
		}

		fullFieldDeclList.add(fullFieldDecl);
	}
}
