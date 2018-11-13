/*
 * @(#) Visitor.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.visitor;

import java.util.ArrayList;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.at.AtColumn;
import metadata.invariant.pbse.at.AtId;
import metadata.invariant.pbse.at.AtJoinTable;
import metadata.invariant.pbse.at.AtTable;
import metadata.invariant.pbse.at.AtTableName;
import metadata.invariant.pbse.programconstructs.ClassDecl;
import metadata.invariant.pbse.programconstructs.FieldDecl;
import metadata.invariant.pbse.programconstructs.FullClassDecl;
import metadata.invariant.pbse.programconstructs.FullFieldDecl;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * @authors Myoungkyu Song & John Edstrom
 * @date Oct 26, 2010
 * @since JDK1.6
 */
public class Visitor extends ASTVisitor {
	String							packageName			= null;
	String							className			= null;
	ArrayList<String>				shortfieldnames	= new ArrayList<String>();
	ArrayList<String>				fullfieldnames		= new ArrayList<String>();

	ArrayList<FieldDecl>			fieldDeclList		= new ArrayList<FieldDecl>();
	ArrayList<FullFieldDecl>	fullFieldDeclList	= new ArrayList<FullFieldDecl>();
	FullClassDecl					fullClassDecl		= null;
	ClassDecl						classDecl			= null;
	String							fullClassName		= null;

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public ArrayList<String> getFullfieldnames() {
		return fullfieldnames;
	}

	public ArrayList<String> getShortfieldnames() {
		return shortfieldnames;
	}

	public FullClassDecl getFullClassDecl() {
		return fullClassDecl;
	}

	public ArrayList<FieldDecl> getFieldDeclList() {
		return fieldDeclList;
	}

	public ArrayList<FullFieldDecl> getFullFieldDeclList() {
		return fullFieldDeclList;
	}

	/**
	 * @METHOD
	 */
	public boolean visit(PackageDeclaration node) {
		this.packageName = node.getName().toString();
		return true;
	}

	/**
	 * @METHOD
	 */
	public boolean visit(TypeDeclaration node) {
		String annotation_class = node.toString().trim();
		String type = node.getName().toString();

		this.className = type;

		if (node.isInterface())
			fullClassName = makeFullClassName(annotation_class, "interface");
		else
			fullClassName = makeFullClassName(annotation_class, "class");

		String pairAnnotationAttrValue = AtTable.makeAtTableName(annotation_class, type);

		makeFullClassDecl(pairAnnotationAttrValue);

		return true;
	}

	/**
	 * @METHOD
	 */
	public boolean visit(FieldDeclaration node) {
		String annotation_field = node.toString().trim();
		String type = node.getType().toString();

		System.out.println(annotation_field);

		String fullfieldname = makeFullfieldname(annotation_field, type);
		fullfieldnames.add(fullfieldname);

		String shortfieldname = makeShortfieldname(fullfieldname);
		shortfieldnames.add(shortfieldname);

		String pairAnnotationAttrvalue;

		// [check] @Id
		//
		pairAnnotationAttrvalue = AtId.makeAtId(annotation_field, type, shortfieldname);

		if (pairAnnotationAttrvalue.endsWith("-")) {

			// [check] @Column - '@Column' is added soley.
			//
			pairAnnotationAttrvalue = AtColumn.makeAtColumnName(annotation_field, type, shortfieldname);
		}
		else {

			// [check] @Column - '@Id' and '@Column' are added simultaneously.
			//
			String tmp1 = AtColumn.makeAtColumnName(annotation_field, type, shortfieldname);
			pairAnnotationAttrvalue = tmp1 + ":" + pairAnnotationAttrvalue;
		}

		// [check] @JoinTable.name
		// [check] joinColumns.@JoinColumn.name
		// [check] inverseJoinColumns.@JoinColumn.name
		//
		if (pairAnnotationAttrvalue.endsWith("-")) {
			pairAnnotationAttrvalue = AtJoinTable.makeAtJoinTableName(annotation_field, type, shortfieldname);
		}

		// [complete] the full field declarations including the annotation information.
		//
		makeFullFieldDecl(pairAnnotationAttrvalue);

		return false;
	}

	/**
	 * @METHOD
	 */
	private void makeFullFieldDecl(String pairAnnotationAttrvalue) {

		if (pairAnnotationAttrvalue.startsWith(STR.at_column_name)) {
			//
			// handle @Id & @Column.name.
			//
			AtColumn.handleAtColumn(pairAnnotationAttrvalue, this);
		}
		else if (pairAnnotationAttrvalue.startsWith(STR.at_jointable_name)) {
			//
			// handle @JoinTable.name, joinColumns, inverseJoinColumns, @JoinColumn.name.
			//
			AtJoinTable.handleAtJoinTable(pairAnnotationAttrvalue, this);
		}
	}

	/**
	 * @METHOD
	 */
	private String makeFullfieldname(String annotation_field, String type) {
		int pos1 = annotation_field.lastIndexOf(type);
		int pos2 = annotation_field.lastIndexOf(")", pos1);
		String fullfieldname = annotation_field.substring(pos2 + 1).trim();

		if (fullfieldname.trim().endsWith(";")) {
			fullfieldname = fullfieldname.substring(0, fullfieldname.length() - 1);
		}

		if (fullfieldname.contains("=")) {
			int pos3 = fullfieldname.indexOf("=");
			fullfieldname = fullfieldname.substring(0, pos3);
		}

		String[] fullfieldnameDecl = fullfieldname.split("\\s");

		FieldDecl fieldDecl = null;
		String modifier = "";
		String shortname = "";

		if (fullfieldnameDecl.length == 2) {
			modifier = "public";
			shortname = fullfieldnameDecl[1];
			fieldDecl = new FieldDecl(modifier, type, shortname);
		}
		else {
			int typeindex = -1;
			// parse modifiers in front of the position of the type string.
			for (int i = 0; i < fullfieldnameDecl.length; i++) {
				String elem = fullfieldnameDecl[i];
				if (elem.trim().equals(type)) {
					typeindex = i;
					break;
				}
			}
			// parse and create FieldDecl instance.
			for (int i = 0; i < fullfieldnameDecl.length; i++) {
				String elem = fullfieldnameDecl[i].trim();

				if (i < typeindex) {
					modifier += (elem + " ");
				}
				else if (i > typeindex) {
					shortname += elem;
				}
			}
			fieldDecl = new FieldDecl(modifier.trim(), type, shortname);
		}
		// add every field declration to the list at the level of the class unit.
		this.fieldDeclList.add(fieldDecl);
		return fullfieldname;
	}

	/**
	 * @METHOD
	 */
	private String makeShortfieldname(String field) {
		String[] fullfieldname = field.split("\\s");
		int size = fullfieldname.length;
		String shortfieldname = fullfieldname[size - 1];
		return shortfieldname;
	}

	/**
	 * @METHOD
	 */
	private void makeFullClassDecl(String pairAnnotationAttrValue) {
		if (pairAnnotationAttrValue.startsWith(STR.at_table_name)) {
			String[] strs = pairAnnotationAttrValue.split("=");
			String attrVal = strs[1];
			AtTableName atTableName = new AtTableName(attrVal);
			// create the one full field declaration.
			this.fullClassDecl = new FullClassDecl(packageName, this.classDecl, atTableName);
		}
	}

	/**
	 * @METHOD
	 */
	private String makeFullClassName(String annotation_class, String type) {
		int pos1 = annotation_class.lastIndexOf(type + " " + this.className);
		int pos2 = annotation_class.lastIndexOf(")", pos1);
		int pos3 = annotation_class.indexOf("{", pos2);
		String fullClassName = annotation_class.substring(pos2 + 1, pos3).trim();

		if (fullClassName.trim().endsWith("{")) {
			fullClassName = fullClassName.substring(0, fullClassName.length() - 1);
		}
		String[] fullClassNameDecl = fullClassName.split("\\s");

		String modifier = "";

		if (fullClassNameDecl.length == 3) {
			modifier = fullClassNameDecl[0];
			this.classDecl = new ClassDecl(modifier, type, this.className);
		}
		else {
			int typeindex = -1;
			// parse modifiers in front of the position of the type string.
			for (int i = 0; i < fullClassNameDecl.length; i++) {
				String elem = fullClassNameDecl[i];
				if (elem.trim().equals(type)) {
					typeindex = i;
					break;
				}
			}
			// parse and create ClassDecl instance.
			for (int i = 0; i < fullClassNameDecl.length; i++) {
				String elem = fullClassNameDecl[i].trim();

				if (i < typeindex) {
					modifier += (elem + " ");
				}
			}
			this.classDecl = new ClassDecl(modifier.trim(), type, this.className);
		}
		return fullClassName;
	}
}
