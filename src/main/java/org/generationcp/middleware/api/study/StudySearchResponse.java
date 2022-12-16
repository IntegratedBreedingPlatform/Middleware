package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class StudySearchResponse {

	private Integer studyId;
	private String studyName;
	private String studyTypeName;
	private boolean locked;
	private String ownerName;
	private Date startDate;
	private String parentFolderName;
	private String objective;

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getStudyTypeName() {
		return studyTypeName;
	}

	public void setStudyTypeName(final String studyTypeName) {
		this.studyTypeName = studyTypeName;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(final boolean locked) {
		this.locked = locked;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public String getParentFolderName() {
		return parentFolderName;
	}

	public void setParentFolderName(final String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
