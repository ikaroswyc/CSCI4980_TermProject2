/*
 * @(#) Main.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package relation.annotation;

import metadata.invariant.pbse.Comm;
import metadata.invariant.pbse.STR;
import dsl.gen.DSLGen;

/**
 * @author Myoungkyu Song
 * @date Jun 20, 2011
 * @since JDK1.6
 */
public class MainViolationFinder {
	private final String							workplace	= System.getProperty("user.dir") +
																				System.getProperty("file.separator");	;
	private String									relation;
	private String									findViolateTarget;

	private MainRecommender						recommender;
	// private PbsePatternHelper pbseGenerator;
	private DSLGen								pbseGenRef;
	private ViolationDetectorInAnnotation	violationDetector;

	/** @METHOD */
	public static void main(String[] args) {
		new MainViolationFinder();
		System.out.println("==========================================");
	}

	/** @METHOD */
	public MainViolationFinder() {
		// String relation = Comm.getParm(STR.parm_relation);
		// String tokens[] = UtilStr.trim(relation.split(","));

		recommender = new MainRecommender(); // (tokens, tokens[0]);
		// pbseGenerator = new PbsePatternHelper();
		violationDetector = new ViolationDetectorInAnnotation();

		this.setRelation();
		this.setViolateTarget();
		this.execute();
	}

	/** @METHOD */
	private void execute() {
		// * Write PBSE
		// pbseGenerator.writePBSE(relation, recommender);
		// pbseGenRef.pbseGenProc();
		generateMIL();

		// * Detect violation
		violationDetector.readTargetJavaSrc(findViolateTarget);
		violationDetector.findViolation(recommender.getPattern(), relation);
	}

	public void generateMIL() {
		pbseGenRef = new DSLGen();
		pbseGenRef.pbseGenProc();
	}

	private void setRelation() {
		relation = Comm.getParm(STR.parm_relation);
	}

	private void setViolateTarget() {
		findViolateTarget = workplace + Comm.getParm(STR.parm_inspection_target);
	}
}
