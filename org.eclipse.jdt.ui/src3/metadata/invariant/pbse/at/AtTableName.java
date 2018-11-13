/*
 * @(#) AtTableName.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.at;

/**
 * @author John Edstrom 
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class AtTableName implements Annotation {
	private final String 	annotation = "@Table";
	private String			name;
	
	public AtTableName() {}
	
	public AtTableName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAnnotation() {
		return this.annotation;
	}
}
