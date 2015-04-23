package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.service.api.study.Measurement;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * The class <code>TrialMeasurementsTest</code> contains tests for the class
 * <code>{@link TrialMeasurements}</code>.
 *
 */
public class TrialMeasurementsTest {
	/**
	 * Run the {@link TrialMeasurements}.getAllMeasurements() method and makes
	 * sure the query returns appropriate values.
	 *
	 */
	@Test
	public void measurementQueryRetrievesTrialRelatedTraits() throws Exception {
		final Session session = Mockito.mock(Session.class);

		final TrialMeasurements trailTraits = new TrialMeasurements(session);

		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);
		final List<String> testTraits = Arrays.asList("Trait1", "Trait2");
		when(session.createSQLQuery((new MeasurementQuery()).generateQuery(testTraits)))
				.thenReturn(mockSqlQuery);
		
		final Object[] testRow = {1,"TRIAL_INSTACE", "ENTRY_TYPE", 20000, "DESIGNATION", "ENTRY_NO",
				"SEED_SOURCE", "REPITION_NUMBER", "PLOT_NUMBER","Trait1Value",1000,"TraitTwoValue", 2000};
		final List<Object[]> sampleMeasurements = Arrays.<Object[]>asList(testRow);
		
		when(mockSqlQuery.list()).thenReturn(sampleMeasurements);

		
		int projectBusinessIdentifier = 2019;
		List<Measurement> returnedMeasurements = trailTraits.getAllMeasurements(projectBusinessIdentifier, testTraits);

		// There are two columns added per trait
		verify(mockSqlQuery, times(9 + (testTraits.size()*2))).addScalar(anyString());

		int counter = 0;
		for( final String testTrait : testTraits) {
			verify(mockSqlQuery).setParameter(counter++, testTrait);
		}

		verify(mockSqlQuery).setParameter(counter++, projectBusinessIdentifier);

		// add additional test code here
		assertEquals("Make sure that we have one measurment returned", 1, returnedMeasurements.size());
		assertEquals("Make sure the GID is correct", new Integer(20000), returnedMeasurements.get(0).getGid());
	}
}