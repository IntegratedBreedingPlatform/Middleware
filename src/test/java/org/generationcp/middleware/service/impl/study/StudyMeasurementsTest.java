
package org.generationcp.middleware.service.impl.study;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.beust.jcommander.internal.Lists;

/**
 * The class <code>TrialMeasurementsTest</code> contains tests for the class <code>{@link StudyMeasurements}</code>.
 *
 */
public class StudyMeasurementsTest {

	private Session session;
	private StudyMeasurements trailTraits;
	private SQLQuery mockSqlQuery;
	private List<MeasurementVariableDto> testTraits;
	private List<String> germplasmDescriptors;
	private Object[] testRows;
	private List<Object[]> sampleMeasurements;

	private final int TEST_PROJECT_IDENTIFIER = 2019;
	private final int TEST_PLOT_IDENTIFIER = 9999;

	@Before
	public void setup() {
		this.session = Mockito.mock(Session.class);

		this.trailTraits = new StudyMeasurements(this.session);

		this.mockSqlQuery = Mockito.mock(SQLQuery.class);
		this.testTraits = Arrays.asList(new MeasurementVariableDto(1, "Trait1"), new MeasurementVariableDto(2, "Trait2"));
		this.germplasmDescriptors = Lists.newArrayList("STOCK_ID");
		this.testRows = new Object[] {1, "TRIAL_INSTACE", "ENTRY_TYPE", 20000, "DESIGNATION", "ENTRY_NO", "SEED_SOURCE", "REPITION_NUMBER",
			"PLOT_NUMBER", "BLOCK_NO", "ROW", "COL", "", "", "PlotID-ABC123", "SUM OF SAMPLES", "Trait1Value", 1000, "Trait2Value", 2000,
			"Stock_Id_Value"};
		this.sampleMeasurements = Arrays.<Object[]>asList(this.testRows);
		Mockito.when(this.mockSqlQuery.list()).thenReturn(this.sampleMeasurements);

	}

	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes sure the query returns appropriate values.
	 *
	 */
	@Test
	public void allPlotsMeasurementQueryRetrievesDataCorrectly() throws Exception {

		Mockito.when(this.session
				.createSQLQuery(new ObservationQuery().getAllObservationsQuery(this.testTraits, this.germplasmDescriptors, null, null)))
				.thenReturn(this.mockSqlQuery);

		List<ObservationDto> returnedMeasurements =
				this.trailTraits.getAllMeasurements(this.TEST_PROJECT_IDENTIFIER, this.testTraits, this.germplasmDescriptors, 1, 1, 100,
						null, null);

		this.verifyScalarSetting();
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("instanceId"), Matchers.anyString());
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
		Mockito.when(
				this.session.createSQLQuery(new ObservationQuery().getSingleObservationQuery(this.testTraits, this.germplasmDescriptors)))
				.thenReturn(
				this.mockSqlQuery);

		List<ObservationDto> returnedMeasurements =
				this.trailTraits.getMeasurement(this.TEST_PROJECT_IDENTIFIER, this.testTraits, this.germplasmDescriptors,
						this.TEST_PLOT_IDENTIFIER);

		this.verifyScalarSetting();
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("studyId"), Matchers.eq(this.TEST_PROJECT_IDENTIFIER));
		Mockito.verify(this.mockSqlQuery).setParameter(Matchers.eq("experiment_id"), Matchers.eq(this.TEST_PLOT_IDENTIFIER));

		// add additional test code here
		Assert.assertEquals("Make sure that we have one measurment returned", 1, returnedMeasurements.size());
		Assert.assertEquals("Make sure the GID is correct", new Integer(20000), returnedMeasurements.get(0).getGid());
	}

	private void verifyScalarSetting() {
		// 13 - 1 (PLOT_ID) fixed columns + Trait name - no type
		Mockito.verify(this.mockSqlQuery, Mockito.times(15 + this.testTraits.size())).addScalar(Matchers.anyString());

		// PLOT_ID with StringType
		Mockito.verify(this.mockSqlQuery).addScalar(Matchers.eq("PLOT_ID"), Mockito.any(StringType.class));

		// Once for the germplasm factor as StringType
		for (String gpDesc : this.germplasmDescriptors) {
			Mockito.verify(this.mockSqlQuery).addScalar(Matchers.eq(gpDesc), Mockito.any(StringType.class));
		}

		// Trait PhenotypeIds as IntegerType for each trait
		for (MeasurementVariableDto t : this.testTraits) {
			Mockito.verify(this.mockSqlQuery).addScalar(Matchers.eq(t.getName() + "_PhenotypeId"), Matchers.any(IntegerType.class));
		}
	}
}
