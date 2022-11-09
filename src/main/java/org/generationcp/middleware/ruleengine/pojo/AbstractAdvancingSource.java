package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.pojos.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAdvancingSource implements AdvancingSourceAdapter {

	// TODO: these properties should be passed by a context or whatever
	protected Integer studyId;
	protected Integer environmentDatasetId;
	// Setting conditions for Breeders Cross ID
	protected List<MeasurementVariable> conditions;
	protected Boolean designationIsPreviewOnly;

	protected Method breedingMethod;
	protected Method sourceMethod;

	protected String rootName;
	protected Integer rootNameType;

	// These properties values are being set by data resolvers
	protected String season;
	protected String locationAbbreviation;
	protected Integer harvestLocationId;
	protected String selectionTraitValue;

	protected List<BasicNameDTO> names;

	protected Integer plantsSelected;

	protected Map<String, Integer> keySequenceMap = new HashMap<>();

	// TODO: we can implement a visitor to check the type of class extending from AbstractAdvancingSource and move these to AdvancingSource
	// These are only used by crosses generation process. Remove them once cross process will be redo it
	protected int maleGid;
	protected int femaleGid;
	protected boolean isForceUniqueNameGeneration;
	protected int currentMaxSequence;
	protected AdvanceGermplasmChangeDetail changeDetail;

	public Integer getStudyId() {
		return studyId;
	}

	public Integer getEnvironmentDatasetId() {
		return environmentDatasetId;
	}

	public List<MeasurementVariable> getConditions() {
		return conditions;
	}

	public Boolean getDesignationIsPreviewOnly() {
		return designationIsPreviewOnly;
	}

	public Method getBreedingMethod() {
		return breedingMethod;
	}

	public Method getSourceMethod() {
		return this.sourceMethod;
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

	public List<BasicNameDTO> getNames() {
		return names;
	}

	public Integer getPlantsSelected() {
		return plantsSelected;
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

	public int getFemaleGid() {
		return femaleGid;
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

	public AdvanceGermplasmChangeDetail getChangeDetail() {
		return changeDetail;
	}

	public void setChangeDetail(final AdvanceGermplasmChangeDetail changeDetail) {
		this.changeDetail = changeDetail;
	}
}
