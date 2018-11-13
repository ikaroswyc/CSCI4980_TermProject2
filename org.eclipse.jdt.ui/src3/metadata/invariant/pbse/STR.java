/*
 * @(#) STR.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse;

/**
 * @author Myoungkyu Song & John Edstrom
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class STR extends Comm {
	public static String at_column = "@Column";
	public static String at_column_name = at_column + ".name";
	public static String at_configuration = "@Configuration";
	public static String at_id = "@Id";
	public static String at_jointable = "@JoinTable";
	public static String at_jointable_name = at_jointable + ".name";
	public static String at_jtable_inversejcols_jcol_name = "@JoinTable.inverseJoinColumns.@JoinColumn.name";
	public static String at_jtable_jcols_jcol_name = "@JoinTable.joinColumns.@JoinColumn.name";

	public static String at_table = "@Table";
	public static String at_table_name = "@Table.name";

	public static String delim = "#";
	public static String expr_underscore = "underscore(%s)";

	public static String file_dsl = ".mil";
	public static String file_java = ".java";
	public static String file_jpa_pbse = "jpa.pbse";
	public static String file_test = "test";

	public static String line = "------------------------------------------";

	public static String parm_annotation = "annotation";
	public static String parm_attrval = "ATTRVAL";
	public static String parm_check_type = "check-type";
	public static String parm_field = "field";
	public static String parm_inference = "inference";
	public static String parm_inspection_target = "inspection-target";
	public static String parm_metadata = "metadata";
	public static String parm_method = "method";
	public static String parm_name = "NAME";
	public static String parm_name2 = "name";
	public static String parm_ommission = "ommission";
	public static String parm_path = "path";
	public static String parm_progconst = "programconstruct";
	public static String parm_relation = "relation";
	public static String parm_relation_save = "relation-save";
	public static String parm_tag = "parm_tag";
	public static String parm_attr = "parm_attr";

	public static String parm_violation_check = "violation-check";
	public static String parm_xml = "xml";

	public static String pbse_class = "c";
	public static String pbse_classname = "c.name";
	public static String pbse_classSuper = "c.super";
	public static String pbse_decl_class = "Class";
	public static String pbse_decl_field = "Field";
	public static String pbse_decl_method = "Method";

	public static String pbse_decl_superclass = "SuperClass";
	public static String pbse_field = "f";
	public static String pbse_fieldElemType = "f.etype";
	public static String pbse_fieldIter = "Field f in c";
	public static String pbse_fieldname = "f.name";

	public static String pbse_method = "m";
	public static String pbse_methodname = "m.name";
	public static String pbse_methodreturntype = "m.returntype";

	public static String pbse_mod_jcol_jtable = "JoinColumnsAtJoinTable<c, @JoinTable>";
	public static String pbse_op_append = "+=";
	public static String pbse_op_contain = "%s == [%s]";

	public static String pbse_otherclass = "d";
	public static String pbse_otherclassname = "d.name";

	public static String pbse_ptrn_contains = "CONTAINS";
	public static String pbse_ptrn_endswith = "ENDSWITH";
	public static String pbse_ptrn_startswith = "STARTSWITH";
	public static String pbse_ptrn_lowfirstchar = "LOWFIRSTCHAR";
	public static String pbse_ptrn_match = "MATCH";
	public static String pbse_ptrn_omit = "OMIT";
	public static String pbse_ptrn_prefix = "PREFIX";
	public static String pbse_ptrn_uppercase = "UPPERCASE";

	public static String pbse_syn_in = "in";
	public static String pbse_where = "Where(%s)";
	public static String pbse_where2 = "Where";
	public static String pbse_where_id = "Where (private * *->@Id)";

	public static String msg_assert_missing = "\"%s missing %s\"";

	public static String mil_assert_exists = "AssertExists";
	public static String mil_assert = "Assert";
	public static String mil_has = "has";
	public static String mil_eq = "eq";

	public static String relation_annotation_attribute = "ANNOTATION_ATTRIBUTE";
	public static String relation_class_table = "[class, name, table]";
	public static String relation_className_annotationName = "classNameAnnotationName";
	public static String relation_class = "CLASS";
	public static String relation_field = "FIELD";
	public static String relation_method = "METHOD";
	public static String relation_modifier = "MODIFIER";
	public static String relation_method_returntype = "METHOD_RETURNTYPE";

	public static String sp = " ";
	public static String sp_indent = "   ";

	public static String str_empty = "";
	public static String str_injoincol = "inverseJoinColumns";
	public static String str_joincol = "joinColumns";
	public static String str_underScore = "_";

	public static String var_annotation = "$ANNOTATION";
	public static String var_attrval = "$attrval";
	public static String var_classname = "$classname";
	public static String var_classname2 = "$CLASSNAME";
	public static String var_classnamebig = "$CLASSNAME";
	public static String var_fieldname = "$fieldname";
	public static String var_metadata = "$METADATA";
	public static String var_method = "$METHOD";
	public static String var_methodname = "$METHODNAME";
	public static String var_otherclassname = "$otherclassname";
	public static String var_superclass = "$SUPERCLASS";
}
