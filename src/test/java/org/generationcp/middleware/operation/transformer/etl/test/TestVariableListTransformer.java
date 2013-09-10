package org.generationcp.middleware.operation.transformer.etl.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.FactorType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.operation.transformer.etl.VariableListTransformer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestVariableListTransformer {
	
	private long startTime;
	
	private static VariableListTransformer transformer;
	
	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		transformer = new VariableListTransformer();
		
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
	public void testTransformStock() throws Exception {
		System.out.println("testTransformStock");
		VariableTypeList variableTypeList = createVariableTypeListTestData();
		MeasurementRow measurementRow = createMeasurementRowTestData(variableTypeList);
		
		System.out.println("Input MeasurmentRow");
		measurementRow.print(1);
		System.out.println("Input VariableTypeList");
		variableTypeList.print(1);
		
		VariableList stocks = transformer.transformStock(measurementRow, variableTypeList);
		
		Assert.assertNotNull(stocks);
		VariableList result = getStockResult(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;
		System.out.println("Output:");
		for (Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(1);
			i++;
		}
	}
	
	@Test
	public void transformTrialEnvironment() throws Exception {
		System.out.println("testTransformTrialEnvironment");
		VariableTypeList variableTypeList = createVariableTypeListTestData();
		MeasurementRow measurementRow = createMeasurementRowTestData(variableTypeList);
		
		System.out.println("Input MeasurmentRow");
		measurementRow.print(1);
		System.out.println("Input VariableTypeList");
		variableTypeList.print(1);
		
		VariableList stocks = transformer.transformTrialEnvironment(measurementRow, variableTypeList);
		
		VariableList result = getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		int i = 0;
		System.out.println("Output:");
		for (Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(1);
			i++;
		}
	}
	
	@Test
	public void transformTrialEnvironment2() throws Exception {
		System.out.println("testTransformTrialEnvironment 2");
		List<MeasurementVariable> mVarList = createMeasurementVariableListTestData();
		VariableTypeList variableTypeList = createVariableTypeListTestData();
		
		System.out.println("Input MeasurementVariables");
		mVarList.toString();
		System.out.println("Input VariableTypeList");
		variableTypeList.print(1);
		
		VariableList stocks = transformer.transformTrialEnvironment(mVarList, variableTypeList);
		Assert.assertNotNull(stocks);
		
		VariableList result = getStockResult2(variableTypeList);
		Assert.assertEquals(result.getVariables().size(), stocks.getVariables().size());
		
		System.out.println(stocks.toString());
		
		int i = 0;
		System.out.println("Output:");
		for (Variable stock : stocks.getVariables()) {
			Assert.assertEquals(result.getVariables().get(i).getValue(), stock.getValue());
			Assert.assertEquals(result.getVariables().get(i).getVariableType(), stock.getVariableType());
			stock.print(1);
			i++;
		}
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
		
		list.add(new VariableType("FACTOR1", "FACTOR 1", createVariable(FactorType.GERMPLASM), 1));
		list.add(new VariableType("FACTOR2", "FACTOR 2", createVariable(FactorType.DATASET), 2));
		list.add(new VariableType("FACTOR3", "FACTOR 3", createVariable(FactorType.TRIAL_ENVIRONMENT), 3));
		list.add(new VariableType("FACTOR4", "FACTOR 4", createVariable(FactorType.TRIAL_DESIGN), 4));
		list.add(new VariableType("FACTOR5", "FACTOR 5", createVariable(FactorType.GERMPLASM), 5));
		list.add(new VariableType("FACTOR6", "FACTOR 6", createVariable(FactorType.GERMPLASM), 6));
		list.add(new VariableType("FACTOR7", "FACTOR 7", createVariable(FactorType.TRIAL_ENVIRONMENT), 7));
		list.add(new VariableType("FACTOR8", "FACTOR 8", createVariable(FactorType.TRIAL_ENVIRONMENT), 8));
		list.add(new VariableType("VARIATE1", "VARIATE 1", createVariable(null), 9));
		list.add(new VariableType("VARIATE2", "VARIATE 2", createVariable(null), 10));
		
		return list;
	}
	
	private VariableList getStockResult(VariableTypeList varTypeList) {
		VariableList list = new VariableList();
		int i = 0;
		for (VariableType varType : varTypeList.getVariableTypes()) {
			if (varType.getStandardVariable().getFactorType() == FactorType.GERMPLASM) {
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
			if (varType.getStandardVariable().getFactorType() == FactorType.TRIAL_ENVIRONMENT) {
				list.add(new Variable(varType, "value" + (i+1)));
			}
			i++;
		}
		return list;
	}
	
	private StandardVariable createVariable(FactorType factorType) {
		StandardVariable stdvar = new StandardVariable();
		if (factorType != null) {
			stdvar.setFactorType(factorType);
		}
		return stdvar;
	}
	
	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();
		
		mVarList.add(new MeasurementVariable("FACTOR1", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value1", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value2", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR3", "TRIAL NUMBER", "NUMBER",  "ENUMERATED", "TRIAL INSTANCE", "N", "value3", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR4", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value4", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR5", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value5", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR6", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value6", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR7", "TRIAL NUMBER", "NUMBER",  "ENUMERATED", "TRIAL INSTANCE", "N", "value7", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR8", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value8", "TRIAL"));
		mVarList.add(new MeasurementVariable("VARIATE1", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value9", "STUDY"));
		mVarList.add(new MeasurementVariable("VARIATE2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value10", "STUDY"));
		
		return mVarList;
	}
}
