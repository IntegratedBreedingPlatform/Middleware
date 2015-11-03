
package org.generationcp.middleware.service;

import java.util.Collections;

import org.generationcp.middleware.data.initializer.MeasurementDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.MeasurementRowTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
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

	private Measurements measurements;

	@Before
	public void setup() {
		this.mockPhenotypeSaver = Mockito.mock(PhenotypeSaver.class);
		this.mockHibernateSessiong = Mockito.mock(Session.class);
		this.measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
	}

	@Test()
	public void validateNoramalValueMapping() throws Exception {
		final MeasurementData measurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "Test Value");

		final Phenotype phenotypeFromMeasurement = this.measurements.createPhenotypeFromMeasurement(measurementData);

		Assert.assertEquals("We do not map the assay id and thus it should be null", null, phenotypeFromMeasurement.getAssayId());
		Assert.assertEquals("We do not map the assay id and thus it should be null", null, phenotypeFromMeasurement.getAttributeId());
		Assert.assertEquals("Make sure categorical value is null since we have provided an actual value", null,
				phenotypeFromMeasurement.getcValueId());
		Assert.assertEquals("Phenotype value is mapped incorrectly", measurementData.getValue(), phenotypeFromMeasurement.getValue());
		Assert.assertEquals("Phenotype name is mapped incorrectly", measurementData.getLabel(), phenotypeFromMeasurement.getName());
		Assert.assertEquals("Phenotype observable id mapped incorrectly", (Integer) Integer.parseInt(measurementData.getDataType()),
				phenotypeFromMeasurement.getObservableId());
		Assert.assertEquals("Phenotype id mapped incorrectly", measurementData.getPhenotypeId(), phenotypeFromMeasurement.getPhenotypeId());
	}

	@Test()
	public void validateCustomeCategoricalValueMapping() throws Exception {
		final MeasurementData measurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.CATEGORICAL_VARIABLE.getId(), "1");
		measurementData.setCustomCategoricalValue(true);

		final Phenotype phenotypeFromMeasurement = this.measurements.createPhenotypeFromMeasurement(measurementData);

		Assert.assertEquals("Phenotype value is mapped incorrectly", measurementData.getcValueId(), phenotypeFromMeasurement.getcValueId()
				.toString());
	}

	@Test
	public void testUneditiableMeasurementDataAreSkipped() throws Exception {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setEditable(false);

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithPhenotypicIDSetToZeroAndBlankValueAreSkipped() throws Exception {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(0);
		testMeasurementData.setValue("");

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithPhenotypicIDSetToZeroAndNullValueAreSkipped() throws Exception {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(0);
		testMeasurementData.setValue(null);

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithNullPhenotypicIDAndBlankValueAreSkipped() throws Exception {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(null);
		testMeasurementData.setValue("");

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	@Test
	public void testMeasurementDataWithNullPhenotypicIDAndNullValueAreSkipped() throws Exception {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");
		testMeasurementData.setPhenotypeId(null);
		testMeasurementData.setValue(null);

		this.assertIfSaveMeasurementDataIsNotCalled(testMeasurementData);
	}

	private void assertIfSaveMeasurementDataIsNotCalled(final MeasurementData testMeasurementData) {
		final MeasurementRow measurementRow =
				MeasurementRowTestDataInitializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);

		this.measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(0)).saveOrUpdate(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyString(),
				(Phenotype) Matchers.anyObject(), Matchers.anyInt());
	}

	@Test
	public void makeSureCorrectHibernateFlushTypeIsUsed() throws Exception {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, TermId.NUMERIC_VARIABLE.getId(), "1");

		final MeasurementRow measurementRow =
				MeasurementRowTestDataInitializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);

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

	private void testSavingMeasurements(final String value, final int variableDataTypeId) {

		final MeasurementData testMeasurementData =
				MeasurementDataTestDataInitializer.createMeasurementData(TEST_TERM_ID, variableDataTypeId, value);

		final MeasurementRow measurementRow =
				MeasurementRowTestDataInitializer.createMeasurementRowWithAtLeast1MeasurementVar(testMeasurementData);

		this.measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));

		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(1)).saveOrUpdate(Matchers.eq(measurementRow.getExperimentId()),
				Matchers.eq(MeasurementsTest.TEST_TERM_ID), Matchers.eq(value), (Phenotype) Matchers.anyObject(),
				Matchers.eq(variableDataTypeId));
	}

}
