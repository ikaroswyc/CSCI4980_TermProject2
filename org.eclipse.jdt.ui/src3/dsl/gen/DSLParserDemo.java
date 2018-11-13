/*
 * @(#) DSLParser.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl.gen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import util.UtilDirScan;
import util.UtilFile;
import util.UtilStr;

public class DSLParserDemo {
	String			outputDir			= "out/";
	String			patternOperation	= "";
	String			pattern				= "";
	String			subPtrn				= "";

	List<String>	dslfile				= new ArrayList<String>();

	String[]			patternArray		= null;
	String[]			subPatternArray	= null;

	public static void main(String[] args)
	{
		if (Comm.isBind())
			Comm.readTargetToBind();

		DSLParserDemo dslParser = new DSLParserDemo();
		String dslfilename = dslParser.outputDir + Comm.getGlobalParm("run") + STR.file_dsl;
		dslParser.readDSLSpec(dslfilename);
		dslParser.getPatternOperation();
		dslParser.genPattern();
	}

	public DSLParserDemo() {}

	public DSLParserDemo(String refactoredPrj) {
		String _ls = System.getProperty("file.separator");
		File dir = new File(refactoredPrj);
		String[] dirList = dir.list();
		String milFileName = null;
		for (int i = 0; i < dirList.length; i++)
		{
			if (dirList[i].trim().endsWith(".mil"))
			{
				milFileName = dirList[i];
				break;
			}
		}
		readDSLSpec(refactoredPrj + _ls + milFileName);
		getPatternOperation();
		genPattern();
	}

	/** @METHOD */
	public boolean isAnnotation()
	{
		String metadata = getAssertStmt();
		if (metadata.contains("@"))
			return true;
		return false;
	}

	public boolean isXML()
	{
		String metadata = getAssertStmt();
		if (metadata.contains("<"))
			return true;
		return false;
	}

	/** @METHOD */
	public String getClassPattern()
	{
		String classPattern = null;
		for (int i = 0; i < dslfile.size(); i++)
		{
			String elem = dslfile.get(i);
			if (elem.trim().startsWith("Class"))
			{
				classPattern = dslfile.get(i + 1);
			}
		}
		return UtilStr.getStrBetween(classPattern, "(", ")");
	}

	/** @METHOD */
	public String getMethodPattern()
	{
		String methodPattern = null;
		for (int i = 0; i < dslfile.size(); i++)
		{
			String elem = dslfile.get(i);
			if (elem.trim().startsWith("Method"))
			{
				methodPattern = dslfile.get(i + 1);
			}
		}
		if (methodPattern == null)
			return null;
		return UtilStr.getStrBetween(methodPattern, "(", ")");
	}

	/** @METHOD */
	public String getFieldPattern()
	{
		String fieldPattern = null;
		for (int i = 0; i < dslfile.size(); i++)
		{
			String elem = dslfile.get(i);
			if (elem.trim().startsWith("Field"))
			{
				fieldPattern = dslfile.get(i + 1);
			}
		}
		if (fieldPattern == null)
			return fieldPattern;
		return UtilStr.getStrBetween(fieldPattern, "(", ")");
	}

	/** @METHOD */
	public String getAssertStmt()
	{
		String assertStmt = null;
		for (int i = 0; i < dslfile.size(); i++)
		{
			String elem = dslfile.get(i);
			if (elem.trim().startsWith("Assert"))
			{
				assertStmt = elem;
			}
		}
		return UtilStr.getStrBetween2(assertStmt, "(", ")");
	}

	/** @METHOD */
	public String getMsgStmt()
	{
		String assertStmt = null;
		for (int i = 0; i < dslfile.size(); i++)
		{
			String elem = dslfile.get(i);
			if (elem.trim().startsWith("Msg"))
			{
				assertStmt = elem;
			}
		}
		return UtilStr.getStrBetween2(assertStmt, ",", ")");
	}

	String getAssertStmt(String assertStmt, String classPattern, String methodPattern)
	{
		ArrayList<String> list = new ArrayList<String>();
		String[] assertStmtArray = assertStmt.split("\\s");
		StringBuilder annotationList = new StringBuilder();
		int sz = UtilStr.getSize(assertStmt, '|');
		if (sz > 0)
		{
			for (int i = 0; i < assertStmtArray.length; i++)
			{
				String elem = assertStmtArray[i];
				if (elem.trim().equals("c"))
				{
					annotationList.append(" ");
					annotationList.append(elem);
				}
				else if (elem.trim().equals("m"))
				{
					annotationList.append(" ");
					annotationList.append(elem);
				}
				else if (elem.trim().equals("f"))
				{
					annotationList.append(" ");
					annotationList.append(elem);
				}
				else
					annotationList.append(elem);
			}
			assertStmtArray = annotationList.toString().split("\\s");
		}
		if (assertStmt.contains("eq"))
		{
			for (int i = 0; i < assertStmtArray.length; i++)
			{
				String elem = assertStmtArray[i];
				if (elem.equals("Uc(f.name)"))
				{
					return assertStmt;
				}
			}
		}
		for (int i = 0; i < assertStmtArray.length; i++)
		{
			String elem = assertStmtArray[i];
			if (elem.contains("@"))
			{
				list.add(elem);
			}
			else if (elem.trim().contains("<"))
			{
				list.add(elem);
			}
			else if (elem.trim().equals("c"))
			{
				list.add(classPattern);
			}
			else if (elem.trim().equals("f") || elem.trim().contains("f.name"))
			{
				list.add(methodPattern);
			}
			else if (elem.trim().equals("m"))
			{
				list.add(methodPattern);
			}
		}
		assertStmt = UtilStr.getStrBetween(list.toString(), "[", "]").trim();
		return assertStmt;
	}

	public boolean checkClass(String fileName)
	{
		String shortFileName = UtilStr.getShorfileName(fileName);
		String className = shortFileName.replace(".java", "");
		String classPattern = getClassPattern();
		String classSig[] = classPattern.split("\\s");
		boolean check = false;

		if (classSig[2].contains("*"))
		{
			if (classSig[2].charAt(0) == '*')
			{
				check = className.matches("." + classSig[2]);
			}
			else if (classSig[2].charAt(classSig[2].length() - 1) == '*')
			{
				check = className.matches(classSig[2] + ".");
			}
		}
		else
		{
			check = classSig[2].equals(className);
		}
		return check;
	}

	/** @METHOD */
	public boolean checkMethod()
	{
		String assertStmt = getAssertStmt();
		String[] assertStmtArray = assertStmt.split("\\s");
		for (int i = 0; i < assertStmtArray.length; i++)
		{
			String elem = assertStmtArray[i];
			if (elem.trim().equals("m"))
			{
				return true;
			}
		}
		return false;
	}

	/** @METHOD */
	public boolean checkField()
	{
		// String classPattern = getClassPattern();
		// String fieldPattern = getFieldPattern();
		String assertStmt = getAssertStmt();
		// if (fieldPattern != null)
		// assertStmt = getAssertStmt(assertStmt, classPattern, fieldPattern);
		String[] assertStmtArray = assertStmt.split("\\s");
		for (int i = 0; i < assertStmtArray.length; i++)
		{
			String elem = assertStmtArray[i];
			if (elem.trim().equals("f") || elem.trim().contains("f.name"))
			{
				return true;
			}
		}
		return false;
	}

	/** @METHOD */
	public String getPattern()
	{
		String classPattern = getClassPattern();
		String methodPattern = getMethodPattern();
		String fieldPattern = getFieldPattern();
		String assertStmt = getAssertStmt();

		if (fieldPattern != null)
			assertStmt = getAssertStmt(assertStmt, classPattern, fieldPattern);
		else if (methodPattern != null)
			assertStmt = getAssertStmt(assertStmt, classPattern, methodPattern);
		return patternOperation + "(" + assertStmt + ")";
	}

	/** @METHOD */
	public String getAnnotation()
	{
		String assertStmt = null;
		for (int i = 0; i < dslfile.size(); i++)
		{
			String elem = dslfile.get(i);
			if (elem.trim().startsWith("Assert"))
			{
				assertStmt = UtilStr.getStrBetween(elem, "(", ")");
			}
		}
		String[] assertStmtArray = assertStmt.split("\\s");
		int sz = UtilStr.getSize(assertStmt, '|');
		StringBuilder annotationList = new StringBuilder();
		if (sz > 0)
		{
			for (int i = 0; i < assertStmtArray.length; i++)
			{
				String elem = assertStmtArray[i];
				if (elem.trim().equals("c"))
					;
				else if (elem.trim().equals("m"))
					;
				else
					annotationList.append(elem);
			}
			assertStmtArray = annotationList.toString().split("\\s");
		}
		return assertStmtArray[0];
	}

	/** @METHOD */
	public void genPattern()
	{
		if (patternOperation.startsWith(STR.pbse_ptrn_match))
		{
			String classPattern = getClassPattern();
			String methodPattern = getMethodPattern();
			String fieldPattern = getFieldPattern();
			String assertStmt = getAssertStmt();

			if (fieldPattern != null)
				assertStmt = getAssertStmt(assertStmt, classPattern, fieldPattern);
			else if (methodPattern != null)
				assertStmt = getAssertStmt(assertStmt, classPattern, methodPattern);
			print(patternOperation + "(" + assertStmt + ")");
			return;
		}
		if (patternOperation.startsWith(STR.pbse_ptrn_match) || //
				patternOperation.startsWith(STR.pbse_ptrn_contains) || //
				patternOperation.startsWith(STR.pbse_ptrn_lowfirstchar) || //
				patternOperation.startsWith(STR.pbse_ptrn_endswith))
		{

			print(patternOperation);
			print("(");
			if (makeLeftSide())
				print(",");
			makeRightSide();
			print(")");
			return;
		}
		if (patternOperation.split("\\+").length >= 2)
		{

			String[] ptrnOpArray = patternOperation.split("\\+");
			ArrayList<String> oPlist = new ArrayList<String>();
			StringBuilder oPsb = new StringBuilder();

			for (int i = 0; i < ptrnOpArray.length; i++)
			{
				oPsb.setLength(0);
				oPsb.append(ptrnOpArray[i]);

				if (ptrnOpArray[i].equals(STR.pbse_ptrn_prefix))
				{
					oPsb.append("(");
					oPsb.append(subPatternArray[i].replace("*", "").trim());
					oPsb.append(")");
					oPlist.add(oPsb.toString());
				}
				else if (ptrnOpArray[i].equals(STR.pbse_ptrn_uppercase))
				{
					oPsb.append("(");
					String prgConstruct = getStrBetween(subPatternArray[i], "(", ")");
					prgConstruct = translate(prgConstruct).toUpperCase();
					oPsb.append("$" + prgConstruct);
					oPsb.append(")");
					oPlist.add(oPsb.toString());
				}
			}

			for (int i = 0; i < oPlist.size(); i++)
			{
				String elem = oPlist.get(i);
				print(elem);
				if (i != oPlist.size() - 1)
					print("+");
			}
			return;
		}
	}

	/** @METHOD */
	private void makeRightSide()
	{
		String prgConstruct = getPrgConstruct();

		if (prgConstruct == null)
		{

			if (pattern.split("\\s").length == 2)
			{

				String whStmt = getWhStmt();

				if (whStmt.startsWith("*") == false)
				{

					if (makeRightSideWildCard())
						return;

					print(whStmt.split("\\s")[0].trim().toUpperCase());
				}
			}
			return;
		}

		if (prgConstruct.contains("(") && prgConstruct.contains(")"))
			prgConstruct = getStrBetween(prgConstruct, "(", ")");
		prgConstruct = translate(prgConstruct).toUpperCase();
		print("$" + prgConstruct);
	}

	boolean makeRightSideWildCard()
	{
		String metadata = getMetadata();
		String whStmt = getWhStmt();
		String prgConstruct = getPrgFromWhStmt(whStmt);

		if (prgConstruct == null)
			return false;

		if (prgConstruct.replace("*", ""). //
		equals(metadata.replace("@", "")))
		{

			String right = "$" + STR.parm_annotation + STR.parm_name;
			print(right.toUpperCase());
			return true;
		}
		return false;
	}

	/** @METHOD */
	private String translate(String prgConstruct)
	{
		if (prgConstruct.startsWith(STR.pbse_class + "."))
			return prgConstruct.replace(STR.pbse_class + ".", STR.relation_class + "_");
		if (prgConstruct.startsWith(STR.pbse_field + "."))
			return prgConstruct.replace(STR.pbse_field + ".", STR.relation_field + "_");
		if (prgConstruct.startsWith(STR.pbse_method + "."))
			return prgConstruct.replace(STR.pbse_method + ".", STR.relation_method + "_");
		return null;
	}

	/**
	 * @return
	 * @METHOD
	 */
	private boolean makeLeftSide()
	{
		String metadata = getMetadata();
		if (metadata != null && metadata.contains("@"))
		{
			if (pattern.split("\\s").length == 2)
			{
				String whStmt = getWhStmt();
				String itrStmt = getItrStmt();
				if (isWhStmtSuffix(whStmt))
				{
					switch (whichPrgConstruct(whStmt)) {
					case 0:
						print(STR.relation_modifier);
						break;
					case 2:
						print(STR.var_classname2);
						break;
					default:
						break;
					}
				}
				else if (whStmt.startsWith("*") == false)
				{
					if (itrStmt != null)
					{
						if (itrStmt.trim().startsWith(STR.pbse_decl_method))
						{
							String mod = ("$" + STR.relation_method + "." + STR.relation_modifier);
							print(mod.toUpperCase());
						}
						else if (itrStmt.trim().startsWith(STR.pbse_decl_field))
						{
							String mod = ("$" + STR.relation_field + "." + STR.relation_modifier);
							print(mod.toUpperCase());
						}
						else if (itrStmt.trim().startsWith(STR.pbse_decl_class))
						{
							String mod = ("$" + STR.relation_class + "." + STR.relation_modifier);
							print(mod.toUpperCase());
						}
					}
				}
			}
			if (metadata.contains("."))
				print("$" + STR.relation_annotation_attribute);
			if (metadata.split("\\.").length == 2)
				print("." + metadata.split("\\.")[1].toUpperCase());
			return true;
		}
		if (pattern.split("\\s").length == 3)
		{
			String itrStmt = getItrStmt();

			if (itrStmt.trim().startsWith(STR.pbse_decl_method))
			{
				String mod = ("$" + STR.relation_method);
				print(mod.toUpperCase());
				return true;
			}
			else if (itrStmt.trim().startsWith(STR.pbse_decl_field))
			{
				String mod = ("$" + STR.relation_field);
				print(mod.toUpperCase());
				return true;
			}
			else if (itrStmt.trim().startsWith(STR.pbse_decl_class))
			{
				String mod = ("$" + STR.relation_class);
				print(mod.toUpperCase());
				return true;
			}
		}
		return false;
	}

	/** @METHOD */
	private int whichPrgConstruct(String whStmt)
	{
		int cnt = UtilStr.countSubString(whStmt, "*");
		if (cnt == 1)
		{
			String[] whStmtArray = whStmt.split("\\s");

			for (int i = 0; i < whStmtArray.length; i++)
			{
				String elem = whStmtArray[i];
				if (elem.contains("*"))
					return i;
			}
		}
		return 0;
	}

	/** @METHOD */
	private String getPrgFromWhStmt(String whStmt)
	{
		int cnt = UtilStr.countSubString(whStmt, "*");
		if (cnt == 1)
		{
			String[] whStmtArray = whStmt.split("\\s");

			for (int i = 0; i < whStmtArray.length; i++)
			{
				String elem = whStmtArray[i].trim();
				if (elem.contains("*"))
					return elem;
			}
		}
		return null;
	}

	/** @METHOD */
	private boolean isWhStmtSuffix(String whStmt)
	{
		String[] whStmtArray = whStmt.split("\\s");

		for (int i = 0; i < whStmtArray.length; i++)
		{
			String elem = whStmtArray[i].trim();

			if (elem.contains("*") && elem.length() != 1 && //
					elem.charAt(0) == '*')
			{
				return true;
			}
		}

		return false;
	}

	/** @METHOD */
	private String getItrStmt()
	{
		for (int i = dslfile.size() - 1; i >= 0; i--)
		{
			String elem = dslfile.get(i);

			if (elem.trim().startsWith(STR.pbse_decl_method) | //
					elem.trim().startsWith(STR.pbse_decl_field) | //
					elem.trim().startsWith(STR.pbse_decl_class))
			{
				return elem;
			}
		}
		return null;
	}

	/** @METHOD */
	private String getWhStmt()
	{
		for (int i = dslfile.size() - 1; i >= 0; i--)
		{
			String elem = dslfile.get(i);

			if (elem.trim().startsWith(STR.pbse_where2))
			{
				return getStrBetween(elem, "(", ")");
			}
		}
		return null;
	}

	/** @METHOD */
	private String getMetadata()
	{
		for (int i = 0; i < patternArray.length; i++)
		{
			String[] patternElemArray = patternArray[i].split("\\s");

			for (int j = 0; j < patternElemArray.length; j++)
			{
				if (patternElemArray[j].trim().contains("@") || //
						patternElemArray[j].trim().contains("<"))
					return patternElemArray[j].trim();
			}
		}
		return null;
	}

	/** @METHOD */
	private String getPrgConstruct()
	{
		for (int i = 0; i < patternArray.length; i++)
		{
			String[] patternElemArray = patternArray[i].split("\\s");

			for (int j = 0; j < patternElemArray.length; j++)
			{
				if (patternElemArray[j].trim().contains("f.") || //
						patternElemArray[j].trim().contains("m.") || //
						patternElemArray[j].trim().contains("c."))
					return patternElemArray[j].trim();
			}
		}
		return null;
	}

	/** @METHOD */
	public String getPatternOperation()
	{
		for (int i = dslfile.size() - 1; i >= 0; i--)
		{
			String elem = dslfile.get(i).trim();

			if (elem.startsWith(STR.mil_assert) || elem.startsWith(STR.mil_assert_exists))
			{
				pattern = getStrBetween(elem, "(", ")");
				break;
			}
		}

		patternArray = pattern.split("\\|");
		if (patternArray.length == 1)
		{
			patternArray = pattern.split("\\+");
			if (patternArray.length == 2)
			{
				getPatternOperationMulti();
				return patternOperation;
			}
		}

		for (int i = 0; i < patternArray.length; i++)
		{
			String patternElem = patternArray[i];
			String[] patternStatArray = patternElem.split("\\s");

			if (patternStatArray.length == 2)
			{

				String whStmt = getWhStmt();

				if (whStmt.contains("*"))
				{
					if (isSuffix(whStmt))
					{
						patternOperation = STR.pbse_ptrn_endswith;
						break;
					}
					else if (isPrefix(whStmt))
					{
						patternOperation = STR.pbse_ptrn_startswith;
						break;
					}
				}
				patternOperation = STR.pbse_ptrn_match;
				break;
			}
			if (patternStatArray.length == 3)
			{

				if (patternStatArray[1].equals(STR.mil_eq) && //
						patternStatArray[2].startsWith("Lc"))
				{
					patternOperation = STR.pbse_ptrn_lowfirstchar;
					break;
				}
				if (patternStatArray[1].equals(STR.mil_eq))
				{
					patternOperation = STR.pbse_ptrn_match;
					break;
				}
				if (patternStatArray[1].equals(STR.mil_has))
				{
					patternOperation = STR.pbse_ptrn_contains;
					break;
				}
			}
		}
		return patternOperation;
	}

	private boolean isSuffix(String whStmt)
	{
		String[] whStmtArray = whStmt.trim().split("\\s");

		for (int i = 0; i < whStmtArray.length; i++)
		{

			String elem = whStmtArray[i].trim();

			if (elem.contains("*") && //
					elem.length() != 1 && //
					elem.charAt(0) == '*')
			{
				return true;
			}
		}
		return false;
	}

	private boolean isPrefix(String whStmt)
	{
		String[] whStmtArray = whStmt.trim().split("\\s");

		for (int i = 0; i < whStmtArray.length; i++)
		{
			String elem = whStmtArray[i].trim();

			if (elem.contains("*") && //
					elem.length() != 1 && //
					elem.charAt(elem.length() - 1) == '*')
			{
				return true;
			}
		}
		return false;
	}

	/** @METHOD */
	private void getPatternOperationMulti()
	{
		int index = -1;
		for (int i = pattern.length() - 1; i >= 0; i--)
		{
			if (pattern.charAt(i) == 'q' && pattern.charAt(i - 1) == 'e')
			{
				index = i;
				break;
			}
		}
		subPtrn = pattern.substring(index + 1);
		subPatternArray = subPtrn.split("\\+");
		patternOperation = "";
		for (int i = 0; i < subPatternArray.length; i++)
		{

			String elem = subPatternArray[i].trim();

			if (elem.endsWith("*"))
			{
				if (patternOperation.isEmpty())
					patternOperation = STR.pbse_ptrn_prefix;
				else
					patternOperation = patternOperation + "+" + STR.pbse_ptrn_prefix;
			}
			else if (elem.startsWith("Uc"))
			{
				if (patternOperation.isEmpty())
					patternOperation = STR.pbse_ptrn_uppercase;
				else
					patternOperation = patternOperation + "+" + STR.pbse_ptrn_uppercase;
			}
		}
	}

	/** @METHOD */
	private void readDSLSpec(String dslfilename)
	{
		dslfile = readDSLSpec(dslfilename, true);
		System.out.println("[DBG]" + dslfilename);
		System.out.println("------------------------------------------");
	}

	/** @METHOD */
	List<String> readDSLSpec(String dslfilename, boolean b)
	{
		List<String> list = UtilFile.fileRead2List(dslfilename);
		for (int i = 0; i < list.size(); i++)
		{
			String line = list.get(i);
			if (line.isEmpty() || line.trim().startsWith("//") || line.trim().startsWith("#"))
				continue;
			dslfile.add(line);
		}
		return dslfile;
	}

	/** @METHOD */
	String getStrBetween(String src, String left, String right)
	{
		int leftIndex = src.indexOf(left) + left.length();
		int midleIndex = src.indexOf(":");

		int rightIndex = -1;

		if (midleIndex != -1)
			rightIndex = src.lastIndexOf(right, midleIndex);
		else
			rightIndex = src.lastIndexOf(right);

		return src.substring(leftIndex, rightIndex);
	}

	void print(String s)
	{
		System.out.print(s);
	}
}
