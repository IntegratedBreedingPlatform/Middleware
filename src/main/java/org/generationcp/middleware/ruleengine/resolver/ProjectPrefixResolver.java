
package org.generationcp.middleware.ruleengine.resolver;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProjectPrefixResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectPrefixResolver.class);

	public ProjectPrefixResolver(final OntologyVariableDataManager ontologyVariableDataManager,
		final List<MeasurementVariable> studyEnvironmentVariables, Collection<ObservationUnitData> observations,
		final Map<Integer, MeasurementVariable> measurementVariableByTermId) {

		super(ontologyVariableDataManager, studyEnvironmentVariables, observations, measurementVariableByTermId);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.PROJECT_PREFIX;
	}

	@Override
	protected boolean isAbbreviationRequired() {
		return true;
	}

	@Override
	protected String getDefaultValue() {
		ProjectPrefixResolver.LOG.debug("No Project_Prefix(3001) variable was found or it is present but no value is set."
				+ "Resolving Program value to be an empty string.");
		return "";
	}

	@Override
	protected String getValueFromObservationUnitData(final ObservationUnitData observationUnitData) {
		return observationUnitData.getValue();
	}

	@Override
	protected String getValueFromStudyEnvironmentVariable(final MeasurementVariable studyEnvironmentVariable) {
		return studyEnvironmentVariable.getValue();
	}
}
