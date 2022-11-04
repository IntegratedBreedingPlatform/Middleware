package org.generationcp.middleware.service.impl.study.advance.resolver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PlantSelectedResolverTest {

	@InjectMocks
	private PlantSelectedResolver plantSelectedResolver;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * For this scenario the user has selected:
	 * - the same not bulking breeding Method for each advance
	 * - the same number of lines for each plot.
	 * <p>
	 * Should return the given lines selected
	 */
	@Test
	public void resolvePlantSelected_usingSameNotBulkingBreedingMethodAndSameNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer givenLinesSelected = new Random().nextInt();
		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, givenLinesSelected, null, null, null
			);

		final Integer plantSelected =
			this.plantSelectedResolver
				.resolvePlantSelected(advanceStudyRequest, Mockito.mock(ObservationUnitRow.class), new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(givenLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		assertNull(advanceStudyRequest.getBulkingRequest());

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.times(2)).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - the same not bulking breeding Method for each advance
	 * - a variate that defines the number of lines from each plot.
	 * <p>
	 * Should return the value for the given line variate
	 */
	@Test
	public void resolvePlantSelected_usingSameNotBulkingBreedingMethodAndVariateForNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final Integer lineVariateId = new Random().nextInt();
		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null,
			lineVariateId, null, null
		);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(lineVariateId)).thenReturn(expectedLinesSelected.toString());

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		assertNull(advanceStudyRequest.getBulkingRequest());

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.times(1)).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - the same bulking breeding Method for each advance
	 * - all plots are selected.
	 * <p>
	 * Should return 1
	 */
	@Test
	public void resolvePlantSelected_usingSameBulkingBreedingMethodAndAllPlotAreSelected() {
		final boolean isBulkMethod = true;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final boolean allPlotsSelected = true;

		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null, null,
			allPlotsSelected, null
		);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, new ObservationUnitRow(), new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(1));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	/**
	 * For this scenario the user has selected:
	 * - the same bulking breeding Method for each advance
	 * - a variate that defines which plots were selected
	 * <p>
	 * Should return 1
	 */
	@Test
	public void resolvePlantSelected_usingSameBulkingBreedingMethodAndVariateForPlots() {
		final boolean isBulkMethod = true;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer linesSelected = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final boolean allPlotsSelected = false;

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null, null, allPlotsSelected, plotVariateId
			);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(linesSelected.toString());

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(1));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(1)).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	/**
	 * For this scenario the user has selected:
	 * - the same bulking breeding Method for each advance
	 * - a variate that defines which plots were selected but the variate has no value set
	 * <p>
	 * Should return 0
	 */
	@Test
	public void resolvePlantSelected_usingSameBulkingBreedingMethodAndVariateForPlotsWithoutValue() {
		final boolean isBulkMethod = true;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final boolean allPlotsSelected = false;

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null, null, allPlotsSelected, plotVariateId
			);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(null);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(0));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(1)).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a breeding method for each advance, but the breeding method is not defined in the observation
	 * <p>
	 * Should return null
	 */
	@Test
	public void resolvePlantSelected_usingBreedingMethodVariateWithNotDefinedBreedingMethodInObservation() {
		final Boolean isBulkMethod = false;
		final Integer methodVariateId = new Random().nextInt();

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(null, methodVariateId, null, null, null, null
			);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(null);

		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertNull(plantSelected);

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getBulkingRequest();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a not bulking breeding method for each advance
	 * - all plots are selected
	 * <p>
	 * Should return the given lines selected
	 */
	@Test
	public void resolvePlantSelected_usingNotBulkingVariateBreedingMethodAndSameNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer methodVariateId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(null, methodVariateId, expectedLinesSelected, null, null, null
			);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		assertNull(advanceStudyRequest.getBulkingRequest());

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.times(2)).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a not bulking breeding method for each advance
	 * - a variate that defines the number of lines from each plot.
	 * <p>
	 * Should return the value for the given line variate
	 */
	@Test
	public void resolvePlantSelected_usingNotBulkingVariateBreedingMethodAndVariateForNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer methodVariateId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final Integer lineVariateId = new Random().nextInt();
		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, null, lineVariateId, null,
			null
		);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(lineVariateId)).thenReturn(expectedLinesSelected.toString());

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		assertNull(advanceStudyRequest.getBulkingRequest());

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.times(1)).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.times(1)).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a not bulking breeding method for each advance
	 * - all plots are selected
	 * <p>
	 * Should return 1
	 */
	@Test
	public void resolvePlantSelected_usingBulkingBreedingVariateMethodAndAllPlotAreSelected() {
		final boolean isBulkMethod = true;
		final Integer methodVariateId = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final boolean allPlotsSelected = true;
		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, null, null,
			allPlotsSelected, plotVariateId
		);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(plotBreedingMethodCode);

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(1));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a bulking breeding method for each advance
	 * - a variate that defines which plots were selected
	 * <p>
	 * Should return 1
	 */
	@Test
	public void resolvePlantSelected_usingBulkingBreedingVariateMethodAndVariateForPlots() {
		final boolean isBulkMethod = true;
		final Integer methodVariateId = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final boolean allPlotsSelected = false;
		final Integer linesSelected = new Random().nextInt();

		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, null, null,
			allPlotsSelected, plotVariateId
		);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(linesSelected.toString());

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(1));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(1)).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a bulking breeding method for each advance
	 * - a variate that defines which plots were selected but the variate has no value set
	 * <p>
	 * Should return 0
	 */
	@Test
	public void resolvePlantSelected_usingBulkingBreedingVariateMethodAndVariateForPlotsWithoutValue() {
		final boolean isBulkMethod = true;
		final Integer methodVariateId = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final boolean allPlotsSelected = false;

		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, null, null,
			allPlotsSelected, plotVariateId
		);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(null);

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.plantSelectedResolver.resolvePlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(0));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();

		Mockito.verify(advanceStudyRequest, Mockito.times(1)).getBulkingRequest();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(1)).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBulkingRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest, Mockito.never()).getLineSelectionRequest();
		assertNull(advanceStudyRequest.getLineSelectionRequest());
	}

	private AdvanceStudyRequest createMockAdvanceStudyRequest(final Integer breedingMethodId, final Integer methodVariateId,
		final Integer linesSelected, final Integer lineVariateId, final Boolean allPlotsSelected, final Integer plotVariateId) {

		final AdvanceStudyRequest advanceStudyRequest = Mockito.mock(AdvanceStudyRequest.class);

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			Mockito.mock(AdvanceStudyRequest.BreedingMethodSelectionRequest.class);
		Mockito.when(breedingMethodSelectionRequest.getBreedingMethodId()).thenReturn(breedingMethodId);
		Mockito.when(breedingMethodSelectionRequest.getMethodVariateId()).thenReturn(methodVariateId);
		Mockito.when(advanceStudyRequest.getBreedingMethodSelectionRequest()).thenReturn(breedingMethodSelectionRequest);

		if (linesSelected != null || lineVariateId != null) {
			final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest =
				Mockito.mock(AdvanceStudyRequest.LineSelectionRequest.class);
			Mockito.when(lineSelectionRequest.getLinesSelected()).thenReturn(linesSelected);
			Mockito.when(lineSelectionRequest.getLineVariateId()).thenReturn(lineVariateId);
			Mockito.when(advanceStudyRequest.getLineSelectionRequest()).thenReturn(lineSelectionRequest);
		}

		if (plotVariateId != null || allPlotsSelected != null) {
			final AdvanceStudyRequest.BulkingRequest bulkingRequest = Mockito.mock(AdvanceStudyRequest.BulkingRequest.class);
			Mockito.when(bulkingRequest.getPlotVariateId()).thenReturn(plotVariateId);
			Mockito.when(bulkingRequest.getAllPlotsSelected()).thenReturn(allPlotsSelected);
			Mockito.when(advanceStudyRequest.getBulkingRequest()).thenReturn(bulkingRequest);
		}

		return advanceStudyRequest;
	}

}
