package org.generationcp.middleware.ruleengine.resolver;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationAbbreviationResolver implements KeyComponentValueResolver {

	protected final Collection<ObservationUnitData> observations;
	protected final Map<Integer, StudyInstance> studyInstanceMap;

	private static final Logger LOG = LoggerFactory.getLogger(LocationAbbreviationResolver.class);

	public LocationAbbreviationResolver(final Collection<ObservationUnitData> observations,
		final Map<Integer, StudyInstance> studyInstanceMap) {

		this.observations = observations;
		this.studyInstanceMap = studyInstanceMap;
	}

	@Override
	public String resolve() {
		String location = "";

		if (!CollectionUtils.isEmpty(this.studyInstanceMap)) {
			if (!CollectionUtils.isEmpty(this.observations)) {

				final Optional<ObservationUnitData> instanceNoUnitData = this.observations.stream()
					.filter(observationUnitData -> TermId.TRIAL_INSTANCE_FACTOR.getId() == observationUnitData.getVariableId())
					.findFirst();
				if (instanceNoUnitData.isPresent()) {
					final String instanceNo = instanceNoUnitData.get().getValue();
					if (instanceNo != null && this.studyInstanceMap.containsKey(Integer.valueOf(instanceNo))) {
						location = this.studyInstanceMap.get(Integer.valueOf(instanceNo)).getLocationAbbreviation();
					}

				}
			} else {
				final StudyInstance firstInstance = this.studyInstanceMap.get(1);
				if (firstInstance != null) {
					location = firstInstance.getLocationAbbreviation();
				}
			}
		}

		if (StringUtils.isBlank(location)) {
			LocationAbbreviationResolver.LOG.debug(
				"No Location Abbreviation was resolved");
			return "";
		}
		return location;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
