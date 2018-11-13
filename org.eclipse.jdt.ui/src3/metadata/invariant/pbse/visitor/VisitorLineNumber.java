/*
 * @(#) VisitorLineNumber.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.visitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * @author Myoungkyu Song
 * @date May 9, 2012
 * @since JDK1.6
 */
public class VisitorLineNumber extends ASTVisitor {
	String					filename;
	String					fileText;
	ArrayList<Integer>	list	= new ArrayList<Integer>();

	public VisitorLineNumber() {}

	public void setFileText(String filename, String fileText) {
		this.filename = filename;
		this.fileText = fileText;
		calcLineNumber();
	}

	public boolean visit(MethodDeclaration node) {
		System.out.println("[DBG] VisitorLineNumber: " + node.getName() + ", " + node.LINE_COMMENT + ", " + node.getStartPosition());
		return true;
	}

	private void calcLineNumber() {
		for (int i = 0; i < fileText.length(); i++) {

		}
	}
}
