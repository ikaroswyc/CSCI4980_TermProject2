/*
 * @(#) PBSEGenerator.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation;

import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import relation.annotation.MainRecommender;
import relation.type.RelationEnum;
import util.UtilPrint;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jun 16, 2011
 * @since JDK1.6
 */
public class PbsePatternHelper {
	String				dirXML;
	// String fileRelation;
	String				fileRelationClassTable;

	@Deprecated
	ArrayList<String>	_pbsebody	= new ArrayList<String>();

	/** @METHOD */
	public PbsePatternHelper(String dirXML, String fileRelationClassTable) {
		this.dirXML = dirXML;
		// this.fileRelation = fileRelation;
		this.fileRelationClassTable = fileRelationClassTable;
	}

	/** @METHOD */
	public PbsePatternHelper() {}

	@Deprecated
	public void writePBSE(MainRecommender recommender) {
		RelationEnum relenum = recommender.getCurRelation();
		if (relenum == null) {
			String pattern = recommender.getPattern(); // CONTAINS($METHOD, $SUPERCLASS)
			String[] relationConfigured = Comm.getParm(STR.parm_relation).split(",");
			// METHOD, ANNOTATION, @Override
			/*- =================================================
			 Method m in c
			 Where (m == [c.SuperClass.Method])
			 	m += @Override
			 ==================================================== -*/

			if (hasProgramConstruct(pattern)) {
				List<String> elements = new ArrayList<String>();
				elements = getElements(pattern);
				String buf = "";
				if (elements.contains("$METHOD")) {
					buf = STR.pbse_decl_method + STR.sp + STR.pbse_syn_in + STR.sp + STR.pbse_class;
					System.out.println(buf);
					if (elements.contains("$SUPERCLASS")) {
						buf = String.format(STR.pbse_where, getWhereStat(pattern));
						System.out.println(buf);
					}
				}
			}
			String buf = STR.sp_indent;
			if (relationConfigured[0].trim().equalsIgnoreCase(STR.relation_method)) {
				buf += STR.pbse_method + STR.sp;
			}
			if (relationConfigured[1].trim().equalsIgnoreCase("ANNOTATION")) {
				String annotation = relationConfigured[2].trim();
				buf += STR.pbse_op_append + STR.sp + annotation;
			}
			System.out.println(buf);
		}
	}

	@Deprecated
	public void writePBSE(String relation, MainRecommender recommender) {
		RelationEnum relenum = recommender.getCurRelation();
		String[] tokens = UtilStr.trim(relation.split(","));

		switch (relenum) {
		case CONFIGURATION:
			// * Generate a PBSE program with the first item.
			String metadataName = tokens[2];
			writeRelationClassAnnotationName(recommender.getPattern(), metadataName);
			displayPBSE();
			System.out.println("------------------------------------------");
			break;
		case METHOD_RETURNTYPE_ANNOTATION_ATTRIBUTE_NAME:
			// * Generate a PBSE program.
			String annotation = tokens[3];
			System.out.println("Method m in c");
			System.out.println(String.format(STR.pbse_where, annotation + " public *"));
			System.out.println("   " + recommender.getPattern());
			System.out.println("------------------------------------------");
			break;
		case FIELDNAME_ANNOTATTR:
			System.out.println("Field f in c");
			System.out.println(String.format(STR.pbse_where, "private * *"));
			System.out.println("   " + recommender.getPattern());
			System.out.println("------------------------------------------");
			break;
		}
	}

	/** @METHOD */
	private String getWhereStat(String pattern) {
		String buf = "";
		List<String> elements = getElements(pattern);

		if (pattern.startsWith("CONTAINS")) {
			buf = String.format(STR.pbse_op_contain, elements.get(0), elements.get(1));

			if (buf.contains("$METHOD")) {
				buf = buf.replace("$METHOD", STR.pbse_method);

				if (buf.contains("$SUPERCLASS")) {
					buf = buf.replace("$SUPERCLASS", STR.pbse_class + "." +
							STR.pbse_decl_superclass + "." + STR.pbse_decl_method);
				}
			}
		}
		return buf;
	}

	/** @METHOD */
	private boolean hasProgramConstruct(String pattern) {
		if (pattern.contains("$FIELD") || pattern.contains("$METHOD") ||
				pattern.contains("$CLASS") || pattern.contains("$SUPERCLASS"))
			return true;
		return false;
	}

	/** @METHOD */
	public List<String> getElements(String pattern) {
		List<String> words = new ArrayList<String>();
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (ch == '$') {
				i = getWord(i, pattern, words);
			}
		}
		return words;
	}

	/** @METHOD */
	private int getWord(int start, String pattern, List<String> words) {
		StringBuilder buf = new StringBuilder();
		int idx = start;
		for (; idx < pattern.length(); idx++) {
			char ch = pattern.charAt(idx);
			if (ch == ' ' || ch == ',' || ch == ')')
				break;
			buf.append(ch);
		}
		words.add(buf.toString());
		return idx;
	}

	/** @METHOD */
	@Deprecated
	public void writeRelationClassTable(String[] relationVar, String pattern) {
		_pbsebody.add("MyPBSE<Package p>");
		_pbsebody.add("Class c in p");
		_pbsebody.add(String.format(STR.pbse_where, "public *"));
		_pbsebody.add("\t" + "@Metadata" +
				"." +
				relationVar[0].trim().toUpperCase() +
				" += " + pattern.replace(STR.var_classnamebig, "c"));
	}

	/** @METHOD */
	@Deprecated
	public void writeRelationClassAnnotationName(String pattern, String metadata) {
		_pbsebody.add("MyPBSE<Package p>");
		_pbsebody.add("Class c in p");
		_pbsebody.add(String.format(STR.pbse_where, "public *" + metadata.replace("@", STR.str_empty).trim()));
		_pbsebody.add("\t" + "c += " + metadata);
	}

	/** @METHOD */
	@Deprecated
	public void displayPBSE() {
		UtilPrint.printArrayList(_pbsebody);
	}
}
