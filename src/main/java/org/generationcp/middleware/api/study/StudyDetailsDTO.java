package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class StudyDetailsDTO {

	private Integer id;
	private String name;
	private String description;
	private String objective;
	private String createdByName;
	private Date startDate;
	private Date endDate;
	private Date lastUpdateDate;

	private Integer numberOfEntries;
	private Integer numberOfPlots;
	private boolean hasFieldLayout;
	private Integer numberOfVariablesWithData;

//	private List<VariableDetails> studySettings;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(final String createdByName) {
		this.createdByName = createdByName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(final Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Integer getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(final Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public Integer getNumberOfPlots() {
		return numberOfPlots;
	}

	public void setNumberOfPlots(final Integer numberOfPlots) {
		this.numberOfPlots = numberOfPlots;
	}

	public boolean isHasFieldLayout() {
		return hasFieldLayout;
	}

	public void setHasFieldLayout(final boolean hasFieldLayout) {
		this.hasFieldLayout = hasFieldLayout;
	}

	public Integer getNumberOfVariablesWithData() {
		return numberOfVariablesWithData;
	}

	public void setNumberOfVariablesWithData(final Integer numberOfVariablesWithData) {
		this.numberOfVariablesWithData = numberOfVariablesWithData;
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
