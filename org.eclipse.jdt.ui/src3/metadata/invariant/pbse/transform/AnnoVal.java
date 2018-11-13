/*
 * @(#) AnnoVal.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.transform;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import metadata.invariant.pbse.STR;
import metadata.invariant.pbse.util.StrComb;

/**
 * @author John Edstrom
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class AnnoVal {
	String					_className;
	String					_otherClassName;	// @JoinTable(name="STUDENT_COURSE") private Set<Course> courses
	String					_fieldName;
	String					_attrVal;
	Map<String, String>	_variables;
	Expression				_expr;				// express prog.variable with the PBSE style

	public AnnoVal() {}

	public AnnoVal(String fieldName, String attrVal) {
		_fieldName = fieldName;
		_variables = new HashMap<String, String>();
		_variables.put(STR.var_fieldname, _fieldName);
		_attrVal = attrVal;
		_expr = new Expression();
	}

	public AnnoVal(String className, String otherClassName, String fieldName, String attrVal) {
		this(fieldName, attrVal);
		_className = className;
		_otherClassName = otherClassName;
		_variables.put(STR.var_classname, _className);
		_variables.put(STR.var_otherclassname, _otherClassName);
	}

	public AnnoVal(Map<String, String> variables, String attrVal) {
		_variables = variables;
		_attrVal = attrVal;
		_expr = new Expression();
	}

	/**
	 * @METHOD
	 */
	public String getTransformations() {
		String result = null;
		StrComb sc = new StrComb("_");
		Map<String, String> perms = sc.combine(_variables);
		for (String keyStr : perms.keySet()) {

			String progConstructVarName = keyStr; // $className, $fieldName, $classname_$otherclassname, etc
			String progConstructRealName = perms.get(keyStr); // Student, StudentId, Student_Course, etc
			String annotationAttrVal = _attrVal; // STUDENT, STUDENT_ID, STUDENT_COURSE, etc

			result = getTransformations(progConstructVarName, progConstructRealName, annotationAttrVal);
			if (result != null) {

				// in case of '$var1_$var2_$var3_...', it returns 'underscore(var1,var2,var3,..) 
				_expr.setExpression(progConstructVarName);
				if (_expr.contains('_') > 0) {
					// System.out.println("--> Prog.Variable : " + progConstructVarName);
					// System.out.println("--> Prog.Name: " + progConstructRealName);
					// System.out.println("--> Annotation.AttrVal: " + annotationAttrVal);
					// System.out.println("--->" + _expr.getExpression('_'));
					result = _expr.getExpression('_');
				}
				return result;
			}
		}
		return result;
	}

	/**
	 * @METHOD
	 */
	private String getTransformations(String varStr, String str, String attrVal) {
		LinkedList<Transformation> transforms = null;
		String transformStr = null;

		ArrayDeque<State> Q = new ArrayDeque<State>();
		Q.addLast(new State(str));

		while (!Q.isEmpty()) {
			State curr = Q.removeFirst();

			if (curr.toString().equals(attrVal)) {
				transforms = curr.getTransformations();
				transformStr = getTransformationString(transforms, varStr);
				break;
			}

			for (State s : curr.nextTransformations())
				Q.addLast(s);
		}
		return transformStr;
	}

	/**
	 * @METHOD
	 */
	public String getTransformationString(LinkedList<Transformation> transforms, String varStr) {
		if (transforms == null)
			return "NOT POSSIBLE";
		if (transforms.isEmpty())
			return "NO TRANSFORMATIONS NEEDED";
		String ans = new String(varStr);
		for (Transformation t : transforms)
			ans = t.toString() + "(" + ans + ")";
		ans = "$attrval = " + ans;
		return ans;
	}

	/**
	 * @METHOD
	 */
	private class State {
		private String								str;
		private LinkedList<Transformation>	transforms;

		public State(String s) {
			this.str = s;
			this.transforms = new LinkedList<Transformation>();
		}

		public State(State parent) {
			this.str = parent.str;
			this.transforms = new LinkedList<Transformation>(parent.transforms);
		}

		public String toString() {
			return str;
		}

		public LinkedList<Transformation> getTransformations() {
			return transforms;
		}

		public List<State> nextTransformations() {
			List<State> ans = new LinkedList<State>();
			for (Transformation t : Transformation.values()) {
				if (!transforms.contains(t)) {
					State next = new State(this);
					next.str = t.transform(this.str);
					next.transforms.add(t);
					ans.add(next);
				}
			}
			return ans;
		}
	}

}
