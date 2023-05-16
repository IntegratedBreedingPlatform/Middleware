package org.generationcp.middleware.api.germplasm.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AutoProperty
public class GermplasmSearchRequest extends SearchRequestDto {

	public static class IncludePedigree {

		public enum Type {
			GENERATIVE, DERIVATIVE, BOTH
		}


		private int generationLevel = 1;
		private Type type = Type.GENERATIVE;

		public int getGenerationLevel() {
			return this.generationLevel;
		}

		public void setGenerationLevel(final int generationLevel) {
			this.generationLevel = generationLevel;
		}

		public Type getType() {
			return this.type;
		}

		public void setType(final Type type) {
			this.type = type;
		}
	}

	public static class AttributeRange {
		private String fromValue;

		private String toValue;

		public String getFromValue() {
			return this.fromValue;
		}

		public void setFromValue(final String fromValue) {
			this.fromValue = fromValue;
		}

		public String getToValue() {
			return this.toValue;
		}

		public void setToValue(final String toValue) {
			this.toValue = toValue;
		}
	}

	// filters

	/**
	 * search all names and variations
	 */
	private SqlTextFilter nameFilter;
	private String germplasmUUID;
	private List<Integer> gids;
	private Integer gid;
	private Integer gidFrom;
	private Integer gidTo;
	private Integer groupId;
	private String sampleUID;
	private List<Integer> germplasmListIds;
	private String stockId;
	private String locationOfOrigin;
	private String locationOfUse;
	private String reference;
	private List<Integer> studyOfUseIds;
	private List<Integer> studyOfOriginIds;
	private List<Integer> plantingStudyIds;
	private List<Integer> harvestingStudyIds;
	private String breedingMethodName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date germplasmDateFrom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date germplasmDateTo;
	private SqlTextFilter femaleParentName;
	private SqlTextFilter maleParentName;
	private SqlTextFilter groupSourceName;
	private SqlTextFilter immediateSourceName;
	private Boolean withInventoryOnly;
	private Boolean withRawObservationsOnly;
	private Boolean withAnalyzedDataOnly;
	private Boolean withSampleOnly;
	private Boolean inProgramListOnly;
	private Map<String, String> attributes;

	private Map<String, AttributeRange> attributeRangeMap;
	private Map<String, String> nameTypes;
	private SqlTextFilter externalReferenceSource;
	private SqlTextFilter externalReferenceId;

	// Include associated gids

	private IncludePedigree includePedigree;
	private boolean includeGroupMembers;

	// added columns
	private List<String> addedColumnsPropertyIds = new LinkedList<>();

	// getter / setter

	public SqlTextFilter getNameFilter() {
		return this.nameFilter;
	}

	public void setNameFilter(final SqlTextFilter nameFilter) {
		this.nameFilter = nameFilter;
	}

	public String getBreedingMethodName() {
		return this.breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public Integer getGidFrom() {
		return this.gidFrom;
	}

	public void setGidFrom(final Integer gidFrom) {
		this.gidFrom = gidFrom;
	}

	public Integer getGidTo() {
		return this.gidTo;
	}

	public void setGidTo(final Integer gidTo) {
		this.gidTo = gidTo;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}

	public String getSampleUID() {
		return this.sampleUID;
	}

	public void setSampleUID(final String sampleUID) {
		this.sampleUID = sampleUID;
	}

	public List<Integer> getGermplasmListIds() {
		return this.germplasmListIds;
	}

	public void setGermplasmListIds(final List<Integer> germplasmListIds) {
		this.germplasmListIds = germplasmListIds;
	}

	public String getStockId() {
		return this.stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public String getLocationOfOrigin() {
		return this.locationOfOrigin;
	}

	public void setLocationOfOrigin(final String locationOfOrigin) {
		this.locationOfOrigin = locationOfOrigin;
	}

	public String getLocationOfUse() {
		return this.locationOfUse;
	}

	public void setLocationOfUse(final String locationOfUse) {
		this.locationOfUse = locationOfUse;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public List<Integer> getStudyOfUseIds() {
		return this.studyOfUseIds;
	}

	public void setStudyOfUseIds(final List<Integer> studyOfUseIds) {
		this.studyOfUseIds = studyOfUseIds;
	}

	public List<Integer> getStudyOfOriginIds() {
		return this.studyOfOriginIds;
	}

	public void setStudyOfOriginIds(final List<Integer> studyOfOriginIds) {
		this.studyOfOriginIds = studyOfOriginIds;
	}

	public List<Integer> getPlantingStudyIds() {
		return this.plantingStudyIds;
	}

	public void setPlantingStudyIds(final List<Integer> plantingStudyIds) {
		this.plantingStudyIds = plantingStudyIds;
	}

	public List<Integer> getHarvestingStudyIds() {
		return this.harvestingStudyIds;
	}

	public void setHarvestingStudyIds(final List<Integer> harvestingStudyIds) {
		this.harvestingStudyIds = harvestingStudyIds;
	}

	public Date getGermplasmDateFrom() {
		return this.germplasmDateFrom;
	}

	public void setGermplasmDateFrom(final Date germplasmDateFrom) {
		this.germplasmDateFrom = germplasmDateFrom;
	}

	public Date getGermplasmDateTo() {
		return this.germplasmDateTo;
	}

	public void setGermplasmDateTo(final Date germplasmDateTo) {
		this.germplasmDateTo = germplasmDateTo;
	}

	public SqlTextFilter getFemaleParentName() {
		return this.femaleParentName;
	}

	public void setFemaleParentName(final SqlTextFilter femaleParentName) {
		this.femaleParentName = femaleParentName;
	}

	public SqlTextFilter getMaleParentName() {
		return this.maleParentName;
	}

	public void setMaleParentName(final SqlTextFilter maleParentName) {
		this.maleParentName = maleParentName;
	}

	public SqlTextFilter getGroupSourceName() {
		return this.groupSourceName;
	}

	public void setGroupSourceName(final SqlTextFilter groupSourceName) {
		this.groupSourceName = groupSourceName;
	}

	public SqlTextFilter getImmediateSourceName() {
		return this.immediateSourceName;
	}

	public void setImmediateSourceName(final SqlTextFilter immediateSourceName) {
		this.immediateSourceName = immediateSourceName;
	}

	public Boolean getWithInventoryOnly() {
		return this.withInventoryOnly;
	}

	public void setWithInventoryOnly(final Boolean withInventoryOnly) {
		this.withInventoryOnly = withInventoryOnly;
	}

	public Boolean getWithRawObservationsOnly() {
		return this.withRawObservationsOnly;
	}

	public void setWithRawObservationsOnly(final Boolean withRawObservationsOnly) {
		this.withRawObservationsOnly = withRawObservationsOnly;
	}

	public Boolean getWithAnalyzedDataOnly() {
		return this.withAnalyzedDataOnly;
	}

	public void setWithAnalyzedDataOnly(final Boolean withAnalyzedDataOnly) {
		this.withAnalyzedDataOnly = withAnalyzedDataOnly;
	}

	public Boolean getWithSampleOnly() {
		return this.withSampleOnly;
	}

	public void setWithSampleOnly(final Boolean withSampleOnly) {
		this.withSampleOnly = withSampleOnly;
	}

	public Boolean getInProgramListOnly() {
		return this.inProgramListOnly;
	}

	public void setInProgramListOnly(final Boolean inProgramListOnly) {
		this.inProgramListOnly = inProgramListOnly;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public Map<String, AttributeRange> getAttributeRangeMap() {
		return this.attributeRangeMap;
	}

	public void setAttributeRangeMap(final Map<String, AttributeRange> attributeRangeMap) {
		this.attributeRangeMap = attributeRangeMap;
	}

	public Map<String, String> getNameTypes() {
		return this.nameTypes;
	}

	public void setNameTypes(final Map<String, String> nameTypes) {
		this.nameTypes = nameTypes;
	}

	public IncludePedigree getIncludePedigree() {
		return this.includePedigree;
	}

	public void setIncludePedigree(final IncludePedigree includePedigree) {
		this.includePedigree = includePedigree;
	}

	public boolean isIncludeGroupMembers() {
		return this.includeGroupMembers;
	}

	public void setIncludeGroupMembers(final boolean includeGroupMembers) {
		this.includeGroupMembers = includeGroupMembers;
	}

	public List<String> getAddedColumnsPropertyIds() {
		if (this.addedColumnsPropertyIds == null) {
			this.addedColumnsPropertyIds = new ArrayList<>();
		}
		return this.addedColumnsPropertyIds;
	}

	public void setAddedColumnsPropertyIds(final List<String> addedColumnsPropertyIds) {
		this.addedColumnsPropertyIds = addedColumnsPropertyIds;
	}

	public SqlTextFilter getExternalReferenceSource() {
		return this.externalReferenceSource;
	}

	public void setExternalReferenceSource(final SqlTextFilter externalReferenceSource) {
		this.externalReferenceSource = externalReferenceSource;
	}

	public SqlTextFilter getExternalReferenceId() {
		return this.externalReferenceId;
	}

	public void setExternalReferenceId(final SqlTextFilter externalReferenceId) {
		this.externalReferenceId = externalReferenceId;
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
