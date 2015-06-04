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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableTypeListTransformerTest extends TestOutputFormatter {

	private static final Logger LOG = LoggerFactory.getLogger(VariableTypeListTransformerTest.class);

	private static VariableTypeListTransformer transformer;
	private static StandardVariableBuilder standardVariableBuilder;

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

	@BeforeClass
	public static void setUp() throws Exception {
		VariableTypeListTransformerTest.transformer = Mockito.mock(VariableTypeListTransformer.class);
		VariableTypeListTransformerTest.standardVariableBuilder = Mockito.mock(StandardVariableBuilder.class);
		VariableTypeListTransformerTest.injectStandardVariableBuilder(VariableTypeListTransformerTest.transformer,
				VariableTypeListTransformerTest.standardVariableBuilder);
		VariableTypeListTransformerTest.STDVAR1.setId(1);
		Mockito.when(
				VariableTypeListTransformerTest.standardVariableBuilder.findOrSave(VariableTypeListTransformerTest.FACTOR1,
						VariableTypeListTransformerTest.FACTOR1, VariableTypeListTransformerTest.TEST_PROPERTY1,
						VariableTypeListTransformerTest.TEST_SCALE1, VariableTypeListTransformerTest.TEST_METHOD1,
						PhenotypicType.TRIAL_ENVIRONMENT, "C")).thenReturn(VariableTypeListTransformerTest.STDVAR1);
		VariableTypeListTransformerTest.STDVAR2.setId(2);
		Mockito.when(
				VariableTypeListTransformerTest.standardVariableBuilder.findOrSave(VariableTypeListTransformerTest.FACTOR2,
						VariableTypeListTransformerTest.FACTOR2, VariableTypeListTransformerTest.TEST_PROPERTY2,
						VariableTypeListTransformerTest.TEST_SCALE2, VariableTypeListTransformerTest.TEST_METHOD2,
						PhenotypicType.TRIAL_ENVIRONMENT, "C")).thenReturn(VariableTypeListTransformerTest.STDVAR2);
		VariableTypeListTransformerTest.STDVAR3.setId(3);
		Mockito.when(
				VariableTypeListTransformerTest.standardVariableBuilder.findOrSave(VariableTypeListTransformerTest.VARIATE1,
						VariableTypeListTransformerTest.VARIATE1, VariableTypeListTransformerTest.TEST_PROPERTY3,
						VariableTypeListTransformerTest.TEST_SCALE3, VariableTypeListTransformerTest.TEST_METHOD3, null, "C")).thenReturn(
				VariableTypeListTransformerTest.STDVAR3);
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
		Mockito.when(VariableTypeListTransformerTest.transformer.transform(measurementVariables, isVariate)).thenCallRealMethod();
		Mockito.when(VariableTypeListTransformerTest.transformer.transform(measurementVariables, isVariate, 1)).thenCallRealMethod();
		VariableTypeList variableTypeList = VariableTypeListTransformerTest.transformer.transform(measurementVariables, isVariate);
		Assert.assertNotNull(variableTypeList);
		int i = 0;
		for (VariableType variableType : variableTypeList.getVariableTypes()) {
			Assert.assertEquals(measurementVariables.get(i).getName(), variableType.getLocalName());
			Assert.assertEquals(measurementVariables.get(i).getDescription(), variableType.getLocalDescription());
			Assert.assertEquals(i + 1, variableType.getRank());
			Assert.assertNotNull(variableType.getStandardVariable());
			if (isVariate) {
				Assert.assertEquals(VariableTypeListTransformerTest.VARIATE_ARR[i], variableType.getStandardVariable());
			} else {
				Assert.assertEquals(VariableTypeListTransformerTest.FACTOR_ARR[i], variableType.getStandardVariable());
			}
			variableType.print(1);
			i++;
		}
	}

	private static void injectStandardVariableBuilder(Transformer transformer, StandardVariableBuilder standardVariableBuilder) {
		try {
			Field field = Transformer.class.getDeclaredField("standardVariableBuilder");
			field.setAccessible(true);
			field.set(transformer, standardVariableBuilder);
		} catch (Exception e) {
			VariableTypeListTransformerTest.LOG.error(e.getMessage(), e);
		}
	}

	private List<MeasurementVariable> createMeasurmentVariablesTestData(boolean isVariate) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();

		if (!isVariate) {
			MeasurementVariable variable =
					new MeasurementVariable(VariableTypeListTransformerTest.FACTOR1, VariableTypeListTransformerTest.FACTOR1,
							VariableTypeListTransformerTest.TEST_SCALE1, VariableTypeListTransformerTest.TEST_METHOD1,
							VariableTypeListTransformerTest.TEST_PROPERTY1, "C", "value1", "TRIAL");
			list.add(variable);

			variable =
					new MeasurementVariable(VariableTypeListTransformerTest.FACTOR2, VariableTypeListTransformerTest.FACTOR2,
							VariableTypeListTransformerTest.TEST_SCALE2, VariableTypeListTransformerTest.TEST_METHOD2,
							VariableTypeListTransformerTest.TEST_PROPERTY2, "C", "value2", "TRIAL");
			list.add(variable);

		} else {
			MeasurementVariable variable =
					new MeasurementVariable(VariableTypeListTransformerTest.VARIATE1, VariableTypeListTransformerTest.VARIATE1,
							VariableTypeListTransformerTest.TEST_SCALE3, VariableTypeListTransformerTest.TEST_METHOD3,
							VariableTypeListTransformerTest.TEST_PROPERTY3, "C", "value3", "TRIAL");
			list.add(variable);
		}

		return list;
	}
}
