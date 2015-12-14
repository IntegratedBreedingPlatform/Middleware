
package org.generationcp.middleware.service.impl.study;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.hibernate.Session;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class Observations {

	private final Session session;
	private OntologyVariableDataManager ontologyVariableDataManager;

	public Observations(final Session session, OntologyVariableDataManager ontologyVariableDataManager) {
		this.session = session;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}

	ObservationDto updataObsevationTraits(final ObservationDto middlewareMeasurement, final String programUuid) {
		final List<MeasurementDto> traits = middlewareMeasurement.getTraitMeasurements();

		for (final MeasurementDto traitMeasurement : traits) {
			final String triatValue = traitMeasurement.getTriatValue();
			// If blank ignore nothing to update
			if (StringUtils.isNotBlank(triatValue)) {
				final Integer traitId = traitMeasurement.getPhenotypeId();
				final Integer observeableId = traitMeasurement.getTrait().getTraitId();

				// Update Trait
				final Variable variable = ontologyVariableDataManager.getVariable(programUuid, observeableId, false, false);

				if (traitId != null && traitId != 0) {
					this.updateTrait(variable, traitId, triatValue);
				} else {
					final Integer phenotypeId = this.insertTrait(variable, triatValue, observeableId, middlewareMeasurement.getMeasurementId());
					traitMeasurement.setPhenotypeId(phenotypeId);
				}
			}
		}
		return middlewareMeasurement;

	}

	private Integer insertTrait(Variable variable, String triatValue, Integer observeableId, Integer measurementId) {

		final Phenotype phenotype = new Phenotype();
		// The name is set to the observable id because that database expects them to be the same.
		phenotype.setName(observeableId.toString());
		phenotype.setObservableId(observeableId);
		phenotype.setValue(triatValue);
		setCategoricalValue(variable, phenotype, triatValue);
		this.session.save(phenotype);

		final ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		experimentPhenotype.setPhenotype(phenotype.getPhenotypeId());

		experimentPhenotype.setExperiment(measurementId);

		this.session.save(experimentPhenotype);
		return phenotype.getPhenotypeId();

	}

	private void setCategoricalValue(Variable variable, Phenotype phenotype, String triatValue) {
		if (variable.getScale().getDataType().getId() == org.generationcp.middleware.domain.ontology.DataType.CATEGORICAL_VARIABLE
				.getId()) {
			// TODO: Please cache the term summary
			final List<TermSummary> categories = variable.getScale().getCategories();
			final ImmutableMap<String, TermSummary> uniqueIndex = Maps.uniqueIndex(categories, new Function<TermSummary, String>() {
				@Override
				public String apply(final TermSummary termSummary) {
					return termSummary.getName().trim();
				}
			});
			TermSummary termSummary = uniqueIndex.get(triatValue.trim());
			// This should actually be caught by the validator. This is a back stop.
			if(termSummary == null) {
				throw new IllegalArgumentException(String.format("Categorical value with name '%s' for variable with name '%s' does not exist in the Ontology. "
						+ "Please check the Ontology Manager for valid values."
						, triatValue, variable.getName() ));
			}
			phenotype.setcValue(termSummary.getId());
		}
	}

	private void updateTrait(final Variable variable, final Integer phenotypeId, final String triatValue) {

		final Phenotype phenotype = (Phenotype) this.session.get(Phenotype.class, phenotypeId);
		if (phenotype == null) {
			throw new IllegalStateException(String.format("The update method should never be called for id '%s' which does not exist. "
					+ "Please contact support for further information.", phenotypeId.toString()));
		}

		phenotype.setValue(triatValue);
		setCategoricalValue(variable, phenotype, triatValue);

		this.session.update(phenotype);

	}

}
