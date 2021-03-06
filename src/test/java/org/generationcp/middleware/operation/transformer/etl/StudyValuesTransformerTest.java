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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StudyValuesTransformerTest extends IntegrationTestBase {

	private static StudyValuesTransformer transformer;

	@Before
	public void setUp() throws Exception {
		StudyValuesTransformerTest.transformer = new StudyValuesTransformer();
	}

	@Test
	public void testTransform() throws Exception {

		final Integer germplasmId = Integer.valueOf(1);
		final Integer locationId = Integer.valueOf(1);
		final StudyDetails studyDetails = this.createStudyDetailsTestData();
		final List<MeasurementVariable> measurementVariables = this.createMeasurementVariableListTestData();
		final VariableTypeList varTypeList = this.createVariableTypeListTestData();

		final StudyValues studyVal =
				StudyValuesTransformerTest.transformer.transform(germplasmId, locationId, measurementVariables, varTypeList);

		final VariableList result = studyVal.getVariableList();

		Debug.println(0, "Output:");
		Debug.println(0, "GermplasmId:" + studyVal.getGermplasmId());
		Debug.println(0, "LocationId:" + studyVal.getLocationId());

		for (final Variable stock : result.getVariables()) {
			Debug.println(this.INDENT, stock.toString());
		}

	}

	private StudyDetails createStudyDetailsTestData() {
		final StudyDetails studyDetails =
				new StudyDetails("pheno_t7", "Phenotyping studies of the Population 114", "To evaluate the Population 114", "20130805",
						"20130805", StudyTypeDto.getNurseryDto(), 1, "This is a TrialDataSetName", "This is a measurementDatasetName", Util
					.getCurrentDateAsStringValue(), "1", false);
		return studyDetails;
	}

	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		final List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();

		mVarList.add(new MeasurementVariable("STUDY1", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "value0",
				"STUDY"));
		mVarList.add(new MeasurementVariable("STUDY2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value1", "STUDY"));
		mVarList.add(new MeasurementVariable("STUDY3", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value9", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR4", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value3", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR5", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "value4",
				"STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR6", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value5",
				"STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR7", "TRIAL NUMBER", "NUMBER", "ENUMERATED", "TRIAL INSTANCE", "N", "value6", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR8", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value7", "TRIAL"));
		mVarList.add(new MeasurementVariable("VARIATE1", "Name of Principal Investigator", "DBCV", "ASSIGNED", "PERSON", "C", "value8",
				"STUDY"));
		mVarList.add(new MeasurementVariable("VARIATE2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value9",
				"STUDY"));

		return mVarList;
	}

	private VariableTypeList createVariableTypeListTestData() {
		final VariableTypeList list = new VariableTypeList();

		list.add(new DMSVariableType("STUDY1", "STUDY 1", this.createVariable(PhenotypicType.STUDY), 1));
		list.add(new DMSVariableType("STUDY2", "STUDY 2", this.createVariable(PhenotypicType.STUDY), 2));
		list.add(new DMSVariableType("STUDY3", "STUDY 3", this.createVariable(PhenotypicType.STUDY), 3));
		list.add(new DMSVariableType("FACTOR4", "FACTOR 4", this.createVariable(PhenotypicType.TRIAL_DESIGN), 4));
		list.add(new DMSVariableType("FACTOR5", "FACTOR 5", this.createVariable(PhenotypicType.GERMPLASM), 5));
		list.add(new DMSVariableType("FACTOR6", "FACTOR 6", this.createVariable(PhenotypicType.GERMPLASM), 6));
		list.add(new DMSVariableType("FACTOR7", "FACTOR 7", this.createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 7));
		list.add(new DMSVariableType("FACTOR8", "FACTOR 8", this.createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 8));
		list.add(new DMSVariableType("VARIATE1", "VARIATE 1", this.createVariable(null), 9));
		list.add(new DMSVariableType("VARIATE2", "VARIATE 2", this.createVariable(null), 10));

		return list;
	}

	private StandardVariable createVariable(final PhenotypicType getPhenotypicType) {
		final StandardVariable stdvar = new StandardVariable();
		if (getPhenotypicType != null) {
			stdvar.setPhenotypicType(getPhenotypicType);
		}
		return stdvar;
	}
}
