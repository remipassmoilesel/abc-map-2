package org.abcmap.gui.dialogs;

import org.abcmap.core.utils.Utils;

/**
 * Result returned by a confirmation dialog.
 */
public class QuestionResult {

    public static final String CANCEL = "CANCEL";
    public static final String YES = "YES";
    public static final String NO = "NO";

    /**
     * Value returned by dialog
     */
    private String returnVal;

    public QuestionResult() {
        this.returnVal = null;
    }

    public QuestionResult(String returnVal) {
        this.returnVal = returnVal;
    }

    /**
     * Set value returned by dialog
     *
     * @param returnVal
     */
    public void setReturnVal(String returnVal) {
        this.returnVal = returnVal;
    }

    /**
     * Update this result object from another result object
     *
     * @param result
     */
    public void update(QuestionResult result) {
        this.returnVal = result.returnVal;
    }

    /**
     * Return the result of the question
     *
     * @return
     */
    public String getReturnVal() {
        return returnVal;
    }

    /**
     * Return true if answer is YES
     *
     * @return
     */
    public boolean isAnswerYes() {
        return Utils.safeEquals(returnVal, YES);
    }

    /**
     * Return true if answer is NO
     *
     * @return
     */
    public boolean isAnswerNo() {
        return Utils.safeEquals(returnVal, NO);
    }

    /**
     * Return true if the answer is CANCEL
     *
     * @return
     */
    public boolean isAnswerCancel() {
        return Utils.safeEquals(returnVal, CANCEL);
    }

    @Override
    public String toString() {
        return "QuestionResult{" +
                "returnVal='" + returnVal + '\'' +
                '}';
    }
}
