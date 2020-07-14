package org.generationcp.middleware.service.api.study.germplasm.source;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmStudySourceDto {

	private Integer germplasmStudySourceId;
	private Integer gid;
	private Integer groupId;
	private String designation;
	private String cross;
	private Integer numberOfLots;
	private String breedingMethodAbbreviation;
	private String breedingMethodName;
	private String breedingMethodType;
	private String breedingLocationName;
	private String trialInstance;
	private Integer plotNumber;
	private Integer replicationNumber;
	private Integer germplasmDate;


	public Integer getGermplasmStudySourceId() {
		return this.germplasmStudySourceId;
	}

	public void setGermplasmStudySourceId(final Integer germplasmStudySourceId) {
		this.germplasmStudySourceId = germplasmStudySourceId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public String getCross() {
		return this.cross;
	}

	public void setCross(final String cross) {
		this.cross = cross;
	}

	public Integer getNumberOfLots() {
		return this.numberOfLots;
	}

	public void setNumberOfLots(final Integer numberOfLots) {
		this.numberOfLots = numberOfLots;
	}

	public String getBreedingMethodAbbreviation() {
		return this.breedingMethodAbbreviation;
	}

	public void setBreedingMethodAbbreviation(final String breedingMethodAbbreviation) {
		this.breedingMethodAbbreviation = breedingMethodAbbreviation;
	}

	public String getBreedingMethodName() {
		return this.breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public String getBreedingMethodType() {
		return this.breedingMethodType;
	}

	public void setBreedingMethodType(final String breedingMethodType) {
		this.breedingMethodType = breedingMethodType;
	}

	public String getBreedingLocationName() {
		return this.breedingLocationName;
	}

	public void setBreedingLocationName(final String breedingLocationName) {
		this.breedingLocationName = breedingLocationName;
	}

	public String getTrialInstance() {
		return this.trialInstance;
	}

	public void setTrialInstance(final String trialInstance) {
		this.trialInstance = trialInstance;
	}

	public Integer getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final Integer plotNumber) {
		this.plotNumber = plotNumber;
	}

	public Integer getReplicationNumber() {
		return this.replicationNumber;
	}

	public void setReplicationNumber(final Integer replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

	public Integer getGermplasmDate() {
		return this.germplasmDate;
	}

	public void setGermplasmDate(final Integer germplasmDate) {
		this.germplasmDate = germplasmDate;
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
