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

package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class VariableListTransformerTest extends TestOutputFormatter {

	private static VariableListTransformer transformer;

	@BeforeClass
	public static void setUp() throws Exception {
		VariableListTransformerTest.transformer = new VariableListTransformer(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testTransformStock() throws Exception {
		Debug.println(TestOutputFormatter.INDENT, "testTransformStock");
		final VariableTypeList variableTypeList = this.createVariableTypeListTestData();
		final MeasurementRow measurementRow = this.createMeasurementRowTestData(variableTypeList);
		final List<String> headers = this.getStudyHeaders(variableTypeList);
		Debug.println(TestOutputFormatter.INDENT, "Input MeasurmentRow");
		measurementRow.print(TestOutputFormatter.INDENT);
		Debug.println(TestOutputFormatter.INDENT, "Input VariableTypeList");
		variableTypeList.print(TestOutputFormatter.INDENT);

		final VariableList stocks = VariableListTransformerTest.transformer.transformStock(measurementRow, variableTypeList, headers);

		Assert.assertNotNull(stocks);
		final VariableList result = this.getStockResult(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;
		Debug.println(TestOutputFormatter.INDENT, "Output:");
		for (final Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(TestOutputFormatter.INDENT);
			i++;
		}
	}

	@Test
	public void transformTrialEnvironment() throws Exception {
		Debug.println(TestOutputFormatter.INDENT, "transformEnvironment");
		final VariableTypeList variableTypeList = this.createVariableTypeListTestData();
		final MeasurementRow measurementRow = this.createMeasurementRowTestData(variableTypeList);
		final List<String> headers = this.getStudyHeaders(variableTypeList);
		Debug.println(TestOutputFormatter.INDENT, "Input MeasurmentRow");
		measurementRow.print(TestOutputFormatter.INDENT);
		Debug.println(TestOutputFormatter.INDENT, "Input VariableTypeList");
		variableTypeList.print(TestOutputFormatter.INDENT);

		final VariableList stocks =
				VariableListTransformerTest.transformer.transformTrialEnvironment(measurementRow, variableTypeList, headers);

		Assert.assertNotNull(stocks);
		final VariableList result = this.getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;
		Debug.println(TestOutputFormatter.INDENT, "Output:");
		for (final Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(TestOutputFormatter.INDENT);
			i++;
		}
	}

	@Test
	public void transformTrialEnvironment2() throws Exception {
		Debug.println(TestOutputFormatter.INDENT, "testTransformEnvironment 2");
		final List<MeasurementVariable> mVarList = this.createMeasurementVariableListTestData();
		final VariableTypeList variableTypeList = this.createVariableTypeListTestData();

		Debug.println(TestOutputFormatter.INDENT, "Input MeasurementVariables");
		mVarList.toString();
		Debug.println(TestOutputFormatter.INDENT, "Input VariableTypeList");
		variableTypeList.print(TestOutputFormatter.INDENT);

		final VariableList stocks = VariableListTransformerTest.transformer.transformTrialEnvironment(mVarList, variableTypeList);
		Assert.assertNotNull(stocks);

		final VariableList result = this.getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());

		Debug.println(TestOutputFormatter.INDENT, stocks.toString());

		int i = 0;
		Debug.println(TestOutputFormatter.INDENT, "Output:");
		for (final Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(TestOutputFormatter.INDENT);
			i++;
		}
	}

	private MeasurementRow createMeasurementRowTestData(final VariableTypeList varTypeList) {
		final MeasurementRow row = new MeasurementRow();
		row.setDataList(new ArrayList<MeasurementData>());

		int i = 0;
		for (final DMSVariableType varType : varTypeList.getVariableTypes()) {
			final MeasurementData data = new MeasurementData(varType.getLocalName(), "value" + i);
			row.getDataList().add(data);
			i++;
		}

		return row;
	}

	private VariableTypeList createVariableTypeListTestData() {
		final VariableTypeList list = new VariableTypeList();

		list.add(new DMSVariableType("FACTOR1", "FACTOR 1", this.createVariable(PhenotypicType.GERMPLASM), 1));
		list.add(new DMSVariableType("FACTOR2", "FACTOR 2", this.createVariable(PhenotypicType.DATASET), 2));
		list.add(new DMSVariableType("FACTOR3", "FACTOR 3", this.createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 3));
		list.add(new DMSVariableType("FACTOR4", "FACTOR 4", this.createVariable(PhenotypicType.TRIAL_DESIGN), 4));
		list.add(new DMSVariableType("FACTOR5", "FACTOR 5", this.createVariable(PhenotypicType.GERMPLASM), 5));
		list.add(new DMSVariableType("FACTOR6", "FACTOR 6", this.createVariable(PhenotypicType.GERMPLASM), 6));
		list.add(new DMSVariableType("FACTOR7", "FACTOR 7", this.createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 7));
		list.add(new DMSVariableType("FACTOR8", "FACTOR 8", this.createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 8));
		list.add(new DMSVariableType("VARIATE1", "VARIATE 1", this.createVariable(null), 9));
		list.add(new DMSVariableType("VARIATE2", "VARIATE 2", this.createVariable(null), 10));

		return list;
	}

	public List<String> getStudyHeaders(final VariableTypeList list) {
		final List<String> headers = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (final DMSVariableType var : list.getVariableTypes()) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.equals(var.getStandardVariable().getPhenotypicType())) {
					headers.add(var.getLocalName());
				}
			}
		}
		return headers;
	}

	private VariableList getStockResult(final VariableTypeList varTypeList) {
		final VariableList list = new VariableList();
		int i = 0;
		for (final DMSVariableType varType : varTypeList.getVariableTypes()) {
			if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
				list.add(new Variable(varType, "value" + i));
			}
			i++;
		}
		return list;
	}

	private VariableList getStockResult2(final VariableTypeList varTypeList) {
		final VariableList list = new VariableList();
		int i = 0;
		for (final DMSVariableType varType : varTypeList.getVariableTypes()) {
			if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
				list.add(new Variable(varType, "value" + i));
			}
			i++;
		}
		return list;
	}

	private StandardVariable createVariable(final PhenotypicType phenotypicType) {
		final StandardVariable stdvar = new StandardVariable();
		if (phenotypicType != null) {
			stdvar.setPhenotypicType(phenotypicType);
		}
		return stdvar;
	}

	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		final List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();

		mVarList.add(new MeasurementVariable("FACTOR1", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "value0",
				"STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value1",
				"STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR3", "TRIAL NUMBER", "NUMBER", "ENUMERATED", "TRIAL INSTANCE", "N", "value2", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR4", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value3", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR5", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "value4",
				"STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR6", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value5",
				"STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR7", "TRIAL NUMBER", "NUMBER", "ENUMERATED", "TRIAL INSTANCE", "N", "value6", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR8", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value7", "TRIAL"));
		mVarList.add(new MeasurementVariable("VARIATE1", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "value8",
				"STUDY"));
		mVarList.add(new MeasurementVariable("VARIATE2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value9",
				"STUDY"));

		return mVarList;
	}

	private StudyDetails createTestStudyDetails() {
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("Study name");
		studyDetails.setDescription("Study title");
		studyDetails.setCreatedBy("1");
		studyDetails.setObjective("Test transformer");
		studyDetails.setStudyType(new StudyTypeDto("T"));
		studyDetails.setStartDate("20000101");
		studyDetails.setEndDate("20000130");
		return studyDetails;
	}
}
