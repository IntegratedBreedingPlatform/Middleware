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
package org.generationcp.middleware.operation.transformer.etl.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.operation.transformer.etl.Transformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestVariableTypeListTransformer extends TestOutputFormatter {
    
    private static final Logger LOG = LoggerFactory.getLogger(TestVariableTypeListTransformer.class);

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
	
	private static final StandardVariable[] FACTOR_ARR = new StandardVariable[] {STDVAR1, STDVAR2}; 
	private static final StandardVariable[] VARIATE_ARR = new StandardVariable[] {STDVAR3}; 

	@BeforeClass
	public static void setUp() throws Exception {
		transformer = Mockito.mock(VariableTypeListTransformer.class);
		standardVariableBuilder = Mockito.mock(StandardVariableBuilder.class);
		injectStandardVariableBuilder(transformer, standardVariableBuilder);
		STDVAR1.setId(1);
		Mockito.when(
				standardVariableBuilder.findOrSave(FACTOR1, FACTOR1, TEST_PROPERTY1, TEST_SCALE1, TEST_METHOD1, 
						PhenotypicType.TRIAL_ENVIRONMENT, "C"))
				.thenReturn(STDVAR1);
		STDVAR2.setId(2);
		Mockito.when(
				standardVariableBuilder.findOrSave(FACTOR2, FACTOR2, TEST_PROPERTY2, TEST_SCALE2, TEST_METHOD2, 
						PhenotypicType.TRIAL_ENVIRONMENT, "C"))
				.thenReturn(STDVAR2);
		STDVAR3.setId(3);
		Mockito.when(
				standardVariableBuilder.findOrSave(VARIATE1, VARIATE1, TEST_PROPERTY3, TEST_SCALE3, TEST_METHOD3, null, "C"))
				.thenReturn(STDVAR3);
	}
	
	@Test
	public void testTransformFactor() throws Exception {
		boolean isVariate = false;
		testTransform(isVariate);
	}
	
	@Test
	public void testTransformVariate() throws Exception {
		boolean isVariate = true;
		testTransform(isVariate);
	}
	
	private void testTransform(boolean isVariate) throws Exception { 
		List<MeasurementVariable> measurementVariables = createMeasurmentVariablesTestData(isVariate);
		Mockito.when(transformer.transform(measurementVariables, isVariate)).thenCallRealMethod();
		Mockito.when(transformer.transform(measurementVariables, isVariate, 1)).thenCallRealMethod();
		VariableTypeList variableTypeList = transformer.transform(measurementVariables, isVariate);
		Assert.assertNotNull(variableTypeList);
		int i = 0;
		for (VariableType variableType : variableTypeList.getVariableTypes()) {
			Assert.assertEquals(measurementVariables.get(i).getName(), variableType.getLocalName());
			Assert.assertEquals(measurementVariables.get(i).getDescription(), variableType.getLocalDescription());
			Assert.assertEquals(i+1, variableType.getRank());
			Assert.assertNotNull(variableType.getStandardVariable());
			if (isVariate) {
				Assert.assertEquals(VARIATE_ARR[i], variableType.getStandardVariable());
			} else {
				Assert.assertEquals(FACTOR_ARR[i], variableType.getStandardVariable());
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
		    LOG.error(e.getMessage(), e);
		}
	}
	
	private List<MeasurementVariable> createMeasurmentVariablesTestData(boolean isVariate) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		
		if (!isVariate) {
			MeasurementVariable variable = new MeasurementVariable(FACTOR1, FACTOR1, TEST_SCALE1, TEST_METHOD1, TEST_PROPERTY1, "C", "value1", "TRIAL");
			list.add(variable);
			
			variable = new MeasurementVariable(FACTOR2, FACTOR2, TEST_SCALE2, TEST_METHOD2, TEST_PROPERTY2, "C", "value2", "TRIAL");
			list.add(variable);
		
		} else {
			MeasurementVariable variable = new MeasurementVariable(VARIATE1, VARIATE1, TEST_SCALE3, TEST_METHOD3, TEST_PROPERTY3, "C", "value3", "TRIAL");
			list.add(variable);
		}
		
		return list;
	}
}
