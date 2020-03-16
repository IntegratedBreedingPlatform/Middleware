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
import org.generationcp.middleware.domain.etl.MeasurementVariable;
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
	public static void setUp() {
		ExperimentValuesTransformerTest.transformer = new ExperimentValuesTransformer(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testTransform() {
		final MeasurementRow mRow = this.createMeasurementRowTestData();
		final VariableTypeList varTypeList = this.createVariableTypeListTestData();

		final ExperimentValues expVal = ExperimentValuesTransformerTest.transformer.transform(mRow, varTypeList, null, null);

		Assert.assertNotNull(expVal);
		Debug.println(TestOutputFormatter.INDENT, expVal.toString());

	}

	private VariableTypeList createVariableTypeListTestData() {
		final VariableTypeList varTypeList = new VariableTypeList();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(2);

		varTypeList.add(new DMSVariableType("ENTRY", "localDescription1", standardVariable, 1));
		varTypeList.add(new DMSVariableType("GID", "localDescription2", standardVariable, 1));
		varTypeList.add(new DMSVariableType("DESIG", "localDescription3", standardVariable, 1));
		varTypeList.add(new DMSVariableType("CROSS", "localDescription4", standardVariable, 1));

		return varTypeList;
	}

	private MeasurementRow createMeasurementRowTestData() {
		final MeasurementRow mRow = new MeasurementRow();
		mRow.setStockId(1);
		mRow.setLocationId(1);

		final List<MeasurementData> dataList = new ArrayList<>();

		final MeasurementData data1 = new MeasurementData("ENTRY", "1");
		data1.setMeasurementVariable(new MeasurementVariable());
		dataList.add(data1);

		final MeasurementData data2 = new MeasurementData("GID", "-1");
		data2.setMeasurementVariable(new MeasurementVariable());
		dataList.add(data2);

		final MeasurementData data3 = new MeasurementData("DESIG", "TIANDOUGOU-9");
		data3.setMeasurementVariable(new MeasurementVariable());
		dataList.add(data3);

		final MeasurementData data4 = new MeasurementData("CROSS", "-");
		data4.setMeasurementVariable(new MeasurementVariable());
		dataList.add(data4);

		mRow.setDataList(dataList);

		return mRow;
	}

}
