
package org.generationcp.middleware.service.impl.study;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.hibernate.Session;

public class Observations {

	private final Session session;

	public Observations(final Session session) {
		this.session = session;
	}

	ObservationDto updataObsevationTraits(final ObservationDto middlewareMeasurement) {
		final List<MeasurementDto> traits = middlewareMeasurement.getTraitMeasurements();

		for (final MeasurementDto traitMeasurement : traits) {
			final String triatValue = traitMeasurement.getTriatValue();
			// If blank ignore nothing to update
			if (StringUtils.isNotBlank(triatValue)) {
				final Integer traitId = traitMeasurement.getPhenotypeId();
				// Update Trait
				if (traitId != null && traitId != 0) {
					this.updateTrait(traitId, triatValue);
				} else {
					final Integer observeableId = traitMeasurement.getTrait().getTraitId();
					final Integer phenotypeId = this.insertTrait(triatValue, observeableId, middlewareMeasurement.getMeasurementId());
					traitMeasurement.setPhenotypeId(phenotypeId);
				}
			}
		}
		return middlewareMeasurement;

	}

	private Integer insertTrait(String triatValue, Integer observeableId, Integer measurementId) {

		final Phenotype phenotype = new Phenotype();
		// TODO: You need a comment here dude :)
		phenotype.setUniqueName(UUID.randomUUID().toString());
		phenotype.setName(observeableId.toString());
		phenotype.setObservableId(observeableId);
		phenotype.setValue(triatValue);

		this.session.save(phenotype);
		phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
		this.session.update(phenotype);

		final ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		experimentPhenotype.setPhenotype(phenotype.getPhenotypeId());
		;
		experimentPhenotype.setExperiment(measurementId);

		this.session.save(experimentPhenotype);
		return phenotype.getPhenotypeId();

	}

	private void updateTrait(final Integer phenotypeId, final String triatValue) {

		final Phenotype phenotype = (Phenotype) this.session.get(Phenotype.class, phenotypeId);
		if (phenotype == null) {
			throw new IllegalStateException(String.format("The update method should never be called for id '%s' which does not exist. "
					+ "Please contact support for further information.", phenotypeId.toString()));
		}

		phenotype.setValue(triatValue);
		this.session.update(phenotype);

	}

}
