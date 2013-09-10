package org.generationcp.middleware.operation.transformer.etl.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.transformer.etl.DatasetValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;

public class TestDatasetValuesTransformer {
	private long startTime;
	
	private static DatasetValuesTransformer transformer;	

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		transformer = new DatasetValuesTransformer(Mockito.mock(HibernateSessionProvider.class), Mockito.mock(HibernateSessionProvider.class));
	}

	@Before
	public void beforeEachTest() {
		startTime = System.nanoTime();
	}
	
	@Test
	public void testTransform() throws Exception {
		String datasetName = "DataSet Name here";
		String datasetDescription = "DataSet Description here";
		DataSetType dataType = DataSetType.PLOT_DATA;
		
		List<MeasurementVariable> mVarList = createMeasurementVariableListTestData();
		VariableTypeList varTypeList = createVariableTypeListTestData();
		
		DatasetValues datasetVal = transformer.transform(datasetName, datasetDescription, dataType, mVarList, varTypeList);
		
		Assert.assertNotNull(datasetVal);
		System.out.println(datasetVal.toString());
		
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
		
		varTypeList.add(new VariableType("PI Name", "Name of Principal Investigator", standardVariable, 1));
		varTypeList.add(new VariableType("PI ID", "ID of Principal Investigator", standardVariable, 1));
		varTypeList.add(new VariableType("TRIAL", "TRIAL NUMBER", standardVariable, 1));
		varTypeList.add(new VariableType("COOPERATOR", "COOPERATOR NAME", standardVariable, 1));
				
		return varTypeList;
	}

	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();
		
		mVarList.add(new MeasurementVariable("PI Name", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "", "STUDY"));
		mVarList.add(new MeasurementVariable("PI ID", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "", "STUDY"));
		mVarList.add(new MeasurementVariable("TRIAL", "TRIAL NUMBER", "NUMBER",  "ENUMERATED", "TRIAL INSTANCE", "N", "1", "TRIAL"));
		mVarList.add(new MeasurementVariable("COOPERATOR", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "", "TRIAL"));
		
		return mVarList;
	}
	
	
}
