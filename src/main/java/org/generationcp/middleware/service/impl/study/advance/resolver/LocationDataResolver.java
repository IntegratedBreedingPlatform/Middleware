package org.generationcp.middleware.service.impl.study.advance.resolver;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.springframework.util.StringUtils;

import java.util.Map;

public class LocationDataResolver {

	/**
	 * Resolves location related data at environment level. The required data will be set into the provided {@link NewAdvancingSource}
	 *
	 * @param source
	 * @param locationsByLocationId
	 */
	public void resolveEnvironmentLevelData(final NewAdvancingSource source, final Map<Integer, Location> locationsByLocationId) {
		if (DataResolverHelper.checkHasTrailInstanceObservations(source.getTrialInstanceObservation())) {
			source.getTrialInstanceObservation().getEnvironmentVariables().values().stream()
				.filter(observationUnitData -> TermId.LOCATION_ID.getId() == observationUnitData.getVariableId())
				.findFirst()
				.ifPresent(observationUnitData -> {
					final String locationIdString = observationUnitData.getValue();
					final Integer locationId = StringUtils.isEmpty(locationIdString) ? null : Integer.valueOf(locationIdString);
					source.setHarvestLocationId(locationId);

					if (locationId != null && locationsByLocationId.containsKey(locationId)) {
						final Location location = locationsByLocationId.get(locationId);
						source.setLocationAbbreviation(location.getLabbr());
					}
				});
		}
	}

}
