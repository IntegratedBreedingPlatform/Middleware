package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

// TODO: please, change this name
public class NewAdvancingSource extends AbstractAdvancingSource {

	private Germplasm originGermplasm;

	// TODO: besides data processors, this is only being used by BreedersCrossIDExpression. Please, try to remove it from there.
	//This will be used if we have trail
	private ObservationUnitRow trailInstanceObservation;

	public NewAdvancingSource() {
	}

	public NewAdvancingSource(final String season, final String selectionTraitValue, final Germplasm originGermplasm) {
		this.setSeason(season);
		this.setSelectionTraitValue(selectionTraitValue);
		this.originGermplasm = originGermplasm;
		this.setLocationAbbreviation("");
	}

	public ObservationUnitRow getTrailInstanceObservation() {
		return trailInstanceObservation;
	}

	public void setTrailInstanceObservation(final ObservationUnitRow trailInstanceObservation) {
		this.trailInstanceObservation = trailInstanceObservation;
	}

}
