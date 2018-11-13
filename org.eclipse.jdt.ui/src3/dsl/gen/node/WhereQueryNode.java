/*
 * @(#) WhereStmtNode.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl.gen.node;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import dsl.gen.ParameterHelper;
import dsl.gen.visitor.CodeGeneratingVisitor;

/**
 * @author Myoungkyu Song
 * @date Aug 11, 2011
 * @since JDK1.6
 */
public class WhereQueryNode extends ParameterHelper implements Node {
	private String				decl;

	private Helper4Clazz		helper4clazz	= new Helper4Clazz();
	private Helper4Method	helper4method	= new Helper4Method();
	private Helper4Field		helper4field	= new Helper4Field();

	public void accept(CodeGeneratingVisitor visitor)
	{
		visitor.visitWhereQuery(this);
	}

	/** @METHOD */
	public String getQuery()
	{
		if (relationContains("class") || relationVarContains("class")) {
			helper4clazz.getQuery();
		}
		else if (relationContains(parm_method)) {

			if (getPattern().startsWith(STR.pbse_ptrn_contains)) {
				String annotation = Comm.getParm("relation").split(",")[2].trim();
				helper4method.getQuery(annotation);
			}
			else {
				helper4method.getQuery();
			}
		}
		else if (relationContains("field") && //
				getPattern().startsWith(STR.pbse_ptrn_contains + "(" + // contains
						STR.var_metadata + "." + "ATTRVAL")) // metadata's attribute value
		{
			String annotation = Comm.getParm("relation").split(",")[3].trim();
			helper4clazz.getQuery(annotation);
		}
		else if (relationContains("field") && //
				getPattern().startsWith(STR.pbse_ptrn_match + "(" + //
						"$" + STR.relation_annotation_attribute + ".NAME-OR-EMPTY")) //
		{
			String annotation = Comm.getParm("relation").split(",")[3].trim();
			helper4field.getQuery(annotation);
		}
		else if (relationContains("field")) {
			helper4field.getQuery();
		}
		return String.format(STR.pbse_where, decl);
	}

	/**
	 * @author Myoungkyu Song
	 * @date Aug 17, 2011
	 * @since JDK1.6
	 */
	class Helper4Clazz {
		/** @METHOD */
		public void getQuery(String annotation)
		{
			decl = annotation + "*" + " public class *"; // default
		}

		/** @METHOD */
		public void getQuery()
		{
			decl = "public class *"; // default

			String pattern = getPattern(); // (e.g., ENDSWITH($CLASSNAME, $ANNOTATIONNAME))

			if (pattern.startsWith(pbse_ptrn_endswith))
				decl += getRelationParameter(parm_annotation);
		}
	}

	/**
	 * @author Myoungkyu Song
	 * @date Aug 17, 2011
	 * @since JDK1.6
	 */
	class Helper4Method {

		/** @METHOD */
		public void getQuery()
		{
			decl = "public * *"; // default

			// String relation = Comm.getParm(parm_relation);
			String pattern = getPattern();

			// if (relation.split(",").length > 3 &&
			// relation.split(",")[3].trim().startsWith("@") &&
			// relation.split(",")[3].trim().contains("*")) {
			//
			// String annotationPattern = relation.split(",")[3].trim();
			// decl = annotationPattern + sp + decl;
			// }
			// else
			if (pattern.startsWith(pbse_ptrn_contains)) {
				if (hasVarInPattern(var_method, pattern) && hasVarInPattern(var_superclass, pattern))
					decl = pbse_method + sp + "==" + sp + pbse_class + "." + pbse_decl_superclass + "." + parm_method;
			}
		}

		public void getQuery(String annotation)
		{
			decl = annotation + " " + STR.pbse_method;
		}
	}

	/**
	 * @author Myoungkyu Song
	 * @date Aug 17, 2011
	 * @since JDK1.6
	 */
	class Helper4Field {

		/** @METHOD */
		public void getQuery()
		{
			decl = "private * *"; // default
		}

		public void getQuery(String annotation)
		{
			decl = annotation + " " + STR.pbse_field;
		}
	}
}
