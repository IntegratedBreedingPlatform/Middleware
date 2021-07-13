package org.generationcp.middleware.service.api.study.germplasm.source;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoProperty
public class GermplasmStudySourceSearchRequest extends SearchRequestDto {

	private int studyId;

	private GermplasmStudySourceSearchRequest.Filter filter;

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public GermplasmStudySourceSearchRequest.Filter getFilter() {
		return this.filter;
	}

	public void setFilter(final GermplasmStudySourceSearchRequest.Filter filter) {
		this.filter = filter;
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

	public static class Filter {

		public static final String GID = "gid";
		public static final String GROUP_ID = "groupId";
		public static final String DESIGNATION = "designation";
		public static final String CROSS = "cross";
		public static final String NUMBER_OF_LOTS = "numberOfLots";
		public static final String BREEDING_METHOD_ABBREVIATION = "breedingMethodAbbreviation";
		public static final String BREEDING_METHOD_NAME = "breedingMethodName";
		public static final String BREEDING_METHOD_TYPE = "breedingMethodType";
		public static final String BREEDING_LOCATION_NAME = "breedingLocationName";
		public static final String TRIAL_INSTANCE = "trialInstance";
		public static final String PLOT_NUMBER = "plotNumber";
		public static final String REPLICATION_NUMBER = "replicationNumber";
		public static final String GERMPLASM_DATE = "germplasmDate";

		public static final List<String> SORTABLE_FIELDS = Collections.unmodifiableList(Arrays
			.asList(GID, GROUP_ID, DESIGNATION, CROSS, NUMBER_OF_LOTS, BREEDING_METHOD_ABBREVIATION, BREEDING_METHOD_NAME, BREEDING_METHOD_TYPE,
				BREEDING_LOCATION_NAME, TRIAL_INSTANCE, REPLICATION_NUMBER, GERMPLASM_DATE));

		private Integer germplasmStudySourceId;
		private List<Integer> gidList;
		private List<Integer> groupIdList;
		private String designation;
		private String cross;
		private List<Integer> numberOfLotsList;
		private String breedingMethodAbbreviation;
		private String breedingMethodName;
		private String breedingMethodType;
		private String breedingLocationName;
		private List<String> trialInstanceList;
		private List<Integer> plotNumberList;
		private List<Integer> replicationNumberList;
		private List<Integer> germplasmDateList;

		public Integer getGermplasmStudySourceId() {
			return this.germplasmStudySourceId;
		}

		public void setGermplasmStudySourceId(final Integer germplasmStudySourceId) {
			this.germplasmStudySourceId = germplasmStudySourceId;
		}

		public List<Integer> getGidList() {
			return gidList;
		}

		public void setGidList(List<Integer> gidList) {
			this.gidList = gidList;
		}

		public List<Integer> getGroupIdList() {
			return groupIdList;
		}

		public void setGroupIdList(List<Integer> groupIdList) {
			this.groupIdList = groupIdList;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(String designation) {
			this.designation = designation;
		}

		public String getCross() {
			return cross;
		}

		public void setCross(String cross) {
			this.cross = cross;
		}

		public List<Integer> getNumberOfLotsList() {
			return numberOfLotsList;
		}

		public void setNumberOfLotsList(List<Integer> numberOfLotsList) {
			this.numberOfLotsList = numberOfLotsList;
		}

		public String getBreedingMethodAbbreviation() {
			return breedingMethodAbbreviation;
		}

		public void setBreedingMethodAbbreviation(String breedingMethodAbbreviation) {
			this.breedingMethodAbbreviation = breedingMethodAbbreviation;
		}

		public String getBreedingMethodName() {
			return breedingMethodName;
		}

		public void setBreedingMethodName(String breedingMethodName) {
			this.breedingMethodName = breedingMethodName;
		}

		public String getBreedingMethodType() {
			return breedingMethodType;
		}

		public void setBreedingMethodType(String breedingMethodType) {
			this.breedingMethodType = breedingMethodType;
		}

		public String getBreedingLocationName() {
			return breedingLocationName;
		}

		public void setBreedingLocationName(String breedingLocationName) {
			this.breedingLocationName = breedingLocationName;
		}

		public List<String> getTrialInstanceList() {
			return trialInstanceList;
		}

		public void setTrialInstanceList(List<String> trialInstanceList) {
			this.trialInstanceList = trialInstanceList;
		}

		public List<Integer> getPlotNumberList() {
			return plotNumberList;
		}

		public void setPlotNumberList(List<Integer> plotNumberList) {
			this.plotNumberList = plotNumberList;
		}

		public List<Integer> getReplicationNumberList() {
			return replicationNumberList;
		}

		public void setReplicationNumberList(List<Integer> replicationNumberList) {
			this.replicationNumberList = replicationNumberList;
		}

		public List<Integer> getGermplasmDateList() {
			return germplasmDateList;
		}

		public void setGermplasmDateList(List<Integer> germplasmDateList) {
			this.germplasmDateList = germplasmDateList;
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

}
