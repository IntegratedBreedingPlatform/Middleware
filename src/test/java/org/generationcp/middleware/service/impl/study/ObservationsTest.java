
package org.generationcp.middleware.service.impl.study;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

	@Before
	public void setup() {
		mockSession = Mockito.mock(Session.class);
		observation = new Observations(mockSession);
		final List<MeasurementDto> traitMeasurements = new ArrayList<MeasurementDto>();
		measurementDto = Mockito.mock(MeasurementDto.class);
		traitMeasurements.add(measurementDto);
		
		observationDto = new ObservationDto(new Integer(1), "TrialInstance", "EntryType", new Integer(100), 
				"GID Designation", "Entry No", "Seed Source", "Repition Number", "Plot Number", traitMeasurements);
	}
	
	
	/**
	 * Run the void updataObsevation(Measurement) method test an insert scenario.
	 *
	 */
	@Test
	public void insertTrait() throws Exception {

		when(measurementDto.getTriatValue()).thenReturn("Test Value");
		when(measurementDto.getTrait()).thenReturn(new TraitDto(999, "Test Trait"));
		when(mockSession.save(isA(Phenotype.class))).thenAnswer(new Answer<Serializable>() {
			@Override
			public Serializable answer(InvocationOnMock invocation) throws Throwable {
				final Object[] arugment = invocation.getArguments();
				final Phenotype phenotype = (Phenotype) arugment[0];
				phenotype.setPhenotypeId(999);
				
				return 1;
			}
		});
		
		observation.updataObsevationTraits(observationDto);
		
		verify(mockSession,times(1)).save(isA(Phenotype.class));
		verify(mockSession,times(1)).save(isA(ExperimentPhenotype.class));

		verify(mockSession,times(1)).update(isA(Phenotype.class));

		// add additional test code here
	}

	/**
	 * Run the void updataObsevation(Measurement) method test amd test and update scenario.
	 *
	 */
	@Test
	public void updateTrait() throws Exception {

		when(measurementDto.getTriatValue()).thenReturn("Test Value");
		when(measurementDto.getPhenotypeId()).thenReturn(1);
		when(measurementDto.getTrait()).thenReturn(new TraitDto(999, "Test Trait"));
		when(mockSession.get(Phenotype.class, 1)).thenReturn(Mockito.mock(Phenotype.class));
		observation.updataObsevationTraits(observationDto);
		verify(mockSession,times(1)).update(isA(Phenotype.class));
	}
}
