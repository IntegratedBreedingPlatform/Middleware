package org.generationcp.middleware.operation.transformer.etl.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.transformer.etl.Transformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;

public class TestVariableTypeListTransformer {

	private long startTime;
	
	private static VariableTypeListTransformer transformer;
	private static OntologyDataManager ontologyDataManager;
	
	private static final String TEST_PROPERTY1 = "testProperty1";
	private static final String TEST_METHOD1 = "testMethod1";
	private static final String TEST_SCALE1 = "testScale1";
	
	private static final String TEST_PROPERTY2 = "testProperty2";
	private static final String TEST_METHOD2 = "testMethod2";
	private static final String TEST_SCALE2 = "testScale2";

	private static final String TEST_PROPERTY3 = "testProperty3";
	private static final String TEST_METHOD3 = "testMethod3";
	private static final String TEST_SCALE3 = "testScale3";
	
	private static final StandardVariable stdvar1 = new StandardVariable();
	private static final StandardVariable stdvar2 = new StandardVariable();
	private static final StandardVariable stdvar3 = new StandardVariable();
	
	private static final StandardVariable[] stdvarArr = new StandardVariable[] {stdvar1, stdvar2, stdvar3}; 
	

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		transformer = Mockito.mock(VariableTypeListTransformer.class);
		ontologyDataManager = Mockito.mock(OntologyDataManager.class);
		//Mockito.when(transformer.getOntologyDataManager()).thenReturn(ontologyDataManager);
		injectOntologyDataManager(transformer, ontologyDataManager);
		stdvar1.setId(1);
		Mockito.when(
				ontologyDataManager.findStandardVariableByTraitScaleMethodNames(TEST_PROPERTY1, TEST_SCALE1, TEST_METHOD1))
				.thenReturn(stdvar1);
		stdvar2.setId(2);
		Mockito.when(
				ontologyDataManager.findStandardVariableByTraitScaleMethodNames(TEST_PROPERTY2, TEST_SCALE2, TEST_METHOD2))
				.thenReturn(stdvar2);
		stdvar3.setId(3);
		Mockito.when(
				ontologyDataManager.findStandardVariableByTraitScaleMethodNames(TEST_PROPERTY3, TEST_SCALE3, TEST_METHOD3))
				.thenReturn(stdvar3);
	}

	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}
	
	@Test
	public void testTransform() throws Exception {

		List<MeasurementVariable> measurementVariables = createMeasurmentVariablesTestData();
		Mockito.when(transformer.transform(measurementVariables)).thenCallRealMethod();
		VariableTypeList variableTypeList = transformer.transform(measurementVariables);
		Assert.assertNotNull(variableTypeList);
		int i = 0;
		for (VariableType variableType : variableTypeList.getVariableTypes()) {
			Assert.assertEquals(measurementVariables.get(i).getName(), variableType.getLocalName());
			Assert.assertEquals(measurementVariables.get(i).getDescription(), variableType.getLocalDescription());
			Assert.assertEquals(i+1, variableType.getRank());
			Assert.assertNotNull(variableType.getStandardVariable());
			Assert.assertEquals(stdvarArr[i], variableType.getStandardVariable());
			i++;
		}
		
	}
	
	private static void injectOntologyDataManager(Transformer transformer, OntologyDataManager ontologyDataManager) {
		try {
			Field field = Transformer.class.getDeclaredField("ontologyDataManager");
			field.setAccessible(true);
			field.set(transformer, ontologyDataManager);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<MeasurementVariable> createMeasurmentVariablesTestData() {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		
		MeasurementVariable variable = new MeasurementVariable("FACTOR1", "FACTOR 1", TEST_SCALE1, TEST_METHOD1, TEST_PROPERTY1, "C", "value1", "label1");
		list.add(variable);
		
		variable = new MeasurementVariable("FACTOR2", "FACTOR 2", TEST_SCALE2, TEST_METHOD2, TEST_PROPERTY2, "C", "value2", "label2");
		list.add(variable);
		
		variable = new MeasurementVariable("VARIATE1", "VARIATE 1", TEST_SCALE3, TEST_METHOD3, TEST_PROPERTY3, "C", "value3", "label3");
		list.add(variable);
		
		return list;
	}
}
