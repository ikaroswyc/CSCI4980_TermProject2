/*
 * @(#) FieldDecl.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.programconstructs;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2010
 * @since JDK1.6
 */
public class FieldDecl {
	String	modifier;
	String	type;
	String	name;

	public FieldDecl() {}
	
	public FieldDecl(String modifier, String type, String name) {
		this.modifier = modifier;
		this.type = type;
		this.name = name;
	}
	
	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
