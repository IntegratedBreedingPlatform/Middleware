
package org.generationcp.middleware.ruleengine.pojo;

public class AdvanceGermplasmChangeDetail {

	private int status;
	private int index;
	private String newAdvanceName;
	private String oldAdvanceName;
	private String questionText;
	private String addSequenceText;

	public AdvanceGermplasmChangeDetail() {
		super();
	}

	public AdvanceGermplasmChangeDetail(int status, int index, String newAdvanceName, String oldAdvanceName) {
		super();
		this.status = status;
		this.index = index;
		this.newAdvanceName = newAdvanceName;
		this.oldAdvanceName = oldAdvanceName;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getNewAdvanceName() {
		return this.newAdvanceName;
	}

	public void setNewAdvanceName(String newAdvanceName) {
		this.newAdvanceName = newAdvanceName;
	}

	public String getOldAdvanceName() {
		return this.oldAdvanceName;
	}

	public void setOldAdvanceName(String oldAdvanceName) {
		this.oldAdvanceName = oldAdvanceName;
	}

	/**
	 * @return the questionText
	 */
	public String getQuestionText() {
		return this.questionText;
	}

	/**
	 * @param questionText the questionText to set
	 */
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	/**
	 * @return the addSequenceText
	 */
	public String getAddSequenceText() {
		return this.addSequenceText;
	}

	/**
	 * @param addSequenceText the addSequenceText to set
	 */
	public void setAddSequenceText(String addSequenceText) {
		this.addSequenceText = addSequenceText;
	}

}
