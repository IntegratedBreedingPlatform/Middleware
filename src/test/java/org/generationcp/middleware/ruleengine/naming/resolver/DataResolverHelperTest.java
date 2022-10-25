package org.generationcp.middleware.ruleengine.naming.resolver;

import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataResolverHelperTest {

	@Test
	public void shouldHasTrailInstanceObservations() {
		final ObservationUnitRow row = Mockito.mock(ObservationUnitRow.class);
		assertFalse(DataResolverHelper.checkHasTrailInstanceObservations(row));

		final Map<String, ObservationUnitData> environmentVariables = Mockito.mock(Map.class);
		Mockito.when(row.getEnvironmentVariables()).thenReturn(environmentVariables);
		assertTrue(DataResolverHelper.checkHasTrailInstanceObservations(row));

		environmentVariables.put("something", Mockito.mock(ObservationUnitData.class));
		assertTrue(DataResolverHelper.checkHasTrailInstanceObservations(row));
	}

}
