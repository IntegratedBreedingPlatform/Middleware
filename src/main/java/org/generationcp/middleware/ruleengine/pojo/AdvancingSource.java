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
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * The POJO containing information needed for Advancing.
 *
 */

@Deprecated
public class AdvancingSource extends AbstractAdvancingSource {

	private ImportedGermplasm germplasm;

	/**
	 * This field is used to temporarily store the breeding method ID until such time as it can be resolved to a proper breeding Method object
	 */
	private Integer breedingMethodId;
	private boolean isCheck;
	private String studyName;
	private Method sourceMethod;
	private AdvanceGermplasmChangeDetail changeDetail;
	private String prefix;
	private String suffix;

	//This will be used if we have trail
	private MeasurementRow trailInstanceObservationMeasurementRow;

	private StudyTypeDto studyType;

	private List<SampleDTO> samples = new ArrayList<>();

	public AdvancingSource(final ImportedGermplasm germplasm, final List<Name> names, final Integer plantsSelected, final Method breedingMethod, final boolean isCheck,
			final String studyName, final String plotNumber) {
		super();
		this.germplasm = germplasm;
		this.setNames(names);
		this.setPlantsSelected(plantsSelected);
		this.setBreedingMethod(breedingMethod);
		this.isCheck = isCheck;
		this.studyName = studyName;
		super.setPlotNumber(plotNumber);
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
	@Override
	public boolean isBulkingMethod() {
		final Boolean isBulk = this.getBreedingMethod().isBulkingMethod();
		return this.getBreedingMethod() != null && isBulk != null ? isBulk : false;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
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
        final AdvancingSource source = new AdvancingSource(germplasm, this.getNames(), this.getPlantsSelected(), this.getBreedingMethod(), isCheck,
			studyName, this.getPlotNumber());
        source.setSeason(this.getSeason());
        source.setLocationAbbreviation(this.getLocationAbbreviation());
        source.setRootName(this.getRootName());
        source.setSourceMethod(this.sourceMethod);
        source.setCurrentMaxSequence(this.getCurrentMaxSequence());
        source.setChangeDetail(this.changeDetail);
        source.setPrefix(this.prefix);
        source.setSuffix(this.suffix);
        source.setRootNameType(this.getRootNameType());
        source.setHarvestLocationId(this.getHarvestLocationId());
        source.setSelectionTraitValue(this.getSelectionTraitValue());
        source.setTrialInstanceNumber(this.getTrialInstanceNumber());
        source.setReplicationNumber(this.getReplicationNumber());
        return source;
    }

	public List<SampleDTO> getSamples() {
		return samples;
	}

	public void setSamples(final List<SampleDTO> samples) {
		this.samples = samples;
	}

	@Override
	public String getOriginGermplasmGid() {
		return this.germplasm.getGid();
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
