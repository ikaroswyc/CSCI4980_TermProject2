/*
 * @(#) AtTable.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.at;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.programconstructs.ClassDecl;
import metadata.invariant.pbse.programconstructs.FullClassDecl;
import metadata.invariant.pbse.visitor.Visitor;
import util.UtilAt;
import util.UtilFile;

/**
 * @author John Edstrom
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class AtTable {
	Visitor	visitor;

	public AtTable(Visitor visitor) {
		this.visitor = visitor;
	}

	/**
	 * @METHOD
	 */
	public void atTableName() {
		// printAtTableName();

		System.out.println("==========================================");
		System.out.println("[DBG] * @Table.name");
		System.out.println("------------------------------------------");

		// get the most frequent transformation rule among all transformations.
		String trax = visitor.getFullClassDecl().getTransforms();

		UtilFile.println("Class c in p");
		UtilFile.println("Where (public *)");
		UtilFile.println("\t" + STR.pbse_class + "+=" + STR.at_table);

		String attrval = trax.replace(STR.var_attrval, STR.at_table_name);
		attrval = attrval.replace(STR.var_classname, STR.pbse_classname);
		attrval = attrval.replace(STR.var_otherclassname, STR.pbse_otherclassname);
		UtilFile.println("\t" + attrval);
	}

	/**
	 * @METHOD
	 */
	void printAtTableName() {
		System.out.println("==========================================");
		System.out.println("[DBG] + All Transformation of @Table.name");
		System.out.println("------------------------------------------");

		FullClassDecl elem = visitor.getFullClassDecl();

		String fullClassname = elem.getPackageName() + ".";
		fullClassname += elem.getClassDecl().getName();
		AtTableName atTableName = elem.getAtTableName();
		String annotation = atTableName.getAnnotation() + ".name=" + atTableName.getName();

		if (atTableName.getName().equals("-"))
			return;

		ClassDecl classDecl = elem.getClassDecl();

		String fullclassdecl = classDecl.getModifier() + " ";
		fullclassdecl += classDecl.getType() + " ";
		fullclassdecl += classDecl.getName();

		String trax = elem.getTransforms();

		StringBuilder sb = new StringBuilder();
		sb.append(trax + ": ");
		sb.append(fullClassname + ", ");
		sb.append(fullclassdecl + " => ");
		sb.append(annotation);

		System.out.println("[DBG] " + sb.toString());
	}

	/**
	 * @METHOD
	 */
	public static String makeAtTableName(String annotation_class, String type) {
		int pos1 = annotation_class.lastIndexOf(type);
		int pos2 = annotation_class.lastIndexOf(")", pos1);
		String annot = annotation_class.substring(0, pos2 + 1);
		String attrVal = "";

		int pos3 = annot.lastIndexOf(STR.at_table);
		if (pos3 != -1) {
			String strHasAtTable = annot.substring(pos3);
			int pos4 = strHasAtTable.indexOf("(");
			int pos5 = strHasAtTable.indexOf(")");

			String strVal = strHasAtTable.substring(pos4 + 1, pos5);
			attrVal = UtilAt.getAttrValue(strVal, "name");
			// this.atTableName = attrVal;
		}
		else {
			attrVal = "-";
			// this.atTableName = attrVal;
		}
		return STR.at_table_name + "=" + attrVal;
	}
}
