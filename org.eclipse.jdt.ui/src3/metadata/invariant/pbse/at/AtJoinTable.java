/*
 * @(#) AtJoinTable.java
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
public class AtJoinTable {
	Visitor	visitor;

	public AtJoinTable(Visitor visitor) {
		this.visitor = visitor;
	}

	/**
	 * @METHOD
	 */
	public void atJoinTableName()
	{
		// printAtJoinTableName();

		System.out.println("==========================================");
		System.out.println("[DBG] * @JoinTable.name");
		System.out.println("------------------------------------------");

		// get the most frequent transformation rule among all transformations.
		ArrayList<String> traxList = new ArrayList<String>();
		for (FullFieldDecl fullFieldDecl : visitor.getFullFieldDeclList()) {
			if (fullFieldDecl.getAtJointableName() != null)
				traxList.add(fullFieldDecl.getTransforms());
		}

		String mostFreStr = MostFrequent.getInstance().getMostFrequent(traxList);

		String whereStat = UtilPBSE.getWhereStat(visitor, mostFreStr);
		UtilFile.println(STR.pbse_fieldIter);
		UtilFile.println(String.format(STR.pbse_where, whereStat));
		UtilFile.println("\t" + STR.pbse_field + "=" + STR.at_jointable);

		String attrval = mostFreStr.replace(STR.var_attrval, STR.at_jointable_name);
		attrval = attrval.replace(STR.var_fieldname, STR.pbse_fieldname);
		attrval = attrval.replace(STR.var_classname, STR.pbse_classname);
		attrval = attrval.replace(STR.var_otherclassname, STR.pbse_fieldElemType);

		String pbse_mod_inversejcol_jtable = "InverseJoinColumnsAtJoinTable <f.etype, @JoinTable>";

		UtilFile.println("\t" + attrval);
		UtilFile.println("\t" + STR.pbse_mod_jcol_jtable);
		UtilFile.println("\t" + pbse_mod_inversejcol_jtable);
	}

	/**
	 * @METHOD
	 */
	public void atJoinTableJoinColumns()
	{
		System.out.println("==========================================");
		System.out.println("[DBG] * @JoinTable(joinColumns = { @JoinColumn.name } )");
		System.out.println("------------------------------------------");
		// get the most frequent transformation rule among all transformations.
		ArrayList<String> traxList = new ArrayList<String>();
		for (FullFieldDecl fullFieldDecl : visitor.getFullFieldDeclList()) {

			if (fullFieldDecl.getAtJoinTableJoinColumns() != null) {
				ArrayList<AtJoinColumnName> joinCols = fullFieldDecl.getAtJoinTableJoinColumns().getJoinColumns();

				for (AtJoinColumnName atJoinColumnName : joinCols) {
					traxList.add(atJoinColumnName.getTransforms());
				}
			}
		}

		String mostFreStr = MostFrequent.getInstance().getMostFrequent(traxList);

		String pbse_joinColumnsAtJoinTable = "Metadata JoinColumnsAtJoinTable<Class c, Metadata @JoinTable>";

		UtilFile.println(pbse_joinColumnsAtJoinTable);
		UtilFile.println(STR.pbse_fieldIter);
		UtilFile.println(STR.pbse_where_id);

		String attrval = mostFreStr.replace(STR.var_attrval, STR.at_jtable_jcols_jcol_name);
		attrval = attrval.replace(STR.var_fieldname, STR.pbse_fieldname);
		UtilFile.println("\t" + attrval);
	}

	/**
	 * @METHOD
	 */
	public void atJoinTableInverseJoinColumns()
	{
		System.out.println("==========================================");
		System.out.println("[DBG] * @JoinTable(inverseJoinColumns = { @JoinColumn.name } )");
		System.out.println("------------------------------------------");
		// get the most frequent transformation rule among all transformations.
		ArrayList<String> traxList = new ArrayList<String>();
		for (FullFieldDecl fullFieldDecl : visitor.getFullFieldDeclList()) {

			if (fullFieldDecl.getAtJoinTableInverseJoinColumns() != null) {
				ArrayList<AtJoinColumnName> joinCols =
						fullFieldDecl.getAtJoinTableInverseJoinColumns().getJoinColumns();

				for (AtJoinColumnName atJoinColumnName : joinCols) {
					traxList.add(atJoinColumnName.getTransforms());
				}
			}
		}

		String mostFreStr = MostFrequent.getInstance().getMostFrequent(traxList);

		String pbse_inverseJoinColumnsAtJoinTable =
				"Metadata InverseJoinColumnsAtJoinTable<Class c, Metadata @JoinTable>";

		UtilFile.println(pbse_inverseJoinColumnsAtJoinTable);
		UtilFile.println(STR.pbse_fieldIter);
		UtilFile.println(STR.pbse_where_id);

		String attrval = mostFreStr.replace(STR.var_attrval, STR.at_jtable_inversejcols_jcol_name);
		attrval = attrval.replace(STR.var_fieldname, STR.pbse_fieldname);
		UtilFile.println("\t" + attrval);
	}

	/**
	 * @METHOD
	 */
	void printAtJoinTableName()
	{
		System.out.println("==========================================");
		System.out.println("[DBG] + All Transformation of @JoinTable.name");
		System.out.println("------------------------------------------");

		ArrayList<FullFieldDecl> fullFieldDeclList = visitor.getFullFieldDeclList();
		for (int i = 0; i < fullFieldDeclList.size(); i++) {
			FullFieldDecl elem = fullFieldDeclList.get(i);

			String fullClassname = elem.getPackageName() + ".";
			fullClassname += elem.getClassName();

			Annotation at = elem.getAtJointableName();

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
	public static String makeAtJoinTableName(String annotation_field, String type, String shortfieldname)
	{
		int pos1 = annotation_field.lastIndexOf(type);
		int pos2 = annotation_field.lastIndexOf(")", pos1);
		String annot = annotation_field.substring(0, pos2 + 1);

		String attrVal = "";

		int pos3 = annot.lastIndexOf(STR.at_jointable);
		if (pos3 != -1) {
			String strHasAtJointable = annot.substring(pos3);

			String[] tok = strHasAtJointable.split(",");
			for (int i = 0; i < tok.length; i++) {
				String subAnnot = tok[i];

				if (subAnnot.startsWith(STR.at_jointable)) {
					int pos4 = strHasAtJointable.indexOf("(");
					String strVal = strHasAtJointable.substring(pos4 + 1);
					attrVal = UtilAt.getAttrValue(strVal, "name");
				}

				else if (subAnnot.startsWith(STR.str_joincol)) {
					attrVal += ",joinColumns=" + makeJoinColumnNameAtJoinTableJoinColumns(subAnnot);
				}

				else if (subAnnot.startsWith(STR.str_injoincol)) {
					attrVal += ",inverseJoinColumns=" + makeJoinColumnNameAtJoinTableInverseJoinColumns(subAnnot);
				}
			}
		}
		else {
			attrVal = "-";
		}
		return STR.at_jointable_name + "=" + attrVal;
	}

	/**
	 * @METHOD
	 */
	public static String makeJoinColumnNameAtJoinTableJoinColumns(String annotation)
	{
		int pos4 = annotation.indexOf("(");
		int pos5 = annotation.indexOf(")");
		String strVal = annotation.substring(pos4 + 1, pos5);
		String joinColumnName = UtilAt.getAttrValue(strVal, "name");
		return joinColumnName;
	}

	/**
	 * @METHOD
	 */
	public static String makeJoinColumnNameAtJoinTableInverseJoinColumns(String annotation)
	{
		int pos4 = annotation.indexOf("(");
		int pos5 = annotation.indexOf(")");
		String strVal = annotation.substring(pos4 + 1, pos5);
		String joinColumnName = UtilAt.getAttrValue(strVal, "name");
		return joinColumnName;
	}

	/**
	 * @METHOD
	 */
	public static void handleAtJoinTable(String pairAnnotationAttrvalue, Visitor visitor)
	{
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

		String[] tok = pairAnnotationAttrvalue.split(",");
		String[] strs;
		String attrVal;

		for (int i = 0; i < tok.length; i++) {

			String jointableToken = tok[i];
			//
			// (1) @JoinTable(name = "STUDENT_COURSE",
			//
			if (jointableToken.startsWith(STR.at_jointable_name)) {
				strs = jointableToken.split("=");
				attrVal = strs[1];

				AtJoinTableName atJoinTableName = new AtJoinTableName(attrVal);
				// create the one full field declaration.
				fullFieldDecl = new FullFieldDecl(packageName, className, fieldecl, atJoinTableName);
				fullFieldDeclList.add(fullFieldDecl);
			}
			//
			// (2) joinColumns = { @JoinColumn(name = "STUDENT_ID") },
			//
			else if (jointableToken.startsWith(STR.str_joincol)) {
				strs = jointableToken.split("=");
				attrVal = strs[1];

				AtJoinColumnName atJoinColName = new AtJoinColumnName(attrVal);
				ArrayList<AtJoinColumnName> joinCols = new ArrayList<AtJoinColumnName>();
				joinCols.add(atJoinColName);
				AtJoinTableJoinColumns atJTJoinCols = new AtJoinTableJoinColumns(joinCols);

				// * Get the field with @Id in the current class.
				//
				FieldDecl fieldDeclWithId = UtilAt.getFieldWithId(fullFieldDeclList);

				fullFieldDecl = fullFieldDeclList.get(fullFieldDeclList.size() - 1);
				fullFieldDecl.setFieldDeclWithId(fieldDeclWithId);
				fullFieldDecl.setTransforms(atJTJoinCols);
			}
			//
			// (3) inverseJoinColumns = { @JoinColumn(name = "COURSE_ID") })
			//
			else if (jointableToken.startsWith(STR.str_injoincol)) {
				strs = jointableToken.split("=");
				attrVal = strs[1];

				AtJoinColumnName atJoinColName = new AtJoinColumnName(attrVal);
				ArrayList<AtJoinColumnName> joinCols = new ArrayList<AtJoinColumnName>();
				joinCols.add(atJoinColName);
				AtJoinTableInverseJoinColumns atJTInverseJoinCols = new AtJoinTableInverseJoinColumns(joinCols);

				// * Get the field with @Id in the other join class.
				//
				FieldDecl fieldDeclWithId = UtilAt.getFieldWithId(fieldecl.getType());

				fullFieldDecl = fullFieldDeclList.get(fullFieldDeclList.size() - 1);
				fullFieldDecl.setFieldDeclWithId(fieldDeclWithId);
				fullFieldDecl.setTransforms(atJTInverseJoinCols);
			}
		}
	}
}
