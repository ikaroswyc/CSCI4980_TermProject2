/*
 * @(#) AbstractMain.java
 * 
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl;

import metadata.invariant.pbse.Comm;
import dsl.gen.ParameterHelper;

/**
 * @author Myoungkyu Song
 * @date Aug 11, 2011
 * @since JDK1.6
 */
public class Starter extends ParameterHelper {
	static {
		Comm.readTargetToBind();
		preface();
	}

	/** @METHOD */
	static void preface() {
		System.out.println("==========================================");
//		System.out.println("[DBG] Path used for inference: " + Comm.getParm(STR.parm_path));
//		System.out.println("[DBG] Element used for inference: " + Comm.getParm(STR.parm_relation));
//		System.out.println("[DBG] Path for checking invariant violation: " + Comm.getParm(STR.parm_inspection_target));
//		System.out.println("[DBG] Metadata category: " + Comm.getParm(STR.parm_metadata));
		System.out.println("------------------------------------------");
	}
}
 