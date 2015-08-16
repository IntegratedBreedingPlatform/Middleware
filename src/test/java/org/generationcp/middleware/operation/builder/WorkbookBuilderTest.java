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

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookBuilderTest extends DataManagerIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilderTest.class);

	private static DataImportService dataImportService;

	private static FieldbookService fieldbookService;

	private static WorkbookBuilder workbookBuilder;

	private static MeasurementVariableTransformer measurementVariableTransformer;

	private static StandardVariableBuilder standardVariableBuilder;

	private static VariableTypeListTransformer variableTypeListTransformer;

	private static final int SITE_SOIL_PH = 8270;
	private static final int CRUST = 20310;
	private static final int CUSTOM_VARIATE = 18020;
	
	private static final String PROGRAM_UUID = "12345678";

	@BeforeClass
	public static void setUp() throws Exception {
		HibernateSessionProvider sessionProvider = DataManagerIntegrationTest.managerFactory.getSessionProvider();
		WorkbookBuilderTest.workbookBuilder = new WorkbookBuilder(sessionProvider);
		WorkbookBuilderTest.dataImportService = DataManagerIntegrationTest.managerFactory.getDataImportService();
		WorkbookBuilderTest.fieldbookService = DataManagerIntegrationTest.managerFactory.getFieldbookMiddlewareService();
		WorkbookBuilderTest.measurementVariableTransformer = new MeasurementVariableTransformer(sessionProvider);
		WorkbookBuilderTest.standardVariableBuilder = new StandardVariableBuilder(sessionProvider);
		WorkbookBuilderTest.variableTypeListTransformer = new VariableTypeListTransformer(sessionProvider);
	}

	@Test
	public void testCheckingOfMeasurementDatasetWithError() {
		try {
			WorkbookBuilderTest.workbookBuilder.checkMeasurementDataset(null);
		} catch (MiddlewareQueryException e) {
			WorkbookBuilderTest.LOG.error(e.getMessage(), e);
			Assert.assertEquals("Expected code ", ErrorCode.STUDY_FORMAT_INVALID.getCode(), e.getCode());
		}
	}

	@Test
	public void testCheckingOfMeasurementDatasetWithoutError() {
		boolean hasError = false;
		try {
			WorkbookBuilderTest.workbookBuilder.checkMeasurementDataset(1);
		} catch (MiddlewareQueryException e) {
			WorkbookBuilderTest.LOG.error(e.getMessage(), e);
			Assert.fail("Expected no error but got one");
			hasError = true;
		}
		Assert.assertFalse("Expected no error but got one", hasError);
	}

	@Test
	public void testGetTrialObservationsForNursery() throws MiddlewareException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);

		int id = WorkbookBuilderTest.dataImportService.saveDataset(workbook, null);

		Workbook createdWorkbook = WorkbookBuilderTest.fieldbookService.getNurseryDataSet(id);

		Assert.assertTrue("Expected correct values for constants but did not match with old workbook.",
				this.areConstantsMatch(workbook.getConstants(), createdWorkbook.getConstants()));

	}

	private boolean areConstantsMatch(List<MeasurementVariable> constants, List<MeasurementVariable> constants2) {
		if (constants != null && constants2 != null) {
			for (MeasurementVariable var : constants) {
				if (!this.isMactchInNewConstantList(constants2, var.getTermId(), var.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isMactchInNewConstantList(List<MeasurementVariable> constants, int termId, String value) {
		if (constants != null) {
			for (MeasurementVariable var : constants) {
				if (var.getTermId() == termId) {
					return var.getValue().equals(value);
				}
			}
		}
		return false;
	}

	@Test
	public void testGetTrialObservationsForTrial() throws MiddlewareException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);

		int id = WorkbookBuilderTest.dataImportService.saveDataset(workbook, null);

		Workbook createdWorkbook = WorkbookBuilderTest.fieldbookService.getTrialDataSet(id);

		Assert.assertTrue("Expected correct values for trial observations but did not match with old workbook.",
				this.areConstantsCorrect(createdWorkbook.getConstants(), createdWorkbook.getTrialObservations()));
	}

	private boolean areConstantsCorrect(List<MeasurementVariable> constants, List<MeasurementRow> trialObservations) {
		if (trialObservations != null && constants != null) {
			for (MeasurementRow row : trialObservations) {
				return this.areConstantsInRow(row.getDataList(), constants);
			}
		}
		return false;
	}

	private boolean areConstantsInRow(List<MeasurementData> dataList, List<MeasurementVariable> constants) {
		for (MeasurementVariable var : constants) {
			for (MeasurementData data : dataList) {
				if (data.getMeasurementVariable().getTermId() == var.getTermId() && !data.getValue().equals("1")) {
					return false;
				}
			}
		}
		return true;
	}

	@Test
	public void testPopulateMeasurementData_AllEmptyList() throws MiddlewareQueryException {
		List<MeasurementVariable> measurementVariableList = new ArrayList<MeasurementVariable>();
		VariableList variableList = new VariableList();
		List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		WorkbookBuilderTest.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertTrue("Measurement data should be empty", measurementDataList.isEmpty());
	}

	@Test
	public void testPopulateMeasurementData_EmptyMeasurementVariableList() throws MiddlewareException {
		List<MeasurementVariable> measurementVariableList = new ArrayList<MeasurementVariable>();
		VariableList variableList = this.createVariableList(this.createMeasurementVariableList());
		List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		WorkbookBuilderTest.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertTrue("Measurement data should be empty", measurementDataList.isEmpty());
	}

	@Test
	public void testPopulateMeasurementData_EmptyVariableList() throws MiddlewareException {
		List<MeasurementVariable> measurementVariableList = this.createMeasurementVariableList();
		VariableList variableList = new VariableList();
		List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		WorkbookBuilderTest.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertFalse("Measurement data should not be empty", measurementDataList.isEmpty());
		for (MeasurementData measurementData : measurementDataList) {
			Assert.assertEquals("Measurement data value should be empty", "", measurementData.getValue());
		}
	}

	@Test
	public void testPopulateMeasurementData() throws MiddlewareException {
		List<MeasurementVariable> measurementVariableList = this.createMeasurementVariableList();
		VariableList variableList = this.createVariableList(measurementVariableList);
		List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		WorkbookBuilderTest.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertFalse("Measurement data should not be empty", measurementDataList.isEmpty());
		for (MeasurementData measurementData : measurementDataList) {
			if (TermId.CATEGORICAL_VARIABLE.getId() != measurementData.getMeasurementVariable().getDataTypeId()) {
				Assert.assertNull("Categorical value id should be null", measurementData.getcValueId());
			} else if (WorkbookBuilderTest.CUSTOM_VARIATE == measurementData.getMeasurementVariable().getTermId()) {
				Assert.assertNull("Categorical value id should be null", measurementData.getcValueId());
			} else {
				Assert.assertEquals("Categorical value id should equal to " + measurementData.getValue(), measurementData.getValue(),
						measurementData.getcValueId());
			}
		}
	}

	private VariableList createVariableList(List<MeasurementVariable> measurementVariableList) throws MiddlewareException {
		VariableList variableList = new VariableList();
		int count = 0;
		for (MeasurementVariable measurementVariable : measurementVariableList) {
			count++;
			String value = Integer.toString(count);
			if (TermId.CHARACTER_VARIABLE.getId() == measurementVariable.getDataTypeId().intValue()) {
				value = "CODE_" + value;
			}
			Variable variable = this.createVariable(measurementVariable, value);
			variableList.add(variable);
			if (count == measurementVariableList.size()) {
				variable.setCustomValue(true);
			}
		}
		return variableList;
	}

	private Variable createVariable(MeasurementVariable measurementVariable, String value) throws MiddlewareException {
		Variable variable = new Variable();
		DMSVariableType variableType = this.createVariableType(measurementVariable);
		variable.setVariableType(variableType);
		variable.setValue(value);
		return variable;
	}

	private DMSVariableType createVariableType(MeasurementVariable measurementVariable) throws MiddlewareException {
		DMSVariableType variableType =
				this.transformMeasurementVariable(measurementVariable, this.getStandardVariable(measurementVariable.getTermId()));
		return variableType;
	}

	private List<MeasurementVariable> createMeasurementVariableList() throws MiddlewareException {
		List<MeasurementVariable> measurementVariableList = new ArrayList<MeasurementVariable>();
		measurementVariableList.add(this.getMeasurementVariable(TermId.ENTRY_CODE.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.ENTRY_NO.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.GID.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.REP_NO.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.PLOT_NO.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(WorkbookBuilderTest.SITE_SOIL_PH, false));
		measurementVariableList.add(this.getMeasurementVariable(WorkbookBuilderTest.CRUST, false));
		measurementVariableList.add(this.getMeasurementVariable(WorkbookBuilderTest.CUSTOM_VARIATE, false));
		return measurementVariableList;
	}

	private MeasurementVariable getMeasurementVariable(int termId, boolean isFactor) throws MiddlewareException {
		return WorkbookBuilderTest.measurementVariableTransformer.transform(this.getStandardVariable(termId), isFactor);
	}

	private StandardVariable getStandardVariable(int id) throws MiddlewareException {
		return WorkbookBuilderTest.standardVariableBuilder.create(id,"1234567");
	}

	private DMSVariableType transformMeasurementVariable(MeasurementVariable measurementVariable, StandardVariable standardVariable) {
		return new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, 0);
	}

	@Test
	public void testRemoveTrialDatasetVariables() throws MiddlewareException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
		// add trial instance (also added in conditions)
		workbook.getFactors().add(WorkbookTest.createTrialInstanceMeasurementVariable(1));
		VariableTypeList factorsVariableTypeList = WorkbookBuilderTest.variableTypeListTransformer.transform(workbook.getFactors(), false,
				PROGRAM_UUID);
		VariableTypeList conditionsVariableTypeList =
				WorkbookBuilderTest.variableTypeListTransformer.transform(workbook.getConditions(), false, PROGRAM_UUID);
		VariableTypeList constantsVariableTypeList =
				WorkbookBuilderTest.variableTypeListTransformer.transform(workbook.getConstants(), false, PROGRAM_UUID);
		VariableList conditions = this.transformMeasurementVariablesToVariableList(workbook.getConditions(), conditionsVariableTypeList);
		VariableList constants = this.transformMeasurementVariablesToVariableList(workbook.getConstants(), constantsVariableTypeList);
		// find the trial instance variable before removing it as a factor
		DMSVariableType trialInstance = factorsVariableTypeList.findById(TermId.TRIAL_INSTANCE_FACTOR.getId());
		VariableList toBeDeleted = new VariableList();
		toBeDeleted.addAll(conditions);
		toBeDeleted.addAll(constants);
		// call the method to test: remove trial instance
		VariableTypeList finalFactors =
				WorkbookBuilderTest.workbookBuilder.removeTrialDatasetVariables(factorsVariableTypeList, toBeDeleted);
		// verify if the trial instance is no longer found in the final factors
		Assert.assertFalse(finalFactors.getVariableTypes().contains(trialInstance));

	}
	
	@Test
	public void testSetMeasurementVarRoles(){
		List<MeasurementVariable> measurementVariableLists = new ArrayList<MeasurementVariable>();
		MeasurementVariable measurementVar = new MeasurementVariable();		
		measurementVariableLists.add(measurementVar);
		WorkbookBuilderTest.workbookBuilder.setMeasurementVarRoles(measurementVariableLists, false, true);
		for(MeasurementVariable var : measurementVariableLists){
			Assert.assertEquals("Should have a phenotype role of variate since its not a factor", var.getRole(), PhenotypicType.VARIATE);
		}
		
		WorkbookBuilderTest.workbookBuilder.setMeasurementVarRoles(measurementVariableLists, true, true);
		for(MeasurementVariable var : measurementVariableLists){
			Assert.assertEquals("Should have a phenotype role of STUDY", var.getRole(), PhenotypicType.STUDY);
		}
		
		WorkbookBuilderTest.workbookBuilder.setMeasurementVarRoles(measurementVariableLists, true, false);
		for(MeasurementVariable var : measurementVariableLists){
			Assert.assertEquals("Should have a phenotype role of Trial Environment", var.getRole(), PhenotypicType.TRIAL_ENVIRONMENT);
		}
	}

	// derived from VariableListTransformer.transformTrialEnvironment (but with no specific role to filter)
	private VariableList transformMeasurementVariablesToVariableList(List<MeasurementVariable> mVarList, VariableTypeList variableTypeList) {
		VariableList variableList = new VariableList();
		if (mVarList != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mVarList.size() == variableTypeList.getVariableTypes().size()) {

				List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();
				for (int i = 0, l = mVarList.size(); i < l; i++) {
					DMSVariableType varTypeFinal = null;
					String value = mVarList.get(i).getValue();
					for (DMSVariableType varType : varTypes) {
						if (mVarList.get(i).getTermId() == varType.getId()) {
							varTypeFinal = varType;
						}
					}
					variableList.add(new Variable(varTypeFinal, value));
				}
			}
		}

		return variableList;
	}

}
