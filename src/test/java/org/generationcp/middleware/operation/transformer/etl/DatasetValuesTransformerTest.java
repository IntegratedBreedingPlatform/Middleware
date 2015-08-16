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
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class DatasetValuesTransformerTest extends TestOutputFormatter {

	private static DatasetValuesTransformer transformer;

	@BeforeClass
	public static void setUp() throws Exception {
		DatasetValuesTransformerTest.transformer = new DatasetValuesTransformer(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testTransform() throws Exception {
		String datasetName = "DataSet Name here";
		String datasetDescription = "DataSet Description here";
		DataSetType dataType = DataSetType.PLOT_DATA;

		List<MeasurementVariable> mVarList = this.createMeasurementVariableListTestData();
		VariableTypeList varTypeList = this.createVariableTypeListTestData();

		DatasetValues datasetVal =
				DatasetValuesTransformerTest.transformer.transform(datasetName, datasetDescription, dataType, mVarList, varTypeList);

		Assert.assertNotNull(datasetVal);
		Debug.println(TestOutputFormatter.INDENT, datasetVal.toString());
	}

	private VariableTypeList createVariableTypeListTestData() {
		VariableTypeList varTypeList = new VariableTypeList();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(2);

		varTypeList.add(new DMSVariableType("PI Name", "Name of Principal Investigator", standardVariable, 1));
		varTypeList.add(new DMSVariableType("PI ID", "ID of Principal Investigator", standardVariable, 1));
		varTypeList.add(new DMSVariableType("TRIAL", "TRIAL NUMBER", standardVariable, 1));
		varTypeList.add(new DMSVariableType("COOPERATOR", "COOPERATOR NAME", standardVariable, 1));

		return varTypeList;
	}

	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();

		mVarList.add(new MeasurementVariable("PI Name", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "", "STUDY"));
		mVarList.add(new MeasurementVariable("PI ID", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "", "STUDY"));
		mVarList.add(new MeasurementVariable("TRIAL", "TRIAL NUMBER", "NUMBER", "ENUMERATED", "TRIAL INSTANCE", "N", "1", "TRIAL"));
		mVarList.add(new MeasurementVariable("COOPERATOR", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "", "TRIAL"));

		return mVarList;
	}

}
