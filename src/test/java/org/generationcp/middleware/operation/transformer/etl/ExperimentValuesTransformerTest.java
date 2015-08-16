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
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ExperimentValuesTransformerTest extends TestOutputFormatter {

	private static ExperimentValuesTransformer transformer;

	@BeforeClass
	public static void setUp() throws Exception {
		ExperimentValuesTransformerTest.transformer = new ExperimentValuesTransformer(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testTransform() throws Exception {
		MeasurementRow mRow = this.createMeasurementRowTestData();
		VariableTypeList varTypeList = this.createVariableTypeListTestData();

		ExperimentValues expVal = ExperimentValuesTransformerTest.transformer.transform(mRow, varTypeList, null);

		Assert.assertNotNull(expVal);
		Debug.println(TestOutputFormatter.INDENT, expVal.toString());

	}

	private VariableTypeList createVariableTypeListTestData() {
		VariableTypeList varTypeList = new VariableTypeList();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(2);

		varTypeList.add(new DMSVariableType("ENTRY", "localDescription1", standardVariable, 1));
		varTypeList.add(new DMSVariableType("GID", "localDescription2", standardVariable, 1));
		varTypeList.add(new DMSVariableType("DESIG", "localDescription3", standardVariable, 1));
		varTypeList.add(new DMSVariableType("CROSS", "localDescription4", standardVariable, 1));

		return varTypeList;
	}

	private MeasurementRow createMeasurementRowTestData() {
		MeasurementRow mRow = new MeasurementRow();
		mRow.setStockId(1);
		mRow.setLocationId(1);

		List<MeasurementData> dataList = new ArrayList<MeasurementData>();

		dataList.add(new MeasurementData("ENTRY", "1"));
		dataList.add(new MeasurementData("GID", "-1"));
		dataList.add(new MeasurementData("DESIG", "TIANDOUGOU-9"));
		dataList.add(new MeasurementData("CROSS", "-"));

		mRow.setDataList(dataList);

		return mRow;
	}

}
