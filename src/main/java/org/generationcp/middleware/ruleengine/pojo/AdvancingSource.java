package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class AdvancingSource {

	private BasicGermplasmDTO originGermplasm;
	private List<BasicNameDTO> names;
	private ObservationUnitRow plotObservation;

	//This will be used if we have trail
	private ObservationUnitRow trialInstanceObservation;

	private Method breedingMethod;
	private Method sourceMethod;

	// These properties values are being set by data resolvers
	private String season;
	private String selectionTraitValue;
	private String locationAbbreviation;
	private Integer harvestLocationId;

	private Integer plantsSelected;
	private List<Integer> sampleNumbers;

	private String rootName;
	private Integer rootNameType;

	private List<Germplasm> advancedGermplasm = new ArrayList<>();

	// These are only used by crosses generation process. Remove them once cross process will be redo it
	private int maleGid;
	private int femaleGid;
	private boolean isForceUniqueNameGeneration;
	private int currentMaxSequence;
	private AdvanceGermplasmChangeDetail changeDetail;

	public AdvancingSource(final BasicGermplasmDTO originGermplasm, final List<BasicNameDTO> names,
		final ObservationUnitRow plotObservation,
		final ObservationUnitRow trialInstanceObservation,
		final Method breedingMethod,
		final Method sourceMethod,
		final String season, final String selectionTraitValue,
		final Integer plantSelected, final List<Integer> sampleNumbers) {

		this.originGermplasm = originGermplasm;
		this.names = names;
		this.plotObservation = plotObservation;
		this.trialInstanceObservation = trialInstanceObservation;

		this.breedingMethod = breedingMethod;
		this.sourceMethod = sourceMethod;
		this.season = season;
		this.selectionTraitValue = selectionTraitValue;
		this.plantsSelected = plantSelected;
		this.sampleNumbers = sampleNumbers;

		// We are setting this properties due to keep backward compatibility with the AdvancingSource of the old advance process
		this.locationAbbreviation = "";
		this.currentMaxSequence = 0;
	}

	public BasicGermplasmDTO getOriginGermplasm() {
		return originGermplasm;
	}

	public List<BasicNameDTO> getNames() {
		return names;
	}

	public ObservationUnitRow getPlotObservation() {
		return plotObservation;
	}

	public ObservationUnitRow getTrialInstanceObservation() {
		return trialInstanceObservation;
	}

	public Method getBreedingMethod() {
		return breedingMethod;
	}

	public Method getSourceMethod() {
		return sourceMethod;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getSelectionTraitValue() {
		return selectionTraitValue;
	}

	public void setSelectionTraitValue(final String selectionTraitValue) {
		this.selectionTraitValue = selectionTraitValue;
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

	public Integer getPlantsSelected() {
		return plantsSelected;
	}

	public List<Integer> getSampleNumbers() {
		return sampleNumbers;
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

	public List<Germplasm> getAdvancedGermplasm() {
		return advancedGermplasm;
	}

	public int getMaleGid() {
		return maleGid;
	}

	public void setOriginGermplasm(final BasicGermplasmDTO originGermplasm) {
		this.originGermplasm = originGermplasm;
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

	public AdvanceGermplasmChangeDetail getChangeDetail() {
		return changeDetail;
	}

	public void setChangeDetail(final AdvanceGermplasmChangeDetail changeDetail) {
		this.changeDetail = changeDetail;
	}

	public boolean isBulkingMethod() {
		return this.getBreedingMethod().isBulkingMethod();
	}

	public void addAdvancedGermplasm(final Germplasm advancedGermplasm) {
		this.advancedGermplasm.add(advancedGermplasm);
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
