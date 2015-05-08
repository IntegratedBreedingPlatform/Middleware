package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * The class <code>TrialMeasurementsTest</code> contains tests for the class
 * <code>{@link StudyMeasurements}</code>.
 *
 */
public class StudyMeasurementsTest {


	private Session session;
	private StudyMeasurements trailTraits;
	private SQLQuery mockSqlQuery;
	private List<TraitDto> testTraits;
	private Object[] testRows;
	private List<Object[]> sampleMeasurements;
	
	private final int TEST_PROJECT_IDENTIFIER = 2019;
	private final int TEST_MEASUREMTN_IDENTIFIER = 9999;


	@Before
	public void setup() {
		session = Mockito.mock(Session.class);

		trailTraits = new StudyMeasurements(session);

		mockSqlQuery = Mockito.mock(SQLQuery.class);
		testTraits = Arrays.asList(new TraitDto(1, "Trait1"), new TraitDto(1, "Trait2"));
		testRows = new Object[] {1,"TRIAL_INSTACE", "ENTRY_TYPE", 20000, "DESIGNATION", "ENTRY_NO",
				"SEED_SOURCE", "REPITION_NUMBER", "PLOT_NUMBER","Trait1Value",1000,"TraitTwoValue", 2000};
		sampleMeasurements = Arrays.<Object[]>asList(testRows);
		when(mockSqlQuery.list()).thenReturn(sampleMeasurements);

	
	}
 	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes
	 * sure the query returns appropriate values.
	 *
	 */
	@Test
	public void allMeasurementQueryRetrievesTrialRelatedTraits() throws Exception {
		
		when(session.createSQLQuery((new ObservationQuery()).getObservationQuery(testTraits)))
		.thenReturn(mockSqlQuery);
		
		List<ObservationDto> returnedMeasurements = trailTraits.getAllMeasurements(TEST_PROJECT_IDENTIFIER, testTraits);

		verifyScalarQueryCallAndParameterSetting();

		// add additional test code here
		assertEquals("Make sure that we have one measurment returned", 1, returnedMeasurements.size());
		assertEquals("Make sure the GID is correct", new Integer(20000), returnedMeasurements.get(0).getGid());
	}
	
	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes
	 * sure the query returns appropriate values.
	 *
	 */
	@Test
	public void measurementQueryRetrievesTrialRelatedTraits() throws Exception {
		when(session.createSQLQuery((new ObservationQuery()).getSingleObservationQuery(testTraits)))
				.thenReturn(mockSqlQuery);
		
		List<ObservationDto> returnedMeasurements = trailTraits.getMeasurement(TEST_PROJECT_IDENTIFIER, testTraits, TEST_MEASUREMTN_IDENTIFIER);

		int counter = verifyScalarQueryCallAndParameterSetting();
		verify(mockSqlQuery).setParameter(counter++, TEST_MEASUREMTN_IDENTIFIER);


		// add additional test code here
		assertEquals("Make sure that we have one measurment returned", 1, returnedMeasurements.size());
		assertEquals("Make sure the GID is correct", new Integer(20000), returnedMeasurements.get(0).getGid());
	}
	
	private int verifyScalarQueryCallAndParameterSetting() {
		// There are two columns added per trait
		verify(mockSqlQuery, times(9 + (testTraits.size()*2))).addScalar(anyString());

		int counter = 0;
		for( final TraitDto testTrait : testTraits) {
			verify(mockSqlQuery).setParameter(counter++, testTrait.getTraitName());
		}

		verify(mockSqlQuery).setParameter(counter++, TEST_PROJECT_IDENTIFIER);
		return counter;
	}
}