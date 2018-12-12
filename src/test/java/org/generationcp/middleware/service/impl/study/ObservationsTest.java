
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * The class <code>ObservationsTest</code> contains tests for the class <code>{@link Observations}</code>.
 *
 */
public class ObservationsTest {

	private static final String TEST_CATEGORICAL_DESCRIPTION = "Test categorical value";
	private static final String TEST_TERM_NAME = "5";
	private static final int TEST_TERM_ID = 1234;
	private static final int GENERATED_PHENOTYPE_ID = 12;
	private static final String TEST_TRAIT_NAME = "Test Trait";
	private static final int TEST_TRAIT_ID = 999;
	private static final String CHARACTER_TEST_VALUE = "Test Value";
	private Observations observation;
	private Session mockSession;
	private MeasurementDto measurementDto;
	private ObservationDto observationDto;
	private OntologyVariableDataManager mockOntologyVariableDataManager;

	@Before
	public void setup() {
		this.mockSession = Mockito.mock(Session.class);
		mockOntologyVariableDataManager = Mockito.mock(OntologyVariableDataManager.class);
		this.observation = new Observations(this.mockSession, mockOntologyVariableDataManager);
		final List<MeasurementDto> traitMeasurements = new ArrayList<MeasurementDto>();
		this.measurementDto = Mockito.mock(MeasurementDto.class);
		traitMeasurements.add(this.measurementDto);
		this.observationDto =
				new ObservationDto(new Integer(1), "TrialInstance", "EntryType", new Integer(100), "GID Designation", "Entry No",
						"Seed Source", "Repition Number", "Plot Number", "Block Number", traitMeasurements);
	}

	/**
	 * Run insertTrait method
	 *
	 */
	@Test
	public void insertTrait() throws Exception {

		when(this.measurementDto.getVariableValue()).thenReturn(CHARACTER_TEST_VALUE);
		when(this.measurementDto.getMeasurementVariable()).thenReturn(new MeasurementVariableDto(TEST_TRAIT_ID, TEST_TRAIT_NAME));
		when(this.mockSession.save(isA(Phenotype.class))).thenAnswer(new Answer<Serializable>() {

			// Tick to simulate adding in of the ID
			@Override
			public Serializable answer(final InvocationOnMock invocation) throws Throwable {
				final Object[] arugment = invocation.getArguments();
				final Phenotype phenotype = (Phenotype) arugment[0];
				phenotype.setPhenotypeId(GENERATED_PHENOTYPE_ID);
				return 1;
			}
		});

		final Variable mockVariable = Mockito.mock(Variable.class);
		final Scale mockScale = Mockito.mock(Scale.class);
		when(mockVariable.getScale()).thenReturn(mockScale);
		when(mockScale.getDataType()).thenReturn(DataType.NUMERIC_VARIABLE);

		final String programUuid = UUID.randomUUID().toString();
		when(this.mockOntologyVariableDataManager.getVariable(programUuid, TEST_TRAIT_ID, false)).thenReturn(mockVariable);

		this.observation.updataObsevationTraits(this.observationDto, programUuid);

		verify(this.mockSession, times(1)).save(isA(Phenotype.class));
		verify(this.mockSession, times(1)).save(
				new Phenotype(new Integer(GENERATED_PHENOTYPE_ID), null, Integer.toString(TEST_TRAIT_ID), new Integer(TEST_TRAIT_ID), null,
						CHARACTER_TEST_VALUE, null, null));
		// add additional test code here
	}

	/**
	 * Run the void updataObsevation(Measurement) method test an insert scenario.
	 *
	 */
	@Test
	public void insertCategoricalTrait() throws Exception {

		when(this.measurementDto.getVariableValue()).thenReturn(TEST_TERM_NAME);
		when(this.measurementDto.getMeasurementVariable()).thenReturn(new MeasurementVariableDto(TEST_TRAIT_ID, TEST_TRAIT_NAME));
		when(this.mockSession.save(isA(Phenotype.class))).thenAnswer(new Answer<Serializable>() {

			@Override
			public Serializable answer(final InvocationOnMock invocation) throws Throwable {
				final Object[] arugment = invocation.getArguments();
				final Phenotype phenotype = (Phenotype) arugment[0];
				phenotype.setPhenotypeId(GENERATED_PHENOTYPE_ID);

				return 1;
			}
		});
		final String programUuid = UUID.randomUUID().toString();
		final Variable mockVariable = Mockito.mock(Variable.class);
		final Scale mockScale = Mockito.mock(Scale.class);
		when(mockVariable.getScale()).thenReturn(mockScale);
		when(mockScale.getCategories()).thenReturn(
				Collections.singletonList(new TermSummary(TEST_TERM_ID, TEST_TERM_NAME, TEST_CATEGORICAL_DESCRIPTION)));
		when(mockScale.getDataType()).thenReturn(DataType.CATEGORICAL_VARIABLE);
//		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, false, false)).thenReturn(mockVariable);
		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, false)).thenReturn(mockVariable);

		this.observation.updataObsevationTraits(this.observationDto, programUuid);

		verify(this.mockSession, times(1)).save(isA(Phenotype.class));

		verify(this.mockSession, times(1)).save(
				new Phenotype(new Integer(GENERATED_PHENOTYPE_ID), null, Integer.toString(TEST_TRAIT_ID), new Integer(TEST_TRAIT_ID), null,
						TEST_TERM_NAME, new Integer(1234), null));
		// add additional test code here
	}

	/**
	 * Run the void updataObsevation(Measurement) method test amd test and update scenario.
	 *
	 */
	@Test
	public void updateTrait() throws Exception {

		when(this.measurementDto.getVariableValue()).thenReturn(CHARACTER_TEST_VALUE);
		when(this.measurementDto.getPhenotypeId()).thenReturn(1);
		when(this.measurementDto.getMeasurementVariable()).thenReturn(new MeasurementVariableDto(999, TEST_TRAIT_NAME));

		final Phenotype mockPhenotype = Mockito.mock(Phenotype.class);
		when(this.mockSession.get(Phenotype.class, 1)).thenReturn(mockPhenotype);
		final Variable mockVariable = Mockito.mock(Variable.class);
		final Scale mockScale = Mockito.mock(Scale.class);
		when(mockVariable.getScale()).thenReturn(mockScale);
		when(mockScale.getDataType()).thenReturn(DataType.NUMERIC_VARIABLE);

		final String programUuid = UUID.randomUUID().toString();

//		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, false, false)).thenReturn(mockVariable);
		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, false)).thenReturn(mockVariable);

		this.observation.updataObsevationTraits(this.observationDto, programUuid);
		verify(this.mockSession, times(1)).update(isA(Phenotype.class));
		verify(mockPhenotype, times(1)).setValue(CHARACTER_TEST_VALUE);
	}

	/**
	 * Run the void updataObsevation(Measurement) method test amd test and update scenario.
	 *
	 */
	@Test
	public void updateCategoricalTrait() throws Exception {

		when(this.measurementDto.getVariableValue()).thenReturn(TEST_TERM_NAME);
		when(this.measurementDto.getPhenotypeId()).thenReturn(1);
		when(this.measurementDto.getMeasurementVariable()).thenReturn(new MeasurementVariableDto(999, TEST_TRAIT_NAME));

		final Phenotype mockPhenotype = Mockito.mock(Phenotype.class);
		when(this.mockSession.get(Phenotype.class, 1)).thenReturn(mockPhenotype);
		final Variable mockVariable = Mockito.mock(Variable.class);
		final Scale mockScale = Mockito.mock(Scale.class);
		when(mockVariable.getScale()).thenReturn(mockScale);
		when(mockScale.getDataType()).thenReturn(DataType.CATEGORICAL_VARIABLE);

		when(mockVariable.getScale()).thenReturn(mockScale);
		when(mockScale.getCategories()).thenReturn(
				Collections.singletonList(new TermSummary(TEST_TERM_ID, TEST_TERM_NAME, TEST_CATEGORICAL_DESCRIPTION)));
		when(mockScale.getDataType()).thenReturn(DataType.CATEGORICAL_VARIABLE);

		final String programUuid = UUID.randomUUID().toString();

//		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, false, false)).thenReturn(mockVariable);
		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, false)).thenReturn(mockVariable);

		this.observation.updataObsevationTraits(this.observationDto, programUuid);
		verify(this.mockSession, times(1)).update(isA(Phenotype.class));
		verify(mockPhenotype, times(1)).setValue(TEST_TERM_NAME);
		verify(mockPhenotype, times(1)).setcValue(TEST_TERM_ID);

	}
}
