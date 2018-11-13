/*
 * @(#) Expression.java
 * Copyright 2010 The Software Innovations Lab, Dept. of Computer Science,
 * Virginia Tech. All rights reserved.
 * 2202 Kraft Drive, Blacksburg, VA 24060
 */
package metadata.invariant.pbse.transform;

import metadata.invariant.pbse.STR;

/**
 * @author Myoungkyu Song
 * @date Nov 2, 2010
 * @since JDK1.6
 */
public class Expression {
	String	newExpr;
	String	oldExpr;

	public void setExpression(String oldExpr) {
		this.oldExpr = oldExpr;
	}

	public String getExpression() {
		return this.newExpr;
	}

	public String getExpression(char delim) {
		String[] tokens = this.oldExpr.split("" + delim);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append(tokens[i]);

			if (i != tokens.length - 1) {
				sb.append(",");
			}
		}

		this.newExpr = String.format(STR.expr_underscore, sb.toString());
		return this.newExpr;
	}

	public int contains(char delim) {
		int cnt = -1;
		for (int i = 0; i < this.oldExpr.length(); i++) {
			char c = this.oldExpr.charAt(i);

			if (c == delim) {
				cnt++;
			}
		}
		return cnt + 1;
	}

}
