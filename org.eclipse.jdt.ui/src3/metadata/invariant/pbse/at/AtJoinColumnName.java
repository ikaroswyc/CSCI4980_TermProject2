/*
 * @(#) AtJoinColumnName.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.at;

/**
 * @author Myoungkyu Song
 * @date Nov 5, 2010
 * @since JDK1.6
 * @Example
 * -- AtJoinTableJoinColumns ..> AtJoinColumnName
 * -- AtJoinTableInverseJoinColumns ..> AtJoinColumnName
 */
public class AtJoinColumnName {
	private final String	annotation	= "@JoinColumn";
	private String			name;
	private String			transforms;

	public AtJoinColumnName() {}

	public AtJoinColumnName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getAnnotation() {
		return annotation;
	}

	public String getTransforms() {
		return transforms;
	}

	public void setTransforms(String transforms) {
		this.transforms = transforms;
	}

}
