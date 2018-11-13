package relation;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class PatternAnalyser {
	/** @METHOD */
	public Stack<String> parsePattern(String pattern) {
		Stack<String> stack = new Stack<String>();
		String[] patternArray = pattern.split("\\+");

		for (int i = 0; i < patternArray.length; i++) {
			// System.out.println("[DBG]" + patternArray[i].trim());
			String thePattern = patternArray[i].trim();
			int numOfOpr = getNumOfOpr(thePattern);

			if (numOfOpr == 1) {
				stack.add(thePattern);
			}
			else {
				Queue<String> subQueue = new LinkedList<String>();
				int fromIndex = 0;

				for (int j = 0; j < numOfOpr; j++) {
					int pos = thePattern.indexOf("(", fromIndex);
					String subs = thePattern.substring(fromIndex, pos);

					if (isParam(thePattern, pos + 1)) {
						subQueue.add(subs + getParam(thePattern, fromIndex));
					}
					else {
						subQueue.add(subs);
					}
					fromIndex = pos + 1;
				}
				int size2 = subQueue.size();
				for (int j = 0; j < size2; j++) {
					stack.add(subQueue.remove());
				}
			}
		}
		return stack;
	}

	/** @METHOD */
	int getNumOfOpr(String opr) {
		int cnt = 0;
		for (int i = opr.length() - 1; i > -1; i--) {
			if (opr.charAt(i) == ')')
				cnt++;
			else if (opr.charAt(i) == '(')
				break;
		}
		return cnt;
	}

	/** @METHOD */
	boolean isParam(String thePattern, int fromIndex) {
		int pos1 = thePattern.indexOf('(', fromIndex);
		if (pos1 == -1)
			return true;
		return false;
	}

	/** @METHOD */
	String getParam(String thePattern, int fromIndex) {
		int pos1 = thePattern.indexOf('(', fromIndex);
		int pos2 = thePattern.indexOf(')', fromIndex);
		return thePattern.substring(pos1, pos2 + 1);
	}

	/** @METHOD */
	public PtrnOprEnum getPtrnOpr(String theOpr) {
		if (theOpr.startsWith("UPPERCASE"))
			return PtrnOprEnum.UPPERCASE;
		else if (theOpr.startsWith("PREFIX"))
			return PtrnOprEnum.PREFIX;
		else if (theOpr.startsWith("ENDSWITH"))
			return PtrnOprEnum.ENDSWITH;
		else if (theOpr.startsWith("MATCH"))
			return PtrnOprEnum.MATCH;
		else if (theOpr.startsWith("LOWFIRSTCHAR"))
			return PtrnOprEnum.LOWFIRSTCHAR;
		else if (theOpr.startsWith("CONTAINS"))
			return PtrnOprEnum.CONTAINS;
		return PtrnOprEnum.INVALID;
	}
}
