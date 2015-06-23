
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeasurementVariableTransformerTest extends MiddlewareIntegrationTest {

	private static MeasurementVariableTransformer transformer;
	private static StandardVariableBuilder standardVariableBuilder;
	private static final int SITE_SOIL_PH = 8270;
	private static final int CRUST = 20310;

	@BeforeClass
	public static void setUp() throws Exception {
		HibernateSessionProvider sessionProvider =
				new HibernateSessionPerThreadProvider(MiddlewareIntegrationTest.sessionUtil.getSessionFactory());
		MeasurementVariableTransformerTest.transformer = new MeasurementVariableTransformer(sessionProvider);
		MeasurementVariableTransformerTest.standardVariableBuilder = new StandardVariableBuilder(sessionProvider);
	}

	@Test
	public void testTransform_NullList() throws Exception {
		VariableTypeList varTypeList = null;
		List<MeasurementVariable> measurementVariables = MeasurementVariableTransformerTest.transformer.transform(varTypeList, true);
		Assert.assertTrue("Measurement variable list should be empty", measurementVariables.isEmpty());
	}

	@Test
	public void testTransform_EmptyList() throws Exception {
		VariableTypeList varTypeList = new VariableTypeList();
		List<MeasurementVariable> measurementVariables = MeasurementVariableTransformerTest.transformer.transform(varTypeList, true);
		Assert.assertTrue("Measurement variable list should be empty", measurementVariables.isEmpty());
	}

	@Test
	public void testTransform_FactorList() throws Exception {
		boolean isFactor = true;
		boolean isInTrialDataset = false;
		VariableTypeList varTypeList = this.createFactorVariableTypeList();
		List<MeasurementVariable> measurementVariables = MeasurementVariableTransformerTest.transformer.transform(varTypeList, isFactor);
		Assert.assertFalse("Measurement variable list should not be empty", measurementVariables.isEmpty());
		for (MeasurementVariable measurementVariable : measurementVariables) {
			Assert.assertTrue("Measurement variable should be a factor", measurementVariable.isFactor());
			StandardVariable stdVariable = this.getStandardVariable(measurementVariable.getTermId());
			VariableType variableType = this.transformMeasurementVariable(measurementVariable, stdVariable);
			this.validateMeasurementVariable(measurementVariable, variableType, isInTrialDataset);
		}
	}

	public void validateMeasurementVariable(MeasurementVariable measurementVariable, VariableType variableType, boolean isInTrialDataset) {
		StandardVariable stdVariable = variableType.getStandardVariable();
		Assert.assertEquals("Name should be " + variableType.getLocalName(), variableType.getLocalName(), measurementVariable.getName());
		Assert.assertEquals("Description should be " + stdVariable.getDescription(), stdVariable.getDescription(),
				measurementVariable.getDescription());
		Assert.assertEquals("Scale should be " + stdVariable.getScale().getName(), stdVariable.getScale().getName(),
				measurementVariable.getScale());
		Assert.assertEquals("Method should be " + stdVariable.getMethod().getName(), stdVariable.getMethod().getName(),
				measurementVariable.getMethod());
		Assert.assertEquals("Property should be " + stdVariable.getProperty().getName(), stdVariable.getProperty().getName(),
				measurementVariable.getProperty());
		Assert.assertEquals("Data Type should be " + stdVariable.getDataType().getName(), stdVariable.getDataType().getName(),
				measurementVariable.getDataType());

		String label = this.getLabel(stdVariable.getStoredIn().getId(), isInTrialDataset);
		Assert.assertEquals("Label should be " + label, label, measurementVariable.getLabel());
		List<ValueReference> possibleValues = this.getPossibleValues(stdVariable.getEnumerations());
		Assert.assertEquals("Possible values should be " + possibleValues, possibleValues, measurementVariable.getPossibleValues());
		if (stdVariable.getConstraints() != null) {
			measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
			measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
			Assert.assertEquals("Min Range should be " + stdVariable.getConstraints().getMinValue(), stdVariable.getConstraints()
					.getMinValue(), measurementVariable.getMinRange());
			Assert.assertEquals("Max Range should be " + stdVariable.getConstraints().getMaxValue(), stdVariable.getConstraints()
					.getMaxValue(), measurementVariable.getMaxRange());
		}
		if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
			Assert.assertEquals("Treatment label should be " + variableType.getTreatmentLabel(), variableType.getTreatmentLabel(),
					measurementVariable.getTreatmentLabel());
		}
	}

	private String getLabel(int storedIn, boolean isInTrialDataset) {
		if (isInTrialDataset) {
			return PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0);
		}
		return PhenotypicType.getPhenotypicTypeById(storedIn).getLabelList().get(0);
	}

	private VariableTypeList createFactorVariableTypeList() throws MiddlewareException {
		VariableTypeList varTypeList = new VariableTypeList();
		StandardVariable trialInstance = this.getStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StandardVariable entryType = this.getStandardVariable(TermId.CHECK.getId());
		StandardVariable gid = this.getStandardVariable(TermId.GID.getId());
		StandardVariable entryNo = this.getStandardVariable(TermId.ENTRY_NO.getId());
		StandardVariable repNo = this.getStandardVariable(TermId.REP_NO.getId());
		varTypeList.add(new VariableType("TRIAL_INSTANCE", "Trial instance - enumerated (number)", trialInstance, 1));
		varTypeList.add(new VariableType("ENTRY_TYPE", "Entry type (test/check)- assigned (type)", entryType, 2));
		varTypeList.add(new VariableType("GID", "Germplasm identifier - assigned (DBID)", gid, 3));
		varTypeList.add(new VariableType("ENTRY_NO", "Germplasm entry - enumerated (number)", entryNo, 4));
		varTypeList.add(new VariableType("REP_NO", "Replication - assigned (number)", repNo, 5));
		return varTypeList;
	}

	private StandardVariable getStandardVariable(int id) throws MiddlewareException {
		return MeasurementVariableTransformerTest.standardVariableBuilder.create(id,"1234567");
	}

	private VariableType transformMeasurementVariable(MeasurementVariable measurementVariable, StandardVariable standardVariable) {
		return new VariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, 0);
	}

	private List<ValueReference> getPossibleValues(List<Enumeration> enumerations) {
		List<ValueReference> list = new ArrayList<ValueReference>();
		if (enumerations != null) {
			for (Enumeration enumeration : enumerations) {
				list.add(new ValueReference(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
			}
		}
		return list;
	}

	@Test
	public void testTransform_VariateList() throws Exception {
		boolean isFactor = false;
		boolean isInTrialDataset = false;
		VariableTypeList varTypeList = this.createVariateVariableTypeList();
		List<MeasurementVariable> measurementVariables =
				MeasurementVariableTransformerTest.transformer.transform(varTypeList, isFactor, isInTrialDataset);
		Assert.assertFalse("Measurement variable list should not be empty", measurementVariables.isEmpty());
		for (MeasurementVariable measurementVariable : measurementVariables) {
			Assert.assertFalse("Measurement variable should not be a factor", measurementVariable.isFactor());
			StandardVariable stdVariable = this.getStandardVariable(measurementVariable.getTermId());
			VariableType variableType = this.transformMeasurementVariable(measurementVariable, stdVariable);
			this.validateMeasurementVariable(measurementVariable, variableType, isInTrialDataset);
		}
	}

	@Test
	public void testTransform_TrialConstantList() throws Exception {
		boolean isFactor = false;
		boolean isInTrialDataset = true;
		VariableTypeList varTypeList = this.createTrialConstantVariableTypeList();
		List<MeasurementVariable> measurementVariables =
				MeasurementVariableTransformerTest.transformer.transform(varTypeList, isFactor, isInTrialDataset);
		Assert.assertFalse("Measurement variable list should not be empty", measurementVariables.isEmpty());
		for (MeasurementVariable measurementVariable : measurementVariables) {
			Assert.assertFalse("Measurement variable should not be a factor", measurementVariable.isFactor());
			StandardVariable stdVariable = this.getStandardVariable(measurementVariable.getTermId());
			VariableType variableType = this.transformMeasurementVariable(measurementVariable, stdVariable);
			this.validateMeasurementVariable(measurementVariable, variableType, isInTrialDataset);
		}
	}

	private VariableTypeList createVariateVariableTypeList() throws MiddlewareException {
		VariableTypeList varTypeList = new VariableTypeList();
		StandardVariable asi = this.getStandardVariable(20308);
		varTypeList.add(new VariableType("ASI", "Determined by (i) measuring the number of days after "
				+ "planting until 50 % of the plants shed pollen (anthesis date, AD) "
				+ "and show silks (silking date, SD), respectively, " + "and (ii) calculating: ASI = SD - AD.", asi, 1));
		return varTypeList;
	}

	private VariableTypeList createTrialConstantVariableTypeList() throws MiddlewareException {
		VariableTypeList varTypeList = new VariableTypeList();
		StandardVariable siteSoilPh = this.getStandardVariable(MeasurementVariableTransformerTest.SITE_SOIL_PH);
		StandardVariable crust = this.getStandardVariable(MeasurementVariableTransformerTest.CRUST);
		varTypeList.add(new VariableType("SITE_SOIL_PH", "Soil acidity - ph meter (pH)", siteSoilPh, 1));
		varTypeList.add(new VariableType("CRUST", "Score for the severity of common rust, "
				+ "(In highlands and mid altitude, Puccinia sorghi) " + "symptoms rated on a scale from 1 (= clean, no infection) to "
				+ "5 (= severely diseased).", crust, 2));
		return varTypeList;
	}

}
