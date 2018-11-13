/*
 * @(#) CtrlFieldType.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation.ctrl;

import java.util.List;

import relation.progelem.AnnotatedClassDecl;

/**
 * @author Myoungkyu Song
 * @date Aug 3, 2011
 * @since JDK1.6
 */
public interface CtrlFieldType {
	public List<AnnotatedClassDecl> cbfGetAnnotatedClassList(List<AnnotatedClassDecl> parm);
}
