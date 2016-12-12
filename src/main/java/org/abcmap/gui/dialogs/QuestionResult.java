package org.abcmap.gui.dialogs;

import org.abcmap.core.utils.Utils;

public class QuestionResult {

	public static final String CANCEL = "CANCEL";
	public static final String YES = "YES";
	public static final String NO = "NO";

	private String returnVal;

	public QuestionResult() {
		this.returnVal = null;
	}
	
	public void setReturnVal(String returnVal) {
		this.returnVal = returnVal;
	}

	public QuestionResult(String returnVal) {
		this.returnVal = returnVal;
	}

	public void update(QuestionResult result) {
		this.returnVal = result.returnVal;
	}

	public String getReturnVal() {
		return returnVal;
	}

	public boolean isAnswerYes() {
		return Utils.safeEquals(returnVal, YES);
	}

	public boolean isAnswerNo() {
		return Utils.safeEquals(returnVal, NO);
	}

	public boolean isAnswerCancel() {
		return Utils.safeEquals(returnVal, CANCEL);
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { returnVal, };
		Object[] keys = new Object[] { "returnVal", };

		return Utils.toString(this, keys, values);

	}

}
