/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.transformer.etl.VariableListTransformer;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class TestVariableListTransformer extends TestOutputFormatter {

    private static VariableListTransformer transformer;
	
	@BeforeClass
	public static void setUp() throws Exception {
		transformer = new VariableListTransformer(Mockito.mock(HibernateSessionProvider.class), Mockito.mock(HibernateSessionProvider.class));
	}
	
	@Test
	public void testTransformStock() throws Exception {
		Debug.println(INDENT, "testTransformStock");
		VariableTypeList variableTypeList = createVariableTypeListTestData();
		MeasurementRow measurementRow = createMeasurementRowTestData(variableTypeList);
		List<String> trialHeaders = getTrialHeaders(variableTypeList);
		Debug.println(INDENT, "Input MeasurmentRow");
		measurementRow.print(INDENT);
		Debug.println(INDENT, "Input VariableTypeList");
		variableTypeList.print(INDENT);
		
		VariableList stocks = transformer.transformStock(measurementRow, variableTypeList, trialHeaders);
		
		Assert.assertNotNull(stocks);
		VariableList result = getStockResult(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;
		Debug.println(INDENT, "Output:");
		for (Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(INDENT);
			i++;
		}
	}
	
	@Test
	public void transformTrialEnvironment() throws Exception {
		Debug.println(INDENT, "transformTrialEnvironment");
		VariableTypeList variableTypeList = createVariableTypeListTestData();
		MeasurementRow measurementRow = createMeasurementRowTestData(variableTypeList);
		List<String> trialHeaders = getTrialHeaders(variableTypeList);
		Debug.println(INDENT, "Input MeasurmentRow");
		measurementRow.print(INDENT);
		Debug.println(INDENT, "Input VariableTypeList");
		variableTypeList.print(INDENT);
		
		VariableList stocks = transformer.transformTrialEnvironment(measurementRow, variableTypeList, trialHeaders);
		
		Assert.assertNotNull(stocks);
		VariableList result = getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;
		Debug.println(INDENT, "Output:");
		for (Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(INDENT);
			i++;
		}
	}
	
	@Test
	public void transformTrialEnvironment2() throws Exception {
		Debug.println(INDENT, "testTransformTrialEnvironment 2");
		List<MeasurementVariable> mVarList = createMeasurementVariableListTestData();
		VariableTypeList variableTypeList = createVariableTypeListTestData();
		
		Debug.println(INDENT, "Input MeasurementVariables");
		mVarList.toString();
		Debug.println(INDENT, "Input VariableTypeList");
		variableTypeList.print(INDENT);
		
		VariableList stocks = transformer.transformTrialEnvironment(mVarList, variableTypeList);
		Assert.assertNotNull(stocks);
		
		VariableList result = getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		
		Debug.println(INDENT, stocks.toString());
		
		int i = 0;
		Debug.println(INDENT, "Output:");
		for (Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(INDENT);
			i++;
		}
	}
	
	@Test
	public void testTransformStudyDetails() throws Exception {
		StudyDetails studyDetails = createTestStudyDetails();
		Debug.println(INDENT, "Input studyDetails");
		studyDetails.print(INDENT);
		
		VariableList variables = transformer.transformStudyDetails(studyDetails, null);
		
		Assert.assertNotNull(variables);
		
		for (Variable v : variables.getVariables()) {
			Assert.assertEquals(v.getValue(),getStudyDetailValue(v.getVariableType().getRank(),studyDetails));
			v.print(INDENT);
		}
	}
	
	private String getStudyDetailValue(int rank, StudyDetails studyDetails) {
		switch(rank) {
			case 1: return studyDetails.getStudyName();	
			case 2: return studyDetails.getTitle();	
			/*case 3: return studyDetails.getPmKey();*/
			case 4: return studyDetails.getObjective();
			case 5: return Integer.toString(studyDetails.getStudyType().getId());
			case 6: return studyDetails.getStartDate();
			case 7: return studyDetails.getEndDate();
		}
		return null;
	}
	
	private MeasurementRow createMeasurementRowTestData(VariableTypeList varTypeList) {
		MeasurementRow row = new MeasurementRow();
		row.setDataList(new ArrayList<MeasurementData>());
		
		int i = 0;
		for (VariableType varType : varTypeList.getVariableTypes()) {
			MeasurementData data = new MeasurementData(varType.getLocalName(), "value" + i);
			row.getDataList().add(data);
			i++;
		}
		
		return row;
	}
	
	private VariableTypeList createVariableTypeListTestData() {
		VariableTypeList list = new VariableTypeList();
		
		list.add(new VariableType("FACTOR1", "FACTOR 1", createVariable(PhenotypicType.GERMPLASM), 1));
		list.add(new VariableType("FACTOR2", "FACTOR 2", createVariable(PhenotypicType.DATASET), 2));
		list.add(new VariableType("FACTOR3", "FACTOR 3", createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 3));
		list.add(new VariableType("FACTOR4", "FACTOR 4", createVariable(PhenotypicType.TRIAL_DESIGN), 4));
		list.add(new VariableType("FACTOR5", "FACTOR 5", createVariable(PhenotypicType.GERMPLASM), 5));
		list.add(new VariableType("FACTOR6", "FACTOR 6", createVariable(PhenotypicType.GERMPLASM), 6));
		list.add(new VariableType("FACTOR7", "FACTOR 7", createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 7));
		list.add(new VariableType("FACTOR8", "FACTOR 8", createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 8));
		list.add(new VariableType("VARIATE1", "VARIATE 1", createVariable(null), 9));
		list.add(new VariableType("VARIATE2", "VARIATE 2", createVariable(null), 10));
		
		return list;
	}
	
	public List<String> getTrialHeaders(VariableTypeList list) {
		List<String> trialHeaders = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (VariableType var : list.getVariableTypes()) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.equals(var.getStandardVariable().getPhenotypicType())) {
					trialHeaders.add(var.getLocalName());
				}
			}
		}
		return trialHeaders;
	}
	
	private VariableList getStockResult(VariableTypeList varTypeList) {
		VariableList list = new VariableList();
		int i = 0;
		for (VariableType varType : varTypeList.getVariableTypes()) {
			if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
				list.add(new Variable(varType, "value" + i));
			}
			i++;
		}
		return list;
	}
	
	private VariableList getStockResult2(VariableTypeList varTypeList) {
		VariableList list = new VariableList();
		int i = 0;
		for (VariableType varType : varTypeList.getVariableTypes()) {
			if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
				list.add(new Variable(varType, "value" + i));
			}
			i++;
		}
		return list;
	}
	
	private StandardVariable createVariable(PhenotypicType phenotypicType) {
		StandardVariable stdvar = new StandardVariable();
		if (phenotypicType != null) {
			stdvar.setPhenotypicType(phenotypicType);
		}
		return stdvar;
	}
	
	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();
		
		mVarList.add(new MeasurementVariable("FACTOR1", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value0", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value1", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR3", "TRIAL NUMBER", "NUMBER",  "ENUMERATED", "TRIAL INSTANCE", "N", "value2", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR4", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value3", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR5", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value4", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR6", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value5", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR7", "TRIAL NUMBER", "NUMBER",  "ENUMERATED", "TRIAL INSTANCE", "N", "value6", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR8", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value7", "TRIAL"));
		mVarList.add(new MeasurementVariable("VARIATE1", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value8", "STUDY"));
		mVarList.add(new MeasurementVariable("VARIATE2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value9", "STUDY"));
		
		return mVarList;
	}
	
	private StudyDetails createTestStudyDetails() {
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("Study name");
		studyDetails.setTitle("Study title");
		/*studyDetails.setPmKey("123");*/
		studyDetails.setObjective("Test transformer");
		studyDetails.setStudyType(StudyType.T);
		studyDetails.setStartDate("20000101");
		studyDetails.setEndDate("20000130");
		return studyDetails;
	}
}
