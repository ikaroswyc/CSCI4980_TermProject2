/*
 * @(#) ClassDecl.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.programconstructs;

/**
 * @author John Edstrom
 * @date Oct 28, 2010
 * @since JDK1.6
 */
public class ClassDecl {
	String modifier;
	String type; 	/*"class" or "interface"*/
	String name;
	
	public ClassDecl() {}
	
	public ClassDecl(String modifier, String type, String name) {
		this.modifier = modifier;
		this.type = type;
		this.name = name;
	}
	
	public String getModifier() {
		return this.modifier;
	}
	
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.modifier + " " + this.type + " " + this.name;
	}
}
