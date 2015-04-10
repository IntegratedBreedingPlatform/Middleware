
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.oms.StudyType;

/**
 * Summary information about a study (Trials and Nurseries).
 *
 */
public class StudySummary {

	private Integer id;
	private String name;
	private String title;
	private String objective;
	private StudyType type;
	private String startDate;
	private String endDate;
	private String programUUID;

	public StudySummary() {

	}

	public StudySummary(Integer id, String name, String title, String objective, StudyType type, String startDate, String endDate, String programUUID) {
		this.id = id;
		this.name = name;
		this.title = title;
		this.objective = objective;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.programUUID = programUUID;
	}

	public StudySummary(Integer studyId) {
		this.id = studyId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public StudyType getType() {
		return this.type;
	}

	public void setType(StudyType type) {
		this.type = type;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}

	@Override
	public String toString() {
		return "StudySummary [id=" + this.id + ", name=" + this.name + ", title=" + this.title + ", type=" + this.type + "]";
	}
}
