/*
 * @(#) UtilAST.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.transform.RelationShip;
import metadata.invariant.pbse.visitor.ClassVisitor;
import metadata.invariant.pbse.visitor.Visitor;
import metadata.invariant.pbse.visitor.VisitorLineNumber;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import relation.annotation.ASTAccessor;
import relation.annotation.ctrl.CtrlClassType;
import relation.annotation.ctrl.CtrlFieldType;
import relation.progelem.AnnotatedClassDecl;
import relation.progelem.AnnotatedFieldDecl;
import relation.progelem.AnnotatedMethodDecl;

/**
 * @author Myoungkyu Song
 * @date Oct 26, 2010
 * @since JDK1.6
 */
public class UtilAST {
	/** @METHOD */
	public static Visitor startVisit(String filename)
	{
		String fileText = UtilFile.fileRead(new File(filename));
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(fileText.toCharArray());
		parser.setResolveBindings(true);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		Visitor visitor = new Visitor();
		unit.accept(visitor);

		return visitor;
	}

	/** @METHOD */
	public static ASTVisitor startCustomVisit(String filename, ASTVisitor visitor)
	{
		String fileText = UtilFile.fileRead(new File(filename));
		if (visitor instanceof VisitorLineNumber)
		{
			VisitorLineNumber visitorLN = (VisitorLineNumber) visitor;
			visitorLN.setFileText(filename, fileText);
		}
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(fileText.toCharArray());
		parser.setResolveBindings(true);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.accept(visitor);
		return visitor;
	}

	/** @METHOD */
	public static ClassVisitor startCustomVisit(String filename, ClassVisitor visitor)
	{
		String fileText = UtilFile.fileRead(new File(filename));
		visitor.setFileText(fileText);
		visitor.setFileName(filename);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(fileText.toCharArray());
		parser.setResolveBindings(true);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		unit.accept(visitor);
		return visitor;
	}

	/** @METHOD */
	public static String getOnlyClassName(String nameIncludingPackage)
	{
		int pos1 = nameIncludingPackage.lastIndexOf(".");
		String onlyClassName = nameIncludingPackage.substring(pos1 + 1);
		return onlyClassName;
	}

	/** @METHOD */
	public static List<AnnotatedClassDecl> getClassList(String pJavafile, CtrlFieldType cbf)
	{
		ASTAccessor astAccessor = new ASTAccessor();
		List<AnnotatedClassDecl> clazzlist = astAccessor.getAnnotatedClassList(pJavafile);
		clazzlist = cbf.cbfGetAnnotatedClassList(clazzlist);
		return clazzlist;
	}

	/** @METHOD */
	public static AnnotatedClassDecl getClassList(String pJavafile, CtrlClassType cbf)
	{
		ASTAccessor astAccessor = new ASTAccessor();
		List<AnnotatedClassDecl> clazzlist = astAccessor.getAnnotatedClassList(pJavafile);
		AnnotatedClassDecl clazzDecl = cbf.cbfGetAnnotatedClassList(clazzlist);
		return clazzDecl;
	}

	/** @METHOD */
	public static List<AnnotatedClassDecl> getClassList(String pJavafile)
	{
		ASTAccessor astAccessor = new ASTAccessor();
		List<AnnotatedClassDecl> clazzlist = astAccessor.getAnnotatedClassList(pJavafile);
		return clazzlist;
	}

	/** @METHOD */
	public static List<AnnotatedMethodDecl> getMethodList(List<String> files)
	{
		/*
		 * *********************************************************************************************************
		 * // RelationWithAnnotation astAccessor = new RelationWithAnnotation();
		 * // HashMap<String, List<AnnotatedMethodDecl>> classMethodMap =
		 * astAccessor.getAnnotatedMethodList(pAnnotatedJavaSrcStrList);
		 * //
		 * // List<AnnotatedMethodDecl> methodlist = new ArrayList<AnnotatedMethodDecl>();
		 * // for (Map.Entry<String, List<AnnotatedMethodDecl>> e : classMethodMap.entrySet())
		 * // methodlist.addAll(e.getValue()); // Add all list of "AnnotatedMethodDecl" instance.
		 * //
		 * // return methodlist;
		 * ********************************************************************************************
		 * ************
		 */
		ASTAccessor astAccessor = new ASTAccessor();
		List<AnnotatedMethodDecl> methodlist = astAccessor.getMethodList(files);
		return methodlist;
	}

	/** @METHOD */
	public static List<AnnotatedMethodDecl> getMethodList(String javafile)
	{
		ClassVisitor visitor = new ClassVisitor();
		UtilAST.startCustomVisit(javafile, visitor);

		/** Return all methods in the given java file. */
		return visitor.methodDeclRefList;
	}

	/** @METHOD */
	public static List<List<AnnotatedMethodDecl>> getMethodList(String markerAnnot, List<String> files)
	{

		List<AnnotatedMethodDecl> allMethodlist = getMethodList(files);
		List<AnnotatedMethodDecl> subMethodlist = new ArrayList<AnnotatedMethodDecl>();

		for (int i = 0; i < allMethodlist.size(); i++)
		{
			AnnotatedMethodDecl method = allMethodlist.get(i);
			if (hasMarkerAnnotation(markerAnnot, method))
			{
				subMethodlist.add(method);
			}
		}
		List<List<AnnotatedMethodDecl>> results = new ArrayList<List<AnnotatedMethodDecl>>();
		results.add(allMethodlist);
		results.add(subMethodlist);
		return results;
	}

	/** @METHOD */
	static boolean hasMarkerAnnotation(String markerAnnot, AnnotatedMethodDecl method)
	{
		List<String> markerAnnotList = method.markerAnnotationList;
		for (int i = 0; i < markerAnnotList.size(); i++)
		{
			String elem = markerAnnotList.get(i);
			if (elem.equals(markerAnnot))
				return true;
		}
		return false;
	}

	/** @METHOD */
	public static List<String> getFieldList(List<String> pJavafileList, boolean detail)
	{
		List<String> fieldList = new ArrayList<String>();
		ASTAccessor astAccessor = new ASTAccessor();
		if (detail)
			fieldList = astAccessor.getAnnotatedFieldListInDetail(pJavafileList);
		else
			fieldList = astAccessor.getAnnotatedFieldList(pJavafileList);
		return fieldList;
	}

	/** @METHOD */
	public static List<String> getFieldList(String pJavafile, boolean detail)
	{
		List<String> fieldList = new ArrayList<String>();
		ASTAccessor astAccessor = new ASTAccessor();
		if (detail)
			fieldList = astAccessor.getAnnotatedFieldListInDetail(pJavafile);
		else
			fieldList = astAccessor.getAnnotatedFieldList(pJavafile);
		return fieldList;
	}

	/** @METHOD */
	public static List<AnnotatedFieldDecl> getFieldList(String javafile)
	{
		ClassVisitor visitor = new ClassVisitor();
		UtilAST.startCustomVisit(javafile, visitor);

		/** Return all methods in the given java file. */
		return visitor.getFieldDeclRefList();
	}
	
	/** @METHOD */
	public static List<String> getFieldListDEG(List<String> pJavafileList, boolean detail)
	{
		List<String> fieldList = new ArrayList<String>();
		ASTAccessor astAccessor = new ASTAccessor();
		if (detail)
			fieldList = astAccessor.getAnnotatedFieldListInDetail(pJavafileList);
		else
			fieldList = astAccessor.getAnnotatedFieldList(pJavafileList);
		return fieldList;
	}

	/** @METHOD */
	public static List<String[]> getASTPair(String key, String val, List<?> pclist)
	{
		List<String[]> pairlist = new ArrayList<String[]>();
		// [[ Method ]]
		if (key.equals("METHOD_RTYPE") && val.equals("ANNOTATION_ATTR"))
		{
			for (int i = 0; i < pclist.size(); i++)
			{
				if (((AnnotatedMethodDecl) pclist.get(i)).normalAnnotationList.isEmpty())
					continue;
				String method_type = ((AnnotatedMethodDecl) pclist.get(i)).type;
				String annot_attrb = ((AnnotatedMethodDecl) pclist.get(i)).normalAnnotationList.toString();
				pairlist.add(new String[] { method_type, annot_attrb });
			}
		}
		// [[ Field ]]
		else if (key.equals("FIELD_NAME") && val.equals(STR.relation_annotation_attribute))
		{
			for (int i = 0; i < pclist.size(); i++)
			{
				String elem = (String) pclist.get(i);
				int pos1 = elem.lastIndexOf(")");
				String annotPart = elem.substring(0, pos1 + 1);
				String fieldPart = elem.substring(pos1 + 1);
				String annotAttr = getAnnotationAttr(annotPart.trim());
				String fieldName = getFieldName(fieldPart.trim());
				pairlist.add(new String[] { annotAttr, fieldName });
			}
		}
		return pairlist;
	}

	/** @METHOD */
	public static String[] getASTPair(String key, String val, String line)
	{
		// [[ Field ]]
		if (key.equals("FIELD_NAME") && val.equals(STR.relation_annotation_attribute))
		{
			int pos1 = line.lastIndexOf(")");
			String annotPart = line.substring(0, pos1 + 1);
			String fieldPart = line.substring(pos1 + 1);
			String annotAttr = getAnnotationAttr(annotPart.trim());
			String fieldName = getFieldName(fieldPart.trim());
			return new String[] { annotAttr, fieldName };
		}
		return null;
	}

	/** @METHOD */
	static String getAnnotationAttr(String annotPart)
	{
		int pos1 = annotPart.indexOf("\"");
		int pos2 = annotPart.indexOf("\"", pos1 + 1);
		String subStr = annotPart.substring(pos1 + 1, pos2);
		return subStr;
	}

	/** @METHOD */
	public static String getFieldName(String fieldPart)
	{
		String[] tokens = fieldPart.split("\\s");
		String fieldName = tokens[tokens.length - 1];
		fieldName = fieldName.replace(";", "");
		return fieldName;
	}

	/** @METHOD */
	public static void getASTDetailResult(String key, String val, List<AnnotatedMethodDecl> methodlist, List<String> pResultlist)
	{
		System.out.println("------------------------------------------");
		int num = 1;
		Iterator<String> resultlistItr = pResultlist.iterator();

		if (key.equals("METHOD_RTYPE") && val.equals("ANNOTATION_ATTR"))
		{
			for (int i = 0; i < methodlist.size(); i++)
			{
				if (methodlist.get(i).normalAnnotationList.isEmpty())
					continue;
				String annoAttr = methodlist.get(i).normalAnnotationList.toString();
				if (UtilAt.hasNameAttr(annoAttr) == false)
					continue;

				String info = methodlist.get(i).type + " --- " + methodlist.get(i).normalAnnotationList;
				String result = "";
				if (resultlistItr.hasNext())
					result = resultlistItr.next();
				System.out.println("[DBG]" + (num++) + ": " + info + "\n\t" + result);
				System.out.println("------------------------------------------");
			}
		}
	}

	/** @METHOD */
	public static List<String> getRelations(List<String[]> pairlist, String[] tokens)
	{
		List<String> relationList = new ArrayList<String>();
		RelationShip relation = new RelationShip();
		String annotAttr = tokens[2].trim();

		for (int i = 0; i < pairlist.size(); i++)
		{
			String key = pairlist.get(i)[0];
			String val = pairlist.get(i)[1];
			// [[ CASE ]]
			if (tokens.length > 2 && annotAttr.equalsIgnoreCase("NAME"))
			{
				// * ==============================
				// * Check annotation's attribute
				// * ==============================
				if (UtilAt.hasNameAttr(val))
					val = UtilAt.getNameAttrValue(val);
				else
					continue;
				// * ==============================
				// * Find relation with pair list
				// * ==============================
				relation.setInfo(tokens);
				relationList.add(relation.findRelation(key, val));
			}
			else if (tokens.length > 2 && annotAttr.equalsIgnoreCase("NAME-OR-EMPTY"))
			{
				// * ==============================
				// * Check annotation's attribute
				// * ==============================
				if (UtilAt.hasNameAttr(val))
					val = UtilAt.getNameAttrValue(val);
				else if (UtilAt.hasOnlyValue(val))
					val = UtilAt.getAttrValue(val);
				else
					continue;
				// * ==============================
				// * Find relation with pair list
				// * ==============================
				relation.setInfo(tokens);
				String r = relation.findRelation(val, key);
				// System.out.println("[DBG]" + tokens[0] + ": " + val + "  --> " + tokens[1] + ": " +
				// key);
				// System.out.println("[DBG]\t" + r);
				relationList.add(r);
			}
		}
		return relationList;
	}

}
