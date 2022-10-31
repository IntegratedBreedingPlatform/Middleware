package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.ArrayList;
import java.util.List;

// TODO: please, change this name
public class NewAdvancingSource extends AbstractAdvancingSource {

	private Germplasm originGermplasm;

	// TODO: besides data processors, this is only being used by BreedersCrossIDExpression. Please, try to remove it from there.
	//This will be used if we have trail
	private ObservationUnitRow trialInstanceObservation;

	private ObservationUnitRow plotObservation;

	private List<Germplasm> advancedGermplasms = new ArrayList<>();

	public NewAdvancingSource() {
	}

	// TODO: create builder.
	public NewAdvancingSource(final Germplasm originGermplasm, final ObservationUnitRow plotObservation,
		final ObservationUnitRow trialInstanceObservation,
		final List<MeasurementVariable> studyEnvironmentVariables,
		final Method breedingMethod,
		final Integer studyId, final Integer environmentDatasetId, final String season, final String selectionTraitValue,
		final Integer plantSelected) {

		this.originGermplasm = originGermplasm;
		this.plotObservation = plotObservation;
		this.trialInstanceObservation = trialInstanceObservation;
		this.conditions = studyEnvironmentVariables;
		this.breedingMethod = this.originGermplasm.getMethod();

		this.studyId = studyId;
		this.environmentDatasetId = environmentDatasetId;

		this.breedingMethod = breedingMethod;
		this.season = season;
		this.selectionTraitValue = selectionTraitValue;
		this.plantsSelected = plantSelected;

		// We are setting this properties due to keep backward compatibility with the AdvancingSource of the old advance process
		this.locationAbbreviation = "";
		this.currentMaxSequence = 0;
		this.designationIsPreviewOnly = false;
	}

	public ObservationUnitRow getPlotObservation() {
		return plotObservation;
	}

	public Germplasm getOriginGermplasm() {
		return originGermplasm;
	}

	@Override
	public ObservationUnitRow getTrialInstanceObservation() {
		return trialInstanceObservation;
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

	public List<Germplasm> getAdvancedGermplasms() {
		return advancedGermplasms;
	}

	public void addAdvancedGermplasm(final Germplasm advancedGermplasm) {
		this.advancedGermplasms.add(advancedGermplasm);
	}

}
