package org.generationcp.middleware.ruleengine.naming.resolver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LocationDataResolverTest {

	private static final Integer LOCATION_ID = new Random().nextInt(Integer.MAX_VALUE);
	private static final String LOCATION_ABBR = RandomStringUtils.randomAlphabetic(10);

	@InjectMocks
	private LocationDataResolver locationDataResolver;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void resolveEnvironmentLevelData_withLocationData() {
		final ObservationUnitData observationUnitData = Mockito.mock(ObservationUnitData.class);
		Mockito.when(observationUnitData.getVariableId()).thenReturn(TermId.LOCATION_ID.getId());
		Mockito.when(observationUnitData.getValue()).thenReturn(LOCATION_ID.toString());

		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(TermId.LOCATION_ID.name(), observationUnitData);

		final ObservationUnitRow row = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(row.getEnvironmentVariables()).thenReturn(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrialInstanceObservation()).thenReturn(row);

		final Location location = Mockito.mock(Location.class);
		Mockito.when(location.getLabbr()).thenReturn(LOCATION_ABBR);
		final Map<Integer, Location> locationsByLocationId = new HashMap<>();
		locationsByLocationId.put(LOCATION_ID, location);

		locationDataResolver.resolveEnvironmentLevelData(source, locationsByLocationId);

		Mockito.verify(source).setHarvestLocationId(LOCATION_ID);
		Mockito.verify(source).setLocationAbbreviation(LOCATION_ABBR);
	}

	@Test
	public void resolveEnvironmentLevelData_notSetLocationAbbreviation() {
		final ObservationUnitData observationUnitData = Mockito.mock(ObservationUnitData.class);
		Mockito.when(observationUnitData.getVariableId()).thenReturn(TermId.LOCATION_ID.getId());
		Mockito.when(observationUnitData.getValue()).thenReturn(LOCATION_ID.toString());

		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(TermId.LOCATION_ID.name(), observationUnitData);

		final ObservationUnitRow row = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(row.getEnvironmentVariables()).thenReturn(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrialInstanceObservation()).thenReturn(row);

		locationDataResolver.resolveEnvironmentLevelData(source, new HashMap<>());

		Mockito.verify(source).setHarvestLocationId(LOCATION_ID);
		Mockito.verify(source, Mockito.never()).setLocationAbbreviation(LOCATION_ABBR);
	}

	@Test
	public void resolveEnvironmentLevelData_withoutLocationData() {
		final ObservationUnitData observationUnitData = Mockito.mock(ObservationUnitData.class);
		Mockito.when(observationUnitData.getVariableId()).thenReturn(TermId.LOCATION_ABBR.getId());
		Mockito.when(observationUnitData.getValue()).thenReturn(TermId.LOCATION_ABBR.name());

		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(TermId.LOCATION_ABBR.name(), observationUnitData);

		final ObservationUnitRow row = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(row.getEnvironmentVariables()).thenReturn(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrialInstanceObservation()).thenReturn(row);

		locationDataResolver.resolveEnvironmentLevelData(source, null);

		Mockito.verify(source, Mockito.never()).setHarvestLocationId(ArgumentMatchers.anyInt());
		Mockito.verify(source, Mockito.never()).setLocationAbbreviation(ArgumentMatchers.anyString());
	}

}
