/*
 * @(#) ViolationFinder.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.visitor.ClassVisitor;
import relation.annotation.ctrl.CtrlClazz;
import relation.progelem.AnnotatedClassDecl;
import relation.progelem.AnnotatedMethodDecl;
import relation.type.Configuration;
import relation.type.FieldNameAnnotAttr;
import relation.type.FieldNameClassAnnotAttr;
import relation.type.MethodReturnTypeAnnotationAttrName;
import util.UtilAST;
import util.UtilAt;
import util.UtilDirScan;
import util.UtilPrint;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jul 12, 2011
 * @since JDK1.6
 */
public class ViolationFinder {
	ViolationDetectorInAnnotation	detector;

	public ViolationFinder() {}

	/** @METHOD */
	public ViolationFinder(ViolationDetectorInAnnotation pRef) {
		detector = pRef;
	}

	/** @METHOD */
	public void findViolation(Configuration parm,
			String pattern, String relation) {
		String[] tokens = UtilStr.trim(relation.split(","));
		String metadataName = tokens[2].trim();

		// <class name, class annotation>
		HashMap<String, String> violationlist = new HashMap<String, String>();

		for (int i = 0; i < detector._annotatedJavaSrcList.size(); i++) {
			String filepath = detector._annotatedJavaSrcList.get(i).getAbsolutePath();
			ClassVisitor visitor = new ClassVisitor();
			UtilAST.startCustomVisit(filepath, visitor);

			String classAnnotation = visitor.getClassAnnotation();
			String className = visitor.getClassName();
			if (classAnnotation == null || className == null)
				continue;
			className = UtilAST.getOnlyClassName(className);
			String aResultLine = classAnnotation + ", " + className;

			if (classAnnotation.contains(metadataName) == false) {
				aResultLine += " ---out-of-scope-investigation---" + className + "---" +
						classAnnotation + "---" + tokens;
			}
			else {
				String result = detector.findViolation(classAnnotation, className, pattern);
				if (result.equals("false")) {
					violationlist.put(className, classAnnotation);
				}
				aResultLine += (", " + result);
			}
			// _ref._resultlist.add(aResultLine);
		}
		detector.displayViolationList(parm, violationlist, detector._annotatedJavaSrcList.size());
	}

	/** @METHOD */
	public void findViolation(MethodReturnTypeAnnotationAttrName parm,
			String pattern, String relation) {
		List<String> javafiles = detector.getAnnotatedJavaSrcStrList();
		UtilPrint.printArrayList(javafiles);
		System.out.println("------------------------------------------");

		ASTAccessor rwa = new ASTAccessor();
		HashMap<String, List<AnnotatedMethodDecl>> classMethodMap =
				rwa.getAnnotatedMethodList(javafiles);

		HashMap<String, List<String>> violationlist = new HashMap<String, List<String>>();

		for (Entry<String, List<AnnotatedMethodDecl>> e : classMethodMap.entrySet()) {
			String classname = e.getKey();
			List<AnnotatedMethodDecl> methods = e.getValue();
			List<String> violatedMethods = new ArrayList<String>();

			for (int i = 0; i < methods.size(); i++) {
				AnnotatedMethodDecl methodRef = methods.get(i);
				if (methodRef.normalAnnotationList.isEmpty())
					continue;
				String key = methodRef.type;
				String val = methodRef.normalAnnotationList.toString();
				if (UtilAt.hasNameAttr(val) == false)
					continue;

				val = UtilAt.getNameAttrValue(val);
				String result = detector.findViolation(val, key, pattern);
				// StringBuilder sbuf = new StringBuilder();
				// sbuf.append(UtilAST.getOnlyClassName(classname) + "\n");
				// sbuf.append(methodRef.toString() + "\n");
				// sbuf.append(key + " <=> " + val + "\n");
				// sbuf.append(result + "\n");
				// sbuf.append(STR.line + "\n");
				// _ref._resultlist.add(sbuf.toString());
				// System.out.println("[DBG]" + sbuf);
				// System.out.println("[DBG]" + UtilAST.getOnlyClassName(classname) +
				// ": " + key + " --- " + val + ": " + result);
				if (result.equals("false")) {
					StringBuilder buf = new StringBuilder();
					for (int j = 0; j < methodRef.normalAnnotationList.size(); j++) {
						String elem = methodRef.normalAnnotationList.get(j);
						buf.append(elem);
					}
					violatedMethods.add(buf.toString() + "\n" + methodRef.type + " " +
							UtilStr.getShortClassName(methodRef.name) + "(..)");
				}
			}
			if (!violatedMethods.isEmpty()) {
				violationlist.put(classname, violatedMethods);
			}
		}
		System.out.println("[DBG] Violatioin List");
		System.out.println("------------------------------------------");
		for (Map.Entry<String, List<String>> e : violationlist.entrySet()) {
			String className = e.getKey();
			List<String> methods = e.getValue();
			System.out.println("* Location: " + className);
			UtilPrint.printArrayList(methods);
		}
		detector.displayViolationList(parm, violationlist, detector._annotatedJavaSrcList.size());
	}

	/**
	 * @return
	 * @METHOD
	 */
	public boolean findViolation(FieldNameAnnotAttr parm, String pattern, String relation) {

		String[] tokens = UtilStr.trim(relation.split(","));
		String annotation = tokens[3];
		List<String> javafiles = detector.getAnnotatedJavaSrcStrList();

		// UtilPrint.printArrayList(javafiles);
		// System.out.println("------------------------------------------");

		List<String> fieldList1 = UtilAST.getFieldList(javafiles, true);
		List<String> fieldList2 = new ArrayList<String>();

		// [[ FILTER ]]
		for (int i = 0; i < fieldList1.size(); i++) {
			String elem = fieldList1.get(i);
			if (elem.contains(annotation) && UtilStr.hasAttributeStr(elem)) {
				fieldList2.add(elem);
			}
		}
		// [[ CHECK ]] <class, fields>
		HashMap<String, List<String>> violationlist = new HashMap<String, List<String>>();
		List<String> violationfields = new ArrayList<String>();
		String oldJavafile = "";
		int javafilecnt = 0;

		for (int i = 0; i < fieldList2.size(); i++) {
			String fieldfullname[] = fieldList2.get(i).split("\\+");
			String javafile = fieldfullname[0];
			String fieldDecl = fieldfullname[1];

			String[] pair = UtilAST.getASTPair("FIELD_NAME", STR.relation_annotation_attribute, fieldDecl);
			String result = detector.findViolation(pair[0], pair[1], pattern);
			// System.out.println("[DBG]" + javafile + ": " +
			// "(" + pair[0] + " - " + pair[1] + ") -> " + result);

			if ((i != 0) && (oldJavafile.equals(javafile) == false)) {
				javafilecnt++;
				if (result.equals("false"))
					violationfields.add(fieldDecl);
			}
			else if ((i != 0) && (violationfields.isEmpty() == false)) {
				violationlist.put(oldJavafile, violationfields);
				violationfields = new ArrayList<String>();
			}
			oldJavafile = javafile;
		}
		// [[ Let's try other case ]]
		if (violationlist.isEmpty())
			return false;

		// [[ If succucess, display ]]
		detector.displayViolationList(parm, violationlist, javafilecnt, fieldList2.size());
		return true;
	}

	/** @METHOD */
	public void findViolation(FieldNameClassAnnotAttr type, String pattern, String relation) {

		// *****************************************************************
		// Extract all class-decl elements having the given annotation.
		// *****************************************************************
		String path = Comm.getParm(STR.parm_inspection_target);
		List<String> javafiles = UtilDirScan.getResultViaStr(path, STR.file_java);

		CtrlClazz ctrlClazz = new CtrlClazz();
		String attr = Comm.getAttributeInCmdline();
		int javaCounter = 0;
		int clazzCounter = 0;
		int violationCounter = 0;

		for (String javafile : javafiles) {
			List<AnnotatedClassDecl> clazzDeclList = ctrlClazz.getClassList(javafile);

			boolean flag = false;
			for (AnnotatedClassDecl classDecl : clazzDeclList) {
				String theNormalAnnot = classDecl.getNormalAnnotation(Comm.getAnnotationInCmdline());

				// *****************************************************************
				// Pass 'set of attribute values' and 'set of field names'
				// to validate matching the inferred pattern.
				// *****************************************************************

				// [[ * 1st parm ]]
				String annotAttrValues[] = UtilStr.getValues(theNormalAnnot, attr);

				// [[ * 2nd parm ]]
				String pcnames[] = classDecl.getFieldNameList();

				// [[ don't have any intented attribute.
				if (annotAttrValues.length == 0 || pcnames.length == 0) {
					continue;
				}

				// *****************************************************************
				// Pass the 1st and 2nd parameters with the reffered pattern.
				// *****************************************************************
				String result = detector.findViolation(annotAttrValues, pcnames, pattern);
				clazzCounter += clazzDeclList.size();
				flag = true;
				if (!Boolean.valueOf(result))
					violationCounter++;

				System.out.println("[DBG]" + UtilStr.getShorfileName(javafile) + ":" + result);
				UtilPrint.printArray("* ", annotAttrValues, "");
				System.out.println();
				UtilPrint.printArray("+ ", pcnames, "");
				System.out.println("------------------------------------------");
			}
			if (flag)
				javaCounter++;
		}
		System.out.println("------------------------------------------");
		System.out.println("[DBG] Total Java files: " + javafiles.size());
		System.out.println("[DBG] Checked Java files: " + javaCounter);
		System.out.println("[DBG] Checked Class: " + clazzCounter);
		System.out.println("[DBG] Violated Class: " + violationCounter);
		System.out.println("------------------------------------------");
	}
}
