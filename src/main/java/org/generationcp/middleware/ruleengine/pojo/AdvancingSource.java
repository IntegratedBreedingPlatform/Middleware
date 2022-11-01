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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The POJO containing information needed for Advancing.
 */

@Deprecated
public class AdvancingSource extends AbstractAdvancingSource {

	private ImportedGermplasm germplasm;

	/**
	 * This field is used to temporarily store the breeding method ID until such time as it can be resolved to a proper breeding Method object
	 */
	private Integer breedingMethodId;
	private String studyName;
	private Method sourceMethod;
	private String prefix;
	private String suffix;
	private String plotNumber;
	private String trialInstanceNumber;
	private String replicationNumber;

	//This will be used if we have trail
	private MeasurementRow trailInstanceObservationMeasurementRow;

	private StudyTypeDto studyType;

	private List<SampleDTO> samples = new ArrayList<>();

	public AdvancingSource(final ImportedGermplasm germplasm, final List<Name> names, final Integer plantsSelected,
		final Method breedingMethod,
		final String studyName, final String plotNumber) {
		super();
		this.germplasm = germplasm;
		this.names = names;
		this.plantsSelected = plantsSelected;
		this.breedingMethod = breedingMethod;
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
	 * @param plantsSelected the plantsSelected to set
	 */
	public void setPlantsSelected(final Integer plantsSelected) {
		this.plantsSelected = plantsSelected;
	}

	/**
	 * @return the isBulk
	 */
	@Override
	public boolean isBulkingMethod() {
		final Boolean isBulk = this.getBreedingMethod().isBulkingMethod();
		return this.getBreedingMethod() != null && isBulk != null ? isBulk : false;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(final List<Name> names) {
		this.names = names;
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

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public void setEnvironmentDatasetId(final Integer environmentDatasetId) {
		this.environmentDatasetId = environmentDatasetId;
	}

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

	public String getTrialInstanceNumber() {
		return this.trialInstanceNumber;
	}

	public void setTrialInstanceNumber(final String trialInstanceNumber) {
		this.trialInstanceNumber = trialInstanceNumber;
	}

	public String getReplicationNumber() {
		return replicationNumber;
	}

	public void setReplicationNumber(final String replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

	public void setConditions(final List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public void setTrailInstanceObservationMeasurementRow(final MeasurementRow trailInstanceObservationMeasurementRow) {
		this.trailInstanceObservationMeasurementRow = trailInstanceObservationMeasurementRow;
	}

	public MeasurementRow getTrailInstanceObservationMeasurementRow() {
		return trailInstanceObservationMeasurementRow;
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
		final AdvancingSource source = new AdvancingSource(germplasm, this.names, this.plantsSelected, this.breedingMethod,
			studyName, this.plotNumber);
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

	public void setMaleGid(final int maleGid) {
		this.maleGid = maleGid;
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

	public void setDesignationIsPreviewOnly(final Boolean designationIsPreviewOnly) {
		this.designationIsPreviewOnly = designationIsPreviewOnly;
	}

	@Override
	public Integer getOriginGermplasmGid() {
		return NumberUtils.isNumber(this.germplasm.getGid()) ? Integer.valueOf(this.germplasm.getGid()) : null;
	}

	@Override
	public Integer getOriginGermplasmGpid1() {
		return this.germplasm.getGpid1();
	}

	@Override
	public Integer getOriginGermplasmGpid2() {
		return this.germplasm.getGpid2();
	}

	@Override
	public Integer getOriginGermplasmGnpgs() {
		return this.germplasm.getGnpgs();
	}

	@Override
	public String getOriginGermplasmBreedingMethodType() {
		return (this.sourceMethod == null) ? null : this.sourceMethod.getMtype();
	}

	@Override
	public ObservationUnitRow getTrialInstanceObservation() {
		return ObservationUnitUtils.fromMeasurementRow(this.trailInstanceObservationMeasurementRow);
	}

}
