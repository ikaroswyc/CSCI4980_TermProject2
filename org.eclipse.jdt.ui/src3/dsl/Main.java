/*
 * @(#) Main.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package dsl;

import java.util.Calendar;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import dsl.gen.DSLGen;

/**
 * @author Myoungkyu Song
 * @date Aug 4, 2011
 * @since JDK1.6
 */
public class Main extends Starter {
	public Main() {
		super();
	}

	/** @METHOD */
	public static void main(String[] args)
	{
		Main m = new Main();
		// --------------------------------------
		// * comment for unit testing.

		long before = Calendar.getInstance().getTimeInMillis();
		if (Comm.getGlobalParm(STR.parm_inference).trim().equals("true")) {
			m.inferInvariantProc();
		}
		long after = Calendar.getInstance().getTimeInMillis();
//		System.out.println("[DBG]" + before);
//		System.out.println("[DBG]" + after);
		System.out.println("[DBG] Inference Time: " + (after - before) + " milliseconds.");
		// --------------------------------------

		m.generateMIL();

		if (Comm.getGlobalParm(STR.parm_violation_check).trim().equals("true")) {
			before = Calendar.getInstance().getTimeInMillis();
			m.checkInvariantProc();
			after = Calendar.getInstance().getTimeInMillis();
			System.out.println("[DBG] Check Time: " + (after - before) + " milliseconds.");
		}
		System.out.println("------------------------------------------");
		System.out.println("[DBG] Done.");
		System.out.println("==========================================");
	}

	/** @METHOD */
	void inferInvariantProc()
	{
		if (Comm.getParm(STR.parm_metadata).equals(STR.parm_annotation)) {
			new relation.annotation.MainRecommender();
		}
		else if (Comm.getParm(STR.parm_metadata).equals(STR.parm_xml)) {
			String main_ex = Comm.getParm("main-extractor");
			System.out.println("[DBG]" + main_ex);

			// if (main_ex != null && !main_ex.isEmpty() && Boolean.valueOf(main_ex))
			new relation.xml.MainExtractor();

			// new relation.xml.MainRecommender();
		}
		else {
			System.out.println("[DBG] Check command batch file.");
		}
	}

	/**
	 * @METHOD Created for separating the module that generates the MIL specification.
	 */
	public void generateMIL()
	{
		DSLGen pbseGenRef = new DSLGen();
		pbseGenRef.pbseGenProc();
	}

	/** @METHOD */
	void checkInvariantProc()
	{
		if (Comm.getParm(STR.parm_metadata).equals(STR.parm_annotation)) {
			if (Comm.getParm(STR.parm_check_type).equals(STR.parm_ommission)) {
				new relation.annotation.MainOmissionFinder();
			}
			else {
				new relation.annotation.MainViolationFinder();
			}
		}
		else if (Comm.getParm(STR.parm_metadata).equals(STR.parm_xml)) {
			new relation.xml.MainViolationFinder();
		}
		else {
			System.out.println("[DBG] Check command batch file.");
		}
	}

	public void checkInvocation()
	{
		System.out.println("[DBG] Check Invocation... pbse.Main!!");
	}
}
