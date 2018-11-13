/*
 * @(#) PbseGen.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl.gen;

import metadata.invariant.pbse.STR;
import dsl.Starter;
import dsl.gen.node.ClazzRefNode;
import dsl.gen.node.FieldRefNode;
import dsl.gen.node.MethodRefNode;
import dsl.gen.node.WhereBodyNode;
import dsl.gen.node.WhereQueryNode;
import dsl.gen.visitor.CodeGeneratingVisitor;

/**
 * @author Myoungkyu Song
 * @date Aug 18, 2011
 * @since JDK1.6
 */
public class DSLGen extends Starter {
	public DSLGen() {
		super();
	}

	public void pbseGenProc()
	{
		CodeGeneratingVisitor codeGenVisitor = new CodeGeneratingVisitor();
		//System.out.println("[DBG] >>> Auto-Generated PBSE Specification <<<");
		//System.out.println("------------------------------------------");

		codeGenVisitor.fileopen();
		accept(codeGenVisitor);
		codeGenVisitor.fileclose();
	}

	/** @METHOD */
	public void accept(CodeGeneratingVisitor visitor)
	{
		if (relationContains("class") || relationVarContains("class")) {
			new ClazzRefNode().accept(visitor);
		}
		else if (relationContains("method")) {
			// System.out.println("[DBG]" + getPattern());
			if (getPattern().startsWith(STR.pbse_ptrn_contains)) {
				new ClazzRefNode().accept(visitor);
				new MethodRefNode().accept(visitor);
				new WhereQueryNode().accept(visitor);
				new WhereBodyNode().accept(visitor);
				return;
			}
			else {
				new MethodRefNode().accept(visitor);
			}
		}
		else if (relationContains("field")) {
			/* contains */
			if (getPattern().startsWith(STR.pbse_ptrn_contains + "(" + // "CONTAINS("
					STR.var_metadata + ".ATTRVAL")) { // "$METADATA.ATTRVAL"
				new ClazzRefNode().accept(visitor);
				new WhereQueryNode().accept(visitor);
				new FieldRefNode().accept(visitor);
				new WhereBodyNode().accept(visitor);
				return;
			}
			/* match */
			if (getPattern().startsWith(STR.pbse_ptrn_match + "(" + //
					"$" + STR.relation_annotation_attribute + ".NAME-OR-EMPTY")) { //
				new FieldRefNode().accept(visitor);
				new WhereQueryNode().accept(visitor);
				new WhereBodyNode().accept(visitor);
				return;
			}
			new FieldRefNode().accept(visitor);
		}
		new WhereQueryNode().accept(visitor);
		new WhereBodyNode().accept(visitor);
	}
}
