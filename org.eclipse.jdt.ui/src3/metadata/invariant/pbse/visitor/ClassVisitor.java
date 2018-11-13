/*
 * @(#) ClassVisitor.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.visitor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import relation.progelem.AnnotatedClassDecl;
import relation.progelem.AnnotatedFieldDecl;
import relation.progelem.AnnotatedMethodDecl;
import util.UtilStr;

enum CurVisit {
	invalid, clazzVisit, methodVisit, fieldvisit
}

/**
 * @author Myoungkyu Song
 * @date Dec 27, 2010
 * @since JDK1.6
 */
public class ClassVisitor extends ASTVisitor {
	private String							superclassName;
	private String							packageName;
	private String							className;
	private String							ifaceName;
	private String							classAnnotation;
	private String							methodDeclWithAnnotation;
	private String							methodDecl;
	private String							methodName;
	private String							fileText;

	private boolean						classAnnotated			= false;
	private boolean						methodAnnotated		= false;

	// [[[ old version ]]]
	private ArrayList<String>			fieldList				= new ArrayList<String>();
	private ArrayList<String>			methodAnnotationList	= new ArrayList<String>();

	// [[[ new version ]]]
	private AnnotatedClassDecl			classDeclRef;
	public List<AnnotatedClassDecl>	classDeclRefList		= new ArrayList<AnnotatedClassDecl>();

	private AnnotatedMethodDecl		methodDeclRef;
	public List<AnnotatedMethodDecl>	methodDeclRefList		= new ArrayList<AnnotatedMethodDecl>();

	private AnnotatedFieldDecl			fieldDeclRef;
	public List<AnnotatedFieldDecl>	fieldDeclRefList		= new ArrayList<AnnotatedFieldDecl>();

	Stack<String>							clazzes					= new Stack<String>();
	Stack<String>							superclazzes			= new Stack<String>();
	ArrayList<Integer>					lineNumList				= new ArrayList<Integer>();

	CurVisit									curVisit					= CurVisit.invalid;

	public List<AnnotatedFieldDecl> getFieldDeclRefList()
	{
		return fieldDeclRefList;
	}

	public ArrayList<String> getFieldList()
	{
		return fieldList;
	}

	public String getClassName()
	{
		return className;
	}

	public String getIfaceName()
	{
		return ifaceName;
	}

	public String getClassAnnotation()
	{
		return classAnnotation;
	}

	public ArrayList<String> getMethodAnnotationList()
	{
		return methodAnnotationList;
	}

	void setCurVisit(CurVisit cur)
	{
		curVisit = cur;
	}

	CurVisit getCurVisit()
	{
		return curVisit;
	}

	public void setFileText(String fileText)
	{
		this.fileText = fileText;
	}

	public void setFileName(String filename)
	{
		int sumOfChar = 0;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null)
			{
				int len = line.length() + 1;
				sumOfChar += len;
				this.lineNumList.add(sumOfChar);
			}
			reader.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** @METHOD */
	public boolean visit(PackageDeclaration node)
	{
		this.packageName = node.getName().toString();
		return true;
	}

	/** @METHOD */
	public boolean visit(TypeDeclaration node)
	{
		setCurVisit(CurVisit.clazzVisit);

		String myname = "";
		classAnnotated = true;
		methodAnnotated = false;
		String type = node.getName().toString();
		if (node.isInterface() == false)
		{
			this.className = this.packageName + "." + type;
			myname = this.className;
		}
		else
		{
			this.ifaceName = this.packageName + "." + type;
			myname = this.ifaceName;
		}

		this.superclassName = "" + node.getSuperclassType();
		this.superclassName = UtilStr.getShortClassName(this.superclassName);

		clazzes.push(myname);
		superclazzes.push(this.superclassName);

		classDeclRef = new AnnotatedClassDecl(clazzes.peek(), superclazzes.peek());
		classDeclRefList.add(classDeclRef);
		return true;
	}

	/** @METHOD */
	public void endVisit(TypeDeclaration node)
	{
		clazzes.pop();
		superclazzes.pop();
	}

	/** @METHOD */
	public boolean visit(MarkerAnnotation node)
	{
		setMarkerAnnotation(node);

		if (methodAnnotated)
			methodDeclRef.markerAnnotationList.add(node.toString());

		if (classAnnotated)
		{
			classAnnotation = node.toString();
		}
		return true;
	}

	/** @METHOD */
	private void setMarkerAnnotation(MarkerAnnotation node)
	{
		switch (curVisit) {
		case clazzVisit:
			classDeclRef.addMarkerAnnotationList(node.toString());
			break;
		case fieldvisit:
			fieldDeclRef.addMarkerAnnotationList(node.toString());
			break;
		}
	}

	/** @METHOD */
	public boolean visit(NormalAnnotation node)
	{
		setNormalAnnotation(node);

		if (methodAnnotated)
		{
			setMethodAnnotation(node.toString());
		}
		if (classAnnotated)
		{
			classAnnotation = node.toString();
		}
		if (methodAnnotated)
		{
			methodAnnotationList.add(node.toString());
		}
		return true;
	}

	/** @METHOD */
	private void setNormalAnnotation(NormalAnnotation node)
	{
		switch (curVisit) {
		case clazzVisit:
			classDeclRef.addNormalAnnotationList(node.toString());
			break;
		case fieldvisit:
			fieldDeclRef.addNormalAnnotationList(node.toString());
			break;
		}
	}

	/** @METHOD */
	private void setMethodAnnotation(String nomalAnnotation)
	{
		int pos = methodDeclWithAnnotation.indexOf(methodName);
		int posOf1stParen = methodDeclWithAnnotation.indexOf("(", pos);
		int posOf2ndParen = methodDeclWithAnnotation.lastIndexOf(")");
		String subStrWithinParm = methodDeclWithAnnotation.substring(posOf1stParen, posOf2ndParen);

		// * Distinguish parameter annotation.
		if (subStrWithinParm.contains(nomalAnnotation) == false)
		{
			// System.out.println("[DBG] " + nomalAnnotation + ", " + methodName);
			methodDeclRef.normalAnnotationList.add(nomalAnnotation);
		}
	}

	/** @METHOD */
	public boolean visit(MethodDeclaration node)
	{
		setCurVisit(CurVisit.methodVisit);

		// System.out.println("[DBG] MethodDeclaration: " + node);
		if (node.getBody() != null)
		{
			methodDecl = methodDeclWithAnnotation = node.toString().replace(node.getBody().toString(), "");
		}
		else
		{
			methodDecl = methodDeclWithAnnotation = node.toString().trim();
		}
		methodDecl = methodDeclWithAnnotation = UtilStr.removeComment(methodDecl);
		methodName = node.getName().getFullyQualifiedName();

		String methodRType = "", methodParm = "";
		if (node.getBody() != null)
		{
			methodRType = getMethodRType(methodDeclWithAnnotation, methodName);
			methodParm = getMethodParm(methodDecl, methodRType, methodName);
		}
		else
		{
			methodRType = getMethodRType(node.toString().trim(), methodName);
			methodParm = getMethodParm(node.toString().trim(), methodRType, methodName);
		}
		if (clazzes.peek() == null)
			return true;

		/** Method data structure. */
		methodDeclRef = new AnnotatedMethodDecl(methodName, methodRType, methodParm, superclazzes.peek(), clazzes.peek());
		int startPosition = node.getStartPosition();
		methodDeclRef.setStartPoint(startPosition);
		int lineNumber = getLineNumber(startPosition);
		methodDeclRef.setLineNumber(lineNumber);

		/*
		 * java.lang.reflect.Modifier
		 * public static final int ABSTRACT 1024
		 * public static final int FINAL 16
		 * public static final int INTERFACE 512
		 * public static final int NATIVE 256
		 * public static final int PRIVATE 2
		 * public static final int PROTECTED 4
		 * public static final int PUBLIC 1
		 * public static final int STATIC 8
		 * public static final int STRICT 2048
		 * public static final int SYNCHRONIZED 32
		 * public static final int TRANSIENT 128
		 * public static final int VOLATILE 64
		 */
		String modifier = "";
		if (Long.toString(node.getModifiers(), 2).endsWith("100"))
			modifier = "protected";
		else if (Long.toString(node.getModifiers(), 2).endsWith("10"))
			modifier = "private";
		else if (Long.toString(node.getModifiers(), 2).endsWith("1"))
			modifier = "public";
		else
			modifier = "X";

		methodDeclRef.modifier = modifier;
		methodDeclRefList.add(methodDeclRef);
		classAnnotated = false;
		methodAnnotated = true;
		return true;
	}

	/** @METHOD */
	private int getLineNumber(int startPosition)
	{
		for (int i = 0; i < this.lineNumList.size(); i++)
		{
			Integer elem = this.lineNumList.get(i);
			if (elem <= startPosition)
				continue;
			else
				return (i + 1);
		}
		return 0;
	}

	/** @METHOD */
	private String getMethodParm(String methodDecl, String methodRType, String methodName)
	{
		String paramDecl = "";
		if (methodRType == null)
		{
			int pos1 = methodDecl.indexOf(methodName);
			int posOf1stParen = methodDecl.indexOf("(", pos1) + 1;
			int posOf2ndParen = methodDecl.lastIndexOf(")");
			paramDecl = methodDecl.substring(posOf1stParen, posOf2ndParen);
		}
		else
		{
			int pos1 = methodDecl.indexOf(methodRType);
			int pos2 = methodDecl.indexOf(methodName, pos1);
			int posOf1stParen = methodDecl.indexOf("(", pos2) + 1;
			int posOf2ndParen = methodDecl.lastIndexOf(")");
			paramDecl = methodDecl.substring(posOf1stParen, posOf2ndParen);
		}

		if (paramDecl.contains("@") == false)
		{
			return paramDecl;
		}
		else
		{
			return getMethodParmList(paramDecl).toArray().toString();
		}
	}

	/** @METHOD */
	List<String> getMethodParmList(String str)
	{
		boolean flag = false;
		List<Integer> commalist = new ArrayList<Integer>();
		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) == '(')
				flag = true;
			if (str.charAt(i) == ')')
				flag = false;
			if (!flag && str.charAt(i) == ',')
				commalist.add(i);
		}
		List<String> parmlist = new ArrayList<String>();
		if (commalist.isEmpty())
		{
			parmlist.add(getOneParm(str));
		}
		else
		{
			int prv = 0, cur = 0;
			for (int i = 0; i < commalist.size(); i++)
			{
				if (cur != 0)
					prv = cur;
				cur = commalist.get(i);
				parmlist.add(getOneParm(str.substring(prv + 1, cur).trim()));
			}
			parmlist.add(getOneParm(str.substring(cur + 1).trim()));
		}
		return parmlist;
	}

	/** @METHOD */
	String getOneParm(String src)
	{
		int pos1 = src.lastIndexOf(")");
		if (pos1 == -1)
		{
			String[] arrStr = src.split("\\s");
			if (src.startsWith("@") && arrStr.length == 3)
			{
				return arrStr[1].trim() + " " + arrStr[2].trim();
			}
			else if (arrStr.length == 2)
			{
				return arrStr[0].trim() + " " + arrStr[1].trim();
			}
			else
				return null;
		}
		return src.substring(pos1 + 1).trim();
	}

	/** @METHOD */
	private String getMethodRType(String methodDecl, String methodName)
	{
		String[] arrayMethodDecl = methodDecl.split("\\s");
		for (int i = 0; i < arrayMethodDecl.length; i++)
		{
			String elem = arrayMethodDecl[i].trim();
			if ((i - 1 > -1) && elem.startsWith(methodName))
				return arrayMethodDecl[i - 1];
		}
		return null;
	}

	/** @METHOD */
	public boolean visit(FieldDeclaration node)
	{
		setCurVisit(CurVisit.fieldvisit);

		classAnnotated = false;
		methodAnnotated = false;
		String decl = node.toString().trim();
		decl = decl.replace("\n", "");

		if (decl.contains("/*"))
		{
			StringBuffer sb = new StringBuffer(decl);
			int pos1 = decl.indexOf("/");
			int pos2 = decl.lastIndexOf("/");

			sb.delete(pos1, pos2);
			decl = sb.toString().trim();
			decl = decl.replace("/", "");
		}
		if (decl.contains("="))
		{
			// -----------------------------------------
			// * the old fashion
			// -----------------------------------------
			// int pos1 = decl.lastIndexOf("=");
			// decl = decl.substring(0, pos1);
			// decl += ";";

			// -----------------------------------------
			// * the new fashion
			// -----------------------------------------
			decl = getShrinkedField(decl);
		}
		// [[[ case 1 - old version ]]]
		fieldList.add(decl);

		String name = "";
		String type = node.getType().toString();

		if (node.fragments().isEmpty() == false)
			name = getFieldName(node.fragments().get(0).toString(), true);
		else
			name = getFieldName(decl, false);

		// [[[ case 2 - new version ]]]
		fieldDeclRef = new AnnotatedFieldDecl(name, type, superclazzes.peek(), clazzes.peek());
		int startPosition = node.getStartPosition();
		fieldDeclRef.setStartPosition(startPosition); 
		int lineNumber = getLineNumber(startPosition);
		fieldDeclRef.setLineNumber(lineNumber);
		fieldDeclRefList.add(fieldDeclRef);
		classDeclRef.addFieldDeclList(fieldDeclRef);
		return true;
	}

	/** @METHOD */
	private String getFieldName(String decl, boolean shorten)
	{
		int pos1 = -1;
		for (int i = decl.length() - 1; i > -1; i--)
		{
			char ch = decl.charAt(i);
			// if (ch == ')')
			// break;
			// else
			if (ch == '=')
			{
				pos1 = i;
				break;
			}
		}

		String name = "";

		if (pos1 != -1)
		{
			if (shorten)
			{
				name = decl.substring(0, pos1);
			}
			else
			{
				int pos2 = decl.lastIndexOf(" ", pos1 + 1);
				name = name.substring(pos2, pos1);
			}
		}
		else if (shorten)
			return name = decl;
		else
		{
			int pos2 = decl.lastIndexOf(" ");
			name = decl.substring(pos2).replace(";", "");
		}
		return name.trim();
	}

	/** @METHOD */
	public String getShrinkedField(String str)
	{
		boolean flag = false;
		Integer index = -1;

		for (int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			if (ch == '(')
				flag = true;
			else if (ch == ')')
				flag = false;
			else if (flag == false && ch == '=')
			{
				index = i;
				break;
			}
		}
		if (index == -1)
		{
			return str;
		}
		else
		{
			return str.substring(0, index).trim() + ";";
		}
	}
}
