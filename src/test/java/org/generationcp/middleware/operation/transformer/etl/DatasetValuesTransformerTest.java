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
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
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
		final String datasetName = "DataSet Name here";
		final String datasetDescription = "DataSet Description here";

		final List<MeasurementVariable> mVarList = this.createMeasurementVariableListTestData();
		final VariableTypeList varTypeList = this.createVariableTypeListTestData();

		final DatasetValues datasetVal =
				DatasetValuesTransformerTest.transformer.transform(datasetName, datasetDescription, mVarList, varTypeList);

		Assert.assertNotNull("The transformation must result in valued dataset collection", datasetVal);
		Assert.assertEquals("Data set name mapping did not work.", datasetName, datasetVal.getName());
		Assert.assertEquals("Data set description mapping did not work.", datasetDescription, datasetVal.getDescription());

		final VariableList variables = datasetVal.getVariables();
		final List<Variable> newlyMappedVariables = variables.getVariables();
		for (final Variable variable : newlyMappedVariables) {
			Assert.assertEquals("According to our test data all variables mapped must be traits", VariableType.TRAIT, variable
					.getVariableType().getVariableType());
		}

	}

	private VariableTypeList createVariableTypeListTestData() {
		final VariableTypeList varTypeList = new VariableTypeList();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(2);

		varTypeList.add(new DMSVariableType("PI Name", "Name of Principal Investigator", standardVariable, 1));
		varTypeList.add(new DMSVariableType("PI ID", "ID of Principal Investigator", standardVariable, 1));
		varTypeList.add(new DMSVariableType("TRIAL", "TRIAL NUMBER", standardVariable, 1));
		varTypeList.add(new DMSVariableType("COOPERATOR", "COOPERATOR NAME", standardVariable, 1));

		return varTypeList;
	}

	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		final List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();

		mVarList.add(new MeasurementVariable(2, "PI Name", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "",
				"STUDY", VariableType.TRAIT));
		mVarList.add(new MeasurementVariable(2, "PI ID", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "", "STUDY",
				VariableType.TRAIT));
		mVarList.add(new MeasurementVariable(2, "TRIAL", "TRIAL NUMBER", "NUMBER", "ENUMERATED", "TRIAL INSTANCE", "N", "1", "TRIAL",
				VariableType.TRAIT));
		mVarList.add(new MeasurementVariable(2, "COOPERATOR", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "", "TRIAL",
				VariableType.TRAIT));

		return mVarList;
	}

}
