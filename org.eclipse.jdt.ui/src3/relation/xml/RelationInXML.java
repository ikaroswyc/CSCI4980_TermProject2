package relation.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.transform.RelationShip;
import util.UtilDirScan;
import util.UtilFile;
import util.UtilPrint;
import util.UtilStr;

public class RelationInXML extends RelationShip {
	String			_relationParm[];
	String			_xmlpath;
	String			_relationSavePath;
	String			_xmlsuffix;

	RelationShip	_relation;
	List<String>	_relationList;

	public RelationInXML(String pXmlpath, String[] pRelationParm, String savePath, String suffix) {
		this();
		_relationParm = pRelationParm;
		_xmlpath = pXmlpath;
		_relationSavePath = savePath;
		_xmlsuffix = suffix;

	}

	public RelationInXML() {
		_relation = new RelationShip();
		_relationList = new ArrayList<String>();
	}

	public List<String> getRelationList() {
		return _relationList;
	}

	/** @METHOD */
	void findRelationAndSave(String fileToSave) {
		// fileRelation = workingDir + "/" + fileRelation;
		// _xmlpath = workingDir + "/" + _xmlpath;
		List<String> relationList = new ArrayList<String>();

		// ------------------------------------------
		// * Get all XML files.
		// ------------------------------------------
		List<String> xmlfileList = UtilDirScan.getResultViaStr(_xmlpath, _xmlsuffix);

		// ------------------------------------------
		// * Handle the parameter in the exterior configuration file,
		// * then invoke the XML parser.
		// ------------------------------------------
		String parm = Comm.getParm(STR.parm_relation);
		if (parm.startsWith("?"))
			relationList = findRelation(xmlfileList);
		else
			relationList = findRelation(xmlfileList, _relationParm);

		// ------------------------------------------
		// * Sort and save the pattern list.
		// ------------------------------------------
		Collections.sort(relationList);
		System.out.println("------------------------------------------");
		UtilPrint.printArrayList(relationList);
		System.out.println("------------------------------------------");
		UtilFile.fileWrite(fileToSave, relationList);
		System.out.println("[DBG] Saved in " + fileToSave);
	}

	/** @METHOD */
	List<String> findRelation(List<String> xmllist) {
		String parm = Comm.getParm(STR.parm_relation);
		String parmUpdated = parm.replace("?", " ");
		String[] checkstr = parmUpdated.split(","); // " ", class, "="

		XMLGrep xmlgrep = new XMLGrep();
		// Extract all pair from tag.
		List<XMLTree> xmltrees = xmlgrep.getAllValues(checkstr, xmllist);
		// xmlgrep.print(xmltrees);

		String[] mostPromisingOtherAttr = xmlgrep.getMostPromisingOtherAttr(xmltrees);
		List<String> listValid = new ArrayList<String>();
		List<String> listInvalid = new ArrayList<String>();
		for (int i = 0; i < xmltrees.size(); i++) {
			XMLTree treeElem = xmltrees.get(i);

			String first = treeElem.base;
			String second = xmlgrep.getValue(mostPromisingOtherAttr, treeElem.others);

			if (second == null)
				continue;

			_relationList.add(second);
			_relationList.add(first);

			if (first.contains("."))
				first = UtilStr.getShortClassName(first);
			if (second.contains("."))
				second = UtilStr.getShortClassName(second);

			_relation.setInfo(Comm.getParm("relation-var").split(","));
			String relationResult = _relation.findRelation(first, second);

			if (relationResult.equals("INVALID PATTERN")) {
				listInvalid.add(relationResult + " - " + first + "\t" + second);
			}
			else {
				listValid.add(relationResult);
			}
		}
		// System.out.println("------------------------------------------");
		// Collections.sort(listValid);
		// UtilPrint.printArrayList(listValid);
		// System.out.println("------------------------------------------");
		// System.out.println("------------------------------------------");
		// UtilPrint.printArrayList(listInvalid);
		// System.out.println("------------------------------------------");
		return listValid;
	}

	/** @METHOD */
	ArrayList<String> findRelation(List<String> xmlfileList, String[] relationParm) {
		ArrayList<String> relationList = new ArrayList<String>();

		for (int j = 0; j < xmlfileList.size(); j++) {
			String xmlfile = xmlfileList.get(j);
			ArrayList<String> relationSubList = findRelation(relationParm, xmlfile);

			if (relationSubList != null && relationSubList.isEmpty() == false) {
				relationList.addAll(relationSubList);
			}
		}
		return relationList;
	}

	/** @METHOD */
	public ArrayList<String> findRelation(String[] relationParmArray, String xmlFilepath) {
		// * Move array to list.
		ArrayList<String> relationList = new ArrayList<String>();
		List<String> relationParmList = new ArrayList<String>();

		Collections.addAll(relationParmList, relationParmArray);
		// System.out.println("[DBG] RELATION PARAM: " + relationParmList);

		// * Read the specified value from XML
		List<String> tmp = readXML(xmlFilepath, relationParmList);
		List<String> values = (tmp == null) ? new ArrayList<String>() : tmp;

		// -------------------------------------------
		// * <<< RELATION CASE #1 >>>
		// -------------------------------------------
		// * It is configured through the config-file: <class name="?" table="?" />
		// * [0]: class, [1]: name, [2]: table
		// -------------------------------------------
		if (UtilStr.compare(STR.relation_class_table, relationParmList)) {
			if (values.size() % 2 != 0) { // ___________* Error Case: should be a pair
				System.err.println("[WRN] relation"); // * to find the relation between them.
			}
			for (int i = 0; i < values.size(); i += 2) {
				String className = values.get(i);
				String tableName = values.get(i + 1);
				String relationResult = RelationShip.findClassTable(className, tableName);
				System.out.println("[DBG] RELATION RESULT: " + relationResult + ", classname: " + className);
				relationList.add(relationResult);
			}
		}
		// -------------------------------------------
		// * <<< RELATION CASE #2 >>>
		// -------------------------------------------
		else {
			if (values.size() % 2 != 0) // ___________* Error Case: should be a pair
				System.err.println("[WRN] relation"); // * to find the relation between them.

			String relationVar = Comm.getParm("relation-var");
			
			if (relationVar != null && !relationVar.isEmpty()) {
				_relation.setInfo(Comm.getParm("relation-var").split(","));
			}

			for (int i = 0; i < values.size(); i += 2) {
				String first = values.get(i);
				String second = values.get(i + 1);
				if (first.contains("."))
					first = UtilStr.getShortClassName(first);
				if (second.contains("."))
					second = UtilStr.getShortClassName(second);
				String relationResult = _relation.findRelation(first, second);
				// if (!relationResult.equals("INVALID PATTERN"))
				relationList.add(relationResult);
				// for debugging.
				// if (relationResult.equals("INVALID PATTERN")) {
				// System.out.println("[DBG] file path: " + xmlFilepath);
				// System.out.println("[DBG] ==> " + first + " \t " + second);
				// System.out.println("[DBG]\t" + relationResult);
				// }
			}
		}
		return relationList;
	}

	/** @METHOD */
	List<String> readXML(String xmlFilepath, List<String> relationParmList) {
		XMLReader readXMLInst = new XMLReader(xmlFilepath);
		readXMLInst.parse(relationParmList);
		List<String> values = readXMLInst.getValues();
		// System.out.print("[DBG] VALUE FROM XML: ");
		if (values.isEmpty()) {
			// System.out.println("[DBG]\tNO DATA");
			return null;
		}
		else {
			;// System.out.println();
		}
		// UtilPrint.printListPair(values);
		return values;
	}
}
