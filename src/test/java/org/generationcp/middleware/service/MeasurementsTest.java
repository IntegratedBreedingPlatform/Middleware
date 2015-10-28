
package org.generationcp.middleware.service;

import java.util.Collections;

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

public class MeasurementsTest {

	private static final int TEST_DATA_TYPE_ID = 200;

	private static final int TEST_TERM_ID = 100;

	private Session mockHibernateSessiong;

	private PhenotypeSaver mockPhenotypeSaver;

	@Before
	public void setup() {
		this.mockPhenotypeSaver = Mockito.mock(PhenotypeSaver.class);
		this.mockHibernateSessiong = Mockito.mock(Session.class);
	}

	@Test()
	public void validateNoramalValueMapping() throws Exception {
		final MeasurementData measurementData = this.getTestMeasurementData();
		measurementData.setValue("Test Value");

		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
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

		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
		final Phenotype phenotypeFromMeasurement = measurements.createPhenotypeFromMeasurement(measurementData);

		Assert.assertEquals("Phenotype value is mapped incorrectly", measurementData.getcValueId(), phenotypeFromMeasurement.getcValueId()
				.toString());
	}

	@Test
	public void testUneditiableMeasurementDataAreSkipped() throws Exception {
		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		testMeasurementData.setEditable(false);
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));
		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(0)).saveOrUpdate(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyString(),
				(Phenotype) Matchers.anyObject(), Matchers.anyInt());
	}

	@Test
	public void testNullValueMeasurementDataAreSkipped() throws Exception {
		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));
		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));
		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(0)).saveOrUpdate(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyString(),
				(Phenotype) Matchers.anyObject(), Matchers.anyInt());
	}

	@Test
	public void makeSureCorrectHibernateFlushTypeIsUsed() throws Exception {
		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);
		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
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

	private void testSavingMeasurements(final String value, final boolean isCategorical) {

		final Measurements measurements = new Measurements(this.mockHibernateSessiong, this.mockPhenotypeSaver);

		final MeasurementData testMeasurementData = this.getTestMeasurementData();
		if (isCategorical) {
			testMeasurementData.setcValueId(value);
		} else {
			testMeasurementData.setValue(value);
		}

		final MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setExperimentId(1);
		measurementRow.setDataList(Collections.<MeasurementData>singletonList(testMeasurementData));

		measurements.saveMeasurementData(Collections.<MeasurementRow>singletonList(measurementRow));

		Mockito.verify(this.mockPhenotypeSaver, Mockito.times(1)).saveOrUpdate(Matchers.eq(measurementRow.getExperimentId()),
				Matchers.eq(MeasurementsTest.TEST_TERM_ID), Matchers.eq(value), (Phenotype) Matchers.anyObject(),
				Matchers.eq(MeasurementsTest.TEST_DATA_TYPE_ID));
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
		measurementData.setPhenotypeId(123);
		measurementData.setVariable(new Variable());
		return measurementData;
	}

}
