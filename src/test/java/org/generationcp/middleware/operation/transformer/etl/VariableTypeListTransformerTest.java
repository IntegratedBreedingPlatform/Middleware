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

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class VariableTypeListTransformerTest extends DataManagerIntegrationTest {

	private static VariableTypeListTransformer transformer;

	private static final String FACTOR1 = "FACTOR1";
	private static final String FACTOR2 = "FACTOR2";
	private static final String VARIATE1 = "VARIATE1";

	private static final String TEST_PROPERTY1 = "testProperty1";
	private static final String TEST_METHOD1 = "testMethod1";
	private static final String TEST_SCALE1 = "testScale1";

	private static final String TEST_PROPERTY2 = "testProperty2";
	private static final String TEST_METHOD2 = "testMethod2";
	private static final String TEST_SCALE2 = "testScale2";

	private static final String TEST_PROPERTY3 = "testProperty3";
	private static final String TEST_METHOD3 = "testMethod3";
	private static final String TEST_SCALE3 = "testScale3";

	private static final StandardVariable STDVAR1 = new StandardVariable();
	private static final StandardVariable STDVAR2 = new StandardVariable();
	private static final StandardVariable STDVAR3 = new StandardVariable();

	private static final StandardVariable[] FACTOR_ARR = new StandardVariable[] {VariableTypeListTransformerTest.STDVAR1,
			VariableTypeListTransformerTest.STDVAR2};
	private static final StandardVariable[] VARIATE_ARR = new StandardVariable[] {VariableTypeListTransformerTest.STDVAR3};

	private static final String PROGRAM_UUID = "1234567";
	@BeforeClass
	public static void setUp() throws Exception {
		HibernateSessionProvider sessionProvider = DataManagerIntegrationTest.managerFactory.getSessionProvider();
		VariableTypeListTransformerTest.transformer = new VariableTypeListTransformer(sessionProvider);
	}

	@Test
	public void testTransformFactor() throws Exception {
		boolean isVariate = false;
		this.testTransform(isVariate);
	}

	@Test
	public void testTransformVariate() throws Exception {
		boolean isVariate = true;
		this.testTransform(isVariate);
	}

	private void testTransform(boolean isVariate) throws Exception {
		List<MeasurementVariable> measurementVariables = this.createMeasurmentVariablesTestData(isVariate);
		VariableTypeList variableTypeList = VariableTypeListTransformerTest.transformer.transform(
				measurementVariables, isVariate, PROGRAM_UUID);
		Assert.assertNotNull(variableTypeList);
		int i = 0;
		for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {
			Assert.assertEquals(measurementVariables.get(i).getName(), variableType.getLocalName());
			Assert.assertEquals(measurementVariables.get(i).getDescription(), variableType.getLocalDescription());
			Assert.assertEquals(i + 1, variableType.getRank());
			Assert.assertNotNull(variableType.getStandardVariable());
			StandardVariable actual = variableType.getStandardVariable();
			StandardVariable expected = null;
			if (isVariate) {
				expected = VariableTypeListTransformerTest.VARIATE_ARR[i];
			} else {
				expected = VariableTypeListTransformerTest.FACTOR_ARR[i];
			}
			Assert.assertEquals(expected.getName(), actual.getName());
			Assert.assertEquals(expected.getDescription(), actual.getDescription());
			Assert.assertEquals(expected.getProperty().getName(), actual.getProperty().getName());
			Assert.assertEquals(expected.getScale().getName(), actual.getScale().getName());
			Assert.assertEquals(expected.getMethod().getName(), actual.getMethod().getName());
			Assert.assertEquals(expected.getDataType().getName(), actual.getDataType().getName());
			Assert.assertEquals(expected.getPhenotypicType(), actual.getPhenotypicType());
			Assert.assertEquals(expected.getName(), actual.getName());
			variableType.print(1);
			i++;
		}
	}

	private List<MeasurementVariable> createMeasurmentVariablesTestData(boolean isVariate) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();

		if (!isVariate) {
			MeasurementVariable variable =
					new MeasurementVariable(VariableTypeListTransformerTest.FACTOR1, VariableTypeListTransformerTest.FACTOR1,
							VariableTypeListTransformerTest.TEST_SCALE1, VariableTypeListTransformerTest.TEST_METHOD1,
							VariableTypeListTransformerTest.TEST_PROPERTY1, "C", "value1", "TRIAL");
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			mapMeasurementVariableToStandardVariable(variable,STDVAR1);
			list.add(variable);
			

			variable =
					new MeasurementVariable(VariableTypeListTransformerTest.FACTOR2, VariableTypeListTransformerTest.FACTOR2,
							VariableTypeListTransformerTest.TEST_SCALE2, VariableTypeListTransformerTest.TEST_METHOD2,
							VariableTypeListTransformerTest.TEST_PROPERTY2, "C", "value2", "TRIAL");
			variable.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
			mapMeasurementVariableToStandardVariable(variable,STDVAR2);
			list.add(variable);

		} else {
			MeasurementVariable variable =
					new MeasurementVariable(VariableTypeListTransformerTest.VARIATE1, VariableTypeListTransformerTest.VARIATE1,
							VariableTypeListTransformerTest.TEST_SCALE3, VariableTypeListTransformerTest.TEST_METHOD3,
							VariableTypeListTransformerTest.TEST_PROPERTY3, "C", "value3", "TRIAL");
			variable.setRole(PhenotypicType.VARIATE);
			mapMeasurementVariableToStandardVariable(variable,STDVAR3);
			list.add(variable);
		}

		return list;
	}

	private void mapMeasurementVariableToStandardVariable(
			MeasurementVariable variable, StandardVariable stdVar) {
		stdVar.setName(variable.getName());
		stdVar.setDescription(variable.getDescription());
		stdVar.setProperty(new Term(10,variable.getProperty(),variable.getProperty()));
		stdVar.setScale(new Term(10,variable.getScale(),variable.getScale()));
		stdVar.setMethod(new Term(10,variable.getMethod(),variable.getMethod()));
		DataType dataType = null;
		if("N".equals(variable.getDataType())) {
			dataType = DataType.getById(TermId.NUMERIC_VARIABLE.getId());
		} else if("C".equals(variable.getDataType())) {
			dataType = DataType.getById(TermId.CHARACTER_VARIABLE.getId());
		} else {
			dataType = DataType.getByName(variable.getDataType());
		}	
		if(dataType!=null) {
			stdVar.setDataType(new Term(dataType.getId(),dataType.getName(),dataType.getName()));
		}
		stdVar.setPhenotypicType(variable.getRole());
	}
}
