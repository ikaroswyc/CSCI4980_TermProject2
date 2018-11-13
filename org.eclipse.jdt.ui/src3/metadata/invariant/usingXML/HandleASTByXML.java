/*
 * @(#) HandleAST.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.usingXML;

import java.io.File;
import java.util.ArrayList;

import metadata.invariant.pbse.STR;

/**
 * @author Myoungkyu Song
 * @date Dec 29, 2010
 * @since JDK1.6
 */
public class HandleASTByXML {

	public ArrayList<AST>	list	= new ArrayList<AST>();

	/**
	 * @METHOD
	 */
	public void addAST(File javafile, ArrayList<String> fieldlist) {
		list.add(new AST(javafile, fieldlist));
	}

	/**
	 * @METHOD
	 */
	public void displayAST() {
		System.out.println("------------------------------------------");
		System.out.println();

		for (int i = 0; i < list.size(); i++) {
			AST elemAST = list.get(i);
			System.out.println("[DBG] file name: " + elemAST.getJavafile().getName());

			for (int j = 0; j < elemAST.getFieldlist().size(); j++) {
				String elemFieldDecl = elemAST.getFieldlist().get(j);
				System.out.println("[DBG] \tfield: " + elemFieldDecl);
			}
		}

		System.out.println();
		System.out.println("------------------------------------------");
	}

	/**
	 * @METHOD
	 */
	public String getFieldDecl(String theClassName, String theFieldName) {

		String result = null;

		for (int i = 0; i < list.size(); i++) {
			AST elem = list.get(i);

			// * - get Java file.
			String className = elem.getJavafile().getName().replace(STR.file_java, "");

			if (theClassName.equals(className) == false)
				continue;

			// * - get field declaration from the given Java file.
			ArrayList<String> fieldlist = elem.getFieldlist();

			for (int j = 0; j < fieldlist.size(); j++) {
				String fieldDecl = fieldlist.get(j);

				// * - the last token is the field name.
				String[] fieldDeclTokens = fieldDecl.split("\\s");
				String fieldName = fieldDeclTokens[fieldDeclTokens.length - 1].replace(";", "");

				if (fieldName.equals(theFieldName)) {
					result = fieldDecl;
					break;
				}
			}
			if (result != null)
				break;
		}

		return result;
	}

	/**
	 * @NESTED_CLASS
	 */
	public class AST {
		public File						_javafile	= null;
		public ArrayList<String>	_fieldlist	= null;

		public AST(File javafile, ArrayList<String> fieldlist) {
			_javafile = javafile;
			_fieldlist = fieldlist;
		}

		public File getJavafile() {
			return _javafile;
		}

		public ArrayList<String> getFieldlist() {
			return _fieldlist;
		}
	}
}
