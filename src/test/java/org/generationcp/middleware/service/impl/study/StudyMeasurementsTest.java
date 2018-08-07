
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.study.MeasurementDto;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.beust.jcommander.internal.Lists;

/**
 * The class <code>StudyMeasurementsTest</code> contains tests for the class
 * <code>{@link StudyMeasurements}</code>.
 *
 */
public class StudyMeasurementsTest {

	private static final String DESIGNATION1 = "DESIGNATION1";
	private static final String DESIGNATION2 = "DESIGNATION2";
	private static final String SEED_SOURCE1 = "CRUZ1";
	private static final String SEED_SOURCE2 = "CRUZ2";
	private static final int GID1 = 20000;
	private static final int GID2 = 98765;
	private static final String FACT2 = "FACT2";
	private static final String FACT1 = "FACT1";
	private static final String TRAIT2 = "Trait2";
	private static final String TRAIT1 = "Trait1";
	private static final String STOCK_ID = "STOCK_ID";
	private static final int PROJECT_IDENTIFIER = 2019;
	private static final int PLOT_IDENTIFIER1 = 9999;
	private static final int PLOT_IDENTIFIER2 = 8888;
	private static final String PLOT_ID1 = "ABC" + StudyMeasurementsTest.PLOT_IDENTIFIER1;
	private static final String PLOT_ID2 = "ABC" + StudyMeasurementsTest.PLOT_IDENTIFIER2;
	private static final String PLOT_NO1 = "1";
	private static final String PLOT_NO2 = "2";
	private static final String TRIAL_INSTANCE = "1";
	private static final String REP_NO = "2";
	private static final String BLOCK_NO = "3";
	private static final String ROW = "4";
	private static final String COL = "5";
	private static final String FIELDMAP_COLUMN = "6";
	private static final String FIELDMAP_RANGE = "7";
	private static final String SUM_OF_SAMPLES = "20";
	private static final String ROW1_TRAIT1_VALUE = "1001";
	private static final int ROW1_TRAIT1_PHENOTYPE_ID = 100100;
	private static final String ROW1_TRAIT2_VALUE = "1002";
	private static final int ROW1_TRAIT2_PHENOTYPE_ID = 100200;
	private static final String ROW2_TRAIT1_VALUE = "2001";
	private static final int ROW2_TRAIT1_PHENOTYPE_ID = 200100;
	private static final String ROW2_TRAIT2_VALUE = "2002";
	private static final int ROW2_TRAIT2_PHENOTYPE_ID = 200200;
	private static final String STOCKID1 = "STK-1";
	private static final String STOCKID2 = "STK-2";
	private static final String FACT1_VALUE1 = "1";
	private static final String FACT1_VALUE2 = "2";
	private static final String FACT2_VALUE1 = "3";
	private static final String FACT2_VALUE2 = "4";
	private static final String ENTRY_TYPE = String
			.valueOf(SystemDefinedEntryType.CHECK_ENTRY.getEntryTypeCategoricalId());
	private static final List<String> STRING_COLUMNS = Lists.newArrayList("nd_experiment_id", "TRIAL_INSTANCE",
			"ENTRY_TYPE", "GID", "DESIGNATION", "ENTRY_NO", "ENTRY_CODE", "REP_NO", "PLOT_NO", "BLOCK_NO", "ROW", "COL",
			"FIELDMAP COLUMN", "FIELDMAP RANGE", "SUM_OF_SAMPLES");
	private final List<String> germplasmDescriptors = Lists.newArrayList(StudyMeasurementsTest.STOCK_ID);
	private final List<String> designFactors = Lists.newArrayList(StudyMeasurementsTest.FACT1,
			StudyMeasurementsTest.FACT2);
	private final List<MeasurementVariableDto> testTraits = Arrays.asList(
			new MeasurementVariableDto(1, StudyMeasurementsTest.TRAIT1),
			new MeasurementVariableDto(2, StudyMeasurementsTest.TRAIT2));
	private static final String ROW1_TRAIT1_STATUS = "MANUALLY_EDITED";
	private static final String ROW1_TRAIT2_STATUS = "OUT_OF_SYNC";
	private static final String ROW2_TRAIT2_STATUS = null;
	private static final String ROW2_TRAIT1_STATUS = null;

	@Mock
	private Session session;

	@Mock
	private SQLQuery mockSqlQueryForAllMeasurements;

	@Mock
	private SQLQuery mockSqlQueryForSingleMeasurement;

	private StudyMeasurements studyMeasurements;
	private Object[] testRow1;
	private Object[] testRow2;
	private List<Object[]> allMeasurements;
	private List<Object[]> singleMeasurement;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.studyMeasurements = new StudyMeasurements(this.session);

		this.testRow1 = new Object[] {
			StudyMeasurementsTest.PLOT_IDENTIFIER1, StudyMeasurementsTest.TRIAL_INSTANCE,
			StudyMeasurementsTest.ENTRY_TYPE, StudyMeasurementsTest.GID1, StudyMeasurementsTest.DESIGNATION1,
			StudyMeasurementsTest.PLOT_NO1, StudyMeasurementsTest.SEED_SOURCE1, StudyMeasurementsTest.REP_NO,
			StudyMeasurementsTest.PLOT_NO1, StudyMeasurementsTest.BLOCK_NO, StudyMeasurementsTest.ROW, StudyMeasurementsTest.COL,
			StudyMeasurementsTest.PLOT_ID1, StudyMeasurementsTest.FIELDMAP_COLUMN, StudyMeasurementsTest.FIELDMAP_RANGE,
			StudyMeasurementsTest.SUM_OF_SAMPLES, StudyMeasurementsTest.ROW1_TRAIT1_VALUE,
			StudyMeasurementsTest.ROW1_TRAIT1_PHENOTYPE_ID, StudyMeasurementsTest.ROW1_TRAIT1_STATUS,
			StudyMeasurementsTest.ROW1_TRAIT2_VALUE,
			StudyMeasurementsTest.ROW1_TRAIT2_PHENOTYPE_ID, StudyMeasurementsTest.ROW1_TRAIT2_STATUS, StudyMeasurementsTest.STOCKID1,
			StudyMeasurementsTest.FACT1_VALUE1,
			StudyMeasurementsTest.FACT2_VALUE1};
		this.testRow2 = new Object[] {
			StudyMeasurementsTest.PLOT_IDENTIFIER2, StudyMeasurementsTest.TRIAL_INSTANCE,
			StudyMeasurementsTest.ENTRY_TYPE, StudyMeasurementsTest.GID2, StudyMeasurementsTest.DESIGNATION2,
			StudyMeasurementsTest.PLOT_NO2, StudyMeasurementsTest.SEED_SOURCE2, StudyMeasurementsTest.REP_NO,
			StudyMeasurementsTest.PLOT_NO2, StudyMeasurementsTest.BLOCK_NO, StudyMeasurementsTest.ROW, StudyMeasurementsTest.COL,
			StudyMeasurementsTest.PLOT_ID2, StudyMeasurementsTest.FIELDMAP_COLUMN, StudyMeasurementsTest.FIELDMAP_RANGE,
			StudyMeasurementsTest.SUM_OF_SAMPLES, StudyMeasurementsTest.ROW2_TRAIT1_VALUE,
			StudyMeasurementsTest.ROW2_TRAIT1_PHENOTYPE_ID, StudyMeasurementsTest.ROW2_TRAIT1_STATUS,
			StudyMeasurementsTest.ROW2_TRAIT2_VALUE,
			StudyMeasurementsTest.ROW2_TRAIT2_PHENOTYPE_ID, StudyMeasurementsTest.ROW2_TRAIT2_STATUS, StudyMeasurementsTest.STOCKID2,
			StudyMeasurementsTest.FACT1_VALUE2,
			StudyMeasurementsTest.FACT2_VALUE2};
		this.singleMeasurement = Arrays.<Object[]>asList(this.testRow1);
		this.allMeasurements = Arrays.<Object[]>asList(this.testRow1, this.testRow2);
		Mockito.when(this.mockSqlQueryForAllMeasurements.list()).thenReturn(this.allMeasurements);
		Mockito.when(this.mockSqlQueryForSingleMeasurement.list()).thenReturn(this.singleMeasurement);
	}

	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes
	 * sure the query returns appropriate values.
	 *
	 */
	@Test
	public void allPlotsMeasurementQueryRetrievesDataCorrectly() throws Exception {

		Mockito.when(this.session.createSQLQuery(new ObservationQuery().getAllObservationsQuery(this.testTraits,
				this.germplasmDescriptors, this.designFactors, null, null)))
				.thenReturn(this.mockSqlQueryForAllMeasurements);

		final int instanceId = 1;
		final int pageNumber = 1;
		final int pageSize = 100;
		final List<ObservationDto> returnedMeasurements = this.studyMeasurements.getAllMeasurements(
				StudyMeasurementsTest.PROJECT_IDENTIFIER, this.testTraits, this.germplasmDescriptors,
				this.designFactors, instanceId, pageNumber, pageSize, null, null);

		this.verifyScalarSetting(this.mockSqlQueryForAllMeasurements);
		Mockito.verify(this.mockSqlQueryForAllMeasurements).setParameter(Matchers.eq("studyId"),
				Matchers.eq(StudyMeasurementsTest.PROJECT_IDENTIFIER));
		Mockito.verify(this.mockSqlQueryForAllMeasurements).setParameter(Matchers.eq("instanceId"),
				Matchers.eq(String.valueOf(instanceId)));

		Assert.assertEquals("Make sure that we have all measurements returned", this.allMeasurements.size(),
				returnedMeasurements.size());
		final ObservationDto measurement1 = returnedMeasurements.get(0);
		this.verifyObservationValues(measurement1, StudyMeasurementsTest.PLOT_IDENTIFIER1, StudyMeasurementsTest.GID1,
				StudyMeasurementsTest.PLOT_NO1, StudyMeasurementsTest.SEED_SOURCE1, StudyMeasurementsTest.PLOT_NO1,
				StudyMeasurementsTest.PLOT_ID1,
				Arrays.asList(
						new ImmutablePair<Integer, String>(StudyMeasurementsTest.ROW1_TRAIT1_PHENOTYPE_ID,
								StudyMeasurementsTest.ROW1_TRAIT1_VALUE),
						new ImmutablePair<Integer, String>(StudyMeasurementsTest.ROW1_TRAIT2_PHENOTYPE_ID,
								StudyMeasurementsTest.ROW1_TRAIT2_VALUE)),
				Arrays.asList(new ImmutablePair<String, String>(StudyMeasurementsTest.STOCK_ID,
						StudyMeasurementsTest.STOCKID1)),
				Arrays.asList(
						new ImmutablePair<String, String>(StudyMeasurementsTest.FACT1,
								StudyMeasurementsTest.FACT1_VALUE1),
						new ImmutablePair<String, String>(StudyMeasurementsTest.FACT2,
								StudyMeasurementsTest.FACT2_VALUE1)));

		final ObservationDto measurement2 = returnedMeasurements.get(1);
		this.verifyObservationValues(measurement2, StudyMeasurementsTest.PLOT_IDENTIFIER2, StudyMeasurementsTest.GID2,
				StudyMeasurementsTest.PLOT_NO2, StudyMeasurementsTest.SEED_SOURCE2, StudyMeasurementsTest.PLOT_NO2,
				StudyMeasurementsTest.PLOT_ID2,
				Arrays.asList(
						new ImmutablePair<Integer, String>(StudyMeasurementsTest.ROW2_TRAIT1_PHENOTYPE_ID,
								StudyMeasurementsTest.ROW2_TRAIT1_VALUE),
						new ImmutablePair<Integer, String>(StudyMeasurementsTest.ROW2_TRAIT2_PHENOTYPE_ID,
								StudyMeasurementsTest.ROW2_TRAIT2_VALUE)),
				Arrays.asList(new ImmutablePair<String, String>(StudyMeasurementsTest.STOCK_ID,
						StudyMeasurementsTest.STOCKID2)),
				Arrays.asList(
						new ImmutablePair<String, String>(StudyMeasurementsTest.FACT1,
								StudyMeasurementsTest.FACT1_VALUE2),
						new ImmutablePair<String, String>(StudyMeasurementsTest.FACT2,
								StudyMeasurementsTest.FACT2_VALUE2)));
	}

	@Test
	public void testGetAllStudyDetailsAsTable() {
		final List<MeasurementVariableDto> traits = Arrays
				.asList(new MeasurementVariableDto(TermId.ALTITUDE.getId(), TermId.ALTITUDE.name()));
		final List<Object[]> results = new ArrayList<>();
		final Object[] result = { 1, 1, "Test", 1, "desig", 1, "entry code", "1", "PLOT_NO", "1", 1, 1, "PLOT_ID",
				"LOC_NAME", "LOC_ABBR", 1, 1, 1, 1, "Study Name", 1 };
		results.add(result);
		final SQLQuery query = Mockito.mock(SQLQuery.class);
		Mockito.when(this.session.createSQLQuery(new ObservationQuery().getObservationQueryWithBlockRowCol(traits, 1)))
				.thenReturn(query);
		Mockito.when(query.list()).thenReturn(results);
		final List<Object[]> returned = this.studyMeasurements.getAllStudyDetailsAsTable(2007, traits, 1);
		for (int i = 0; i < returned.size(); i++) {
			Assert.assertEquals(result[i], returned.get(0)[i]);
		}
		Mockito.verify(query).addScalar(StudyMeasurements.PROJECT_NAME);
		Mockito.verify(query).addScalar(StudyMeasurements.LOCATION_DB_ID);
		Mockito.verify(query).addScalar(StudyMeasurements.ND_GEOLOCATION_ID);
		Mockito.verify(query).addScalar(StudyMeasurements.FIELD_MAP_ROW);
		Mockito.verify(query).addScalar(StudyMeasurements.FIELD_MAP_COLUMN);
		Mockito.verify(query).addScalar(StudyMeasurements.LOCATION_ABBREVIATION);
		Mockito.verify(query).addScalar(StudyMeasurements.LOCATION_NAME);
		Mockito.verify(query).addScalar(Matchers.eq(StudyMeasurements.PLOT_ID), Matchers.any(StringType.class));
		Mockito.verify(query).addScalar(StudyMeasurements.COL);
		Mockito.verify(query).addScalar(StudyMeasurements.ROW);
		Mockito.verify(query).addScalar(StudyMeasurements.BLOCK_NO);
		Mockito.verify(query).addScalar(StudyMeasurements.PLOT_NO);
		Mockito.verify(query).addScalar(StudyMeasurements.REP_NO);
		Mockito.verify(query).addScalar(StudyMeasurements.ENTRY_CODE);
		Mockito.verify(query).addScalar(StudyMeasurements.ENTRY_NO);
		Mockito.verify(query).addScalar(StudyMeasurements.DESIGNATION);
		Mockito.verify(query).addScalar(StudyMeasurements.GID);
		Mockito.verify(query).addScalar(StudyMeasurements.ENTRY_TYPE);
		Mockito.verify(query).addScalar(StudyMeasurements.TRIAL_INSTANCE);
		Mockito.verify(query).addScalar(StudyMeasurements.ND_EXPERIMENT_ID);
		Mockito.verify(query).addScalar(TermId.ALTITUDE.name());
		Mockito.verify(query).addScalar(Matchers.eq(TermId.ALTITUDE.name() + "_PhenotypeId"),
				Matchers.any(IntegerType.class));
	}

	/**
	 * Run the {@link StudyMeasurements}.getAllMeasurements() method and makes
	 * sure the query returns appropriate values.
	 *
	 */
	@Test
	public void singlePlotMeasurementsQueryRetrievesDataCorrectly() throws Exception {
		Mockito.when(this.session.createSQLQuery(new ObservationQuery().getSingleObservationQuery(this.testTraits,
				this.germplasmDescriptors, this.designFactors))).thenReturn(this.mockSqlQueryForSingleMeasurement);

		final List<ObservationDto> returnedMeasurements = this.studyMeasurements.getMeasurement(
				StudyMeasurementsTest.PROJECT_IDENTIFIER, this.testTraits, this.germplasmDescriptors,
				this.designFactors, StudyMeasurementsTest.PLOT_IDENTIFIER1);

		this.verifyScalarSetting(this.mockSqlQueryForSingleMeasurement);
		Mockito.verify(this.mockSqlQueryForSingleMeasurement).setParameter(Matchers.eq("studyId"),
				Matchers.eq(StudyMeasurementsTest.PROJECT_IDENTIFIER));
		Mockito.verify(this.mockSqlQueryForSingleMeasurement).setParameter(Matchers.eq("experiment_id"),
				Matchers.eq(StudyMeasurementsTest.PLOT_IDENTIFIER1));

		final ObservationDto measurement1 = returnedMeasurements.get(0);
		this.verifyObservationValues(measurement1, StudyMeasurementsTest.PLOT_IDENTIFIER1, StudyMeasurementsTest.GID1,
				StudyMeasurementsTest.PLOT_NO1, StudyMeasurementsTest.SEED_SOURCE1, StudyMeasurementsTest.PLOT_NO1,
				StudyMeasurementsTest.PLOT_ID1,
				Arrays.asList(
						new ImmutablePair<Integer, String>(StudyMeasurementsTest.ROW1_TRAIT1_PHENOTYPE_ID,
								StudyMeasurementsTest.ROW1_TRAIT1_VALUE),
						new ImmutablePair<Integer, String>(StudyMeasurementsTest.ROW1_TRAIT2_PHENOTYPE_ID,
								StudyMeasurementsTest.ROW1_TRAIT2_VALUE)),
				Arrays.asList(new ImmutablePair<String, String>(StudyMeasurementsTest.STOCK_ID,
						StudyMeasurementsTest.STOCKID1)),
				Arrays.asList(
						new ImmutablePair<String, String>(StudyMeasurementsTest.FACT1,
								StudyMeasurementsTest.FACT1_VALUE1),
						new ImmutablePair<String, String>(StudyMeasurementsTest.FACT2,
								StudyMeasurementsTest.FACT2_VALUE1)));

	}

	private void verifyObservationValues(final ObservationDto measurement, final int plotIdentifier, final int gid,
			final String entryNo, final String seedSource, final String plotNo, final String plotId,
			final List<ImmutablePair<Integer, String>> traits,
			final List<ImmutablePair<String, String>> germplasmDescriptors,
			final List<ImmutablePair<String, String>> designFactors) {
		Assert.assertEquals("Make sure the Experiment ID is correct", new Integer(plotIdentifier),
				measurement.getMeasurementId());
		Assert.assertEquals("Make sure the Trial Instance is correct", StudyMeasurementsTest.TRIAL_INSTANCE,
				measurement.getTrialInstance());
		Assert.assertEquals("Make sure the Entry Type is correct", StudyMeasurementsTest.ENTRY_TYPE,
				measurement.getEntryType());
		Assert.assertEquals("Make sure the GID is correct", new Integer(gid), measurement.getGid());
		Assert.assertEquals("Make sure the Entry # is correct", entryNo, measurement.getEntryNo());
		Assert.assertEquals("Make sure the Entry Code is correct", seedSource, measurement.getEntryCode());
		Assert.assertEquals("Make sure the Plot # is correct", plotNo, measurement.getPlotNumber());
		Assert.assertEquals("Make sure the Rep # is correct", StudyMeasurementsTest.REP_NO,
				measurement.getRepitionNumber());
		Assert.assertEquals("Make sure the Block # is correct", StudyMeasurementsTest.BLOCK_NO,
				measurement.getBlockNumber());
		Assert.assertEquals("Make sure the Row # is correct", StudyMeasurementsTest.ROW, measurement.getRowNumber());
		Assert.assertEquals("Make sure the Col # is correct", StudyMeasurementsTest.COL, measurement.getColumnNumber());
		Assert.assertEquals("Make sure the Plot Id is correct", plotId, measurement.getPlotId());
		Assert.assertEquals("Make sure the Fieldmap Column is correct", StudyMeasurementsTest.FIELDMAP_COLUMN,
				measurement.getFieldMapColumn());
		Assert.assertEquals("Make sure the Fielmap Range is correct", StudyMeasurementsTest.FIELDMAP_RANGE,
				measurement.getFieldMapRange());
		Assert.assertEquals("Make sure the Sum of Samples is correct", StudyMeasurementsTest.SUM_OF_SAMPLES,
				measurement.getSamples());
		final Iterator<MeasurementDto> traitMeasurementIterator = measurement.getVariableMeasurements().iterator();
		for (final Pair<Integer, String> trait : traits) {
			final MeasurementDto traitMeasurement = traitMeasurementIterator.next();
			Assert.assertEquals("Make sure the Trait Phenotype Id is correct", trait.getLeft(),
					traitMeasurement.getPhenotypeId());
			Assert.assertEquals("Make sure the Trait Value is correct", trait.getRight(),
					traitMeasurement.getVariableValue());
		}
		final Iterator<Pair<String, String>> germplasmDescripIterator = measurement.getAdditionalGermplasmDescriptors()
				.iterator();
		for (final Pair<String, String> trait : germplasmDescriptors) {
			final Pair<String, String> descriptor = germplasmDescripIterator.next();
			Assert.assertEquals("Make sure the Stock Id Name is correct", trait.getLeft(), descriptor.getLeft());
			Assert.assertEquals("Make sure the Stock Id Value is correct", trait.getRight(), descriptor.getRight());
		}
		final Iterator<Pair<String, String>> designFactorIterator = measurement.getAdditionalDesignFactors().iterator();
		for (final Pair<String, String> trait : designFactors) {
			final Pair<String, String> designFactor = designFactorIterator.next();
			Assert.assertEquals("Make sure the Design Factor Name is correct", trait.getLeft(), designFactor.getLeft());
			Assert.assertEquals("Make sure the Design Factor Value is correct", trait.getRight(),
					designFactor.getRight());
		}
	}

	private void verifyScalarSetting(final SQLQuery query) {
		// Fixed columns + Trait name - no type
		Mockito.verify(query, Mockito.times(StudyMeasurementsTest.STRING_COLUMNS.size() + this.testTraits.size() + 2))
				.addScalar(Matchers.anyString());
		for (final String column : StudyMeasurementsTest.STRING_COLUMNS) {
			Mockito.verify(query).addScalar(column);
		}
		for (final MeasurementVariableDto variable : this.testTraits) {
			Mockito.verify(query).addScalar(variable.getName());
		}
		// PLOT_ID with StringType
		Mockito.verify(query).addScalar(Matchers.eq("PLOT_ID"), Matchers.any(StringType.class));

		// Once for each non-fixed germplasm factor as StringType
		for (final String gpDesc : this.germplasmDescriptors) {
			Mockito.verify(query).addScalar(Matchers.eq(gpDesc), Matchers.any(StringType.class));
		}

		// Once for each non-fixed design factor as StringType
		for (final String designFactor : this.designFactors) {
			Mockito.verify(query).addScalar(Matchers.eq(designFactor), Matchers.any(StringType.class));
		}

		// Trait PhenotypeIds as IntegerType for each trait
		for (final MeasurementVariableDto t : this.testTraits) {
			Mockito.verify(query).addScalar(Matchers.eq(t.getName() + "_PhenotypeId"), Matchers.any(IntegerType.class));
		}
	}
}
