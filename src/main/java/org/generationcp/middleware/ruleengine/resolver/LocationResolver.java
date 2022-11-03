package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationResolver implements KeyComponentValueResolver {

	protected final List<MeasurementVariable> studyEnvironmentVariables;
	protected final Collection<ObservationUnitData> observations;
	protected final Map<String, String> locationIdNameMap;

	private static final Logger LOG = LoggerFactory.getLogger(LocationResolver.class);

	public LocationResolver(final List<MeasurementVariable> studyEnvironmentVariables, final Collection<ObservationUnitData> observations,
		final Map<String, String> locationIdNameMap) {

		this.observations = observations;
		this.studyEnvironmentVariables = studyEnvironmentVariables;
		this.locationIdNameMap = locationIdNameMap;
	}

	@Override
	public String resolve() {
		String location = "";

		ImmutableMap<Integer, MeasurementVariable> studyEnvironmentVariablesByTermId = null;
		if (this.studyEnvironmentVariables != null) {
			studyEnvironmentVariablesByTermId = Maps.uniqueIndex(this.studyEnvironmentVariables, MeasurementVariable::getTermId);
		}

		if (studyEnvironmentVariablesByTermId != null) {
			// FIXME See IBP-2575
			if (studyEnvironmentVariablesByTermId.containsKey(TermId.LOCATION_ABBR.getId())) {
				location = studyEnvironmentVariablesByTermId.get(TermId.LOCATION_ABBR.getId()).getValue();
			} else if (studyEnvironmentVariablesByTermId.containsKey(TermId.TRIAL_LOCATION.getId())) {
				location = studyEnvironmentVariablesByTermId.get(TermId.TRIAL_LOCATION.getId()).getValue();
			} else {
				location = studyEnvironmentVariablesByTermId.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
			}
		}

		if (!CollectionUtils.isEmpty(this.observations)) {
			final Map<Integer, ObservationUnitData> dataListMap =
				this.observations.stream()
					.collect(Collectors.toMap(ObservationUnitData::getVariableId, observationUnitData -> observationUnitData));

			if (dataListMap.containsKey(TermId.LOCATION_ABBR.getId())) {
				location = dataListMap.get(TermId.LOCATION_ABBR.getId()).getValue();
			} else if (studyEnvironmentVariablesByTermId != null && studyEnvironmentVariablesByTermId.containsKey(TermId.LOCATION_ABBR.getId())) {
				location = studyEnvironmentVariablesByTermId.get(TermId.LOCATION_ABBR.getId()).getValue();
			} else if (dataListMap.containsKey(TermId.LOCATION_ID.getId())) {
				final String locationId = dataListMap.get(TermId.LOCATION_ID.getId()).getValue();
				location = this.locationIdNameMap.get(locationId);
			}

			if (StringUtils.isBlank(location)) {
				location = dataListMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
			}
		}

		if (StringUtils.isBlank(location)) {
			LocationResolver.LOG.debug(
					"No LOCATION_ABBR(8189), LOCATION_NAME(8180) or TRIAL_INSTANCE(8170) variable was found in the study. "
						+ "Or it is present but no value is set. Resolving location value to be an empty string.");
			return "";
		}

		return location;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
