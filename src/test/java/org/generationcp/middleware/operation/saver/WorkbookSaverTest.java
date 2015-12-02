/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.generationcp.middleware.utils.test.VariableTypeListDataUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class WorkbookSaverTest extends TestOutputFormatter {

	private static WorkbookSaver workbookSaver;

	@BeforeClass
	public static void setUp() {
		WorkbookSaverTest.workbookSaver = new WorkbookSaver(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndWOTrialFactorWithEnvironmentAndVariates() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(true);

		VariableTypeList plotVariables = WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected an aditional entry for trial instance but found none.", effectVariables.size() + 1,
				plotVariables.size());
		Assert.assertFalse("Expected non trial environment and non constant variables but found at least one.",
				this.areTrialAndConstantsInList(plotVariables, effectVariables));
	}

	private boolean areTrialAndConstantsInList(VariableTypeList plotVariables, VariableTypeList effectVariables) {
		if (plotVariables != null) {
			for (DMSVariableType var : plotVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() != TermId.TRIAL_INSTANCE_FACTOR.getId()
						&& (PhenotypicType.TRIAL_ENVIRONMENT == 
								var.getRole() || PhenotypicType.VARIATE == var.getRole()
								&& !this.isInOriginalPlotDataset(var.getStandardVariable().getId(), effectVariables))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isInOriginalPlotDataset(int id, VariableTypeList effectVariables) {
		if (effectVariables != null) {
			for (DMSVariableType var : effectVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() == id) {
					return true;
				}
			}
		}
		return false;
	}

	@Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndWOTrialFactorWOEnvironmentAndVariates() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(false);

		VariableTypeList plotVariables = WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected an aditional entry for trial instance but found none.", effectVariables.size() + 1,
				plotVariables.size());
		Assert.assertFalse("Expected non trial environment and non constant variables but found at least one.",
				this.areTrialAndConstantsInList(plotVariables, effectVariables));
	}

	@Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndTrialFactor() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
		VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(false);

		VariableTypeList plotVariables = WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testPropagationOfTrialFactorsWOTrialVariablesWithTrialFactor() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
		VariableTypeList trialVariables = null;

		VariableTypeList plotVariables = WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testPropagationOfTrialFactorsWOTrialVariablesAndTrialFactor() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		VariableTypeList trialVariables = null;

		VariableTypeList plotVariables = WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testSaveVariables() throws Exception {

		final String programUUID = "abc";
		final String studyName = "nursery_1" + new Random().nextInt(10000);

		Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(2, StudyType.N, studyName, 1, true);
		WorkbookSaver workbookSaver = Mockito.mock(WorkbookSaver.class, Mockito.CALLS_REAL_METHODS);

		VariableTypeListTransformer transformer = Mockito.mock(VariableTypeListTransformer.class); //new VariableTypeListTransformer(Mockito.mock(HibernateSessionProvider.class));

		VariableTypeList trialConditionsVariableTypeList = createVariableTypeList(workbook.getTrialConditions(), 1);
		Mockito.doReturn(trialConditionsVariableTypeList).when(transformer).transform(workbook.getTrialConditions(), programUUID);

		VariableTypeList nonTrialFactorsVariableTypeList = createVariableTypeList(workbook.getNonTrialFactors(), 1);
		Mockito.doReturn(nonTrialFactorsVariableTypeList).when(transformer).transform(workbook.getNonTrialFactors(), programUUID);

		VariableTypeList trialFactorsVariableTypeList= createVariableTypeList(workbook.getTrialFactors(), 1);
		Mockito.doReturn(trialFactorsVariableTypeList).when(transformer).transform(workbook.getTrialFactors(),
				workbook.getTrialConditions().size() + 1, programUUID);

		VariableTypeList trialConstantsVariableTypeList= createVariableTypeList(workbook.getTrialConstants(), 1);
		Mockito.doReturn(trialConstantsVariableTypeList).when(transformer).transform(workbook.getTrialConstants(),
				workbook.getTrialConditions().size() + workbook.getTrialFactors().size() + 1, programUUID);

		VariableTypeList variatesVariableTypeList= createVariableTypeList(workbook.getVariates(), 1);
		Mockito.doReturn(variatesVariableTypeList).when(transformer).transform(workbook.getVariates(), 10, programUUID);

		Mockito.doReturn(transformer).when(workbookSaver).getVariableTypeListTransformer();

		Map variableMap = workbookSaver.saveVariables(workbook, programUUID);

		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");

		final List<String> trialHeaders = headerMap.get("trialHeaders");

		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		final VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		final VariableTypeList effectVariables = variableTypeMap.get("effectVariables");

		final List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		Assert.assertNotEquals(0, trialHeaders.size());

		Assert.assertNotEquals(0, trialMV.size());
		Assert.assertNotEquals(0, effectMV.size());

		Assert.assertNotEquals(0, effectVariables.getVariableTypes().size());
		Assert.assertNotEquals(0, trialVariables.getVariableTypes().size());
		Assert.assertNotEquals(0, trialVariableTypeList.getVariableTypes().size());
	}

	private StandardVariable transformMeasurementVariableToVariable(MeasurementVariable mv){
		StandardVariable standardVariable = new StandardVariable();

		standardVariable.setId(mv.getTermId());
		standardVariable.setName(mv.getName());
		standardVariable.setDescription(mv.getDescription());

		Integer methodId = new Random().nextInt(10000);
		Integer propertyId = new Random().nextInt(10000);
		Integer scaleId = new Random().nextInt(10000);

		standardVariable.setMethod(new Method(new Term(methodId, mv.getMethod(), "Method Description")));
		standardVariable.setProperty(new Property(new Term(propertyId, mv.getProperty(), "Property Description")));
		standardVariable.setScale(new Scale(new Term(scaleId, mv.getScale(), "Scale Description")));
		standardVariable.setDataType(new Term(DataType.NUMERIC_VARIABLE.getId(), DataType.NUMERIC_VARIABLE.getName(), "Data Type Description"));
		standardVariable.setIsA(new Term(new Random().nextInt(1000), "IsA", "IsA Description"));
		standardVariable.setPhenotypicType(mv.getRole());
		standardVariable.setCropOntologyId("CO:100");
		standardVariable.setVariableTypes(new HashSet<>(
				new ArrayList<>(Collections.singletonList(OntologyDataHelper.mapFromPhenotype(mv.getRole(), mv.getProperty())))));

		return standardVariable;
	}

	private DMSVariableType transformToDMSVariableType(MeasurementVariable measurementVariable, int rank){
		StandardVariable standardVariable = transformMeasurementVariableToVariable(measurementVariable);

		DMSVariableType dmsVariableType = new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, rank++);
		dmsVariableType.setTreatmentLabel(measurementVariable.getTreatmentLabel());
		return dmsVariableType;
	}

	private VariableTypeList createVariableTypeList(List<MeasurementVariable> measurementVariables, int rank){
		VariableTypeList variableTypeList = new VariableTypeList();

		for(MeasurementVariable mv : measurementVariables){
			variableTypeList.add(transformToDMSVariableType(mv, rank));
		}
		return variableTypeList;
	}
}
