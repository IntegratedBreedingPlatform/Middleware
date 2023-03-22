package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class AdvancingSource {

	private BasicGermplasmDTO originGermplasm;
	private final List<BasicNameDTO> names;
	private final ObservationUnitRow observation;

	//This will be used if we have trial
	private final ObservationUnitRow trialInstanceObservation;

	private final Method breedingMethod;
	private final Method sourceMethod;

	// These properties values are being set by data resolvers
	private String season;
	private String selectionTraitValue;
	private String locationAbbreviation;
	private Integer harvestLocationId;

	private final Integer plantsSelected;
	private final List<SampleDTO> sampleDTOS;

	private String rootName;
	private Integer rootNameType;

	private final List<Germplasm> advancedGermplasm = new ArrayList<>();

	// These are only used by crosses generation process. Remove them once cross process will be redo it
	private int maleGid;
	private int femaleGid;
	private boolean isForceUniqueNameGeneration;
	private int currentMaxSequence;
	private AdvanceGermplasmChangeDetail changeDetail;

	// These are used to track sequences on preview mode if needed
	private boolean isPreview = false;
	private Map<String, Integer> keySequenceMap = new HashMap<>();

	public AdvancingSource(final BasicGermplasmDTO originGermplasm, final List<BasicNameDTO> names,
		final ObservationUnitRow observation,
		final ObservationUnitRow trialInstanceObservation,
		final Method breedingMethod,
		final Method sourceMethod,
		final String season, final String selectionTraitValue,
		final Integer plantSelected, final List<SampleDTO> sampleDTOS) {

		this.originGermplasm = originGermplasm;
		this.names = names;
		this.observation = observation;
		this.trialInstanceObservation = trialInstanceObservation;

		this.breedingMethod = breedingMethod;
		this.sourceMethod = sourceMethod;
		this.season = season;
		this.selectionTraitValue = selectionTraitValue;
		this.plantsSelected = plantSelected;
		this.sampleDTOS = sampleDTOS;

		// We are setting this properties due to keep backward compatibility with the AdvancingSource of the old advance process
		this.locationAbbreviation = "";
		this.currentMaxSequence = 0;
	}

	public BasicGermplasmDTO getOriginGermplasm() {
		return this.originGermplasm;
	}

	public List<BasicNameDTO> getNames() {
		return this.names;
	}

	public ObservationUnitRow getObservation() {
		return this.observation;
	}

	public ObservationUnitRow getTrialInstanceObservation() {
		return this.trialInstanceObservation;
	}

	public Method getBreedingMethod() {
		return this.breedingMethod;
	}

	public Method getSourceMethod() {
		return this.sourceMethod;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getSelectionTraitValue() {
		return this.selectionTraitValue;
	}

	public void setSelectionTraitValue(final String selectionTraitValue) {
		this.selectionTraitValue = selectionTraitValue;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public Integer getHarvestLocationId() {
		return this.harvestLocationId;
	}

	public void setHarvestLocationId(final Integer harvestLocationId) {
		this.harvestLocationId = harvestLocationId;
	}

	public Integer getPlantsSelected() {
		return this.plantsSelected;
	}

	public List<SampleDTO> getSampleDTOS() {
		return this.sampleDTOS;
	}

	public String getRootName() {
		return this.rootName;
	}

	public void setRootName(final String rootName) {
		this.rootName = rootName;
	}

	public Integer getRootNameType() {
		return this.rootNameType;
	}

	public void setRootNameType(final Integer rootNameType) {
		this.rootNameType = rootNameType;
	}

	public List<Germplasm> getAdvancedGermplasm() {
		return this.advancedGermplasm;
	}

	public int getMaleGid() {
		return this.maleGid;
	}

	public void setOriginGermplasm(final BasicGermplasmDTO originGermplasm) {
		this.originGermplasm = originGermplasm;
	}

	public void setMaleGid(final int maleGid) {
		this.maleGid = maleGid;
	}

	public int getFemaleGid() {
		return this.femaleGid;
	}

	public void setFemaleGid(final int femaleGid) {
		this.femaleGid = femaleGid;
	}

	public boolean isForceUniqueNameGeneration() {
		return this.isForceUniqueNameGeneration;
	}

	public void setForceUniqueNameGeneration(final boolean forceUniqueNameGeneration) {
		this.isForceUniqueNameGeneration = forceUniqueNameGeneration;
	}

	public int getCurrentMaxSequence() {
		return this.currentMaxSequence;
	}

	public void setCurrentMaxSequence(final int currentMaxSequence) {
		this.currentMaxSequence = currentMaxSequence;
	}

	public AdvanceGermplasmChangeDetail getChangeDetail() {
		return this.changeDetail;
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

	public boolean isPreview() {
		return this.isPreview;
	}

	public void setPreview(final boolean preview) {
		this.isPreview = preview;
	}

	public Map<String, Integer> getKeySequenceMap() {
		return this.keySequenceMap;
	}

	public void setKeySequenceMap(final Map<String, Integer> keySequenceMap) {
		this.keySequenceMap = keySequenceMap;
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
