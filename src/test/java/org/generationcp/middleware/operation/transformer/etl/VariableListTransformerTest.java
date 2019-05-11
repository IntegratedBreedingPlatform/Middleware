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

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VariableListTransformerTest {

	private static VariableListTransformer transformer;
	private final Random random = new Random();

	@BeforeClass
	public static void setUp() {
		VariableListTransformerTest.transformer = new VariableListTransformer(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testTransformStock() {

		final VariableTypeList variableTypeList = this.createVariableTypeListTestData();
		final MeasurementRow measurementRow = this.createMeasurementRowTestData(variableTypeList);

		final VariableList stocks =
			VariableListTransformerTest.transformer.transformStock(measurementRow, variableTypeList, new ArrayList<String>());

		Assert.assertNotNull(stocks);
		final VariableList result = this.getStockResult(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());

		int i = 0;
		for (final Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			i++;
		}
	}

	@Test
	public void transformTrialEnvironment() throws Exception {

		final VariableTypeList variableTypeList = this.createVariableTypeListTestData();
		final MeasurementRow measurementRow = this.createMeasurementRowTestData(variableTypeList);
		final List<String> headers = this.getStudyHeaders(variableTypeList);

		final VariableList stocks =
			VariableListTransformerTest.transformer.transformTrialEnvironment(measurementRow, variableTypeList, headers);

		Assert.assertNotNull(stocks);
		final VariableList result = this.getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;

		for (final Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			i++;
		}
	}

	@Test
	@Ignore // FIXME IBP-2634
	public void transformTrialEnvironment2() throws Exception {

		final List<MeasurementVariable> mVarList = this.createMeasurementVariableListTestData();
		final VariableTypeList variableTypeList = this.createVariableTypeListTestData();

		final VariableList stocks = VariableListTransformerTest.transformer.transformTrialEnvironment(mVarList, variableTypeList);
		Assert.assertNotNull(stocks);

		final VariableList result = this.getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());

		int i = 0;
		for (final Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			i++;
		}
	}

	private MeasurementRow createMeasurementRowTestData(final VariableTypeList varTypeList) {
		final MeasurementRow row = new MeasurementRow();
		row.setDataList(new ArrayList<MeasurementData>());

		int i = 0;
		for (final DMSVariableType varType : varTypeList.getVariableTypes()) {
			final MeasurementData data = new MeasurementData(varType.getLocalName(), "value" + i);
			data.setMeasurementVariable(new MeasurementVariable());
			row.getDataList().add(data);
			i++;
		}

		return row;
	}

	private VariableTypeList createVariableTypeListTestData() {
		final VariableTypeList list = new VariableTypeList();

		list.add(this.createDMSVariableType("FACTOR1", "", 1, PhenotypicType.GERMPLASM, VariableType.GERMPLASM_DESCRIPTOR));
		list.add(this.createDMSVariableType("FACTOR2", "", 2, PhenotypicType.DATASET, VariableType.STUDY_DETAIL));
		list.add(this.createDMSVariableType("FACTOR3", "", 3, PhenotypicType.TRIAL_ENVIRONMENT, VariableType.ENVIRONMENT_DETAIL));
		list.add(this.createDMSVariableType("FACTOR4", "", 4, PhenotypicType.TRIAL_DESIGN, VariableType.ENVIRONMENT_DETAIL));
		list.add(this.createDMSVariableType("FACTOR5", "", 5, PhenotypicType.GERMPLASM, VariableType.GERMPLASM_DESCRIPTOR));
		list.add(this.createDMSVariableType("FACTOR6", "", 6, PhenotypicType.GERMPLASM, VariableType.GERMPLASM_DESCRIPTOR));
		list.add(this.createDMSVariableType("FACTOR7", "", 7, PhenotypicType.TRIAL_ENVIRONMENT, VariableType.ENVIRONMENT_DETAIL));
		list.add(this.createDMSVariableType("FACTOR8", "", 8, PhenotypicType.TRIAL_ENVIRONMENT, VariableType.ENVIRONMENT_DETAIL));
		list.add(this.createDMSVariableType("VARIATE1", "", 9, PhenotypicType.VARIATE, VariableType.TRAIT));
		list.add(this.createDMSVariableType("VARIATE2", "", 10, PhenotypicType.VARIATE, VariableType.TRAIT));

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

	private DMSVariableType createDMSVariableType(
		final String localName, final String localDescription, final int rank,
		final PhenotypicType role, final VariableType variableType) {
		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName(localName);
		dmsVariableType.setLocalDescription(localDescription);
		dmsVariableType.setRank(rank);
		dmsVariableType.setRole(role);
		dmsVariableType.setVariableType(variableType);
		dmsVariableType.setStandardVariable(this.createStandardVariable(localName));
		return dmsVariableType;
	}

	private StandardVariable createStandardVariable(final String name) {

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(this.random.nextInt(1000));
		standardVariable.setName(name);
		standardVariable.setProperty(
			new Term(this.random.nextInt(1000), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10)));
		standardVariable
			.setScale(new Term(this.random.nextInt(1000), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10)));
		standardVariable
			.setMethod(new Term(this.random.nextInt(1000), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10)));
		standardVariable.setDataType(new Term(DataType.CHARACTER_VARIABLE.getId(), "Character variable", "variable with char values"));
		standardVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));

		return standardVariable;
	}
}
