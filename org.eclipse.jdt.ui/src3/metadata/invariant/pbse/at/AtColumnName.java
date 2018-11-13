/*
 * @(#) AtColumnName.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.at;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2010
 * @since JDK1.6
 */
public class AtColumnName implements Annotation {
	private final String	annotation	= "@Column";
	private String			name;

	public AtColumnName() {}

	public AtColumnName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getAnnotation() {
		return annotation;
	}
}
