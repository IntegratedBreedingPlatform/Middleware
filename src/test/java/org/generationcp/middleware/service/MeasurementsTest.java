
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.data.initializer.MeasurementTestDataInitializer;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.operation.saver.PhenotypeOutlierSaver;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.hibernate.FlushMode;
import org.hibernate.classic.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class MeasurementsTest {

	private static final int TEST_TERM_ID = 100;

	private Session mockHibernateSessiong;

	private PhenotypeSaver mockPhenotypeSaver;

	private PhenotypeOutlierSaver mockPhenotypeOutlierSaver;

	private Measurements measurements;

	private final MeasurementTestDataInitializer initializer = new MeasurementTestDataInitializer();

	@Before
	public void setup() {
		this.mockPhenotypeSaver = Mockito.mock(PhenotypeSaver.class);
		this.mockPhenotypeOutlierSaver = Mockito.mock(PhenotypeOutlierSaver.class);
		this.mockHibernateSessiong = Mockito.mock(Session.class);
		this.measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver, this.mockPhenotypeOutlierSaver);
	}

	@Test
	public void testTrialDesignSaving() {

		final MeasurementRow measurementRow = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<>();
		final List<MeasurementRow> rowList = new ArrayList<>();
		final MeasurementData measurementData = this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		final MeasurementVariable variable = new MeasurementVariable();

		variable.setRole(PhenotypicType.TRIAL_DESIGN);
		measurementData.setMeasurementVariable(variable);
		dataList.add(measurementData);
		measurementRow.setDataList(dataList);
		rowList.add(measurementRow);

		this.measurements.saveMeasurementData(rowList);
		Mockito.verify(this.mockPhenotypeSaver, Mockito.never()).saveOrUpdate(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(Phenotype.class), Mockito.anyInt());

	}

	@Test()
	public void validateNoramalValueMapping() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "Test Value");

		final Phenotype phenotypeFromMeasurement = this.measurements.createPhenotypeFromMeasurement(testMeasurementData);

		Assert.assertEquals("We do not map the assay id and thus it should be null", null, phenotypeFromMeasurement.getAssayId());
		Assert.assertEquals("We do not map the assay id and thus it should be null", null, phenotypeFromMeasurement.getAttributeId());
		Assert.assertEquals("Make sure categorical value is null since we have provided an actual value", null,
				phenotypeFromMeasurement.getcValueId());
		Assert.assertEquals("Phenotype value is mapped incorrectly", testMeasurementData.getValue(), phenotypeFromMeasurement.getValue());
		Assert.assertEquals("Phenotype name is mapped incorrectly", testMeasurementData.getLabel(), phenotypeFromMeasurement.getName());
		Assert.assertEquals("Phenotype observable id mapped incorrectly", (Integer) Integer.parseInt(testMeasurementData.getDataType()),
				phenotypeFromMeasurement.getObservableId());
		Assert.assertEquals("Phenotype id mapped incorrectly", testMeasurementData.getPhenotypeId(),
				phenotypeFromMeasurement.getPhenotypeId());
	}

	@Test()
	public void validateCustomeCategoricalValueMapping() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.CATEGORICAL_VARIABLE.getId(), "1");
		testMeasurementData.setCustomCategoricalValue(true);

		final Phenotype phenotypeFromMeasurement = this.measurements.createPhenotypeFromMeasurement(testMeasurementData);

		Assert.assertEquals("Phenotype value is mapped incorrectly", testMeasurementData.getcValueId(), phenotypeFromMeasurement
				.getcValueId().toString());
	}

	@Test
	public void testUneditableMeasurementDataAreSkipped() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setEditable(false);

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithPhenotypicIDSetToZeroAndBlankValueAreSkipped() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(0);
		testMeasurementData.setValue("");

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithPhenotypicIDSetToZeroAndNullValueAreSkipped() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(0);
		testMeasurementData.setValue(null);

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithNullPhenotypicIDAndBlankValueAreSkipped() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(null);
		testMeasurementData.setValue("");

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithNullPhenotypicIDAndNullValueAreSkipped() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(null);
		testMeasurementData.setValue(null);

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	private void assertIfSaveMeasurementDataIsNotCalled(final MeasurementData testMeasurementData) {
		final MeasurementRow measurementRow = this.initializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);

		this.measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(0)).saveOrUpdate(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyString(),
				(Phenotype) Matchers.anyObject(), Matchers.anyInt());
	}

	@Test
	public void makeSureCorrectHibernateFlushTypeIsUsed() throws Exception {
		final MeasurementData testMeasurementData =
				this.initializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		final MeasurementRow measurementRow = this.initializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);

		Mockito.when(this.mockHibernateSessiong.getFlushMode()).thenReturn(FlushMode.AUTO);
		this.measurements.saveMeasurements(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockHibernateSessiong).setFlushMode(FlushMode.MANUAL);
		Mockito.verify(this.mockHibernateSessiong).flush();
		Mockito.verify(this.mockHibernateSessiong).setFlushMode(FlushMode.AUTO);
	}

	@Test
	public void testMeasurementDataAreSavedAsPhenotypes() throws Exception {
		this.testSavingMeasurements("Numeric Value", TermId.NUMERIC_VARIABLE.getId());
	}

	@Test
	public void testCategoricalMeasurementDataAreSavedAsPhenotypes() throws Exception {
		this.testSavingMeasurements("Categorical Value", TermId.CATEGORICAL_VARIABLE.getId());
	}

	/**
	 * Simple test to ensure that the Phenotype id is set into the measurement data so that the UI does not recreate data. The test does not
	 * do any logical validation but just ensures that the measurement data is updated with a phenotype id when created. Without this saving
	 * an updating of measurement data is broken.
	 */
	@Test
	public void testPhenotypeIdSetOnSave() {
		final Measurements measurements =
				new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver, this.mockPhenotypeOutlierSaver);
		final MeasurementData testMeasurementData = Mockito.mock(MeasurementData.class);
		final MeasurementVariable testMeasurementVariable = new MeasurementVariable();
		final MeasurementRow measurementRow = this.initializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));

		// Set up measurement data so that it actually tries to save something.
		testMeasurementVariable.setRole(PhenotypicType.VARIATE);
		Mockito.when(testMeasurementData.isEditable()).thenReturn(true);
		Mockito.when(testMeasurementData.getValue()).thenReturn("Test Data");
		Mockito.when(testMeasurementData.getMeasurementVariable()).thenReturn(testMeasurementVariable);

		final int testPhenotypeId = 245;
		Mockito.when(testMeasurementData.getPhenotypeId()).thenReturn(testPhenotypeId);

		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(testMeasurementData).setPhenotypeId(245);
	}

	private void testSavingMeasurements(final String value, final int variableDataTypeId) {

		final MeasurementData testMeasurementData = this.initializer.createMeasurementData(TEST_TERM_ID, variableDataTypeId, value);
		final MeasurementVariable testMeasurementVariable = testMeasurementData.getMeasurementVariable();
		testMeasurementVariable.setRole(PhenotypicType.VARIATE);

		final MeasurementRow measurementRow = this.initializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);

		this.measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));

		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(1)).saveOrUpdate(Matchers.eq(measurementRow.getExperimentId()),
				Matchers.eq(MeasurementsTest.TEST_TERM_ID), Matchers.eq(value), (Phenotype) Matchers.anyObject(),
				Matchers.eq(variableDataTypeId));
	}

}
