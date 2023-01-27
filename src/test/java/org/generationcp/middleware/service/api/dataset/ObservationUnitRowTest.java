package org.generationcp.middleware.service.api.dataset;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ObservationUnitRowTest {

	private static final Integer VARIABLE_ID = new Random().nextInt(Integer.MAX_VALUE);

	@Test
	public void shouldGetVariableById() {
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final ObservationUnitData observationUnitData = this.mockObservationUnitData(RandomStringUtils.randomAlphabetic(10));
		final Optional<ObservationUnitData> expectedVariable =
			observationUnitRow.getVariableById(Arrays.asList(observationUnitData), VARIABLE_ID);
		assertTrue(expectedVariable.isPresent());
		assertThat(expectedVariable.get(), is(observationUnitData));

		assertFalse(
			observationUnitRow.getVariableById(Arrays.asList(observationUnitData), new Random().nextInt(Integer.MAX_VALUE)).isPresent());
	}

	@Test
	public void shouldGetVariableValueByVariableId() {
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		assertNull(observationUnitRow.getVariableValueByVariableId(VARIABLE_ID));

		final String expectedValue = RandomStringUtils.randomAlphabetic(10);
		final ObservationUnitData observationUnitData = this.mockObservationUnitData(expectedValue);
		final Map<String, ObservationUnitData> variables = new HashMap<>();
		variables.put(RandomStringUtils.randomAlphabetic(10), observationUnitData);
		observationUnitRow.setVariables(variables);

		final String currentValue = observationUnitRow.getVariableValueByVariableId(VARIABLE_ID);
		assertThat(expectedValue, is(currentValue));
	}

	@Test
	public void shouldGetEnvironmentVariableValueByVariableId() {
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		assertNull(observationUnitRow.getEnvironmentVariableValueByVariableId(VARIABLE_ID));

		final String expectedValue = RandomStringUtils.randomAlphabetic(10);
		final ObservationUnitData observationUnitData = this.mockObservationUnitData(expectedValue);
		final Map<String, ObservationUnitData> variables = new HashMap<>();
		variables.put(RandomStringUtils.randomAlphabetic(10), observationUnitData);
		observationUnitRow.setEnvironmentVariables(variables);

		final String currentValue = observationUnitRow.getEnvironmentVariableValueByVariableId(VARIABLE_ID);
		assertThat(expectedValue, is(currentValue));
	}

	private ObservationUnitData mockObservationUnitData(final String value) {
		final ObservationUnitData observationUnitData = Mockito.mock(ObservationUnitData.class);
		Mockito.when(observationUnitData.getVariableId()).thenReturn(VARIABLE_ID);
		Mockito.when(observationUnitData.getValue()).thenReturn(value);
		return observationUnitData;
	}

}
