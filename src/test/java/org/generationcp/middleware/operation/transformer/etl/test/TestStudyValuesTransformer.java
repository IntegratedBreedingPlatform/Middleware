package org.generationcp.middleware.operation.transformer.etl.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.transformer.etl.StudyValuesTransformer;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;

public class TestStudyValuesTransformer {
	private long startTime;
	
	private static StudyValuesTransformer transformer;

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		
		transformer = new StudyValuesTransformer(Mockito.mock(HibernateSessionProvider.class), Mockito.mock(HibernateSessionProvider.class));
	}

	@Before
	public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
	}
	
	@Test
	public void testTransform() throws Exception {

		Integer germplasmId = Integer.valueOf(1);
		Integer locationId = Integer.valueOf(1);
		StudyDetails studyDetails = createStudyDetailsTestData();
		List<MeasurementVariable> measurementVariables= createMeasurementVariableListTestData();
		VariableTypeList varTypeList = createVariableTypeListTestData();
		
		StudyValues studyVal = transformer.transform(germplasmId, locationId, studyDetails, measurementVariables, varTypeList);
		
		VariableList result = studyVal.getVariableList();

		Debug.println(0, "Output:");
		Debug.println(0, "GermplasmId:" + studyVal.getGermplasmId());
		Debug.println(0, "LocationId:" + studyVal.getLocationId());
		
		for (Variable stock : result.getVariables()) {
			Debug.println(0, stock.toString());
		}
		
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}
	
	private StudyDetails createStudyDetailsTestData() {
		StudyDetails studyDetails = new StudyDetails("pheno_t7", "Phenotyping trials of the Population 114", "0",
				"To evaluate the Population 114", "20130805", "20130805", StudyType.N, 1,"This is a TrialDataSetName","This is a measurementDatasetName");
		return studyDetails;
	}
	
	private List<MeasurementVariable> createMeasurementVariableListTestData() {
		List<MeasurementVariable> mVarList = new ArrayList<MeasurementVariable>();
		
		mVarList.add(new MeasurementVariable("STUDY1", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value0", "STUDY"));
		mVarList.add(new MeasurementVariable("STUDY2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value1", "STUDY"));
		mVarList.add(new MeasurementVariable("STUDY3", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value9", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR4", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value3", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR5", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value4", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR6", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value5", "STUDY"));
		mVarList.add(new MeasurementVariable("FACTOR7", "TRIAL NUMBER", "NUMBER",  "ENUMERATED", "TRIAL INSTANCE", "N", "value6", "TRIAL"));
		mVarList.add(new MeasurementVariable("FACTOR8", "COOPERATOR NAME", "DBCV", "Conducted", "Person", "C", "value7", "TRIAL"));
		mVarList.add(new MeasurementVariable("VARIATE1", "Name of Principal Investigator", "DBCV",  "ASSIGNED", "PERSON",  "C", "value8", "STUDY"));
		mVarList.add(new MeasurementVariable("VARIATE2", "ID of Principal Investigator", "DBID", "ASSIGNED", "PERSON", "N", "value9", "STUDY"));
		
		return mVarList;
	}
	
	private VariableTypeList createVariableTypeListTestData() {
		VariableTypeList list = new VariableTypeList();
		
		list.add(new VariableType("STUDY1", "STUDY 1", createVariable(PhenotypicType.STUDY), 1));
		list.add(new VariableType("STUDY2", "STUDY 2", createVariable(PhenotypicType.STUDY), 2));
		list.add(new VariableType("STUDY3", "STUDY 3", createVariable(PhenotypicType.STUDY), 3));
		list.add(new VariableType("FACTOR4", "FACTOR 4", createVariable(PhenotypicType.TRIAL_DESIGN), 4));
		list.add(new VariableType("FACTOR5", "FACTOR 5", createVariable(PhenotypicType.GERMPLASM), 5));
		list.add(new VariableType("FACTOR6", "FACTOR 6", createVariable(PhenotypicType.GERMPLASM), 6));
		list.add(new VariableType("FACTOR7", "FACTOR 7", createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 7));
		list.add(new VariableType("FACTOR8", "FACTOR 8", createVariable(PhenotypicType.TRIAL_ENVIRONMENT), 8));
		list.add(new VariableType("VARIATE1", "VARIATE 1", createVariable(null), 9));
		list.add(new VariableType("VARIATE2", "VARIATE 2", createVariable(null), 10));
		
		return list;
	}
	
	private StandardVariable createVariable(PhenotypicType getPhenotypicType) {
		StandardVariable stdvar = new StandardVariable();
		if (getPhenotypicType != null) {
			stdvar.setPhenotypicType(getPhenotypicType);
		}
		return stdvar;
	}
}
