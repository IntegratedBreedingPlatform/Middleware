package org.generationcp.middleware.operation.transformer.etl.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;

public class TestExperimentValuesTransformer {
	private long startTime;
	
	private static ExperimentValuesTransformer transformer;	

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		transformer = new ExperimentValuesTransformer(Mockito.mock(HibernateSessionProvider.class), Mockito.mock(HibernateSessionProvider.class));
	}

	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@Test
	public void testTransform() throws Exception {
		System.out.println("testTransform");
		MeasurementRow mRow = createMeasurementRowTestData();
		VariableTypeList varTypeList = createVariableTypeListTestData();
		
		ExperimentValues expVal = transformer.transform(mRow,varTypeList);
		
		Assert.assertNotNull(expVal);
		System.out.println(expVal.toString());
		
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}
	
	private VariableTypeList createVariableTypeListTestData() {
		VariableTypeList varTypeList = new VariableTypeList();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(2);
		
		varTypeList.add(new VariableType("ENTRY", "localDescription1", standardVariable, 1));
		varTypeList.add(new VariableType("GID", "localDescription2", standardVariable, 1));
		varTypeList.add(new VariableType("DESIG", "localDescription3", standardVariable, 1));
		varTypeList.add(new VariableType("CROSS", "localDescription4", standardVariable, 1));
				
		return varTypeList;
	}

	private MeasurementRow createMeasurementRowTestData() {
		MeasurementRow mRow = new MeasurementRow();
		mRow.setStockId(1);
		mRow.setLocationId(1);
		
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		
		dataList.add(new MeasurementData("ENTRY","1"));
		dataList.add(new MeasurementData("GID","-1"));
		dataList.add(new MeasurementData("DESIG","TIANDOUGOU-9"));
		dataList.add(new MeasurementData("CROSS","-"));
		
		mRow.setDataList(dataList);
		
		return mRow;
	}
	
	
}
