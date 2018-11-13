/*
 * @(#) MainExtractor.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation;

import java.io.File;
import java.util.List;

import metadata.invariant.pbse.Comm;
import util.UtilFile;
import util.UtilPrint;
import util.UtilStr;

/**
 * @author Myoungkyu Song
 * @date Jun 29, 2011
 * @since JDK1.6
 */
public class MainExtractor {
	private final String	_fileRelation	= "test5/relation.txt";
	/* class<->table */
	private final String	_tmpdir			= "./test5/temp/";
	private String			_workplace;
	private final String	_path				= "test5/spring-framework-3.1.0.M2/projects";

	public static void main(String[] args) {
		new MainExtractor();
	}

	public MainExtractor() {
		System.out.println("==========================================");
		_workplace = System.getProperty("user.dir") + System.getProperty("file.separator");

		List<String> argList = Comm.readArgs(_fileRelation);
		System.out.println("[DBG] Read relation: " + argList.toString());
		System.out.println("------------------------------------------");

		// * Read the annotated Java source files according to the relation configuration file.
		for (int i = 0; i < argList.size(); i++) {
			String elem = argList.get(i);
			String[] arrayElem = UtilStr.trim(elem.split(","));

			// * Grep all annotated files to save in the temporary directory.
			UtilFile.deleteAllFiles(_tmpdir, "*");
			AnnotationReader readAnnotation = new AnnotationReader(arrayElem);

			List<File> annotatedJavaSrcList = readAnnotation.grepAnnotatedJavaSrc(_workplace + _path);
			UtilFile.saveTemporally(_tmpdir, annotatedJavaSrcList);

			UtilPrint.printFileArrayList(annotatedJavaSrcList);
			// List<String> annotatedJavaClassList = getAnnotatedJavaClassList(_tmpdir);
			// UtilPrint.printArrayList(annotatedJavaClassList);
		}
		System.out.println("==========================================");
	}
}
