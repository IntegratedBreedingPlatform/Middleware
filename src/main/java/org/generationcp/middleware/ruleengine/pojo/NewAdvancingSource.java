package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

// TODO: please, change this name
public class NewAdvancingSource extends AbstractAdvancingSource {

	private Germplasm originGermplasm;

	// TODO: besides data processors, this is only being used by BreedersCrossIDExpression. Please, try to remove it from there.
	//This will be used if we have trail
	private ObservationUnitRow trialInstanceObservation;

	public NewAdvancingSource() {
	}

	// TODO: create builder. Move all setters of the constructor arguments from the abstract class to AdvancingSource
	public NewAdvancingSource(final Germplasm originGermplasm, final Method breedingMethod, final Integer studyId,
		final Integer environmentDatasetId, final String season,
		final String selectionTraitValue,
		final Integer plantSelected) {

		this.originGermplasm = originGermplasm;
		this.setBreedingMethod(this.originGermplasm.getMethod());

		this.setStudyId(studyId);
		this.setEnvironmentDatasetId(environmentDatasetId);

		this.setBreedingMethod(breedingMethod);
		this.setSeason(season);
		this.setSelectionTraitValue(selectionTraitValue);
		this.setPlantsSelected(plantSelected);

		// We are setting this properties due to keep backward compatibility with the AdvancingSource of the old advance process
		this.setLocationAbbreviation("");
		this.setCurrentMaxSequence(0);
		this.setDesignationIsPreviewOnly(false);
	}

	public Germplasm getOriginGermplasm() {
		return originGermplasm;
	}

	public void setOriginGermplasm(final Germplasm originGermplasm) {
		this.originGermplasm = originGermplasm;
	}

	@Override
	public ObservationUnitRow getTrialInstanceObservation() {
		return trialInstanceObservation;
	}

	public void setTrialInstanceObservation(final ObservationUnitRow trialInstanceObservation) {
		this.trialInstanceObservation = trialInstanceObservation;
	}

	@Override
	public Integer getOriginGermplasmGid() {
		return this.originGermplasm.getGid();
	}

	@Override
	public Integer getOriginGermplasmGpid1() {
		return this.originGermplasm.getGpid1();
	}

	@Override
	public Integer getOriginGermplasmGpid2() {
		return this.originGermplasm.getGpid2();
	}

	@Override
	public Integer getOriginGermplasmGnpgs() {
		return this.originGermplasm.getGnpgs();
	}

	@Override
	public String getOriginGermplasmBreedingMethodType() {
		return this.originGermplasm.getMethod().getMtype();
	}

	@Override
	public boolean isBulkingMethod() {
		return this.getBreedingMethod().isBulkingMethod();
	}

}
