package org.generationcp.middleware.service.impl.study.advance;

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

public class AdvanceServiceImplTest {

	@InjectMocks
	private AdvanceServiceImpl advanceService;

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
	public void getPlantSelected_usingSameNotBulkingBreedingMethodAndSameNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer givenLinesSelected = new Random().nextInt();
		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null, givenLinesSelected,
				null, null);

		final Integer plantSelected =
			this.advanceService
				.getPlantSelected(advanceStudyRequest, Mockito.mock(ObservationUnitRow.class), new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(givenLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getAllPlotsSelected();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();

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
	public void getPlantSelected_usingSameNotBulkingBreedingMethodAndVariateForNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final Integer lineVariateId = new Random().nextInt();
		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null, null,
			lineVariateId, null);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(lineVariateId)).thenReturn(expectedLinesSelected.toString());

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getAllPlotsSelected();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();

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
	public void getPlantSelected_usingSameBulkingBreedingMethodAndAllPlotAreSelected() {
		final boolean isBulkMethod = true;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final boolean allPlotsSelected = true;

		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, null, null,
			null, allPlotsSelected);

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, new ObservationUnitRow(), new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(1));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - the same bulking breeding Method for each advance
	 * - a variate that defines which plots were selected
	 * <p>
	 * Should return the value of the given plot variate value
	 */
	@Test
	public void getPlantSelected_usingSameBulkingBreedingMethodAndVariateForPlots() {
		final boolean isBulkMethod = true;
		final Integer selectedBreedingMethodId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final boolean allPlotsSelected = false;

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(selectedBreedingMethodId, null, plotVariateId, null,
				null, allPlotsSelected);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(expectedLinesSelected.toString());

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, new HashMap<>(), isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a breeding method for each advance, but the breeding method is not defined in the observation
	 * <p>
	 * Should return null
	 */
	@Test
	public void getPlantSelected_usingBreedingMethodVariateWithNotDefinedBreedingMethodInObservation() {
		final Boolean isBulkMethod = false;
		final Integer methodVariateId = new Random().nextInt();

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(null, methodVariateId, null, null,
				null, null);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(null);

		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertNull(plantSelected);

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a not bulking breeding method for each advance
	 * - all plots are selected
	 * <p>
	 * Should return the given lines selected
	 */
	@Test
	public void getPlantSelected_usingNotBulkingVariateBreedingMethodAndSameNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer methodVariateId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);

		final AdvanceStudyRequest advanceStudyRequest =
			this.createMockAdvanceStudyRequest(null, methodVariateId, null, expectedLinesSelected,
				null, null);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getAllPlotsSelected();

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
	public void getPlantSelected_usingNotBulkingVariateBreedingMethodAndVariateForNumberOfLines() {
		final boolean isBulkMethod = false;
		final Integer methodVariateId = new Random().nextInt();
		final Integer expectedLinesSelected = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final Integer lineVariateId = new Random().nextInt();
		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, null, null,
			lineVariateId, null);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(lineVariateId)).thenReturn(expectedLinesSelected.toString());

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getAllPlotsSelected();

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
	public void getPlantSelected_usingBulkingBreedingVariateMethodAndAllPlotAreSelected() {
		final boolean isBulkMethod = true;
		final Integer methodVariateId = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final boolean allPlotsSelected = true;
		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, plotVariateId, null,
			null, allPlotsSelected);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(plotBreedingMethodCode);

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(1));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.never()).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLinesSelected();
	}

	/**
	 * For this scenario the user has selected:
	 * - a variate that defines a bulking breeding method for each advance
	 * - a variate that defines which plots were selected
	 * <p>
	 * Should return the value for the given plot variate value
	 */
	@Test
	public void getPlantSelected_usingBulkingBreedingVariateMethodAndVariateForPlots() {
		final boolean isBulkMethod = true;
		final Integer methodVariateId = new Random().nextInt();
		final Integer plotVariateId = new Random().nextInt();
		final String plotBreedingMethodCode = RandomStringUtils.randomAlphabetic(10);
		final boolean allPlotsSelected = false;
		final Integer expectedLinesSelected = new Random().nextInt();

		final AdvanceStudyRequest advanceStudyRequest = this.createMockAdvanceStudyRequest(null, methodVariateId, plotVariateId, null,
			null, allPlotsSelected);

		final ObservationUnitRow observationUnitRow = Mockito.mock(ObservationUnitRow.class);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(methodVariateId)).thenReturn(plotBreedingMethodCode);
		Mockito.when(observationUnitRow.getVariableValueByVariableId(plotVariateId)).thenReturn(expectedLinesSelected.toString());

		final Method breedingMethod = Mockito.mock(Method.class);
		Mockito.when(breedingMethod.isBulkingMethod()).thenReturn(isBulkMethod);
		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		breedingMethodsByCode.put(plotBreedingMethodCode, breedingMethod);

		final Integer plantSelected =
			this.advanceService.getPlantSelected(advanceStudyRequest, observationUnitRow, breedingMethodsByCode, isBulkMethod);
		assertThat(plantSelected, is(expectedLinesSelected));

		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getBreedingMethodId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getMethodVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(1)).getPlotVariateId();
		Mockito.verify(advanceStudyRequest.getBreedingMethodSelectionRequest(), Mockito.times(2)).getAllPlotsSelected();

		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLineVariateId();
		Mockito.verify(advanceStudyRequest.getLineSelectionRequest(), Mockito.never()).getLinesSelected();
	}

	private AdvanceStudyRequest createMockAdvanceStudyRequest(final Integer breedingMethodId, final Integer methodVariateId,
		final Integer plotVariateId, final Integer linesSelected,
		final Integer lineVariateId, final Boolean allPlotsSelected) {

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			Mockito.mock(AdvanceStudyRequest.BreedingMethodSelectionRequest.class);
		Mockito.when(breedingMethodSelectionRequest.getBreedingMethodId()).thenReturn(breedingMethodId);
		Mockito.when(breedingMethodSelectionRequest.getMethodVariateId()).thenReturn(methodVariateId);
		Mockito.when(breedingMethodSelectionRequest.getPlotVariateId()).thenReturn(plotVariateId);
		Mockito.when(breedingMethodSelectionRequest.getAllPlotsSelected()).thenReturn(allPlotsSelected);

		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest =
			Mockito.mock(AdvanceStudyRequest.LineSelectionRequest.class);
		Mockito.when(lineSelectionRequest.getLinesSelected()).thenReturn(linesSelected);
		Mockito.when(lineSelectionRequest.getLineVariateId()).thenReturn(lineVariateId);

		final AdvanceStudyRequest advanceStudyRequest = Mockito.mock(AdvanceStudyRequest.class);
		Mockito.when(advanceStudyRequest.getBreedingMethodSelectionRequest()).thenReturn(breedingMethodSelectionRequest);
		Mockito.when(advanceStudyRequest.getLineSelectionRequest()).thenReturn(lineSelectionRequest);

		return advanceStudyRequest;
	}

}
