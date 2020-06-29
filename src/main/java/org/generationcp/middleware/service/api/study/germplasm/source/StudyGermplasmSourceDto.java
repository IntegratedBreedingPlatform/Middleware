package org.generationcp.middleware.service.api.study.germplasm.source;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StudyGermplasmSourceDto {

	private Integer gid;
	private Integer groupId;
	private String designation;
	private String cross;
	private Integer lots;
	private String breedingMethodAbbrevation;
	private String breedingMethodName;
	private String breedingMethodType;
	private String location;
	private String trialInstance;
	private Integer plotNumber;
	private Integer replicationNumber;
	private Integer germplasmDate;

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

	public Integer getLots() {
		return this.lots;
	}

	public void setLots(final Integer lots) {
		this.lots = lots;
	}

	public String getBreedingMethodAbbrevation() {
		return this.breedingMethodAbbrevation;
	}

	public void setBreedingMethodAbbrevation(final String breedingMethodAbbrevation) {
		this.breedingMethodAbbrevation = breedingMethodAbbrevation;
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

	public String getLocation() {
		return this.location;
	}

	public void setLocation(final String location) {
		this.location = location;
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
