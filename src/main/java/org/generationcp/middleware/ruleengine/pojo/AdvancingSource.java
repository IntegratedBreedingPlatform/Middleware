/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.ruleengine.pojo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * The POJO containing information needed for Advancing.
 *
 */

// TODO: create an abstract class which implements an interface -> add originGermplasm
@Deprecated
public class AdvancingSource extends AbstractAdvancingSource {

	private ImportedGermplasm germplasm;
	private List<Name> names;
	private Integer plantsSelected;
	private Method breedingMethod;
	/**
	 * This field is used to temporarily store the breeding method ID until such time as it can be resolved to a proper breeding Method object
	 */
	private Integer breedingMethodId;
	private boolean isCheck;
	private boolean isBulk;
	private String studyName;
	private Integer studyId;
	private Integer environmentDatasetId;
	// TODO: move to abstract
	private String season;
	private String locationAbbreviation;
	private String rootName;
	private Method sourceMethod;
	private int currentMaxSequence;
	private AdvanceGermplasmChangeDetail changeDetail;
	private String prefix;
	private String suffix;
	private Integer rootNameType;
	// TODO: move to abstract
	private Integer harvestLocationId;
	private String plotNumber;
    private String selectionTraitValue;
    
    private String trialInstanceNumber;
    private String replicationNumber;

	private int maleGid;

	private int femaleGid;

	private boolean isForceUniqueNameGeneration;

	//This will be used to store conditions
	private List<MeasurementVariable> conditions;
	//This will be used if we have trail
	private MeasurementRow trailInstanceObservation;

	private StudyTypeDto studyType;

	private List<SampleDTO> samples = new ArrayList<>();
	private Boolean designationIsPreviewOnly;
	private Map<String, Integer> keySequenceMap = new HashMap<>();

	public AdvancingSource(final ImportedGermplasm germplasm, final List<Name> names, final Integer plantsSelected, final Method breedingMethod, final boolean isCheck,
			final String studyName, final String plotNumber) {
		super();
		this.germplasm = germplasm;
		this.names = names;
		this.plantsSelected = plantsSelected;
		this.breedingMethod = breedingMethod;
		this.isCheck = isCheck;
		this.studyName = studyName;
		this.plotNumber = plotNumber;
	}

	public AdvancingSource(final ImportedGermplasm germplasm) {
		super();
		this.germplasm = germplasm;
	}

    public AdvancingSource() {
		super();
	}

	/**
	 * @return the germplasm
	 */
	public ImportedGermplasm getGermplasm() {
		return this.germplasm;
	}

	/**
	 * @param germplasm the germplasm to set
	 */
	public void setGermplasm(final ImportedGermplasm germplasm) {
		this.germplasm = germplasm;
	}

	/**
	 * @return the plantsSelected
	 */
	public Integer getPlantsSelected() {
		return this.plantsSelected;
	}

	/**
	 * @param plantsSelected the plantsSelected to set
	 */
	public void setPlantsSelected(final Integer plantsSelected) {
		this.plantsSelected = plantsSelected;
	}

	/**
	 * @return the isCheck
	 */
	public boolean isCheck() {
		return this.isCheck;
	}

	/**
	 * @param isCheck the isCheck to set
	 */
	public void setCheck(final boolean isCheck) {
		this.isCheck = isCheck;
	}

	/**
	 * @return the isBulk
	 */
	public boolean isBulk() {
		final Boolean isBulk = this.getBreedingMethod().isBulkingMethod();
		return this.getBreedingMethod() != null && isBulk != null ? isBulk : false;
	}

	/**
	 * @param isBulk the isBulk to set
	 */
	public void setBulk(final boolean isBulk) {
		this.isBulk = isBulk;
	}

	/**
	 * @return the names
	 */
	public List<Name> getNames() {
		return this.names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(final List<Name> names) {
		this.names = names;
	}

	/**
	 * @return the breedingMethod
	 */
	public Method getBreedingMethod() {
		return this.breedingMethod;
	}

	/**
	 * @param breedingMethod the breedingMethod to set
	 */
	public void setBreedingMethod(final Method breedingMethod) {
		this.breedingMethod = breedingMethod;
	}

	/**
	 * @return the studyName
	 */
	public String getStudyName() {
		return this.studyName;
	}

	/**
	 * @param studyName the studyName to set
	 */
	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getEnvironmentDatasetId() {
		return this.environmentDatasetId;
	}

	public void setEnvironmentDatasetId(final Integer environmentDatasetId) {
		this.environmentDatasetId = environmentDatasetId;
	}

	/**
	 * @return the season
	 */
	public String getSeason() {
		return this.season;
	}

	/**
	 * @param season the season to set
	 */
	public void setSeason(final String season) {
		this.season = season;
	}

	/**
	 * @return the locationAbbreviation
	 */
	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	/**
	 * @param locationAbbreviation the locationAbbreviation to set
	 */
	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	/**
	 * @return the rootName
	 */
	public String getRootName() {
		return this.rootName;
	}

	/**
	 * @param rootName the rootName to set
	 */
	public void setRootName(final String rootName) {
		this.rootName = rootName;
	}

	/**
	 * @return the sourceMethod
	 */
	public Method getSourceMethod() {
		return this.sourceMethod;
	}

	/**
	 * @param sourceMethod the sourceMethod to set
	 */
	public void setSourceMethod(final Method sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	/**
	 * @return the currentMaxSequence
	 */
	public int getCurrentMaxSequence() {
		return this.currentMaxSequence;
	}

	/**
	 * @param currentMaxSequence the currentMaxSequence to set
	 */
	public void setCurrentMaxSequence(final int currentMaxSequence) {
		this.currentMaxSequence = currentMaxSequence;
	}

	/**
	 * @return the changeDetail
	 */
	public AdvanceGermplasmChangeDetail getChangeDetail() {
		return this.changeDetail;
	}

	/**
	 * @param changeDetail the changeDetail to set
	 */
	public void setChangeDetail(final AdvanceGermplasmChangeDetail changeDetail) {
		this.changeDetail = changeDetail;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return this.suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	public boolean isForceUniqueNameGeneration() {
		return this.isForceUniqueNameGeneration;
	}

	public void setForceUniqueNameGeneration(final boolean isForceUniqueNameGeneration) {
		this.isForceUniqueNameGeneration = isForceUniqueNameGeneration;
	}

	public Integer getRootNameType() {
		return this.rootNameType;
	}

	public void setRootNameType(final Integer rootNameType) {
		this.rootNameType = rootNameType;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final String plotNumber) {
		this.plotNumber = plotNumber;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Integer getHarvestLocationId() {
		return this.harvestLocationId;
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

	public String getTrialInstanceNumber() {
		return this.trialInstanceNumber;
	}

	public void setTrialInstanceNumber(final String trialInstanceNumber) {
		this.trialInstanceNumber = trialInstanceNumber;
	}

	public String getReplicationNumber() {
		return this.replicationNumber;
	}

	public void setReplicationNumber(final String replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

	// NOTE: conditions are only being used by BreedersCrossIDExpression
	public List<MeasurementVariable> getConditions() {
		return conditions;
	}

	public void setConditions(final List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public MeasurementRow getTrailInstanceObservation() {
		return trailInstanceObservation;
	}

	public void setTrailInstanceObservation(final MeasurementRow trailInstanceObservation) {
		this.trailInstanceObservation = trailInstanceObservation;
	}

	public StudyTypeDto getStudyType() {
		return studyType;
	}

	public void setStudyType(final StudyTypeDto studyType) {
		this.studyType = studyType;
	}

	public Integer getBreedingMethodId() {
		return breedingMethodId;
	}

	public void setBreedingMethodId(final Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	public AdvancingSource copy() {
        final AdvancingSource source = new AdvancingSource(germplasm, names, plantsSelected, breedingMethod, isCheck, studyName, plotNumber);
        source.setSeason(this.season);
        source.setLocationAbbreviation(this.locationAbbreviation);
        source.setRootName(this.rootName);
        source.setSourceMethod(this.sourceMethod);
        source.setCurrentMaxSequence(this.currentMaxSequence);
        source.setChangeDetail(this.changeDetail);
        source.setPrefix(this.prefix);
        source.setSuffix(this.suffix);
        source.setRootNameType(this.rootNameType);
        source.setHarvestLocationId(this.harvestLocationId);
        source.setSelectionTraitValue(this.selectionTraitValue);
        source.setTrialInstanceNumber(this.trialInstanceNumber);
        source.setReplicationNumber(this.replicationNumber);
        return source;
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

	public List<SampleDTO> getSamples() {
		return samples;
	}

	public void setSamples(final List<SampleDTO> samples) {
		this.samples = samples;
	}

	public Boolean getDesignationIsPreviewOnly() {
		return designationIsPreviewOnly;
	}

	public void setDesignationIsPreviewOnly(Boolean designationIsPreviewOnly) {
		this.designationIsPreviewOnly = designationIsPreviewOnly;
	}

	public Map<String, Integer> getKeySequenceMap() {
		return keySequenceMap;
	}

	public void setKeySequenceMap(Map<String, Integer> keySequenceMap) {
		this.keySequenceMap = keySequenceMap;
	}
}
