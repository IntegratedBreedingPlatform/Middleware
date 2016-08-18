
package org.generationcp.middleware.service.impl.study;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * The class <code>TrialMeasurementsTest</code> contains tests for the class <code>{@link StudyMeasurements}</code>.
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
	private final int TEST_PLOT_IDENTIFIER = 9999;

	@Before
	public void setup() {
		this.session = Mockito.mock(Session.class);

		this.trailTraits = new StudyMeasurements(this.session);

		this.mockSqlQuery = Mockito.mock(SQLQuery.class);
		this.testTraits = Arrays.asList(new TraitDto(1, "Trait1"), new TraitDto(2, "Trait2"));
		this.testRows =
				new Object[] {1, "TRIAL_INSTACE", "ENTRY_TYPE", 20000, "DESIGNATION", "ENTRY_NO", "SEED_SOURCE", "REPITION_NUMBER",
						"PLOT_NUMBER", "BLOCK_NO", "ROW_NO", "COL_NO", "Trait1Value", 1000, "Trait2Value", 2000};
		this.sampleMeasurements = Arrays.<Object[]>asList(this.testRows);
		Mockito.when(this.mockSqlQuery.list()).thenReturn(this.sampleMeasurements);

	}

	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes sure the query returns appropriate values.
	 *
	 */
	@Test
	public void allPlotsMeasurementQueryRetrievesDataCorrectly() throws Exception {

		Mockito.when(this.session.createSQLQuery(new ObservationQuery().getObservationQuery(this.testTraits)))
				.thenReturn(this.mockSqlQuery);

		List<ObservationDto> returnedMeasurements =
				this.trailTraits.getAllMeasurements(this.TEST_PROJECT_IDENTIFIER, this.testTraits, 1, 1, 100);

		this.verifyScalarSetting();
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("instance_number"), Matchers.anyString());
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("studyId"), Matchers.eq(this.TEST_PROJECT_IDENTIFIER));

		// add additional test code here
		Assert.assertEquals("Make sure that we have one measurment returned", 1, returnedMeasurements.size());
		Assert.assertEquals("Make sure the GID is correct", new Integer(20000), returnedMeasurements.get(0).getGid());
	}

	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes sure the query returns appropriate values.
	 *
	 */
	@Test
	public void singlePlotMeasurementsQueryRetrievesDataCorrectly() throws Exception {
		Mockito.when(this.session.createSQLQuery(new ObservationQuery().getSingleObservationQuery(this.testTraits))).thenReturn(
				this.mockSqlQuery);

		List<ObservationDto> returnedMeasurements =
				this.trailTraits.getMeasurement(this.TEST_PROJECT_IDENTIFIER, this.testTraits, this.TEST_PLOT_IDENTIFIER);

		this.verifyScalarSetting();
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("instance_number"), Matchers.anyString());
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("studyId"), Matchers.eq(this.TEST_PROJECT_IDENTIFIER));
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("experiment_id"), Matchers.eq(this.TEST_PLOT_IDENTIFIER));

		// add additional test code here
		Assert.assertEquals("Make sure that we have one measurment returned", 1, returnedMeasurements.size());
		Assert.assertEquals("Make sure the GID is correct", new Integer(20000), returnedMeasurements.get(0).getGid());
	}

	private void verifyScalarSetting() {
		// There are two columns added per trait
		Mockito.verify(this.mockSqlQuery, Mockito.times(12 + this.testTraits.size())).addScalar(Matchers.anyString());
		Mockito.verify(this.mockSqlQuery, Mockito.times(this.testTraits.size())).addScalar(Matchers.anyString(),
				Mockito.any(IntegerType.class));

	}
}
