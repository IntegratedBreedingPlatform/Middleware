
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.hibernate.FlushMode;
import org.hibernate.classic.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import edu.umd.cs.findbugs.annotations.When;

public class MeasurementsTest {

	private static final int TEST_DATA_TYPE_ID = 200;

	private static final int TEST_TERM_ID = 100;

	private Session mockHibernateSessiong;

	private PhenotypeSaver mockPhenotypeSaver;

	private Measurements measurements;

	@Before
	public void setup() {
		this.mockPhenotypeSaver = Mockito.mock(PhenotypeSaver.class);
		this.mockHibernateSessiong = Mockito.mock(Session.class);
		measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);

	}

	@Test
	public void testTrialDesignSaving() {

		MeasurementRow measurementRow = new MeasurementRow();
		List<MeasurementData> dataList = new ArrayList<>();
		List<MeasurementRow> rowList = new ArrayList<>();
		MeasurementData measurementData = this.getTestMeasurementData();
		MeasurementVariable variable = new MeasurementVariable();

		variable.setRole(PhenotypicType.TRIAL_DESIGN);
		measurementData.setMeasurementVariable(variable);
		dataList.add(measurementData);
		measurementRow.setDataList(dataList);
		rowList.add(measurementRow);

		measurements.saveMeasurementData(rowList);
		Mockito.verify(mockPhenotypeSaver, Mockito.never()).saveOrUpdate(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(Phenotype.class), Mockito.anyInt());

	}

	@Test()
	public void validateNoramalValueMapping() throws Exception {
		final MeasurementData measurementData = this.getTestMeasurementData();
		measurementData.setValue("Test Value");

		final Phenotype phenotypeFromMeasurement = measurements.createPhenotypeFromMeasurement(measurementData);

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
		final MeasurementData measurementData = this.getTestMeasurementData();
		measurementData.setCustomCategoricalValue(true);
		measurementData.setcValueId("1");

		final Phenotype phenotypeFromMeasurement = measurements.createPhenotypeFromMeasurement(measurementData);

		Assert.assertEquals("Phenotype value is mapped incorrectly", measurementData.getcValueId(), phenotypeFromMeasurement.getcValueId()
				.toString());
	}

	@Test
	public void testUneditableMeasurementDataAreSkipped() throws Exception {
		final MeasurementRow measurementRow = getMeasurementRow();
		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		testMeasurementData.setEditable(false);
		testSkippingOfNonCreatedPhenotypes(measurementRow, testMeasurementData);
	}

	@Test
	public void testNullValueMeasurementDataAreSkippedWhenPhenotypeIdNull() throws Exception {
		final MeasurementRow measurementRow = getMeasurementRow();
		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		testMeasurementData.setPhenotypeId(null);
		testSkippingOfNonCreatedPhenotypes(measurementRow, testMeasurementData);
	}
	
	@Test
	public void testNullValueMeasurementDataAreSkippedWhenPhenotypeIdIsZero() throws Exception {
		final MeasurementRow measurementRow = getMeasurementRow();
		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		testMeasurementData.setPhenotypeId(0);
		testSkippingOfNonCreatedPhenotypes(measurementRow, testMeasurementData);
	}

	
	private void testSkippingOfNonCreatedPhenotypes(
			final MeasurementRow measurementRow,
			final MeasurementData testMeasurementData) {
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));
		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		// We have a lot of any matchers because we are verifying that the method is never called.
		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(0)).saveOrUpdate(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyString(),
				(Phenotype) Matchers.anyObject(), Matchers.anyInt());
	}


	@Test
	public void makeSureCorrectHibernateFlushTypeIsUsed() throws Exception {
		final MeasurementRow measurementRow = getMeasurementRow();
		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));
		Mockito.when(this.mockHibernateSessiong.getFlushMode()).thenReturn(FlushMode.AUTO);
		measurements.saveMeasurements(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockHibernateSessiong).setFlushMode(FlushMode.MANUAL);
		Mockito.verify(this.mockHibernateSessiong).flush();
		Mockito.verify(this.mockHibernateSessiong).setFlushMode(FlushMode.AUTO);

	}

	@Test
	public void testMeasurementDataAreSavedAsPhenotypes() throws Exception {
		this.testSavingMeasurements("Normal Value", false);
	}

	@Test
	public void testCategoricalMeasurementDataAreSavedAsPhenotypes() throws Exception {
		this.testSavingMeasurements("Categorical Value", true);
	}
	
	/**
	 * Simple test to ensure that the Phenotype id is set into the measurement data so that the UI does not recreate data. The test does not
	 * do any logical validation but just ensures that the measurement data is updated with a phenotype id when created. Without this saving
	 * an updating of measurement data is broken.
	 */
	@Test
	public void testPhenotypeIdSetOnSave() {
		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
		final MeasurementData testMeasurementData = Mockito.mock(MeasurementData.class);
		final MeasurementRow measurementRow = getMeasurementRow();
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));
		
		// Set up measurement data so that it actually tries to save something.
		Mockito.when(testMeasurementData.isEditable()).thenReturn(true);
		Mockito.when(testMeasurementData.getValue()).thenReturn("Test Data");
		Mockito.when(testMeasurementData.getMeasurementVariable()).thenReturn(Mockito.mock(MeasurementVariable.class));
		
		int testPhenotypeId = 245;
		Mockito.when(testMeasurementData.getPhenotypeId()).thenReturn(testPhenotypeId);

		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(testMeasurementData).setPhenotypeId(245);
	}

	private void testSavingMeasurements(final String value, final boolean isCategorical) {

		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);

		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		if (isCategorical) {
			testMeasurementData.setcValueId(value);
		} else {
			testMeasurementData.setValue(value);
		}

		final MeasurementRow measurementRow = getMeasurementRow();
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));

		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(1)).saveOrUpdate(Matchers.eq(measurementRow.getExperimentId()),
				Matchers.eq(MeasurementsTest.TEST_TERM_ID), Matchers.eq(value), (Phenotype) Matchers.anyObject(),
				Matchers.eq(MeasurementsTest.TEST_DATA_TYPE_ID));
	}
	
	private MeasurementRow getMeasurementRow() {
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		return measurementRow;
	}

	private MeasurementData getTestMeasurementData() {
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setAccepted(true);

		measurementData.setDataType("1");
		measurementData.setEditable(true);
		measurementData.setLabel("Plant Height");
		final MeasurementVariable measurementVariable =
				new MeasurementVariable(MeasurementsTest.TEST_TERM_ID, "Variable Name", "Variable Description", "Variable Scale",
						"Variable Method", "Variable Property", "1", "Variable Value", "Variable Lable");
		measurementVariable.setDataTypeId(MeasurementsTest.TEST_DATA_TYPE_ID);
		measurementData.setMeasurementVariable(measurementVariable);
		measurementData.setVariable(new Variable());
		return measurementData;
	}

}
