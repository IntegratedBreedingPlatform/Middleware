
package org.generationcp.middleware.service.impl.study;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The class <code>ObservationsTest</code> contains tests for the class <code>{@link Observations}</code>.
 *
 */
public class ObservationsTest {

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
						"Seed Source", "Repition Number", "Plot Number", traitMeasurements);
	}

	/**
	 * Run the void updataObsevation(Measurement) method test an insert scenario.
	 *
	 */
	@Test
	public void insertTrait() throws Exception {

		when(this.measurementDto.getTriatValue()).thenReturn("Test Value");
		when(this.measurementDto.getTrait()).thenReturn(new TraitDto(999, "Test Trait"));
		when(this.mockSession.save(isA(Phenotype.class))).thenAnswer(new Answer<Serializable>() {

			@Override
			public Serializable answer(InvocationOnMock invocation) throws Throwable {
				final Object[] arugment = invocation.getArguments();
				final Phenotype phenotype = (Phenotype) arugment[0];
				phenotype.setPhenotypeId(999);

				return 1;
			}
		});
		String programUuid = UUID.randomUUID().toString();
		final Variable mockVariable = Mockito.mock(Variable.class);
		final Scale mockScale = Mockito.mock(Scale.class);
		when(mockVariable.getScale()).thenReturn(mockScale);
		when(mockScale.getDataType()).thenReturn(DataType.NUMERIC_VARIABLE);
		
		when(this.mockOntologyVariableDataManager.getVariable(programUuid, 999, 
				false, false)).thenReturn(mockVariable);
		this.observation.updataObsevationTraits(this.observationDto, programUuid);

		verify(this.mockSession, times(1)).save(isA(Phenotype.class));
		verify(this.mockSession, times(1)).save(isA(ExperimentPhenotype.class));

		// add additional test code here
	}

	/**
	 * Run the void updataObsevation(Measurement) method test amd test and update scenario.
	 *
	 */
	@Test
	public void updateTrait() throws Exception {

		when(this.measurementDto.getTriatValue()).thenReturn("Test Value");
		when(this.measurementDto.getPhenotypeId()).thenReturn(1);
		when(this.measurementDto.getTrait()).thenReturn(new TraitDto(999, "Test Trait"));
		when(this.mockSession.get(Phenotype.class, 1)).thenReturn(Mockito.mock(Phenotype.class));
		this.observation.updataObsevationTraits(this.observationDto, UUID.randomUUID().toString());
		verify(this.mockSession, times(1)).update(isA(Phenotype.class));
	}
}
