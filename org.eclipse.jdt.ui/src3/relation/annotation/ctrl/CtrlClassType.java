/*
 * @(#) CtrlClassType.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation.ctrl;

import java.util.List;

import relation.progelem.AnnotatedClassDecl;

/**
 * @author Myoungkyu Song
 * @date Jan 7, 2012
 * @since JDK1.6
 */
public interface CtrlClassType {
	public AnnotatedClassDecl cbfGetAnnotatedClassList(List<AnnotatedClassDecl> clazzlist);
}
