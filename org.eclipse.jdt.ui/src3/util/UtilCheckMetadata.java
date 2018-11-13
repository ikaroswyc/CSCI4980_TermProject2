/*
 * @(#) UtilCheckMetadata.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package util;

import java.util.ArrayList;
import java.util.List;

import relation.progelem.AnnotatedFieldDecl;
import dsl.gen.DSLParserDemo;

/**
 * @author Myoungkyu Song
 * @date May 15, 2012
 * @since JDK1.6
 */
public class UtilCheckMetadata {
	private static final String	x_class_field	= "<class>.<property>";

	/** @METHOD */
	public static String checkMetadataInvariants_XML(String refactoredFileName, DSLParserDemo dslParser)
	{
		String displayResult = "";
		XMLParser xmlParser = new XMLParser(refactoredFileName);
		if (xmlParser.checkClass(refactoredFileName))
		{
			if (dslParser.checkMethod())
			{
				// List<AnnotatedMethodDecl> violationList = checkMatchWithMethodMulti(refactoredFileName, dslParser, xmlParser);
			}
			else if (dslParser.checkField())
			{
				List<AnnotatedFieldDecl> violationList = checkMatchWithFieldMulti(refactoredFileName, dslParser, xmlParser);
				for (int i = 0; i < violationList.size(); i++)
				{
					AnnotatedFieldDecl elem = violationList.get(i);
					String column = null; // xmlParser.getColumn(elem.getFieldName());
					//if (column == null)
					//{
					column = dslParser.getMsgStmt().split(",")[1].trim();
					//}
					displayResult += UtilPrint.getDisplayResult_Violation(elem, refactoredFileName, column);
					if ((violationList.size() - 1) != i)
						displayResult += ",";
				}
				return displayResult;
			}
		}
		return displayResult;
	}

	/** @METHOD */
	private static List<AnnotatedFieldDecl> checkMatchWithFieldMulti(String refactoredFileName, DSLParserDemo dslParser, XMLParser xmlParser)
	{
		List<AnnotatedFieldDecl> violationList = new ArrayList<AnnotatedFieldDecl>();
		List<AnnotatedFieldDecl> fieldRefList = UtilAST.getFieldList(refactoredFileName);
		List<XMLFieldUnit> xmlUnitList = xmlParser.getXmlUnitList();

		String fieldPattern[] = dslParser.getFieldPattern().split("\\s");
		String assertPattern[] = dslParser.getAssertStmt().split("\\s");

		for (int i = 0; i < fieldRefList.size(); i++)
		{
			AnnotatedFieldDecl elem = fieldRefList.get(i);
			String fieldName = elem.getFieldName();
			boolean validCheckField = false;

			if (fieldPattern.length == 3 && //
					fieldPattern[0].trim().equals(x_class_field + ".name") && //
					fieldPattern[1].trim().equals("eq") && //
					fieldPattern[2].trim().equals("f.name"))
			{
				validCheckField = UtilCheckMetadata.violationCheckField(fieldName, xmlUnitList);
			}
			if (validCheckField && //
					assertPattern.length == 3 && //
					assertPattern[0].trim().equals(x_class_field + ".column") && //
					assertPattern[1].trim().equals("eq") && //
					assertPattern[2].trim().equals("Uc(f.name)"))
			{
				boolean violation1 = UtilCheckMetadata.violationCheckColumn(fieldName, xmlUnitList);
				if (violation1)
					violationList.add(elem);
			}
		}
		if (!violationList.isEmpty())
			return violationList;
		for (int i = 0; i < fieldRefList.size(); i++)
		{
			AnnotatedFieldDecl elem = fieldRefList.get(i);
			String fieldName = elem.getFieldName();
			if (fieldPattern[2].equals("*") && fieldName.matches("." + fieldPattern[2]))
			{

				if (assertPattern.length == 3 && //
						assertPattern[0].trim().equals(x_class_field + ".name") && //
						assertPattern[1].trim().equals("eq") && //
						assertPattern[2].trim().equals("f.name"))
				{
					boolean violation1 = UtilCheckMetadata.violationCheckField(fieldName, xmlUnitList);
					if (!violation1)
						violationList.add(elem);
				}
			}
		}
		return violationList;
	}

	/** @METHOD */
	public static boolean violationCheckField(String fieldName, List<XMLFieldUnit> xmlUnitList)
	{
		for (int i = 0; i < xmlUnitList.size(); i++)
		{
			XMLFieldUnit xmlFieldUnit = xmlUnitList.get(i);
			String xmlFieldName = xmlFieldUnit.getFieldName();
			if (fieldName.equals(xmlFieldName))
				return true;
		}
		return false;
	}

	/** @METHOD */
	public static boolean violationCheckColumn(String elem, List<XMLFieldUnit> xmlUnitList)
	{
		String fieldName = UtilAST.getFieldName(elem);
		for (int i = 0; i < xmlUnitList.size(); i++)
		{
			XMLFieldUnit xmlFieldUnit = xmlUnitList.get(i);
			String xmlColumnName = xmlFieldUnit.getColumnName();
			if (fieldName.toUpperCase().equals(xmlColumnName))
				return false;
		}
		return true;
	}

}
