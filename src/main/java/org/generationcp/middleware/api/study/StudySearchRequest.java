package org.generationcp.middleware.api.study;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class StudySearchRequest {

	private List<Integer> studyIds;
	private SqlTextFilter studyNameFilter;
	private List<Integer> studyTypeIds;
	private Boolean locked;
	private String ownerName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date studyStartDateFrom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date studyStartDateTo;

	private String parentFolderName;
	private String objective;

	private Map<Integer, String> studySettings = new HashMap<>();

	public List<Integer> getStudyIds() {
		return studyIds;
	}

	public void setStudyIds(final List<Integer> studyIds) {
		this.studyIds = studyIds;
	}

	public SqlTextFilter getStudyNameFilter() {
		return studyNameFilter;
	}

	public void setStudyNameFilter(final SqlTextFilter studyNameFilter) {
		this.studyNameFilter = studyNameFilter;
	}

	public List<Integer> getStudyTypeIds() {
		return studyTypeIds;
	}

	public void setStudyTypeIds(final List<Integer> studyTypeIds) {
		this.studyTypeIds = studyTypeIds;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}

	public Date getStudyStartDateFrom() {
		return studyStartDateFrom;
	}

	public void setStudyStartDateFrom(final Date studyStartDateFrom) {
		this.studyStartDateFrom = studyStartDateFrom;
	}

	public Date getStudyStartDateTo() {
		return studyStartDateTo;
	}

	public void setStudyStartDateTo(final Date studyStartDateTo) {
		this.studyStartDateTo = studyStartDateTo;
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

	public Map<Integer, String> getStudySettings() {
		return studySettings;
	}

	public void setStudySettings(final Map<Integer, String> studySettings) {
		this.studySettings = studySettings;
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
