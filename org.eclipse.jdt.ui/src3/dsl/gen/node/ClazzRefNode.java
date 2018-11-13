/*
 * @(#) ClazzRefNode.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl.gen.node;

import dsl.gen.visitor.CodeGeneratingVisitor;

/**
 * @author Myoungkyu Song
 * @date Aug 11, 2011
 * @since JDK1.6
 */
public class ClazzRefNode implements Node {
	private String	name	= "Class c in p";

	public String getName() {
		return name;
	}

	public void accept(CodeGeneratingVisitor visitor) {
		visitor.visitClazzRef(this);
	}
}
