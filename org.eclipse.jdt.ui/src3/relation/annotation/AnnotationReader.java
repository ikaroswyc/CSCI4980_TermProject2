package relation.annotation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import metadata.invariant.pbse.STR;
import util.UtilDirScan;
import util.UtilFile;


public class AnnotationReader {
	// String[] mustSeeAnnotation = {
	// // "@Autowired",
	// "@Configuration" };
	String[]			skipFileAndDir				= { ".jar", ".javax", "/src/test/java/" };
	String[]			skiplist						= { "@param", "@link", "@Override", "assert", "/*", "@throws",
														"@return", "@author", "@value", "@code", "@serial", "test", "@see",
														"@version", "@Usage", "@since", "@Test", "@SuppressWarnings", "@Deprecated",
														"@deprecated", "@inheritDoc", "@Documented", "@Schedule", "Ignore",
														"Override", "@localhost", "@jabber", "@Evaluator",
														"@Before", "@After", "@Target", "@Retention",
														"@gmail", "@blah" };

	List<String>	mustSeeAnnotationList	= new ArrayList<String>();

	public AnnotationReader(String[] arrayElem) {
		if (arrayElem.length == 3) {
			mustSeeAnnotationList.add(arrayElem[2]);
		}
	}

	/** @METHOD */
	List<File> grepAnnotatedJavaSrc(String path) {
		String filter = STR.file_java;
		ArrayList<File> javaSrcList = UtilDirScan.getResult(path, filter);
		List<File> javaAnnotatedSrcList = new ArrayList<File>();

		for (int i = 0; i < javaSrcList.size(); i++) {
			File javafile = javaSrcList.get(i);

			if (isSkipFile(javafile))
				continue;

			List<String> filebody = UtilFile.fileRead2List(javafile.getAbsolutePath());
			List<String> tmplist = fileScanAnnotation(filebody, javafile);
			if (tmplist.isEmpty() == false) {
				javaAnnotatedSrcList.add(javafile);
			}
		}
		return javaAnnotatedSrcList;
	}

	/** @METHOD */
	List<String> fileScanAnnotation(List<String> filebody, File file) {
		List<String> mustSeeList = new ArrayList<String>();

		for (int i = 0; i < filebody.size(); i++) {
			String line = filebody.get(i);
			if (isSkipStr(line))
				continue;
			for (int j = 0; j < mustSeeAnnotationList.size(); j++) {
				if (line.contains(mustSeeAnnotationList.get(j)))
					mustSeeList.add(line);
			}
		}
		return mustSeeList;
	}

	/** @METHOD */
	boolean isSkipFile(File file) {
		for (int i = 0; i < skipFileAndDir.length; i++)
			if (file.getAbsolutePath().contains(skipFileAndDir[i]))
				return true;
		return false;
	}

	/** @METHOD */
	boolean isSkipStr(String line) {
		for (int i = 0; i < skiplist.length; i++) {
			if (line.trim().startsWith("//") ||
					line.trim().startsWith("*") ||
					line.trim().contains("\"") ||
					line.trim().contains("\"@") ||
					line.trim().startsWith("fail"))
				return true;
			if (line.trim().contains(skiplist[i]))
				return true;
		}
		return false;
	}
}
