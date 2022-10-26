package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAdvancingSource implements AdvancingSourceAdapter {

	// TODO: these properties should be passed by a context or whatever
	private Integer studyId;
	private Integer environmentDatasetId;
	private List<MeasurementVariable> conditions;
	private Boolean designationIsPreviewOnly;

	private Method breedingMethod;

	private String rootName;
	private Integer rootNameType;

	// These properties values are being set by data resolvers
	private String season;
	private String locationAbbreviation;
	private Integer harvestLocationId;
	private String selectionTraitValue;

	private List<Name> names;

	private Integer plantsSelected;

	private Map<String, Integer> keySequenceMap = new HashMap<>();

	// These are only used by crosses generation process
	private int maleGid;
	private int femaleGid;
	private boolean isForceUniqueNameGeneration;
	private int currentMaxSequence;

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getEnvironmentDatasetId() {
		return environmentDatasetId;
	}

	public void setEnvironmentDatasetId(final Integer environmentDatasetId) {
		this.environmentDatasetId = environmentDatasetId;
	}

	public List<MeasurementVariable> getConditions() {
		return conditions;
	}

	public void setConditions(final List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public Boolean getDesignationIsPreviewOnly() {
		return designationIsPreviewOnly;
	}

	public void setDesignationIsPreviewOnly(final Boolean designationIsPreviewOnly) {
		this.designationIsPreviewOnly = designationIsPreviewOnly;
	}

	public Method getBreedingMethod() {
		return breedingMethod;
	}

	public void setBreedingMethod(final Method breedingMethod) {
		this.breedingMethod = breedingMethod;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(final String rootName) {
		this.rootName = rootName;
	}

	public Integer getRootNameType() {
		return rootNameType;
	}

	public void setRootNameType(final Integer rootNameType) {
		this.rootNameType = rootNameType;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getLocationAbbreviation() {
		return locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public Integer getHarvestLocationId() {
		return harvestLocationId;
	}

	public void setHarvestLocationId(final Integer harvestLocationId) {
		this.harvestLocationId = harvestLocationId;
	}

	public String getSelectionTraitValue() {
		return selectionTraitValue;
	}

	public void setSelectionTraitValue(final String selectionTraitValue) {
		this.selectionTraitValue = selectionTraitValue;
	}

	public List<Name> getNames() {
		return names;
	}

	public void setNames(final List<Name> names) {
		this.names = names;
	}

	public Integer getPlantsSelected() {
		return plantsSelected;
	}

	public void setPlantsSelected(final Integer plantsSelected) {
		this.plantsSelected = plantsSelected;
	}

	public Map<String, Integer> getKeySequenceMap() {
		return keySequenceMap;
	}

	public void setKeySequenceMap(final Map<String, Integer> keySequenceMap) {
		this.keySequenceMap = keySequenceMap;
	}

	public int getMaleGid() {
		return maleGid;
	}

	public void setMaleGid(final int maleGid) {
		this.maleGid = maleGid;
	}

	public int getFemaleGid() {
		return femaleGid;
	}

	public void setFemaleGid(final int femaleGid) {
		this.femaleGid = femaleGid;
	}

	public boolean isForceUniqueNameGeneration() {
		return isForceUniqueNameGeneration;
	}

	public void setForceUniqueNameGeneration(final boolean forceUniqueNameGeneration) {
		isForceUniqueNameGeneration = forceUniqueNameGeneration;
	}

	public int getCurrentMaxSequence() {
		return currentMaxSequence;
	}

	public void setCurrentMaxSequence(final int currentMaxSequence) {
		this.currentMaxSequence = currentMaxSequence;
	}
}
