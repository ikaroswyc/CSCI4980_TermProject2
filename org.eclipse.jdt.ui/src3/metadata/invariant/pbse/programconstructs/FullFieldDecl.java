/*
 * @(#) FullFieldDecl.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.programconstructs;

import metadata.invariant.pbse.at.AtColumnName;
import metadata.invariant.pbse.at.AtId;
import metadata.invariant.pbse.at.AtJoinColumnName;
import metadata.invariant.pbse.at.AtJoinTableInverseJoinColumns;
import metadata.invariant.pbse.at.AtJoinTableJoinColumns;
import metadata.invariant.pbse.at.AtJoinTableName;
import metadata.invariant.pbse.transform.AnnoVal;
import util.UtilAt;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2010
 * @since JDK1.6
 */
public class FullFieldDecl {
	private String									packageName;
	private String									className;
	private FieldDecl								fieldDecl;
	private FieldDecl								fieldDeclWithId;
	private AtId									atId;
	private AtColumnName							atColumnName;
	private AtJoinTableName						atJointableName;
	private AtJoinTableJoinColumns			atJoinTableJoinColumns;
	private AtJoinTableInverseJoinColumns	atJoinTableInverseJoinColumns;
	private String									transforms;

	public FullFieldDecl(String packageName, String className, FieldDecl fieldDecl) {
		this.packageName = packageName;
		this.className = className;
		this.fieldDecl = fieldDecl;
	}

	public FullFieldDecl(String packageName, String className, FieldDecl fieldDecl, AtColumnName atColumnName) {
		this(packageName, className, fieldDecl);
		this.atColumnName = atColumnName;
		setTransforms(this.atColumnName.getName());
	}

	public FullFieldDecl(String packageName, String className, FieldDecl fieldDecl, AtJoinTableName atJointableName) {
		this(packageName, className, fieldDecl);
		this.atJointableName = atJointableName;
		setTransforms(this.atJointableName.getName(), this.className);
	}

	private void setTransforms(String attrVal) {
		String fieldName = this.fieldDecl.getName();
		AnnoVal annoval = new AnnoVal(fieldName, attrVal);
		transforms = annoval.getTransformations();
	}

	private void setTransforms(String attrVal, String className) {
		String fieldName = this.fieldDecl.getName();
		String otherClassName = UtilAt.getOtherClassName(this.fieldDecl.getType());
		AnnoVal annoval = new AnnoVal(className, otherClassName, fieldName, attrVal);
		transforms = annoval.getTransformations();
	}

	/**
	 * @METHOD
	 * To get transformation--from the field name to the attr.value,
	 * the source code should be re-scanned, which means that
	 * it is necessary to find and manipulate the specific field name
	 * having @Id after the first scan.
	 */
	public void setTransforms(AtJoinTableJoinColumns atJoinTableJoinColumns) {
		this.atJoinTableJoinColumns = atJoinTableJoinColumns;

		for (int i = 0; i < atJoinTableJoinColumns.getJoinColumns().size(); i++) {

			// * @JoinTable(.., joinColumns{@JoinColumn(name="C-NAME")})
			//
			AtJoinColumnName atJoinColName = atJoinTableJoinColumns.getJoinColumns().get(i);

			String attrVal = atJoinColName.getName();
			AnnoVal annoval = new AnnoVal(fieldDeclWithId.getName(), attrVal);
			String transform = annoval.getTransformations();
			atJoinColName.setTransforms(transform);
		}
	}

	/**
	 * @METHOD
	 */
	public void setTransforms(AtJoinTableInverseJoinColumns atJoinTableInverseJoinColumns) {
		this.atJoinTableInverseJoinColumns = atJoinTableInverseJoinColumns;

		for (int i = 0; i < atJoinTableInverseJoinColumns.getJoinColumns().size(); i++) {

			// * @JoinTable(.., inverseJoinColumns{@JoinColumn(name="C-NAME")})
			//
			AtJoinColumnName atJoinColName = atJoinTableInverseJoinColumns.getJoinColumns().get(i);

			String attrVal = atJoinColName.getName();
			AnnoVal annoval = new AnnoVal(fieldDeclWithId.getName(), attrVal);
			String transform = annoval.getTransformations();
			atJoinColName.setTransforms(transform);
		}
	}

	public FieldDecl getFieldDeclWithId() {
		return fieldDeclWithId;
	}

	public void setFieldDeclWithId(FieldDecl fieldDeclWithId) {
		this.fieldDeclWithId = fieldDeclWithId;
	}

	public AtJoinTableJoinColumns getAtJoinTableJoinColumns() {
		return atJoinTableJoinColumns;
	}

	public void setAtJoinTableJoinColumns(AtJoinTableJoinColumns atJoinTableJoinColumns) {
		this.atJoinTableJoinColumns = atJoinTableJoinColumns;
	}

	public AtJoinTableInverseJoinColumns getAtJoinTableInverseJoinColumns() {
		return atJoinTableInverseJoinColumns;
	}

	public void setAtJoinTableInverseJoinColumns(AtJoinTableInverseJoinColumns atJoinTableInverseJoinColumns) {
		this.atJoinTableInverseJoinColumns = atJoinTableInverseJoinColumns;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setFieldDecl(FieldDecl fieldDecl) {
		this.fieldDecl = fieldDecl;
	}

	public void setAtColumnName(AtColumnName atColumnName) {
		this.atColumnName = atColumnName;
	}

	public void setAtJointableName(AtJoinTableName atJointableName) {
		this.atJointableName = atJointableName;
	}

	public AtId getAtId() {
		return atId;
	}

	public void setAtId(AtId atId) {
		this.atId = atId;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public FieldDecl getFieldDecl() {
		return fieldDecl;
	}

	public AtColumnName getAtColumnName() {
		return atColumnName;
	}

	public AtJoinTableName getAtJointableName() {
		return atJointableName;
	}

	public String getTransforms() {
		return transforms;
	}

}
