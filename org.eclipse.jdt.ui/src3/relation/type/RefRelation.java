/*
 * @(#) RefRelation.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.type;

/**
 * @author Myoungkyu Song
 * @date Jul 12, 2011
 * @since JDK1.6
 */
public class RefRelation implements Configuration, FieldNameAnnotAttr, FieldNameClassAnnotAttr,
								MethodReturnTypeAnnotationAttrName {
	String	_msg;

	public RefRelation(String s) {
		_msg = s;
	}

	public String toString() {
		return _msg;
	}
}
