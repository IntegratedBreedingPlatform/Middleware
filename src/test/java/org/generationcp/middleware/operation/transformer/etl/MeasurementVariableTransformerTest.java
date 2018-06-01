
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeasurementVariableTransformerTest extends IntegrationTestBase {

	private MeasurementVariableTransformer transformer;
	private StandardVariableBuilder standardVariableBuilder;
	private static final int SITE_SOIL_PH = 8270;
	private static final int CRUST = 20310;
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current crop database in use
		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);
	}

	@Before
	public void setUp() throws Exception {
		this.transformer = new MeasurementVariableTransformer(this.sessionProvder);
		this.standardVariableBuilder = new StandardVariableBuilder(this.sessionProvder);
	}

	@Test
	public void testTransform_NullList() throws Exception {
		final VariableTypeList varTypeList = null;
		final List<MeasurementVariable> measurementVariables = this.transformer.transform(varTypeList, true);
		Assert.assertTrue("Measurement variable list should be empty", measurementVariables.isEmpty());
	}

	@Test
	public void testTransform_EmptyList() throws Exception {
		final VariableTypeList varTypeList = new VariableTypeList();
		final List<MeasurementVariable> measurementVariables = this.transformer.transform(varTypeList, true);
		Assert.assertTrue("Measurement variable list should be empty", measurementVariables.isEmpty());
	}

	@Test
	public void testTransform_FactorList() throws Exception {
		final boolean isFactor = true;
		final boolean isInDataset = false;
		final VariableTypeList varTypeList = this.createFactorVariableTypeList();
		final List<MeasurementVariable> measurementVariables = this.transformer.transform(varTypeList, isFactor);
		Assert.assertFalse("Measurement variable list should not be empty", measurementVariables.isEmpty());
		int index = 0;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			Assert.assertTrue("Measurement variable should be a factor", measurementVariable.isFactor());
			final StandardVariable stdVariable = this.getStandardVariable(measurementVariable.getTermId());
			final DMSVariableType variableType = this.transformMeasurementVariable(measurementVariable, stdVariable);
			this.validateMeasurementVariable(measurementVariable, variableType, isInDataset);
			final VariableType expectedVariableType = varTypeList.getVariableTypes().get(index).getVariableType();
			Assert.assertEquals("Variable type must be " + expectedVariableType, expectedVariableType,
					measurementVariable.getVariableType());
			index++;
		}
	}

	public void validateMeasurementVariable(final MeasurementVariable measurementVariable, final DMSVariableType variableType,
			final boolean isInDataset) {
		final StandardVariable stdVariable = variableType.getStandardVariable();
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

		final String label = this.getLabel(stdVariable.getPhenotypicType(), isInDataset);
		Assert.assertEquals("Label should be " + label, label, measurementVariable.getLabel());
		final List<ValueReference> possibleValues = this.getPossibleValues(stdVariable.getEnumerations());
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

	private String getLabel(final PhenotypicType role, final boolean isInDataset) {
		if (isInDataset) {
			return PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0);
		}

		if (role == null) {
			return "";
		}

		return role.getLabelList().get(0);
	}

	private VariableTypeList createFactorVariableTypeList() throws MiddlewareException {
		final VariableTypeList varTypeList = new VariableTypeList();
		final StandardVariable trialInstance = this.getStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final StandardVariable entryType = this.getStandardVariable(TermId.CHECK.getId());
		final StandardVariable gid = this.getStandardVariable(TermId.GID.getId());
		final StandardVariable entryNo = this.getStandardVariable(TermId.ENTRY_NO.getId());
		final StandardVariable repNo = this.getStandardVariable(TermId.REP_NO.getId());
		varTypeList.add(new DMSVariableType("TRIAL_INSTANCE", "Trial instance - enumerated (number)", trialInstance, 1));
		varTypeList.add(new DMSVariableType("ENTRY_TYPE", "Entry type (test/check)- assigned (type)", entryType, 2));
		varTypeList.add(new DMSVariableType("GID", "Germplasm identifier - assigned (DBID)", gid, 3));
		varTypeList.add(new DMSVariableType("ENTRY_NO", "Germplasm entry - enumerated (number)", entryNo, 4));
		varTypeList.add(new DMSVariableType("REP_NO", "Replication - assigned (number)", repNo, 5));
		return varTypeList;
	}

	private StandardVariable getStandardVariable(final int id) throws MiddlewareException {
		return this.standardVariableBuilder.create(id, "1234567");
	}

	private DMSVariableType transformMeasurementVariable(final MeasurementVariable measurementVariable,
			final StandardVariable standardVariable) {
		return new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, 0);
	}

	private List<ValueReference> getPossibleValues(final List<Enumeration> enumerations) {
		final List<ValueReference> list = new ArrayList<ValueReference>();
		if (enumerations != null) {
			for (final Enumeration enumeration : enumerations) {
				list.add(new ValueReference(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
			}
		}
		return list;
	}

	@Test
	public void testTransform_VariateList() throws Exception {
		final boolean isFactor = false;
		final boolean isInDataset = false;
		final VariableTypeList varTypeList = this.createVariateVariableTypeList();
		final List<MeasurementVariable> measurementVariables = this.transformer.transform(varTypeList, isFactor);
		Assert.assertFalse("Measurement variable list should not be empty", measurementVariables.isEmpty());
		int index = 0;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			Assert.assertFalse("Measurement variable should not be a factor", measurementVariable.isFactor());
			final StandardVariable stdVariable = this.getStandardVariable(measurementVariable.getTermId());
			final DMSVariableType variableType = this.transformMeasurementVariable(measurementVariable, stdVariable);
			this.validateMeasurementVariable(measurementVariable, variableType, isInDataset);
			final VariableType expectedVariableType = varTypeList.getVariableTypes().get(index).getVariableType();
			Assert.assertEquals("Variable type must be " + expectedVariableType, expectedVariableType,
					measurementVariable.getVariableType());
			index++;
		}
	}

	@Test
	public void testTransform_TrialConstantList() throws Exception {
		final boolean isFactor = false;
		final boolean isInDataset = true;
		final VariableTypeList varTypeList = this.createTrialConstantVariableTypeList();
		final List<MeasurementVariable> measurementVariables = this.transformer.transform(varTypeList, isFactor, isInDataset);
		Assert.assertFalse("Measurement variable list should not be empty", measurementVariables.isEmpty());
		int index = 0;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			Assert.assertFalse("Measurement variable should not be a factor", measurementVariable.isFactor());
			final StandardVariable stdVariable = this.getStandardVariable(measurementVariable.getTermId());
			final DMSVariableType variableType = this.transformMeasurementVariable(measurementVariable, stdVariable);
			this.validateMeasurementVariable(measurementVariable, variableType, isInDataset);
			final VariableType expectedVariableType = varTypeList.getVariableTypes().get(index).getVariableType();
			Assert.assertEquals("Variable type must be " + expectedVariableType, expectedVariableType,
					measurementVariable.getVariableType());
			index++;
		}
	}

	private VariableTypeList createVariateVariableTypeList() throws MiddlewareException {
		final VariableTypeList varTypeList = new VariableTypeList();
		final StandardVariable asi = this.getStandardVariable(20308);
		varTypeList.add(new DMSVariableType("ASI", "Determined by (i) measuring the number of days after "
				+ "planting until 50 % of the plants shed pollen (anthesis date, AD) "
				+ "and show silks (silking date, SD), respectively, " + "and (ii) calculating: ASI = SD - AD.", asi, 1));
		return varTypeList;
	}

	private VariableTypeList createTrialConstantVariableTypeList() throws MiddlewareException {
		final VariableTypeList varTypeList = new VariableTypeList();
		final StandardVariable siteSoilPh = this.getStandardVariable(MeasurementVariableTransformerTest.SITE_SOIL_PH);
		final StandardVariable crust = this.getStandardVariable(MeasurementVariableTransformerTest.CRUST);
		varTypeList.add(new DMSVariableType("SITE_SOIL_PH", "Soil acidity - ph meter (pH)", siteSoilPh, 1));
		varTypeList.add(new DMSVariableType("CRUST", "Score for the severity of common rust, "
				+ "(In highlands and mid altitude, Puccinia sorghi) " + "symptoms rated on a scale from 1 (= clean, no infection) to "
				+ "5 (= severely diseased).", crust, 2));
		return varTypeList;
	}

}
