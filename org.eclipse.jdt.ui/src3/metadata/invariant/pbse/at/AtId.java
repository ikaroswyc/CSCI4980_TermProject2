package metadata.invariant.pbse.at;

import metadata.invariant.pbse.STR;

public class AtId {
	private final String	annotation	= "@Id";

	public String getAnnotation() {
		return annotation;
	}

	public static String makeAtId(String annotation_field, String type, String shortfieldname) {
		int pos1 = annotation_field.lastIndexOf(type);
		int pos2 = annotation_field.lastIndexOf(")", pos1);
		String annot = annotation_field.substring(0, pos2 + 1);
		String attrVal = "";

		if(annot.trim().startsWith(STR.at_id)) {
			attrVal = STR.at_id;
		}
		else {
			attrVal = "-";
		}
		
		return STR.at_id + "=" + attrVal;
	}
}
