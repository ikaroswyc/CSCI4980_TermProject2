/*
 * @(#) Transformation.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.transform;

import java.util.ArrayList;

import util.UtilStr;


/**
 * @author John Edstrom
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public enum Transformation {
	UPPER("upper"), 
	LOWER("lower"), 
	UNDERSCORE("underscore"), 
	CAMEL("camel"),
	SINGULAR("singular"),
	PLURAL("plural");

	private String	m_str;

	Transformation(String str) {
		m_str = str;
	}

	public String toString() {
		return m_str;
	}

	public String transform(String str) {
		ArrayList<String> tokens = UtilStr.ExplodeString(str);
		String ans = "";
		int i = 0;
		switch (this) {
		case UPPER:
			ans = str.toUpperCase();
			break;
		case LOWER:
			ans = str.toLowerCase();
			break;
		case UNDERSCORE:
			for (String s : tokens)
				ans += ((i++) == 0 ? "" : "_") + s;
			break;
		case CAMEL:
			for (String s : tokens)
				ans += ((i++) == 0 ? s.substring(0, 1) : s.substring(0, 1).toUpperCase()) + s.substring(1).toLowerCase();
			break;
		}
		return ans;
	}
}
