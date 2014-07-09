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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class TestExperimentValuesTransformer extends TestOutputFormatter {

    private static ExperimentValuesTransformer transformer;	

    @BeforeClass
	public static void setUp() throws Exception {
		transformer = new ExperimentValuesTransformer(Mockito.mock(HibernateSessionProvider.class), Mockito.mock(HibernateSessionProvider.class));
	}
	
	@Test
	public void testTransform() throws Exception {
		MeasurementRow mRow = createMeasurementRowTestData();
		VariableTypeList varTypeList = createVariableTypeListTestData();
		
		ExperimentValues expVal = transformer.transform(mRow,varTypeList,null);
		
		Assert.assertNotNull(expVal);
		Debug.println(INDENT, expVal.toString());
		
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
